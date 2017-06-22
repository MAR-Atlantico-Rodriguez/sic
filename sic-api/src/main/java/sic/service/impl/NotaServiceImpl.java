package sic.service.impl;

import java.util.List;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sic.modelo.Cliente;
import sic.modelo.Empresa;
import sic.modelo.FacturaVenta;
import sic.modelo.Movimiento;
import sic.modelo.NotaDeCredito;
import sic.modelo.NotaDeDebito;
import sic.modelo.Producto;
import sic.modelo.RenglonNota;
import sic.modelo.TipoDeComprobante;
import sic.repository.NotaDeCreditoRepository;
import sic.repository.NotaDeDebitoRepository;
import sic.service.BusinessServiceException;
import sic.service.IClienteService;
import sic.service.IEmpresaService;
import sic.service.IFacturaService;
import sic.service.INotaService;
import sic.service.IProductoService;

@Service
public class NotaServiceImpl implements INotaService {
    
    private final NotaDeCreditoRepository notaDeCreditoRepository;
    private final NotaDeDebitoRepository notaDeDebitoRespository;
    private final IProductoService productoService;
    private final IFacturaService facturaService;
    private final IClienteService clienteService;
    private final IEmpresaService empresaService;
    
    @Autowired
    @Lazy
    public NotaServiceImpl(NotaDeCreditoRepository notaDeCreditoRepository, NotaDeDebitoRepository notaDeDebitoRespository,
                            IProductoService productoService, IFacturaService facturaService, IClienteService clienteService,
                            IEmpresaService empresaService) {
        this.notaDeCreditoRepository = notaDeCreditoRepository;
        this. notaDeDebitoRespository = notaDeDebitoRespository;
        this.productoService = productoService;
        this.facturaService = facturaService;
        this.clienteService = clienteService;
        this.empresaService = empresaService;
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
    public List<NotaDeCredito> getNotasDeCreditoPorClienteYEmpresa(Long idCliente, Long idEmpresa) {
        return this.notaDeCreditoRepository.findAllByClienteAndEmpresaAndEliminada(this.clienteService.getClientePorId(idCliente),
                this.empresaService.getEmpresaPorId(idEmpresa), false);
    }

    @Override
    public List<NotaDeDebito> getNotasDeDebitoPorClienteYEmpresa(Long idCliente, Long idEmpresa) {
        return this.notaDeDebitoRespository.findAllByClienteAndEmpresaAndEliminada(this.clienteService.getClientePorId(idCliente),
                this.empresaService.getEmpresaPorId(idEmpresa), false);
    }
    
    @Override
    public double getSaldoNotas(Long idCliente, Long IdEmpresa) {
        List<NotaDeCredito> notasCredito = this.getNotasDeCreditoPorClienteYEmpresa(idCliente, IdEmpresa);
        List<NotaDeDebito> notasDebito = this.getNotasDeDebitoPorClienteYEmpresa(idCliente, IdEmpresa);
        double credito = 0.0;
        double debito = 0.0;
        credito = notasCredito.stream().map((nota) -> nota.getTotal()).reduce(credito, (accumulator, _item) -> accumulator + _item);
        debito = notasDebito.stream().map((nota) -> nota.getTotal()).reduce(debito, (accumulator, _item) -> accumulator + _item);
        return credito - debito; 
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
                    .getString("mensaje_nota_de_credito_tipo_comprobante_vacio"));
        }
        //validaciones de los con los calculos sobre los renglones de la nota
    }
    
    private void validarNotaDebito(NotaDeDebito notaDebito){
        //Requeridos
        if (notaDebito.getEmpresa() != null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_nota_de_debito_empresa_vacia"));
        }
        if (notaDebito.getCliente() != null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_nota_de_debito_cliente_vacia"));
        }
        if (notaDebito.getFecha() != null) {
            if (notaDebito.getFacturasVenta() != null && !notaDebito.getFacturasVenta().isEmpty()) {
                for (FacturaVenta f : notaDebito.getFacturasVenta()) {
                    if (notaDebito.getFecha().compareTo(f.getFecha()) >= 0) {
                        throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                                .getString("mensaje_nota_de_debito_fecha_incorrecta"));
                    }
                    if (!notaDebito.getCliente().equals(f.getCliente())) {
                        throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                                .getString("mensaje_nota_de_debito_cliente_incorrecto"));
                    }
                    if (!notaDebito.getEmpresa().equals(f.getEmpresa())) {
                        throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                                .getString("mensaje_nota_de_debito_empresa_incorrecta"));
                    }
                }
            }
        }
        if (notaDebito.getTipoDeComprobante() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_nota_de_debito_tipo_comprobante_vacio"));
        }
        //validaciones de los con los calculos sobre los renglones de la nota
    }

    @Override
    public NotaDeCredito guardarNotaCredito(NotaDeCredito notaDeCredito) {
        this.ValidarNotaCredito(notaDeCredito);
        return notaDeCreditoRepository.save(notaDeCredito);
    }
    
    @Override 
    public NotaDeDebito guardarNotaDebito(NotaDeDebito notaDeDebito){
        this.validarNotaDebito(notaDeDebito);
        return notaDeDebitoRespository.save(notaDeDebito);
    }
    
    @Override
    @Transactional
    public void eliminarNotaDeCredito(long[] idsNotaDeCredito) {
        for (long idNotaDeCredito : idsNotaDeCredito) {
            NotaDeCredito notaDeCredito = this.getNotaDeCreditoPorID(idNotaDeCredito);
            if (notaDeCredito != null) {
                notaDeCredito.setEliminada(true);
                notaDeCreditoRepository.save(notaDeCredito);
            }
        }
    }

    @Override
    @Transactional
    public void eliminarNotaDeDebito(long[] idsNotaDeDebito) {
        for (long idNotaDeDebito : idsNotaDeDebito) {
            NotaDeDebito notaDeDebito = this.getNotaDeDebitoPorID(idNotaDeDebito);
            if(notaDeDebito != null){
                notaDeDebito.setEliminada(true);
                notaDeDebitoRespository.save(notaDeDebito);
            }
        }
    }

    @Override
    public double calcularTotalNotaDeCredito(NotaDeCredito notaDeCredito) {
        double total = 0.0;
        for (RenglonNota renglonNota : this.getRenglonesDeNotaCredito(notaDeCredito.getId_NotaDeCredito())) {
            total += renglonNota.getSubTotal();
        }
        return total;
    }

    @Override
    public double calcularIvaNetoNotaDeCredito(NotaDeCredito notaDeCredito) {
        double ivaNeto = 0.0;
        for (RenglonNota renglonNota : this.getRenglonesDeNotaCredito(notaDeCredito.getId_NotaDeCredito())) {
            ivaNeto += (renglonNota.getIvaPorcentaje() / 100) * renglonNota.getImporte();
        }
        return ivaNeto;
    }

    @Override
    public double calcularTotalNotaDeDebito(NotaDeDebito notaDeDebito) {
        double totalNotaDebito = 0.0;
        for (RenglonNota renglonNota : this.getRenglonesDeNotaDebito(notaDeDebito.getId_NotaDeDebito())) {
            totalNotaDebito += renglonNota.getSubTotal();
        }
        return totalNotaDebito;
    }

    @Override
    public double calcularIvaNetoNotaDeDebito(NotaDeDebito notaDeDebito) {
        double totalIvaNotaCredito = 0.0;
        for (RenglonNota renglonNota : this.getRenglonesDeNotaDebito(notaDeDebito.getId_NotaDeDebito())) {
            totalIvaNotaCredito += (renglonNota.getIvaPorcentaje() / 100) * renglonNota.getImporte();
        }
        return totalIvaNotaCredito;
    }

    @Override
    public RenglonNota calcularRenglon(TipoDeComprobante tipoDeComprobante, Movimiento movimiento, double cantidad, Long idProducto, 
                                        double descuentoPorcentaje) {
        Producto producto = productoService.getProductoPorId(idProducto);
        RenglonNota renglonNota = new RenglonNota();
        renglonNota.setId_ProductoItem(producto.getId_Producto());
        renglonNota.setCodigoItem(producto.getCodigo());
        renglonNota.setDescripcionItem(producto.getDescripcion());
        renglonNota.setMedidaItem(producto.getMedida().getNombre());
        renglonNota.setCantidad(cantidad);
        renglonNota.setPrecioUnitario(facturaService.calcularPrecioUnitario(movimiento, tipoDeComprobante, producto));
        renglonNota.setDescuentoPorcentaje(descuentoPorcentaje);
        renglonNota.setDescuentoNeto(facturaService.calcularDescuentoNeto(renglonNota.getPrecioUnitario(), descuentoPorcentaje));
        renglonNota.setIvaPorcentaje(producto.getIva_porcentaje());
        //renglonNota.setIvaNeto(facturaService.calcularIVANetoRenglon(movimiento, tipoDeComprobante, producto, renglonNota.getDescuentoPorcentaje()));
        renglonNota.setGananciaPorcentaje(producto.getGanancia_porcentaje());
        renglonNota.setGananciaNeto(producto.getGanancia_neto());
        //calcular subtotal (sin nada nada) la x no lleva IVA
        this.calcularSubTotal(producto.getPrecioLista(), cantidad);
        renglonNota.setSubTotalBruto(this.calcularSubTotalBrutoRenglon(renglonNota.getSubTotal(), renglonNota.getDescuentoNeto(), 0)); 
        renglonNota.setImporte(facturaService.calcularImporte(cantidad, renglonNota.getPrecioUnitario(), renglonNota.getDescuentoNeto()));
        return renglonNota;
    }

    private double calcularIvaNetoPorcentaje(List<RenglonNota> renglonesNota, double ivaPorcetanje) {
        double ivaNetoPorcentaje =  0.0;
        for (RenglonNota renglonNota : renglonesNota) {
            if(renglonNota.getIvaPorcentaje() == ivaPorcetanje){
                ivaNetoPorcentaje += renglonNota.getSubTotalBruto() * (renglonNota.getIvaPorcentaje() / 100);
            }
        }
        return ivaNetoPorcentaje;
    }
    
    private double calcularSubTotalBruto(TipoDeComprobante tipoDeComprobante, double subTotal, double iva21Neto, double iva105Neto) {
        if (tipoDeComprobante.equals(TipoDeComprobante.NOTA_CREDITO_B) || tipoDeComprobante.equals(TipoDeComprobante.NOTA_DEBITO_B)) {
            subTotal = subTotal - (iva105Neto + iva21Neto);
        }
        return subTotal;
    }
  
    private double calcularSubTotalBrutoRenglon(double subTotal, double descuentoNeto, double recargoNeto) {
        return subTotal + recargoNeto - descuentoNeto;
    }

    private double calcularSubTotal(double importeProducto, double cantidad) {
        return importeProducto * cantidad;
    }
    
}
