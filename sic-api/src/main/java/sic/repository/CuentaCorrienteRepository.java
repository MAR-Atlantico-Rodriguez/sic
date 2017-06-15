package sic.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import sic.modelo.CuentaCorriente;
import sic.modelo.Empresa;

public interface CuentaCorrienteRepository extends PagingAndSortingRepository<CuentaCorriente, Long>, QueryDslPredicateExecutor<CuentaCorriente>  {
    
      @Query("SELECT c FROM CuentaCorriente c WHERE c.id_CuentaCorriente = :idCuentaCorriente AND c.eliminada = false")
      CuentaCorriente findById(@Param("idCuentaCorriente") long idCuentaCorriente);
      
      CuentaCorriente findByNroCuentaCorrienteAndEmpresaAndEliminada(int nroCuentaCorriente, Empresa empresa, boolean eliminada);

      List<CuentaCorriente> findAllByFechaAperturaAndEmpresaAndEliminada(Date fechaApertura, Empresa empresa, boolean eliminada);
      
      CuentaCorriente findTopByEmpresaAndEliminadaOrderByFechaAperturaDesc(Empresa empresa, boolean eliminada);
    
}
