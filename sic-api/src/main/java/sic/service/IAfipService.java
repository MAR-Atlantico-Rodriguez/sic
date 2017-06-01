package sic.service;

import afip.wsfe.wsdl.FEAuthRequest;
import afip.wsfe.wsdl.FECAERequest;
import sic.modelo.AfipWSAACredencial;
import sic.modelo.Empresa;
import sic.modelo.FacturaVenta;
import sic.modelo.TipoDeComprobante;

public interface IAfipService {

    AfipWSAACredencial getAfipWSAACredencial(String afipNombreServicio, Empresa empresa);
    
    FacturaVenta autorizarFacturaVenta(FacturaVenta factura);
    
    int getSiguienteNroComprobante(FEAuthRequest feAuthRequest, TipoDeComprobante tipo, int nroPuntoDeVentaAfip);
    
    FECAERequest transformFacturaVentaToFECAERequest(FacturaVenta factura, int siguienteNroComprobante, int nroPuntoDeVentaAfip);
    
}
