package sic.service;

import java.util.List;
import sic.modelo.BusquedaProductoCriteria;
import sic.modelo.Empresa;
import sic.modelo.Factura;
import sic.modelo.Medida;
import sic.modelo.PreciosProducto;
import sic.modelo.Producto;
import sic.modelo.Proveedor;
import sic.modelo.Rubro;

public interface IProductoService {

    void actualizar(Producto producto);

    void actualizarStock(Factura factura, TipoDeOperacion operacion);

    List<Producto> buscarProductos(BusquedaProductoCriteria criteria);

    double calcularGanancia_Neto(double precioCosto, double ganancia_porcentaje);

    boolean existeStockDisponible(long idProducto, double cantidad);

    double calcularGanancia_Porcentaje(double precioCosto, double PVP);

    double calcularIVA_Neto(double precioCosto, double iva_porcentaje);

    double calcularImpInterno_Neto(double precioCosto, double impInterno_porcentaje);

    double calcularPVP(double precioCosto, double ganancia_porcentaje);

    double calcularPrecioLista(double PVP, double iva_porcentaje, double impInterno_porcentaje);

    void eliminarMultiplesProductos(long[] idProducto);

    Producto getProductoPorCodigo(String codigo, Empresa empresa);

    Producto getProductoPorDescripcion(String descripcion, Empresa empresa);

    Producto getProductoPorId(long id_Producto);
  
    byte[] getReporteListaDePreciosPorEmpresa(List<Producto> productos, long idEmpresa);

    Producto guardar(Producto producto);

    List<Producto> modificarMultiplesProductos(long[] idProductos, boolean checkPrecios, PreciosProducto preciosProducto, boolean checkMedida, Medida medida, boolean checkRubro, Rubro rubro, boolean checkProveedor, Proveedor proveedor);

}
