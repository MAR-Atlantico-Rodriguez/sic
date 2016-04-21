package sic.repository;

import java.util.List;
import sic.modelo.Localidad;
import sic.modelo.Provincia;

public interface ILocalidadRepository {

    void actualizar(Localidad localidad);

    Localidad getLocalidadPorNombre(String nombre, Provincia provincia);

    List<Localidad> getLocalidadesDeLaProvincia(Provincia provincia);

    void guardar(Localidad localidad);
    
}