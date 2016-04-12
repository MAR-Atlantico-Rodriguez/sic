package sic.repository;

import java.util.List;
import sic.modelo.Usuario;
import sic.service.ServiceException;

public interface IUsuarioRepository {

    void actualizar(Usuario usuario);

    Usuario getUsuarioPorNombre(String nombre);

    Usuario getUsuarioPorNombreContrasenia(String nombre, String contrasenia) throws ServiceException;

    List<Usuario> getUsuarios();

    List<Usuario> getUsuariosAdministradores();

    void guardar(Usuario usuario);
    
}
