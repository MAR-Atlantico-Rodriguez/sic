package sic.service.impl;

import java.util.List;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sic.modelo.Cliente;
import sic.modelo.Empresa;
import sic.modelo.Factura;
import sic.modelo.FacturaVenta;
import sic.modelo.Movimiento;
import sic.modelo.NotaDeCredito;
import sic.modelo.NotaDeDebito;
import sic.modelo.RenglonNota;
import sic.modelo.TipoDeComprobante;
import sic.repository.NotaDeCreditoRepository;
import sic.repository.NotaDeDebitoRepository;
import sic.service.BusinessServiceException;
import sic.service.INotaService;

@Service
public class NotaServiceImpl implements INotaService {
    
    private final NotaDeCreditoRepository notaDeCreditoRepository;
    private final NotaDeDebitoRepository notaDeDebitoRespository;
    
    @Autowired
    @Lazy
    public NotaServiceImpl(NotaDeCreditoRepository notaDeCreditoRepository, NotaDeDebitoRepository notaDeDebitoRespository) {
        this.notaDeCreditoRepository = notaDeCreditoRepository;
        this. notaDeDebitoRespository = notaDeDebitoRespository;
    }

    @Override
    public NotaDeCredito getNotaDeCreditoPorID(Long id_NotaDeCredito) {
        return this.notaDeCreditoRepository.findById(id_NotaDeCredito);
    }

    @Override
    public NotaDeDebito getNotaDeDebitoPorID(Long id_NotaDeDebito) {
        return this.notaDeDebitoRespository.findById(id_NotaDeDebito);
    }

    @Override
    public List<FacturaVenta> getFacturasDeNotaDeCredito(Long id_NotaDeCredito) {
        return this.getNotaDeCreditoPorID(id_NotaDeCredito).getFacturasVenta();
    }

    @Override
    public List<FacturaVenta> getFacturasDeNotaDeDebito(Long id_NotaDeDebito) {
        return this.getNotaDeDebitoPorID(id_NotaDeDebito).getFacturasVenta();
    }

    @Override
    public TipoDeComprobante[] getTipoNota(Empresa empresa, Cliente cliente) {
        if (empresa.getCondicionIVA().isDiscriminaIVA() && cliente.getCondicionIVA().isDiscriminaIVA()) {
            TipoDeComprobante[] tiposPermitidos = new TipoDeComprobante[4];
            tiposPermitidos[0] = TipoDeComprobante.NOTA_CREDITO_A;
            tiposPermitidos[1] = TipoDeComprobante.NOTA_CREDITO_X;
            tiposPermitidos[2] = TipoDeComprobante.NOTA_DEBITO_A;
            tiposPermitidos[3] = TipoDeComprobante.NOTA_DEBITO_X;
            return tiposPermitidos;
        } else if (empresa.getCondicionIVA().isDiscriminaIVA() && !cliente.getCondicionIVA().isDiscriminaIVA()) {
            TipoDeComprobante[] tiposPermitidos = new TipoDeComprobante[4];
            tiposPermitidos[0] = TipoDeComprobante.NOTA_CREDITO_B;
            tiposPermitidos[1] = TipoDeComprobante.NOTA_CREDITO_X;
            tiposPermitidos[2] = TipoDeComprobante.NOTA_DEBITO_B;
            tiposPermitidos[3] = TipoDeComprobante.NOTA_DEBITO_X;
            return tiposPermitidos;
        } else {
            TipoDeComprobante[] tiposPermitidos = new TipoDeComprobante[4];
            tiposPermitidos[0] = TipoDeComprobante.NOTA_CREDITO_C;
            tiposPermitidos[1] = TipoDeComprobante.NOTA_CREDITO_X;
            tiposPermitidos[2] = TipoDeComprobante.NOTA_DEBITO_C;
            tiposPermitidos[3] = TipoDeComprobante.NOTA_DEBITO_X;
            return tiposPermitidos;
        }
    }

    @Override
    public List<RenglonNota> getRenglonesDeNotaCredito(Long id_notaCredito) {
        return this.getNotaDeCreditoPorID(id_notaCredito).getRenglonesNota();
    }
    
    @Override
    public List<RenglonNota> getRenglonesDeNotaDebito(Long id_notaDebito) {
        return this.getNotaDeDebitoPorID(id_notaDebito).getRenglonesNota();
    }

    private void ValidarNotaCredito(NotaDeCredito notaDeCredito) {
        //Requeridos
        if (notaDeCredito.getEmpresa() != null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_nota_de_credito_empresa_vacia"));
        }
        if (notaDeCredito.getCliente() != null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_nota_de_credito_cliente_vacio"));
        }
        if (notaDeCredito.getFecha() != null) {
            if (notaDeCredito.getFacturasVenta() != null && !notaDeCredito.getFacturasVenta().isEmpty()) {
                for (FacturaVenta f : notaDeCredito.getFacturasVenta()) {
                    if (notaDeCredito.getFecha().compareTo(f.getFecha()) >= 0) {
                        throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                                .getString("mensaje_nota_de_credito_fecha_incorrecta"));
                    }
                    if (!notaDeCredito.getCliente().equals(f.getCliente())) {
                        throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                                .getString("mensaje_nota_de_credito_cliente_incorrecto"));
                    }
                    if (!notaDeCredito.getEmpresa().equals(f.getEmpresa())) {
                        throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                                .getString("mensaje_nota_de_credito_empresa_incorrecta"));
                    }
                }
            } else {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_nota_de_credito_sin_factura"));
            }
        }
        if (notaDeCredito.getTipoDeComprobante() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_nota_de_cretido_tipo_comprobante_vacio"));
        }
//        if (notaDeCredito.) {} hacer el calculo de renglones
    }

    @Override
    @Transactional
    public void actualizarNotaDeCredito(NotaDeCredito notaDeCredito) {
        //gdhgfjgkl
    }

    @Override
    @Transactional
    public void actualizarNotaDeDebito(NotaDeDebito notaDeDebito) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Transactional
    public void eliminarNotaDeCredito(long[] idNotaDeCredito) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Transactional
    public void eliminarNotaDeDebito(long[] idNotaDeDebito) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double calcularTotalNotaDeCredito(NotaDeCredito notaDeCredito) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double calcularIvaNetoNotaDeCredito(NotaDeCredito notaDeCredito) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double calcularTotalNotaDeDebito(NotaDeCredito notaDeCredito) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double calcularIvaNetoNotaDeDebito(NotaDeCredito notaDeCredito) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RenglonNota calcularRenglon(TipoDeComprobante tipoDeComprobante, Movimiento movimiento, double cantidad, Long idProducto) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
