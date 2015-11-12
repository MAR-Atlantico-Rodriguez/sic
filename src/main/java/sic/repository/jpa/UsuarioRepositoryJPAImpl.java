package sic.repository.jpa;

import java.util.List;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import sic.modelo.Usuario;
import sic.repository.IUsuarioRepository;
import sic.service.ServiceException;

@Repository
public class UsuarioRepositoryJPAImpl implements IUsuarioRepository {
    
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Usuario> getUsuarios() {        
        TypedQuery<Usuario> typedQuery = em.createNamedQuery("Usuario.buscarTodos", Usuario.class);
        List<Usuario> usuarios = typedQuery.getResultList();        
        return usuarios;
    }

    @Override
    public Usuario getUsuarioPorNombre(String nombre) {        
        TypedQuery<Usuario> typedQuery = em.createNamedQuery("Usuario.buscarPorNombre", Usuario.class);
        typedQuery.setParameter("nombre", nombre);
        List<Usuario> usuarios = typedQuery.getResultList();        
        if (usuarios.isEmpty()) {
            return null;
        } else {
            return usuarios.get(0);
        }
    }

    @Override
    public List<Usuario> getUsuariosAdministradores() {        
        TypedQuery<Usuario> typedQuery = em.createNamedQuery("Usuario.buscarUsuariosAdministradores", Usuario.class);
        List<Usuario> usuarios = typedQuery.getResultList();        
        return usuarios;
    }

    @Override
    public Usuario getUsuarioPorNombreContrasenia(String nombre, String contrasenia) throws ServiceException {        
        TypedQuery<Usuario> typedQuery = em.createNamedQuery("Usuario.buscarPorNombreContrasenia", Usuario.class);
        typedQuery.setParameter("nombre", nombre);
        typedQuery.setParameter("password", contrasenia);
        List<Usuario> usuarios = typedQuery.getResultList();        
        if (usuarios.isEmpty()) {
            throw new ServiceException(ResourceBundle.getBundle("Mensajes").getString("mensaje_usuario_logInInvalido"));
        } else {
            return usuarios.get(0);
        }
    }

    @Override
    public void actualizar(Usuario usuario) {        
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.merge(usuario);
        tx.commit();        
    }

    @Override
    public void guardar(Usuario usuario) {        
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(usuario);
        tx.commit();        
    }
}