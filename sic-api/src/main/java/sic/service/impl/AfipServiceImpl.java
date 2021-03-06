package sic.service.impl;

import sic.service.IAfipService;
import afip.wsaa.wsdl.LoginCms;
import afip.wsfe.wsdl.AlicIva;
import afip.wsfe.wsdl.ArrayOfAlicIva;
import afip.wsfe.wsdl.ArrayOfFECAEDetRequest;
import afip.wsfe.wsdl.FEAuthRequest;
import afip.wsfe.wsdl.FECAECabRequest;
import afip.wsfe.wsdl.FECAEDetRequest;
import afip.wsfe.wsdl.FECAERequest;
import afip.wsfe.wsdl.FECAEResponse;
import afip.wsfe.wsdl.FECAESolicitar;
import afip.wsfe.wsdl.FECompUltimoAutorizado;
import afip.wsfe.wsdl.FERecuperaLastCbteResponse;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.ResourceBundle;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.client.WebServiceClientException;
import sic.modelo.AfipWSAACredencial;
import sic.modelo.Empresa;
import sic.modelo.FacturaVenta;
import sic.modelo.TipoDeComprobante;
import sic.service.BusinessServiceException;
import sic.service.IConfiguracionDelSistemaService;
import sic.util.FormatterFechaHora;
import sic.util.Utilidades;

@Service
@Transactional
public class AfipServiceImpl implements IAfipService {

    private final AfipWebServiceSOAPClient afipWebServiceSOAPClient;
    private final IConfiguracionDelSistemaService configuracionDelSistemaService;
    private final Logger LOGGER = Logger.getLogger(this.getClass());    
    private final FormatterFechaHora formatterFechaHora = new FormatterFechaHora(FormatterFechaHora.FORMATO_FECHA_INTERNACIONAL);

    @Autowired
    public AfipServiceImpl(AfipWebServiceSOAPClient afipWebServiceSOAPClient, IConfiguracionDelSistemaService cds) {
        this.afipWebServiceSOAPClient = afipWebServiceSOAPClient;
        this.configuracionDelSistemaService = cds;
    }

