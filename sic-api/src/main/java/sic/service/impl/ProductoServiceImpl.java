package sic.service.impl;

import com.querydsl.core.BooleanBuilder;
import sic.modelo.BusquedaProductoCriteria;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.persistence.EntityNotFoundException;
import javax.swing.ImageIcon;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sic.modelo.Empresa;
import sic.modelo.Factura;
import sic.modelo.FacturaCompra;
import sic.modelo.FacturaVenta;
import sic.modelo.Medida;
import sic.modelo.Producto;
import sic.modelo.Proveedor;
import sic.modelo.QProducto;
import sic.modelo.RenglonFactura;
import sic.modelo.Rubro;
import sic.service.IEmpresaService;
import sic.service.IProductoService;
import sic.service.BusinessServiceException;
import sic.service.ServiceException;
import sic.modelo.TipoDeOperacion;
import sic.util.Validator;
import sic.repository.ProductoRepository;

@Service
public class ProductoServiceImpl implements IProductoService {

    private final ProductoRepository productoRepository;
    private final IEmpresaService empresaService;
    private static final Logger LOGGER = Logger.getLogger(ProductoServiceImpl.class.getPackage().getName());
    
    @Value("${SIC_STATIC_CONTENT}")
    private String pathStaticContent;

    @Autowired
    public ProductoServiceImpl(ProductoRepository productoRepository, IEmpresaService empresaService) {
        this.productoRepository = productoRepository;
        this.empresaService = empresaService;
    }

