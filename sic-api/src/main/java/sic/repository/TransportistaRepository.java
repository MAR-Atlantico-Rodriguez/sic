package sic.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import sic.modelo.Empresa;
import sic.modelo.Transportista;

public interface TransportistaRepository extends PagingAndSortingRepository<Transportista, Long>, QueryDslPredicateExecutor<Transportista> {
    
      @Query("SELECT t FROM Transportista t WHERE t.id_Transportista = :idTransportista AND t.eliminado = :eliminado")
      Transportista findOne(@Param("idTransportista") long idTransportista, @Param("eliminado") boolean eliminado);
      
      Transportista findByNombreAndEmpresaAndEliminado(String nombre, Empresa empresa, boolean eliminado);

      List<Transportista> findAllByAndEmpresaAndEliminado(Empresa empresa, boolean eliminado);

    
}
