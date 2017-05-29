package sic.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import sic.modelo.Empresa;
import sic.modelo.Factura;
import sic.modelo.Pedido;
import sic.modelo.TipoDeComprobante;

public interface FacturaRepository extends PagingAndSortingRepository<Factura, Long>, FacturaRepositoryCustom {

    @Query("SELECT f FROM Factura f WHERE f.id_Factura = :idFactura AND f.eliminada = false")
    Factura findById(@Param("idFactura") long idFactura);
    
    Factura findByTipoComprobanteAndNumSerieAndNumFacturaAndEmpresaAndEliminada(TipoDeComprobante tipoComprobante, long serie, long num, Empresa empresa, boolean eliminada);

    List<Factura> findAllByPedidoAndEliminada(Pedido pedido, boolean eliminada);

    @Query("SELECT max(fv.numFactura) FROM FacturaVenta fv WHERE fv.tipoComprobante = :tipoComprobante AND fv.numSerie = :numSerie AND fv.empresa.id_Empresa = :idEmpresa")
    Long buscarMayorNumFacturaSegunTipo(@Param("tipoComprobante") TipoDeComprobante tipoComprobante, @Param("numSerie") long numSerie, @Param("idEmpresa") long idEmpresa);

}
