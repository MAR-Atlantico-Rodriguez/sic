package sic.modelo;

import java.util.Date;
import lombok.Data;

/*
 * Se utiliza como estructura de transporte, para no estar pasando los campos de
 * manera individual.
 */
@Data
public class BusquedaFacturaVentaCriteria {

    private boolean buscaPorFecha;
    private Date fechaDesde;
    private Date fechaHasta;
    private boolean buscaCliente;
    private Cliente cliente;
    private boolean buscaPorTipoFactura;
    private char tipoFactura;
    private boolean buscaUsuario;
    private Usuario usuario;
    private boolean buscaPorNumeroFactura;
    private int numSerie;
    private int numFactura;
    private boolean buscarPorPedido;
    private long nroPedido;
    private boolean buscaSoloImpagas;
    private boolean buscaSoloPagadas;
    private Empresa empresa;
    private int cantRegistros;
}
