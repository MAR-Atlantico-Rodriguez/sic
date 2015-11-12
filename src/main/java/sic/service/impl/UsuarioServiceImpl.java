package sic.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sic.modelo.UsuarioActivo;
import sic.modelo.Usuario;
import sic.repository.IUsuarioRepository;
import sic.repository.jpa.UsuarioRepositoryJPAImpl;
import sic.service.IUsuarioService;
import sic.service.ServiceException;
import sic.service.TipoDeOperacion;
import sic.util.Utilidades;
import sic.util.Validator;

@Service
public class UsuarioServiceImpl implements IUsuarioService {
        
    private final IUsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioServiceImpl(IUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }      
    
    @Override
    public List<Usuario> getUsuarios() {
        return usuarioRepository.getUsuarios();
    }

    @Override
    public Usuario getUsuarioPorNombre(String nombre) {
        return usuarioRepository.getUsuarioPorNombre(nombre);
    }

    @Override
    public List<Usuario> getUsuariosAdministradores() {
        return usuarioRepository.getUsuariosAdministradores();
    }

    @Override
    public Usuario getUsuarioPorNombreContrasenia(String nombre, String contrasenia) {
        return usuarioRepository.getUsuarioPorNombreContrasenia(nombre, contrasenia);
    }

    @Override
    public UsuarioActivo getUsuarioActivo() {
        return UsuarioActivo.getInstance();
    }

    @Override
    public void setUsuarioActivo(Usuario usuario) {
        UsuarioActivo usuarioActivo = UsuarioActivo.getInstance();
        usuarioActivo.setUsuario(usuario);
        usuarioActivo.setFechaHoraIngreso(new Date());
    }

    private void validarOperacion(TipoDeOperacion operacion, Usuario usuario) {
        //Requeridos
        if (Validator.esVacio(usuario.getNombre())) {
            throw new ServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_usuario_vacio_nombre"));
        }
        if (Validator.esVacio(usuario.getPassword())) {
            throw new ServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_usuario_vacio_password"));
        }
        //Duplicados
        //Nombre
        Usuario usuarioDuplicado = this.getUsuarioPorNombre(usuario.getNombre());
        if (operacion.equals(TipoDeOperacion.ALTA) && usuarioDuplicado != null) {
            throw new ServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_usuario_duplicado_nombre"));
        }
        if (operacion.equals(TipoDeOperacion.ACTUALIZACION)) {
            if (usuarioDuplicado != null && usuarioDuplicado.getId_Usuario() != usuario.getId_Usuario()) {
                throw new ServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_usuario_duplicado_nombre"));
            }
        }
        //Ultimo usuario administrador
        if ((operacion == TipoDeOperacion.ACTUALIZACION && usuario.getPermisosAdministrador() == false)
                || operacion == TipoDeOperacion.ELIMINACION && usuario.getPermisosAdministrador() == true) {
            List<Usuario> adminitradores = this.getUsuariosAdministradores();
            if (adminitradores.size() == 1) {
                if (adminitradores.get(0).getId_Usuario() == usuario.getId_Usuario()) {
                    throw new ServiceException(ResourceBundle.getBundle("Mensajes")
                            .getString("mensaje_usuario_ultimoAdmin"));
                }
            }
        }
    }

    @Override
    public void actualizar(Usuario usuario) {
        this.validarOperacion(TipoDeOperacion.ACTUALIZACION, usuario);
        usuario.setPassword(Utilidades.encriptarConMD5(usuario.getPassword()));
        usuarioRepository.actualizar(usuario);
    }

    @Override
    public void guardar(Usuario usuario) {
        this.validarOperacion(TipoDeOperacion.ALTA, usuario);
        usuario.setPassword(Utilidades.encriptarConMD5(usuario.getPassword()));
        usuarioRepository.guardar(usuario);
    }

    @Override
    public void eliminar(Usuario usuario) {
        this.validarOperacion(TipoDeOperacion.ELIMINACION, usuario);
        usuario.setEliminado(true);
        usuarioRepository.actualizar(usuario);
    }

    @Override
    public Usuario validarUsuario(String nombre, String password) {
        Usuario usuario = this.getUsuarioPorNombreContrasenia(nombre, Utilidades.encriptarConMD5(password));
        if (usuario == null) {
            throw new ServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_usuario_logInInvalido"));
        } else {
            return usuario;
        }
    }
}