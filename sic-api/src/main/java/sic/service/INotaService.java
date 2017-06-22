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
    
    NotaDeCredito guardarNotaCredito(NotaDeCredito  notaCredito);
    
    NotaDeDebito guardarNotaDebito(NotaDeDebito notaDebito);
    
    NotaDeCredito getNotaDeCreditoPorID(Long id_NotaDeCredito);
    
    NotaDeDebito getNotaDeDebitoPorID(Long id_NotaDeDebito);
    
    List<FacturaVenta> getFacturasDeNotaDeCredito(Long id_NotaDeCredito);
    
    List<FacturaVenta> getFacturasDeNotaDeDebito(Long id_NotaDeDebito);
    
    List<NotaDeCredito> getNotasDeCreditoPorClienteYEmpresa(Long idCliente, Long idEmpresa);
    
    List<NotaDeDebito> getNotasDeDebitoPorClienteYEmpresa(Long idCliente, Long idEmpresa);
    
    double getSaldoNotas(Long idCliente, Long idEmpresa);
    
    TipoDeComprobante[] getTipoNota(Empresa empresa, Cliente cliente);
    
    List<RenglonNota> getRenglonesDeNotaCredito(Long id_NotaCredito);
    
    List<RenglonNota> getRenglonesDeNotaDebito(Long id_NotaDebito);
    
    void eliminarNotaDeCredito(long[] idNotaDeCredito);
    
    void eliminarNotaDeDebito(long[] idNotaDeDebito);
    
    double calcularTotalNotaDeCredito(NotaDeCredito notaDeCredito);
    
    double calcularIvaNetoNotaDeCredito(NotaDeCredito notaDeCredito);
    
    double calcularTotalNotaDeDebito(NotaDeDebito notaDeDebito);
 
    double calcularIvaNetoNotaDeDebito(NotaDeDebito notaDeDebito);
    
    RenglonNota calcularRenglon(TipoDeComprobante tipoDeComprobante, Movimiento movimiento, double cantidad, Long idProducto, double descuentoPorcentaje);

}
