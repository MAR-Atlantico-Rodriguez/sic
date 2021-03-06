package sic.modelo;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusquedaFacturaCompraCriteria {

    private boolean buscaPorFecha;
    private Date fechaDesde;
    private Date fechaHasta;
    private boolean buscaPorProveedor;
    private Proveedor proveedor;
    private boolean buscaPorNumeroFactura;
    private long numSerie;
    private long numFactura;
    private boolean buscarSoloInpagas;
    private boolean buscaSoloPagadas;
    private Empresa empresa;
    private int cantRegistros;

}
