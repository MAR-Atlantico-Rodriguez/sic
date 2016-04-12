package sic.service;

import java.util.List;
import sic.modelo.BusquedaTransportistaCriteria;
import sic.modelo.Empresa;
import sic.modelo.Transportista;

public interface ITransportistaService {

    void actualizar(Transportista transportista);

    List<Transportista> buscarTransportistas(BusquedaTransportistaCriteria criteria);

    void eliminar(Transportista transportista);

    Transportista getTransportistaPorNombre(String nombre, Empresa empresa);

    List<Transportista> getTransportistas(Empresa empresa);

    void guardar(Transportista transportista);
    
}
