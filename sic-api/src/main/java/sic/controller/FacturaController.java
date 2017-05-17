package sic.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import sic.modelo.BusquedaFacturaCompraCriteria;
import sic.modelo.BusquedaFacturaVentaCriteria;
import sic.modelo.Cliente;
import sic.modelo.Factura;
import sic.modelo.FacturaCompra;
import sic.modelo.FacturaVenta;
import sic.modelo.RenglonFactura;
import sic.service.BusinessServiceException;
import sic.service.IClienteService;
import sic.service.IEmpresaService;
import sic.service.IFacturaService;
import sic.service.IPagoService;
import sic.service.IPedidoService;
import sic.service.IProveedorService;
import sic.service.IUsuarioService;
import sic.modelo.Movimiento;
import sic.modelo.Proveedor;
import sic.modelo.TipoDeComprobante;
import sic.modelo.Usuario;

@RestController
@RequestMapping("/api/v1")
public class FacturaController {
    
    private final IFacturaService facturaService;
    private final IEmpresaService empresaService;
    private final IProveedorService proveedorService;
    private final IClienteService clienteService;
    private final IUsuarioService usuarioService;
    private final IPedidoService pedidoService;   
    
    @Autowired
    public FacturaController(IFacturaService facturaService, IEmpresaService empresaService,
                             IProveedorService proveedorService, IClienteService clienteService,
                             IUsuarioService usuarioService, IPedidoService pedidoService,
                             IPagoService pagoService) {
        this.facturaService = facturaService;
        this.empresaService = empresaService;
        this.proveedorService = proveedorService;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
        this.pedidoService = pedidoService;       
    }
    
    @GetMapping("/facturas/{idFactura}")
    @ResponseStatus(HttpStatus.OK)
    public Factura getFacturaPorId(@PathVariable long idFactura) {
        return facturaService.getFacturaPorId(idFactura);
    }
    
    @PostMapping("/facturas")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Factura> guardarFactura(@RequestBody Factura factura,
                                        @RequestParam(required = false) int[] indices,
                                        @RequestParam(required = false) Long idPedido) {
        if (factura instanceof FacturaVenta && indices != null) {
            return facturaService.guardar(facturaService.dividirFactura((FacturaVenta) factura, indices), idPedido);
        } else {
            List<Factura> facturas = new ArrayList<>();
            facturas.add(factura);
            return facturaService.guardar(facturas, idPedido);         
        }
    }   
    
