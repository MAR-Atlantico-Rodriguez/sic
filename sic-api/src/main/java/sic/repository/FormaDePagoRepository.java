package sic.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import sic.modelo.Empresa;
import sic.modelo.FormaDePago;

public interface FormaDePagoRepository extends PagingAndSortingRepository<FormaDePago, Long> {
    
      @Query("SELECT fp FROM FormaDePago fp WHERE fp.id_FormaDePago = :idFormaDePago AND fp.eliminada = :eliminada")
      FormaDePago findOne(@Param("idFormaDePago") long idFormaDePago, @Param("eliminada") boolean eliminada);
    
      FormaDePago findByNombreAndEmpresaAndEliminada(String nombre, Empresa empresa, boolean eliminada);

      FormaDePago findByAndEmpresaAndPredeterminadoAndEliminada(Empresa empresa, boolean predeterminado, boolean eliminada);

      List<FormaDePago> findAllByAndEmpresaAndEliminada(Empresa empresa, boolean eliminada);
  
}
