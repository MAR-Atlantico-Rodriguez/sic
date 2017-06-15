package sic.service;

import java.util.List;
import sic.modelo.Cliente;
import sic.modelo.Empresa;
import sic.modelo.FacturaVenta;
import sic.modelo.Movimiento;
import sic.modelo.NotaDeCredito;
import sic.modelo.NotaDeDebito;
import sic.modelo.RenglonNota;
import sic.modelo.TipoDeComprobante;

public interface INotaService {
    
    NotaDeCredito getNotaDeCreditoPorID(Long id_NotaDeCredito);
    
    NotaDeDebito getNotaDeDebitoPorID(Long id_NotaDeDebito);
    
    List<FacturaVenta> getFacturasDeNotaDeCredito(Long id_NotaDeCredito);
    
    List<FacturaVenta> getFacturasDeNotaDeDebito(Long id_NotaDeDebito);
    
    TipoDeComprobante[] getTipoNota(Empresa empresa, Cliente cliente);
    
    List<RenglonNota> getRenglonesDeNotaCredito(Long id_NotaCredito);
    
    List<RenglonNota> getRenglonesDeNotaDebito(Long id_NotaDebito);
    
    void actualizarNotaDeCredito(NotaDeCredito notaDeCredito);
    
    void actualizarNotaDeDebito(NotaDeDebito notaDeDebito);
    
    void eliminarNotaDeCredito(long[] idNotaDeCredito);
    
    void eliminarNotaDeDebito(long[] idNotaDeDebito);
    
    double calcularTotalNotaDeCredito(NotaDeCredito notaDeCredito);
    
    double calcularIvaNetoNotaDeCredito(NotaDeCredito notaDeCredito);
    
    double calcularTotalNotaDeDebito(NotaDeCredito notaDeCredito);
    
    double calcularIvaNetoNotaDeDebito(NotaDeCredito notaDeCredito);
    
    RenglonNota calcularRenglon(TipoDeComprobante tipoDeComprobante, Movimiento movimiento, double cantidad, Long idProducto);

}