    @Override
    public AfipWSAACredencial getAfipWSAACredencial(String afipNombreServicio, Empresa empresa) {
        AfipWSAACredencial afipCredencial = new AfipWSAACredencial();
        String loginTicketResponse = "";
        byte[] p12file = configuracionDelSistemaService.getConfiguracionDelSistemaPorEmpresa(empresa).getCertificadoAfip();
        if (p12file.length == 0) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes").getString("mensaje_cds_certificado_vacio"));
        }
        String p12signer = configuracionDelSistemaService.getConfiguracionDelSistemaPorEmpresa(empresa).getFirmanteCertificadoAfip();
        String p12pass = configuracionDelSistemaService.getConfiguracionDelSistemaPorEmpresa(empresa).getPasswordCertificadoAfip();
        long ticketTime = 3600000L; //siempre devuelve por 12hs
        byte[] loginTicketRequest_xml_cms = afipWebServiceSOAPClient.crearCMS(p12file, p12pass, p12signer, afipNombreServicio, ticketTime);
        LoginCms loginCms = new LoginCms();
        loginCms.setIn0(Base64.getEncoder().encodeToString(loginTicketRequest_xml_cms));
        try {
            loginTicketResponse = afipWebServiceSOAPClient.loginCMS(loginCms);        
        } catch (WebServiceClientException ex) {
            LOGGER.error(ex.getMessage());            
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes").getString("mensaje_token_wsaa_error"));
        }
        try {
            Reader tokenReader = new StringReader(loginTicketResponse);
            Document tokenDoc = new SAXReader(false).read(tokenReader);
            afipCredencial.setToken(tokenDoc.valueOf("/loginTicketResponse/credentials/token"));
            afipCredencial.setSign(tokenDoc.valueOf("/loginTicketResponse/credentials/sign"));
            afipCredencial.setCuit(empresa.getCuip());
        } catch (DocumentException ex) {
            LOGGER.error(ex.getMessage());
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes").getString("mensaje_error_procesando_xml"));
        }
        return afipCredencial;
    }

    @Override    
    public FacturaVenta autorizarFacturaVenta(FacturaVenta factura) {        
        if (configuracionDelSistemaService.getConfiguracionDelSistemaPorEmpresa(factura.getEmpresa()).isFacturaElectronicaHabilitada() == false) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes").getString("mensaje_cds_fe_habilitada"));
        }
        if (factura.getTipoComprobante() != TipoDeComprobante.FACTURA_A
                && factura.getTipoComprobante() != TipoDeComprobante.FACTURA_B
                && factura.getTipoComprobante() != TipoDeComprobante.FACTURA_C) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes").getString("mensaje_factura_tipo_no_valido"));
        }
        if (factura.getCAE() != 0) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes").getString("mensaje_factura_ya_autorizada"));
        }
        AfipWSAACredencial afipCredencial = this.getAfipWSAACredencial("wsfe", factura.getEmpresa());
        FEAuthRequest feAuthRequest = new FEAuthRequest();
        feAuthRequest.setCuit(afipCredencial.getCuit());
        feAuthRequest.setSign(afipCredencial.getSign());
        feAuthRequest.setToken(afipCredencial.getToken());
        FECAESolicitar fecaeSolicitud = new FECAESolicitar();
        fecaeSolicitud.setAuth(feAuthRequest);
        int nroPuntoDeVentaAfip = configuracionDelSistemaService.getConfiguracionDelSistemaPorEmpresa(factura.getEmpresa()).getNroPuntoDeVentaAfip();
        int siguienteNroComprobante = this.getSiguienteNroComprobante(feAuthRequest, factura.getTipoComprobante(), nroPuntoDeVentaAfip);
        fecaeSolicitud.setFeCAEReq(this.transformFacturaVentaToFECAERequest(factura, siguienteNroComprobante, nroPuntoDeVentaAfip));
        try {
            FECAEResponse response = afipWebServiceSOAPClient.FECAESolicitar(fecaeSolicitud);
            String msjError = "";
            // errores generales de la request
            if (response.getErrors() != null) {
                msjError = response.getErrors().getErr().get(0).getCode() + "-" + response.getErrors().getErr().get(0).getMsg();
                LOGGER.error(msjError);
                if (!msjError.isEmpty()) {
                    throw new BusinessServiceException(msjError);
                }
            }
            // errores particulares de cada comprobante
            if (response.getFeDetResp().getFECAEDetResponse().get(0).getResultado().equals("R")) {
                msjError += response.getFeDetResp().getFECAEDetResponse().get(0).getObservaciones().getObs().get(0).getMsg();
                LOGGER.error(msjError);                
                throw new BusinessServiceException(msjError);
            }
            long cae = Long.valueOf(response.getFeDetResp().getFECAEDetResponse().get(0).getCAE());            
            factura.setCAE(cae);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");            
            factura.setVencimientoCAE(formatter.parse(response.getFeDetResp().getFECAEDetResponse().get(0).getCAEFchVto()));
            factura.setNumSerieAfip(nroPuntoDeVentaAfip);
            factura.setNumFacturaAfip(siguienteNroComprobante);
            return factura;
        } catch (WebServiceClientException ex) {
            LOGGER.error(ex.getMessage());
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes").getString("mensaje_autorizacion_error"));
        } catch (ParseException ex) {
            LOGGER.error(ex.getMessage());
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes").getString("mensaje_error_procesando_fecha"));
        }
    }
    
    @Override
    public int getSiguienteNroComprobante(FEAuthRequest feAuthRequest, TipoDeComprobante tipo, int nroPuntoDeVentaAfip) {
        FECompUltimoAutorizado solicitud = new FECompUltimoAutorizado();                
        solicitud.setAuth(feAuthRequest);
        // 1: Factura A, 2: Nota de Débito A, 3: Nota de Crédito A, 6: Factura B, 7: Nota de Débito B, 8: Nota de Crédito B. 11: Factura C
        switch (tipo) {
            case FACTURA_A:
                solicitud.setCbteTipo(1);
                break;
            case FACTURA_B:                
                solicitud.setCbteTipo(6);
                break;
            case FACTURA_C:                
                solicitud.setCbteTipo(11);
                break;
        }
        solicitud.setPtoVta(nroPuntoDeVentaAfip);
        try {
            FERecuperaLastCbteResponse response = afipWebServiceSOAPClient.FECompUltimoAutorizado(solicitud);
            return response.getCbteNro() + 1;
        } catch (WebServiceClientException ex) {
            LOGGER.error(ex.getMessage());            
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes").getString("mensaje_siguiente_nro_comprobante_error"));
        }
    }
    
    @Override
    public FECAERequest transformFacturaVentaToFECAERequest(FacturaVenta factura, int siguienteNroComprobante, int nroPuntoDeVentaAfip) {
        FECAERequest fecaeRequest = new FECAERequest();        
        FECAECabRequest cabecera = new FECAECabRequest();
        FECAEDetRequest detalle = new FECAEDetRequest();        
        // CbteTipo = 1: Factura A, 2: Nota de Débito A, 3: Nota de Crédito A, 6: Factura B, 7: Nota de Débito B, 8: Nota de Crédito B. 11: Factura C
        // DocTipo = 80: CUIT, 86: CUIL, 96: DNI, 99: Doc.(Otro)
        switch (factura.getTipoComprobante()) {
            case FACTURA_A:
                cabecera.setCbteTipo(1);
                detalle.setDocTipo(80);
                detalle.setDocNro(Long.valueOf(factura.getCliente().getIdFiscal().replace("-", "")));
                break;
            case FACTURA_B:
                cabecera.setCbteTipo(6);
                // menor a $1000, si DocTipo = 99 DocNro debe ser igual a 0 (simula un consumidor final ???)
                if (factura.getTotal() < 1000) {
                    detalle.setDocTipo(99);
                    detalle.setDocNro(0);
                } else {
                    if (factura.getCliente().getIdFiscal().equals("")) {
                        throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes").getString("mensaje_cliente_sin_idFiscal_error"));
                    }
                    detalle.setDocTipo(80);
                    detalle.setDocNro(Long.valueOf(factura.getCliente().getIdFiscal().replace("-", "")));
                }
                break;
            case FACTURA_C:
                cabecera.setCbteTipo(11);
                detalle.setDocTipo(0);
                detalle.setDocNro(0);                
                break;
        }
        cabecera.setCantReg(1); // Cantidad de registros del detalle del comprobante o lote de comprobantes de ingreso
        cabecera.setPtoVta(nroPuntoDeVentaAfip); // Punto de Venta del comprobante que se está informando. Si se informa más de un comprobante, todos deben corresponder al mismo punto de venta
        fecaeRequest.setFeCabReq(cabecera);
        ArrayOfFECAEDetRequest arrayDetalle = new ArrayOfFECAEDetRequest();        
        detalle.setCbteDesde(siguienteNroComprobante);
        detalle.setCbteHasta(siguienteNroComprobante);
        detalle.setConcepto(1); // Concepto del Comprobante. Valores permitidos: 1 Productos, 2 Servicios, 3 Productos y Servicios        
        detalle.setCbteFch(formatterFechaHora.format(factura.getFecha()).replace("/", "")); // Fecha del comprobante (yyyymmdd)        
        ArrayOfAlicIva arrayIVA = new ArrayOfAlicIva();
        if (factura.getIva_21_neto() != 0) {
            AlicIva alicIVA21 = new AlicIva();
            alicIVA21.setId(5); // Valores: 5 (21%), 4 (10.5%)
            alicIVA21.setBaseImp(Utilidades.round((100 * factura.getIva_21_neto()) / 21, 2)); // Se calcula con: (100 * IVA_neto) / %IVA
            alicIVA21.setImporte(Utilidades.round(factura.getIva_21_neto(), 2));
            arrayIVA.getAlicIva().add(alicIVA21);
        }
        if (factura.getIva_105_neto() != 0) {
            AlicIva alicIVA105 = new AlicIva();
            alicIVA105.setId(4); // Valores: 5 (21%), 4 (10.5%)
            alicIVA105.setBaseImp(Utilidades.round((100 * factura.getIva_105_neto()) / 10.5, 2)); // Se calcula con: (100 * IVA_neto) / %IVA
            alicIVA105.setImporte(Utilidades.round(factura.getIva_105_neto(), 2));
            arrayIVA.getAlicIva().add(alicIVA105);
        }
        detalle.setIva(arrayIVA); // Array para informar las alícuotas y sus importes asociados a un comprobante <AlicIva>. Para comprobantes tipo C y Bienes Usados – Emisor Monotributista no debe informar el array.
        detalle.setImpIVA(Utilidades.round(factura.getIva_105_neto() + factura.getIva_21_neto(), 2)); // Suma de los importes del array de IVA. Para comprobantes tipo C debe ser igual a cero (0).
        detalle.setImpNeto(Utilidades.round(factura.getSubTotal_bruto(), 2)); // Importe neto gravado. Debe ser menor o igual a Importe total y no puede ser menor a cero. Para comprobantes tipo C este campo corresponde al Importe del Sub Total                
        detalle.setImpTotal(Utilidades.round(factura.getTotal(), 2)); // Importe total del comprobante, Debe ser igual a Importe neto no gravado + Importe exento + Importe neto gravado + todos los campos de IVA al XX% + Importe de tributos                        
        detalle.setMonId("PES"); // Código de moneda del comprobante. Consultar método FEParamGetTiposMonedas para valores posibles
        detalle.setMonCotiz(1); // Cotización de la moneda informada. Para PES, pesos argentinos la misma debe ser 1
        arrayDetalle.getFECAEDetRequest().add(detalle);                               
        fecaeRequest.setFeDetReq(arrayDetalle);
        return fecaeRequest;
    }

}