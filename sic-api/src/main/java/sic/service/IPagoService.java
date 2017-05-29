package sic.service;

import java.util.Date;
import java.util.List;
import sic.modelo.Factura;
import sic.modelo.FacturaCompra;
import sic.modelo.FacturaVenta;
import sic.modelo.FormaDePago;
import sic.modelo.Pago;

public interface IPagoService {
    
    Pago getPagoPorId(long id_pago);

    List<Pago> getPagosDeLaFactura(long idFactura);

    double getSaldoAPagar(Factura factura);    
    
    long getSiguienteNroPago(Long idEmpresa);

    List<Pago> getPagosEntreFechasYFormaDePago(long id_Empresa, long id_FormaDePago, Date desde, Date hasta);
    
    double calcularTotalPagos(List<Pago> pagos);
    
    double calcularTotalAdeudadoFacturasVenta(List<FacturaVenta> facturasVenta);
    
    double calcularTotalAdeudadoFacturasCompra(List<FacturaCompra> facturasCompra);
    
    double calcularTotalAdeudadoFacturas(List<Factura> facturas);
            
    void pagarMultiplesFacturas(List<Factura> facturas, double monto, FormaDePago formaDePago, String nota);
    
    void validarOperacion(Pago pago);     

    Pago guardar(Pago pago);

    void eliminar(long idPago);
}
