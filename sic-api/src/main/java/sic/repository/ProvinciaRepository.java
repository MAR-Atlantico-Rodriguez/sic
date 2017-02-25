package sic.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import sic.modelo.Pais;
import sic.modelo.Provincia;

public interface ProvinciaRepository extends PagingAndSortingRepository<Provincia, Long> {
    
      @Query("SELECT p FROM Provincia p WHERE p.id_Provincia = :idProvincia AND p.eliminada = :eliminada")
      Provincia findOne(@Param("idProvincia") long idProvincia, @Param("eliminada") boolean eliminada);
    
      Provincia findByNombreAndPaisAndEliminadaOrderByNombreAsc(String nombre, Pais pais, boolean eliminada);
      
      List<Provincia> findAllByAndPaisAndEliminada(Pais pais, boolean eliminada);

}
