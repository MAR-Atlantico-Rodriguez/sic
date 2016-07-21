package sic.modelo;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusquedaCajaCriteria {

    /*
     * Se utiliza como estructura de transporte, para no estar pasando los campos de
     * manera individual.
     */
    private boolean buscaPorFecha;
    private Date fechaDesde;
    private Date fechaHasta;
    private Empresa empresa;
    private int cantidadDeRegistros;
    private boolean buscaPorUsuario;
    private Usuario usuario;

}
