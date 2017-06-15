package sic.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import sic.modelo.Empresa;
import sic.modelo.NotaDeDebito;
import sic.modelo.TipoDeComprobante;
import sic.modelo.Usuario;

public interface NotaDeDebitoRepository extends PagingAndSortingRepository<NotaDeDebito, Long>, QueryDslPredicateExecutor<NotaDeDebito> {
    
      @Query("SELECT n FROM NotaDeDebito n WHERE n.id_NotaDeDebito = :idNotaDeDebito AND n.eliminada = false")
      NotaDeDebito findById(@Param("idNotaDeDebito") long idNotaDeDebito);

      NotaDeDebito findByTipoDeComprobanteAndNroNotaDeDebitoAndEmpresaAndEliminada(TipoDeComprobante tipoDeComprobante, long nroNotaDeDebito, Empresa empresa, boolean eliminada);

      List<NotaDeDebito> findAllByUsuarioAndEliminada(Usuario usuario, boolean Eliminada);

      NotaDeDebito findTopByEmpresaAndEliminadaOrderByNroNotaDeCredito(Empresa empresa, boolean eliminada);
    
}
