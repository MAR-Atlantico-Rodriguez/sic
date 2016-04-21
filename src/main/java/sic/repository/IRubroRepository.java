package sic.repository;

import java.util.List;
import sic.modelo.Empresa;
import sic.modelo.Rubro;

public interface IRubroRepository {

    void actualizar(Rubro rubro);

    Rubro getRubroPorNombre(String nombre, Empresa empresa);

    List<Rubro> getRubros(Empresa empresa);

    void guardar(Rubro rubro);
    
}