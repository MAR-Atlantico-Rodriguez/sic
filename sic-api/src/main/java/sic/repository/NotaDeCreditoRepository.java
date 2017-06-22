package sic.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import sic.modelo.Cliente;
import sic.modelo.Empresa;
import sic.modelo.NotaDeCredito;
import sic.modelo.TipoDeComprobante;


public interface NotaDeCreditoRepository extends PagingAndSortingRepository<NotaDeCredito, Long>, QueryDslPredicateExecutor<NotaDeCredito> {
    
      @Query("SELECT n FROM NotaDeCredito n WHERE n.id_NotaDeCredito = :idNotaDeCredito AND n.eliminada = false")
      NotaDeCredito findById(@Param("idNotaDeCredito") long idNotaDeCredito);

      NotaDeCredito findByTipoDeComprobanteAndNroNotaDeCreditoAndClienteAndEliminada(TipoDeComprobante tipoDeComprobante, long nroNotaDeCredito, Cliente cliente, boolean eliminada);

      List<NotaDeCredito> findAllByClienteAndEmpresaAndEliminada(Cliente cliente, Empresa empresa, boolean eliminada);

      NotaDeCredito findTopByClienteAndEmpresaAndEliminadaOrderByNroNotaDeCredito(Cliente cliente, Empresa empresa, boolean eliminada);
    
}