    @DeleteMapping("/facturas")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@RequestParam long[] idFactura) {
        facturaService.eliminar(idFactura);
    }
    
    @GetMapping("/facturas/{idFactura}/renglones")
    @ResponseStatus(HttpStatus.OK)
    public List<RenglonFactura> getRenglonesDeLaFactura(@PathVariable long idFactura) {
        return facturaService.getRenglonesDeLaFactura(idFactura);
    }
    
    @GetMapping("/facturas/compra/busqueda/criteria")
    @ResponseStatus(HttpStatus.OK)
    public List<FacturaCompra> buscarFacturaCompra(@RequestParam Long idEmpresa,
                                                   @RequestParam(required = false) Long desde,
                                                   @RequestParam(required = false) Long hasta,
                                                   @RequestParam(required = false) Long idProveedor,
                                                   @RequestParam(required = false) Integer nroSerie,
                                                   @RequestParam(required = false) Integer nroFactura,
                                                   @RequestParam(required = false) Boolean soloImpagas,
                                                   @RequestParam(required = false) Boolean soloPagas) {
        Calendar fechaDesde = Calendar.getInstance();
        Calendar fechaHasta = Calendar.getInstance();
        if ((desde != null) && (hasta != null)) {
            fechaDesde.setTimeInMillis(desde);            
            fechaHasta.setTimeInMillis(hasta);
        }
        if (soloImpagas == null) {
            soloImpagas = false;
        }
        if (soloPagas == null) {
            soloPagas = false;
        }
        Proveedor proveedor = null;
        if (idProveedor != null) {
            proveedor = proveedorService.getProveedorPorId(idProveedor);
        }
        BusquedaFacturaCompraCriteria criteria = BusquedaFacturaCompraCriteria.builder()
                                                 .empresa(empresaService.getEmpresaPorId(idEmpresa))
                                                 .buscaPorFecha((desde != null) && (hasta != null))
                                                 .fechaDesde(fechaDesde.getTime())
                                                 .fechaHasta(fechaHasta.getTime())
                                                 .buscaPorProveedor(idProveedor != null)
                                                 .proveedor(proveedor)
                                                 .buscaPorNumeroFactura((nroSerie != null) && (nroFactura != null))
                                                 .numSerie((nroSerie != null) ? nroSerie : 0)
                                                 .numFactura((nroFactura != null) ? nroFactura : 0)
                                                 .buscarSoloInpagas(soloImpagas)
                                                 .buscaSoloPagadas(soloPagas)
                                                 .cantRegistros(0)
                                                 .build();
        return facturaService.buscarFacturaCompra(criteria);
    }
    
    @GetMapping("/facturas/venta/busqueda/criteria")
    @ResponseStatus(HttpStatus.OK)
    public List<FacturaVenta> buscarFacturaVenta(@RequestParam Long idEmpresa,
                                                 @RequestParam(required = false) Long desde,
                                                 @RequestParam(required = false) Long hasta,
                                                 @RequestParam(required = false) Long idCliente,
                                                 @RequestParam(required = false) Integer nroSerie,
                                                 @RequestParam(required = false) Integer nroFactura,                                                 
                                                 @RequestParam(required = false) Long idViajante,
                                                 @RequestParam(required = false) TipoDeComprobante tipoDeComprobante,
                                                 @RequestParam(required = false) Long idUsuario,
                                                 @RequestParam(required = false) Long nroPedido,
                                                 @RequestParam(required = false) Boolean soloImpagas,
                                                 @RequestParam(required = false) Boolean soloPagas) {
        Calendar fechaDesde = Calendar.getInstance();
        Calendar fechaHasta = Calendar.getInstance();
        if ((desde != null) && (hasta != null)) {
            fechaDesde.setTimeInMillis(desde);
            fechaHasta.setTimeInMillis(hasta);
        }        
        soloImpagas = (soloImpagas == null) ? false : soloImpagas;
        soloPagas = (soloPagas == null) ? false : soloPagas;
        if ((soloImpagas == true) && (soloPagas == true)) {
            soloImpagas = false;
            soloPagas = false;
        }
        Cliente cliente = new Cliente();
        if (idCliente != null) {
            cliente = clienteService.getClientePorId(idCliente);
        }
        Usuario usuario = new Usuario();
        if (idUsuario != null) {
            usuario = usuarioService.getUsuarioPorId(idUsuario);
        }
        Usuario viajante = new Usuario();
        if (idViajante != null) {
            viajante = usuarioService.getUsuarioPorId(idViajante);
        }
        BusquedaFacturaVentaCriteria criteria = BusquedaFacturaVentaCriteria.builder()
                                                 .empresa(empresaService.getEmpresaPorId(idEmpresa))
                                                 .buscaPorFecha((desde != null) && (hasta != null))
                                                 .fechaDesde(fechaDesde.getTime())
                                                 .fechaHasta(fechaHasta.getTime())
                                                 .buscaCliente(idCliente != null)
                                                 .cliente(cliente)
                                                 .buscaUsuario(idUsuario != null)
                                                 .usuario(usuario)
                                                 .buscaViajante(idViajante != null)
                                                 .viajante(viajante)
                                                 .buscaPorNumeroFactura((nroSerie != null) && (nroFactura != null))
                                                 .numSerie((nroSerie != null)? nroSerie : 0)
                                                 .numFactura((nroFactura != null) ? nroFactura : 0)
                                                 .buscarPorPedido(nroPedido != null)
                                                 .nroPedido((nroPedido != null) ? nroPedido : 0)
                                                 .buscaPorTipoComprobante(tipoDeComprobante != null)
                                                 .tipoComprobante((tipoDeComprobante != null) ? tipoDeComprobante : null)
                                                 .buscaSoloImpagas(soloImpagas)
                                                 .buscaSoloPagadas(soloPagas)
                                                 .cantRegistros(0)
                                                 .build();
        return facturaService.buscarFacturaVenta(criteria);
    }
    
    @GetMapping("/facturas/compra/tipos/empresas/{idEmpresa}/proveedores/{idProveedor}")
    @ResponseStatus(HttpStatus.OK)
    public TipoDeComprobante[] getTipoFacturaCompra(@PathVariable long idEmpresa, @PathVariable long idProveedor) {
        return facturaService.getTipoFacturaCompra(empresaService.getEmpresaPorId(idEmpresa), proveedorService.getProveedorPorId(idProveedor));
    }
    
    @GetMapping("/facturas/venta/tipos/empresas/{idEmpresa}/clientes/{idCliente}")
    @ResponseStatus(HttpStatus.OK)
    public TipoDeComprobante[] getTipoFacturaVenta(@PathVariable long idEmpresa, @PathVariable long idCliente) {
        return facturaService.getTipoFacturaVenta(empresaService.getEmpresaPorId(idEmpresa), clienteService.getClientePorId(idCliente));
    }
    
    @GetMapping("/facturas/tipos/empresas/{idEmpresa}")
    @ResponseStatus(HttpStatus.OK)
    public TipoDeComprobante[] getTiposFacturaSegunEmpresa(@PathVariable long idEmpresa) {
        return facturaService.getTiposFacturaSegunEmpresa(empresaService.getEmpresaPorId(idEmpresa));
    }    
    
    @GetMapping("/facturas/{idFactura}/tipo")
    @ResponseStatus(HttpStatus.OK)
    public TipoDeComprobante getTipoFactura(@PathVariable long idFactura) {
        return facturaService.getFacturaPorId(idFactura).getTipoComprobante();
    }
    
    @GetMapping("/facturas/{idFactura}/reporte")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> getReporteFacturaVenta(@PathVariable long idFactura) {        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);        
        headers.add("content-disposition", "inline; filename=Factura.pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        byte[] reportePDF = facturaService.getReporteFacturaVenta(facturaService.getFacturaPorId(idFactura));
        return new ResponseEntity<>(reportePDF, headers, HttpStatus.OK);
    }
    
    @GetMapping("/facturas/renglones/pedidos/{idPedido}") 
    @ResponseStatus(HttpStatus.OK)
    public List<RenglonFactura> getRenglonesPedidoParaFacturar(@PathVariable long idPedido,
                                                               @RequestParam TipoDeComprobante tipoDeComprobante) {
        return facturaService.convertirRenglonesPedidoARenglonesFactura(pedidoService.getPedidoPorId(idPedido), tipoDeComprobante);
    }    
     
    @GetMapping("/facturas/validaciones-pago-multiple")
    @ResponseStatus(HttpStatus.OK)
    public boolean validarFacturasParaPagoMultiple(@RequestParam long[] idFactura,
                                                   @RequestParam Movimiento movimiento) {
        List<Factura> facturas = new ArrayList<>();
        for (long id : idFactura) {
            facturas.add(facturaService.getFacturaPorId(id));
        }

        if (facturaService.validarFacturasParaPagoMultiple(facturas, movimiento)) {
            return true;
        } else if (!facturaService.validarClienteProveedorParaPagosMultiples(facturas, movimiento)) {              
            if (movimiento == Movimiento.COMPRA) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes").getString("mensaje_facturas_distintos_proveedores"));
            } else if (movimiento == Movimiento.VENTA) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes").getString("mensaje_facturas_distintos_clientes"));
            }

        } else if (!facturaService.validarFacturasImpagasParaPagoMultiple(facturas)) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes").getString("mensaje_facturas_seEncuentran_pagadas"));
        }
        return false;
    }
    
    @GetMapping("/facturas/renglon")
    @ResponseStatus(HttpStatus.OK)
    public RenglonFactura calcularRenglon(@RequestParam long idProducto,
                                          @RequestParam TipoDeComprobante tipoDeComprobante,
                                          @RequestParam Movimiento movimiento,
                                          @RequestParam double cantidad, 
                                          @RequestParam double descuentoPorcentaje) {
        return facturaService.calcularRenglon(tipoDeComprobante, movimiento, cantidad, idProducto, descuentoPorcentaje);
    }
    
    @GetMapping("/facturas/subtotal")
    @ResponseStatus(HttpStatus.OK)
    public double calcularSubTotal(@RequestParam double[] importe) {
        return facturaService.calcularSubTotal(importe);
    }
    
    @GetMapping("/facturas/descuento-neto")
    @ResponseStatus(HttpStatus.OK)
    public double calcularDescuento_neto(@RequestParam double subTotal,
                                         @RequestParam double descuentoPorcentaje) {
        return facturaService.calcularDescuento_neto(subTotal, descuentoPorcentaje);
    }
    
    @GetMapping("/facturas/recargo-neto")
    @ResponseStatus(HttpStatus.OK)
    public double calcularRecargo_neto(@RequestParam double subTotal,
                                       @RequestParam double recargoPorcentaje) {
        return facturaService.calcularRecargo_neto(subTotal, recargoPorcentaje);
    }
    
    @GetMapping("/facturas/subtotal-neto")
    @ResponseStatus(HttpStatus.OK)
    public double calcularSubTotal_neto(@RequestParam double subTotal,
                                        @RequestParam double recargoNeto,
                                        @RequestParam double descuentoNeto) {
        return facturaService.calcularSubTotal_neto(subTotal, recargoNeto, descuentoNeto);
    }
    
    @GetMapping("/facturas/impuesto-interno-neto")
    @ResponseStatus(HttpStatus.OK)
    public double calcularImpInterno_neto(@RequestParam TipoDeComprobante tipoDeComprobante,
                                          @RequestParam double descuentoPorcentaje,
                                          @RequestParam double recargoPorcentaje,
                                          @RequestParam double[] importe,
                                          @RequestParam double[] impuestoPorcentaje) {
        return facturaService.calcularImpInterno_neto(tipoDeComprobante, descuentoPorcentaje,
                recargoPorcentaje, importe, impuestoPorcentaje);

    }
    
    @GetMapping("/facturas/total")
    @ResponseStatus(HttpStatus.OK)
    public double calcularTotal(@RequestParam double subTotal,
                                @RequestParam double descuentoNeto,
                                @RequestParam double recargoNeto,
                                @RequestParam double iva105Neto,
                                @RequestParam double iva21Neto,
                                @RequestParam(defaultValue = "0", required = false) double impuestoInternoNeto) {
        return facturaService.calcularTotal(subTotal, descuentoNeto, recargoNeto,
                iva105Neto, iva21Neto, impuestoInternoNeto);
    }
    
    @GetMapping("/facturas/total-facturado-venta/criteria")
    @ResponseStatus(HttpStatus.OK)
    public double calcularTotalFacturadoVenta(@RequestParam Long idEmpresa,
                                              @RequestParam(required = false) Long desde,
                                              @RequestParam(required = false) Long hasta,
                                              @RequestParam(required = false) Long idCliente,
                                              @RequestParam(required = false) Integer nroSerie,
                                              @RequestParam(required = false) Integer nroFactura,                                              
                                              @RequestParam(required = false) Long idViajante,
                                              @RequestParam(required = false) TipoDeComprobante tipoDeComprobante,
                                              @RequestParam(required = false) Long idUsuario,
                                              @RequestParam(required = false) Long nroPedido,
                                              @RequestParam(required = false) Boolean soloImpagas,
                                              @RequestParam(required = false) Boolean soloPagas) {
        Calendar fechaDesde = Calendar.getInstance();
        Calendar fechaHasta = Calendar.getInstance();
        if ((desde != null) && (hasta != null)) {
            fechaDesde.setTimeInMillis(desde);
            fechaHasta.setTimeInMillis(hasta);
        }
        if ((soloImpagas != null) && (soloPagas != null)) {
            if ((soloImpagas == true) && (soloPagas == true)) {
                soloImpagas = false;
                soloPagas = false;
            }
        }
        Cliente cliente = new Cliente();
        if (idCliente != null) {
            cliente = clienteService.getClientePorId(idCliente);
        }
        Usuario usuario = new Usuario();
        if (idUsuario != null) {
            usuario = usuarioService.getUsuarioPorId(idUsuario);
        }
        Usuario viajante = new Usuario();
        if (idViajante != null) {
            viajante = usuarioService.getUsuarioPorId(idViajante);
        }
        BusquedaFacturaVentaCriteria criteria = BusquedaFacturaVentaCriteria.builder()
                                                 .empresa(empresaService.getEmpresaPorId(idEmpresa))
                                                 .buscaPorFecha((desde != null) && (hasta != null))
                                                 .fechaDesde(fechaDesde.getTime())
                                                 .fechaHasta(fechaHasta.getTime())
                                                 .buscaCliente(idCliente != null)
                                                 .cliente(cliente)
                                                 .buscaUsuario(idUsuario != null)
                                                 .usuario(usuario)
                                                 .buscaViajante(idViajante != null)
                                                 .viajante(viajante)
                                                 .buscaPorNumeroFactura((nroSerie != null) && (nroFactura != null))
                                                 .numSerie((nroSerie != null)? nroSerie : 0)
                                                 .numFactura((nroFactura != null) ? nroFactura : 0)
                                                 .buscarPorPedido(nroPedido != null)
                                                 .nroPedido((nroPedido != null) ? nroPedido : 0)
                                                 .buscaPorTipoComprobante(tipoDeComprobante != null)
                                                 .tipoComprobante((tipoDeComprobante != null) ? tipoDeComprobante : null)
                                                 .buscaSoloImpagas(soloImpagas)
                                                 .buscaSoloPagadas(soloPagas)
                                                 .cantRegistros(0)
                                                 .build();
        return facturaService.calcularTotalFacturadoVenta(criteria);
    }
    
    @GetMapping("/facturas/total-facturado-compra/criteria")
    @ResponseStatus(HttpStatus.OK)
    public double calcularTotalFacturadoCompra(@RequestParam Long idEmpresa,
                                               @RequestParam(required = false) Long desde,
                                               @RequestParam(required = false) Long hasta,
                                               @RequestParam(required = false) Long idProveedor,
                                               @RequestParam(required = false) Integer nroSerie,
                                               @RequestParam(required = false) Integer nroFactura,
                                               @RequestParam(required = false) Boolean soloImpagas,
                                               @RequestParam(required = false) Boolean soloPagas) {
        Calendar fechaDesde = Calendar.getInstance();
        Calendar fechaHasta = Calendar.getInstance();
        if ((desde != null) && (hasta != null)) {
            fechaDesde.setTimeInMillis(desde);            
            fechaHasta.setTimeInMillis(hasta);
        }
        if (soloImpagas == null) {
            soloImpagas = false;
        }
        if (soloPagas == null) {
            soloPagas = false;
        }
        Proveedor proveedor = null;
        if (idProveedor != null) {
            proveedor = proveedorService.getProveedorPorId(idProveedor);
        }
        BusquedaFacturaCompraCriteria criteria = BusquedaFacturaCompraCriteria.builder()
                                                 .empresa(empresaService.getEmpresaPorId(idEmpresa))
                                                 .buscaPorFecha((desde != null) && (hasta != null))
                                                 .fechaDesde(fechaDesde.getTime())
                                                 .fechaHasta(fechaHasta.getTime())
                                                 .buscaPorProveedor(idProveedor != null)
                                                 .proveedor(proveedor)
                                                 .buscaPorNumeroFactura((nroSerie != null) && (nroFactura != null))
                                                 .numSerie((nroSerie != null) ? nroSerie : 0)
                                                 .numFactura((nroFactura != null) ? nroFactura : 0)
                                                 .buscarSoloInpagas(soloImpagas)
                                                 .buscaSoloPagadas(soloPagas)
                                                 .cantRegistros(0)
                                                 .build();
        return facturaService.calcularTotalFacturadoCompra(criteria);
    }
    
    @GetMapping("/facturas/total-iva-venta/criteria")
    @ResponseStatus(HttpStatus.OK)
    public double calcularIvaVenta(@RequestParam Long idEmpresa,
                                   @RequestParam(required = false) Long desde,
                                   @RequestParam(required = false) Long hasta,
                                   @RequestParam(required = false) Long idCliente,
                                   @RequestParam(required = false) Integer nroSerie,
                                   @RequestParam(required = false) Integer nroFactura,                                   
                                   @RequestParam(required = false) Long idViajante,
                                   @RequestParam(required = false) TipoDeComprobante tipoDeComprobante,
                                   @RequestParam(required = false) Long idUsuario,
                                   @RequestParam(required = false) Long nroPedido,
                                   @RequestParam(required = false) Boolean soloImpagas,
                                   @RequestParam(required = false) Boolean soloPagas) {
        Calendar fechaDesde = Calendar.getInstance();
        Calendar fechaHasta = Calendar.getInstance();
        if ((desde != null) && (hasta != null)) {
            fechaDesde.setTimeInMillis(desde);
            fechaHasta.setTimeInMillis(hasta);
        }
        if ((soloImpagas != null) && (soloPagas != null)) {
            if ((soloImpagas == true) && (soloPagas == true)) {
                soloImpagas = false;
                soloPagas = false;
            }
        }
        Cliente cliente = new Cliente();
        if (idCliente != null) {
            cliente = clienteService.getClientePorId(idCliente);
        }
        Usuario usuario = new Usuario();
        if (idUsuario != null) {
            usuario = usuarioService.getUsuarioPorId(idUsuario);
        }
        Usuario viajante = new Usuario();
        if (idViajante != null) {
            viajante = usuarioService.getUsuarioPorId(idViajante);
        }
        BusquedaFacturaVentaCriteria criteria = BusquedaFacturaVentaCriteria.builder()
                .empresa(empresaService.getEmpresaPorId(idEmpresa))
                .buscaPorFecha((desde != null) && (hasta != null))
                .fechaDesde(fechaDesde.getTime())
                .fechaHasta(fechaHasta.getTime())
                .buscaCliente(idCliente != null)
                .cliente(cliente)
                .buscaUsuario(idUsuario != null)
                .usuario(usuario)
                .buscaViajante(idViajante != null)
                .viajante(viajante)
                .buscaPorNumeroFactura((nroSerie != null) && (nroFactura != null))
                .numSerie((nroSerie != null) ? nroSerie : 0)
                .numFactura((nroFactura != null) ? nroFactura : 0)
                .buscarPorPedido(nroPedido != null)
                .nroPedido((nroPedido != null) ? nroPedido : 0)
                .buscaPorTipoComprobante(tipoDeComprobante != null)
                .tipoComprobante((tipoDeComprobante != null) ? tipoDeComprobante : null)
                .buscaSoloImpagas(soloImpagas)
                .buscaSoloPagadas(soloPagas)
                .cantRegistros(0)
                .build();
        return facturaService.calcularIVA_Venta(criteria);
    }
    
    @GetMapping("/facturas/total-iva-compra/criteria")
    @ResponseStatus(HttpStatus.OK)
    public double calcularTotalIvaCompra(@RequestParam Long idEmpresa,
                                         @RequestParam(required = false) Long desde,
                                         @RequestParam(required = false) Long hasta,
                                         @RequestParam(required = false) Long idProveedor,
                                         @RequestParam(required = false) Integer nroSerie,
                                         @RequestParam(required = false) Integer nroFactura,
                                         @RequestParam(required = false) Boolean soloImpagas,
                                         @RequestParam(required = false) Boolean soloPagas) {
        Calendar fechaDesde = Calendar.getInstance();
        Calendar fechaHasta = Calendar.getInstance();
        if ((desde != null) && (hasta != null)) {
            fechaDesde.setTimeInMillis(desde);            
            fechaHasta.setTimeInMillis(hasta);
        }
        if (soloImpagas == null) {
            soloImpagas = false;
        }
        if (soloPagas == null) {
            soloPagas = false;
        }
        Proveedor proveedor = null;
        if (idProveedor != null) {
            proveedor = proveedorService.getProveedorPorId(idProveedor);
        }
        BusquedaFacturaCompraCriteria criteria = BusquedaFacturaCompraCriteria.builder()
                                                 .empresa(empresaService.getEmpresaPorId(idEmpresa))
                                                 .buscaPorFecha((desde != null) && (hasta != null))
                                                 .fechaDesde(fechaDesde.getTime())
                                                 .fechaHasta(fechaHasta.getTime())
                                                 .buscaPorProveedor(idProveedor != null)
                                                 .proveedor(proveedor)
                                                 .buscaPorNumeroFactura((nroSerie != null) && (nroFactura != null))
                                                 .numSerie((nroSerie != null) ? nroSerie : 0)
                                                 .numFactura((nroFactura != null) ? nroFactura : 0)
                                                 .buscarSoloInpagas(soloImpagas)
                                                 .buscaSoloPagadas(soloPagas)
                                                 .cantRegistros(0)
                                                 .build();
        return facturaService.calcularIVA_Compra(criteria);
    }
    
    @GetMapping("/facturas/ganancia-total/criteria")
    @ResponseStatus(HttpStatus.OK)
    public double calcularGananciaTotal(@RequestParam Long idEmpresa,
                                        @RequestParam(required = false) Long desde,
                                        @RequestParam(required = false) Long hasta,
                                        @RequestParam(required = false) Long idCliente,
                                        @RequestParam(required = false) Integer nroSerie,
                                        @RequestParam(required = false) Integer nroFactura,                                        
                                        @RequestParam(required = false) Long idViajante,
                                        @RequestParam(required = false) TipoDeComprobante tipoDeComprobante,
                                        @RequestParam(required = false) Long idUsuario,
                                        @RequestParam(required = false) Long nroPedido,
                                        @RequestParam(required = false) Boolean soloImpagas,
                                        @RequestParam(required = false) Boolean soloPagas) {
        Calendar fechaDesde = Calendar.getInstance();
        Calendar fechaHasta = Calendar.getInstance();
        if ((desde != null) && (hasta != null)) {
            fechaDesde.setTimeInMillis(desde);
            fechaHasta.setTimeInMillis(hasta);
        }
        if ((soloImpagas != null) && (soloPagas != null)) {
            if ((soloImpagas == true) && (soloPagas == true)) {
                soloImpagas = false;
                soloPagas = false;
            }
        }
        Cliente cliente = new Cliente();
        if (idCliente != null) {
            cliente = clienteService.getClientePorId(idCliente);
        }
        Usuario usuario = new Usuario();
        if (idUsuario != null) {
            usuario = usuarioService.getUsuarioPorId(idUsuario);
        }
        Usuario viajante = new Usuario();
        if (idViajante != null) {
            viajante = usuarioService.getUsuarioPorId(idViajante);
        }
        BusquedaFacturaVentaCriteria criteria = BusquedaFacturaVentaCriteria.builder()
                                                 .empresa(empresaService.getEmpresaPorId(idEmpresa))
                                                 .buscaPorFecha((desde != null) && (hasta != null))
                                                 .fechaDesde(fechaDesde.getTime())
                                                 .fechaHasta(fechaHasta.getTime())
                                                 .buscaCliente(idCliente != null)
                                                 .cliente(cliente)
                                                 .buscaUsuario(idUsuario != null)
                                                 .usuario(usuario)
                                                 .buscaViajante(idViajante != null)
                                                 .viajante(viajante)
                                                 .buscaPorNumeroFactura((nroSerie != null) && (nroFactura != null))
                                                 .numSerie((nroSerie != null)? nroSerie : 0)
                                                 .numFactura((nroFactura != null) ? nroFactura : 0)
                                                 .buscarPorPedido(nroPedido != null)
                                                 .nroPedido((nroPedido != null) ? nroPedido : 0)
                                                 .buscaPorTipoComprobante(tipoDeComprobante != null)
                                                 .tipoComprobante((tipoDeComprobante != null) ? tipoDeComprobante : null)
                                                 .buscaSoloImpagas(soloImpagas)
                                                 .buscaSoloPagadas(soloPagas)
                                                 .cantRegistros(0)
                                                 .build();
        return facturaService.calcularGananciaTotal(criteria);
    }
    
    @GetMapping("/facturas/iva-neto")
    @ResponseStatus(HttpStatus.OK)
    public double calcularIVA_neto(@RequestParam TipoDeComprobante tipoDeComprobante,
                                   @RequestParam double descuentoPorcentaje,
                                   @RequestParam double recargoPorcentaje,
                                   @RequestParam double ivaPorcentaje,
                                   @RequestParam double[] importe, 
                                   @RequestParam double[] ivaRenglones) {       
        return facturaService.calcularIva_neto(tipoDeComprobante, descuentoPorcentaje,
                recargoPorcentaje, importe, ivaRenglones, ivaPorcentaje);
    }
    
    @GetMapping("/facturas/calculo-vuelto")
    @ResponseStatus(HttpStatus.OK)
    public double calcularVuelto(@RequestParam double importeAPagar,
                                 @RequestParam double importeAbonado) {
        return facturaService.calcularVuelto(importeAPagar, importeAbonado);
    }
        
}
