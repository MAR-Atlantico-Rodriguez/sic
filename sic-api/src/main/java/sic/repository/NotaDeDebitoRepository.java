package sic.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import sic.modelo.Cliente;
import sic.modelo.Empresa;
import sic.modelo.NotaDeDebito;
import sic.modelo.TipoDeComprobante;

public interface NotaDeDebitoRepository extends PagingAndSortingRepository<NotaDeDebito, Long>, QueryDslPredicateExecutor<NotaDeDebito> {
    
      @Query("SELECT n FROM NotaDeDebito n WHERE n.id_NotaDeDebito = :idNotaDeDebito AND n.eliminada = false")
      NotaDeDebito findById(@Param("idNotaDeDebito") long idNotaDeDebito);

      NotaDeDebito findByTipoDeComprobanteAndNroNotaDeDebitoAndClienteAndEliminada(TipoDeComprobante tipoDeComprobante, long nroNotaDeDebito, Cliente cliente, boolean eliminada);

      List<NotaDeDebito> findAllByClienteAndEmpresaAndEliminada(Cliente cliente, Empresa empresa, boolean eliminada);

      NotaDeDebito findTopByClienteAndEmpresaAndEliminadaOrderByNroNotaDeDebito(Cliente cliente, Empresa empresa, boolean eliminada);
    
}