    private void validarOperacion(TipoDeOperacion operacion, Producto producto) {
        //Entrada de Datos
        if (producto.getCantidad() < 0) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_cantidad_negativa"));
        }
        if (producto.getCantMinima() < 0) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_cantidadMinima_negativa"));
        }
        if (producto.getVentaMinima() < 0) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_cantidadVentaMinima_negativa"));
        }
        if (producto.getPrecioCosto() < 0) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_precioCosto_negativo"));
        }
        if (producto.getPrecioVentaPublico() < 0) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_precioVentaPublico_negativo"));
        }
        if (producto.getIva_porcentaje() < 0) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_IVAPorcentaje_negativo"));
        }
        if (producto.getImpuestoInterno_porcentaje() < 0) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_ImpInternoPorcentaje_negativo"));
        }
        if (producto.getGanancia_porcentaje() < 0) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_gananciaPorcentaje_negativo"));
        }
        if (producto.getPrecioLista() < 0) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_precioLista_negativo"));
        }
        //Requeridos
        if (Validator.esVacio(producto.getDescripcion())) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_vacio_descripcion"));
        }
        if (producto.getMedida() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_vacio_medida"));
        }
        if (producto.getRubro() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_vacio_rubro"));
        }
        if (producto.getProveedor() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_vacio_proveedor"));
        }
        if (producto.getEmpresa() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_vacio_empresa"));
        }
        //Duplicados
        //Codigo
        if (!producto.getCodigo().equals("")) {
            Producto productoDuplicado = this.getProductoPorCodigo(producto.getCodigo(), producto.getEmpresa());
            if (operacion.equals(TipoDeOperacion.ACTUALIZACION)
                    && productoDuplicado != null
                    && productoDuplicado.getId_Producto() != producto.getId_Producto()) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_producto_duplicado_codigo"));
            }
            if (operacion.equals(TipoDeOperacion.ALTA)
                    && productoDuplicado != null
                    && !producto.getCodigo().equals("")) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_producto_duplicado_codigo"));
            }
        }
        //Descripcion
        Producto productoDuplicado = this.getProductoPorDescripcion(producto.getDescripcion(), producto.getEmpresa());
        if (operacion.equals(TipoDeOperacion.ALTA) && productoDuplicado != null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_duplicado_descripcion"));
        }
        if (operacion.equals(TipoDeOperacion.ACTUALIZACION)) {
            if (productoDuplicado != null && productoDuplicado.getId_Producto() != producto.getId_Producto()) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_producto_duplicado_descripcion"));
            }
        }
    }

    @Override
    public Page<Producto> buscarProductos(BusquedaProductoCriteria criteria) {
        //Empresa
        if (criteria.getEmpresa() == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_empresa_no_existente"));
        }
        //Rubro
        if (criteria.isBuscarPorRubro() == true && criteria.getRubro() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_vacio_rubro"));
        }
        //Proveedor
        if (criteria.isBuscarPorProveedor() == true && criteria.getProveedor() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_vacio_proveedor"));
        }
        QProducto qproducto = QProducto.producto;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qproducto.empresa.eq(criteria.getEmpresa()).and(qproducto.eliminado.eq(false)));
        if (criteria.isBuscarPorCodigo() == true && criteria.isBuscarPorDescripcion() == true) {
            builder.and(qproducto.codigo.containsIgnoreCase(criteria.getCodigo())
                    .or(this.buildPredicadoDescripcion(criteria.getDescripcion(), qproducto)));            
        } else {
            if (criteria.isBuscarPorCodigo() == true) {
                builder.and(qproducto.codigo.containsIgnoreCase(criteria.getCodigo()));
            }
            if (criteria.isBuscarPorDescripcion() == true) {
                builder.and(this.buildPredicadoDescripcion(criteria.getDescripcion(), qproducto));
            }
        }
        if (criteria.isBuscarPorRubro() == true) {
            builder.and(qproducto.rubro.eq(criteria.getRubro()));
        }
        if (criteria.isBuscarPorProveedor()) {
            builder.and(qproducto.proveedor.eq(criteria.getProveedor()));
        }
        if (criteria.isListarSoloFaltantes() == true) {
            builder.and(qproducto.cantidad.loe(qproducto.cantMinima)).and(qproducto.ilimitado.eq(false));
        }
        int pageNumber = 0;
        int pageSize = Integer.MAX_VALUE;        
        if (criteria.getPageable() != null) {
            pageNumber = criteria.getPageable().getPageNumber();
            pageSize = criteria.getPageable().getPageSize();
        }
        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.ASC, "descripcion"));
        return productoRepository.findAll(builder, pageable);
    }
    
    private BooleanBuilder buildPredicadoDescripcion(String descripcion, QProducto qproducto) {
        String[] terminos = descripcion.split(" ");
        BooleanBuilder descripcionProducto = new BooleanBuilder();
        for (String termino : terminos) {
            descripcionProducto.and(qproducto.descripcion.containsIgnoreCase(termino));
        }
        return descripcionProducto;
    }

    @Override
    @Transactional
    public Producto guardar(Producto producto) {
        if (producto.getCodigo() == null) {
            producto.setCodigo("");
        }
        this.validarOperacion(TipoDeOperacion.ALTA, producto);
        producto = productoRepository.save(producto);
        LOGGER.warn("El Producto " + producto + " se guardó correctamente.");
        return producto;
    }

    @Override
    @Transactional
    public void actualizar(Producto producto) {
        this.validarOperacion(TipoDeOperacion.ACTUALIZACION, producto);
        productoRepository.save(producto);
    }

    @Override
    public void actualizarStock(Factura factura, TipoDeOperacion operacion) {
        for (RenglonFactura renglon : factura.getRenglones()) {
            Producto producto = productoRepository.findOne(renglon.getId_ProductoItem());
            if (producto == null) {
                LOGGER.warn("Se intenta actualizar el stock de un producto eliminado.");
            }
            if (producto != null && producto.isIlimitado() == false) {

                if (factura instanceof FacturaVenta) {
                    if (operacion == TipoDeOperacion.ALTA) {
                        producto.setCantidad(producto.getCantidad() - renglon.getCantidad());
                    }

                    if (operacion == TipoDeOperacion.ELIMINACION) {
                        producto.setCantidad(producto.getCantidad() + renglon.getCantidad());
                    }
                } else if (factura instanceof FacturaCompra) {
                    if (operacion == TipoDeOperacion.ALTA) {
                        producto.setCantidad(producto.getCantidad() + renglon.getCantidad());
                    }

                    if (operacion == TipoDeOperacion.ELIMINACION) {
                        double result = producto.getCantidad() - renglon.getCantidad();
                        if (result < 0) {
                            result = 0;
                        }
                        producto.setCantidad(result);
                    }
                }
                productoRepository.save(producto);
            }
        }
    }

    @Override
    @Transactional
    public void eliminarMultiplesProductos(long[] idProducto) {
        if (Validator.tieneDuplicados(idProducto)) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_error_ids_duplicados"));
        }
        List<Producto> productos = new ArrayList<>();
        for (Long i : idProducto) {
            Producto producto = this.getProductoPorId(i);
            if (producto == null) {
                throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_producto_no_existente"));
            }
            producto.setEliminado(true);
            productos.add(producto);
        }
        productoRepository.save(productos);
    }

    @Override
    @Transactional
    public List<Producto> modificarMultiplesProductos(long[] idProducto,
            boolean checkPrecios,
            Double gananciaNeto,
            Double gananciaPorcentaje,
            Double impuestoInternoNeto,
            Double impuestoInternoPorcentaje,
            Double IVANeto,
            Double IVAPorcentaje,
            Double precioCosto,
            Double precioLista,
            Double precioVentaPublico,
            boolean checkMedida, Medida medida,
            boolean checkRubro, Rubro rubro,
            boolean checkProveedor, Proveedor proveedor) {

        if (Validator.tieneDuplicados(idProducto)) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_error_ids_duplicados"));
        }
        List<Producto> productos = new ArrayList<>();
        for (long i : idProducto) {
            productos.add(this.getProductoPorId(i));
        }
        //Requeridos
        if (checkMedida == true && medida == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_vacio_medida"));
        }
        if (checkRubro == true && rubro == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_vacio_rubro"));
        }
        if (checkProveedor == true && proveedor == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_vacio_proveedor"));
        }
        if (checkPrecios == true) {
            productos.forEach((producto) -> {
                producto.setPrecioCosto(precioCosto);
                producto.setGanancia_porcentaje(gananciaPorcentaje);
                producto.setGanancia_neto(gananciaNeto);
                producto.setPrecioVentaPublico(precioVentaPublico);
                producto.setIva_porcentaje(IVAPorcentaje);
                producto.setIva_neto(IVANeto);
                producto.setImpuestoInterno_porcentaje(impuestoInternoPorcentaje);
                producto.setImpuestoInterno_neto(impuestoInternoNeto);
                producto.setPrecioLista(precioLista);
            });
        }
        if (checkMedida == true) {
            productos.stream().forEach((producto) -> {
                producto.setMedida(medida);
            });
        }
        if (checkRubro == true) {
            productos.stream().forEach((producto) -> {
                producto.setRubro(rubro);
            });
        }
        if (checkProveedor == true) {
            productos.stream().forEach((producto) -> {
                producto.setProveedor(proveedor);
            });
        }
        //modifica el campo fecha ultima modificacion
        if (checkPrecios == true || checkMedida == true || checkRubro == true || checkProveedor == true) {
            Calendar fechaHora = new GregorianCalendar();
            Date fechaHoraActual = fechaHora.getTime();
            productos.stream().forEach((producto) -> {
                producto.setFechaUltimaModificacion(fechaHoraActual);
            });
        }
        productoRepository.save(productos);
        return productos;
    }

    @Override
    public Producto getProductoPorId(long idProducto) {
        Producto producto = productoRepository.findOne(idProducto);
        if (producto == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_producto_no_existente"));
        }
        return producto;
    }

    @Override
    public Producto getProductoPorCodigo(String codigo, Empresa empresa) {
        if (codigo.isEmpty() == true || empresa == null) {
            return null;
        } else {
            return productoRepository.findByCodigoAndEmpresaAndEliminado(codigo, empresa, false);
        }
    }

    @Override
    public Producto getProductoPorDescripcion(String descripcion, Empresa empresa) {
        return productoRepository.findByDescripcionAndEmpresaAndEliminado(descripcion, empresa, false);
    }

    @Override
    public double calcularValorStock(BusquedaProductoCriteria criteria) {
        return productoRepository.calcularValorStock(criteria);
    }

    @Override
    public boolean existeStockDisponible(long idProducto, double cantidad) {
        return (this.getProductoPorId(idProducto).getCantidad() >= cantidad) || this.getProductoPorId(idProducto).isIlimitado();
    }

    @Override
    public double calcularGanancia_Porcentaje(Double precioDeListaNuevo,
            Double precioDeListaAnterior, double pvp, Double ivaPorcentaje,
            Double impInternoPorcentaje, double precioCosto, boolean ascendente) {
        //evita la division por cero
        if (precioCosto == 0) {
            return 0;
        }
        double resultado;
        if (ascendente == false) {
            resultado = ((pvp - precioCosto) / precioCosto) * 100;
        } else if (precioDeListaAnterior == 0 || precioCosto == 0) {
            return 0;
        } else {
            resultado = precioDeListaNuevo;
            double porcentajeIncremento = precioDeListaNuevo / precioDeListaAnterior;
            resultado = resultado - ((pvp * (impInternoPorcentaje / 100)) * porcentajeIncremento);
            resultado = resultado - ((pvp * (ivaPorcentaje / 100)) * porcentajeIncremento);
            resultado = ((resultado - precioCosto) * 100) / precioCosto;
        }
        return resultado;
    }

    @Override
    public double calcularGanancia_Neto(double precioCosto, double ganancia_porcentaje) {
        double resultado = (precioCosto * ganancia_porcentaje) / 100;
        return resultado;
    }

    @Override
    public double calcularPVP(double precioCosto, double ganancia_porcentaje) {
        double resultado = (precioCosto * (ganancia_porcentaje / 100)) + precioCosto;
        return resultado;
    }

    @Override
    public double calcularIVA_Neto(double pvp, double iva_porcentaje) {
        double resultado = (pvp * iva_porcentaje) / 100;
        return resultado;
    }

    @Override
    public double calcularImpInterno_Neto(double pvp, double impInterno_porcentaje) {
        double resultado = (pvp * impInterno_porcentaje) / 100;
        return resultado;
    }

    @Override
    public double calcularPrecioLista(double PVP, double iva_porcentaje, double impInterno_porcentaje) {
        double resulIVA = PVP * (iva_porcentaje / 100);
        double resultImpInterno = PVP * (impInterno_porcentaje / 100);
        double PVPConImpuestos = PVP + resulIVA + resultImpInterno;
        return PVPConImpuestos;
    }

    @Override
    public byte[] getReporteListaDePreciosPorEmpresa(List<Producto> productos, long idEmpresa) {
        ClassLoader classLoader = FacturaServiceImpl.class.getClassLoader();
        InputStream isFileReport = classLoader.getResourceAsStream("sic/vista/reportes/ListaPreciosProductos.jasper");        
        Map params = new HashMap();
        Empresa empresa = empresaService.getEmpresaPorId(idEmpresa);
        params.put("empresa", empresa);
        if (!empresa.getLogo().isEmpty()) {
            params.put("logo", new ImageIcon(pathStaticContent + empresa.getLogo()).getImage());    
        }
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(productos);
        try {
            return JasperExportManager.exportReportToPdf(JasperFillManager.fillReport(isFileReport, params, ds));
        } catch (JRException ex) {
            LOGGER.error(ex.getMessage());
            throw new ServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_error_reporte"), ex);
        }
    }
}
