package sic.repository.custom;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import sic.modelo.BusquedaFacturaCompraCriteria;
import sic.modelo.BusquedaFacturaVentaCriteria;
import sic.modelo.FacturaCompra;
import sic.modelo.FacturaVenta;
import sic.modelo.TipoDeComprobante;
import sic.util.FormatterFechaHora;
import sic.repository.FacturaRepositoryCustom;

@Repository
public class FacturaRepositoryImpl implements FacturaRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public double calcularTotalFacturadoVenta(BusquedaFacturaVentaCriteria criteria) {
        String query = "SELECT SUM(f.total) FROM FacturaVenta f WHERE f.empresa = :empresa AND f.eliminada = false";
        //Fecha
        if (criteria.isBuscaPorFecha() == true) {
            FormatterFechaHora formateadorFecha = new FormatterFechaHora(FormatterFechaHora.FORMATO_FECHAHORA_INTERNACIONAL);
            query += " AND f.fecha BETWEEN '" + formateadorFecha.format(criteria.getFechaDesde()) + "' AND '" + formateadorFecha.format(criteria.getFechaHasta()) + "'";
        }
        //Cliente
        if (criteria.isBuscaCliente() == true) {
            query += " AND f.cliente = " + criteria.getCliente().getId_Cliente();
        }
        //Tipo de Factura
        if (criteria.isBuscaPorTipoComprobante() == true) {
            query += " AND f.tipoComprobante = " + "\'" + criteria.getTipoComprobante() + "\'";
        }
        //Usuario
        if (criteria.isBuscaUsuario() == true) {
            query += " AND f.usuario = " + criteria.getUsuario().getId_Usuario();
        }
        if (criteria.isBuscaViajante() == true) {
            query += " AND f.cliente.viajante = " + criteria.getViajante().getId_Usuario();
        }
        //Nro de Factura
        if (criteria.isBuscaPorNumeroFactura() == true) {
            query += " AND f.numSerie = " + criteria.getNumSerie() + " AND f.numFactura = " + criteria.getNumFactura();
        }
        //Pedido
        if (criteria.isBuscarPorPedido() == true) {
            query += " AND f.pedido.nroPedido = " + criteria.getNroPedido();
        }
        //Inpagas
        if (criteria.isBuscaSoloImpagas() == true) {
            query += " AND f.pagada = false";
        }
        //Pagas
        if (criteria.isBuscaSoloPagadas() == true) {
            query += " AND f.pagada = true";
        }
        query += " ORDER BY f.fecha DESC";
        TypedQuery<Double> typedQuery = em.createQuery(query, Double.class);
        typedQuery.setParameter("empresa", criteria.getEmpresa());
        //si es 0, recupera TODOS los registros
        if (criteria.getCantRegistros() != 0) {
            typedQuery.setMaxResults(criteria.getCantRegistros());
        }
        return (typedQuery.getSingleResult() == null) ? 0.0 : typedQuery.getSingleResult();
    }

    @Override
    public double calcularTotalFacturadoCompra(BusquedaFacturaCompraCriteria criteria) {
        String query = "SELECT SUM(f.total) FROM FacturaCompra f WHERE f.empresa = :empresa AND f.eliminada = false";
        //Fecha Factura
        if (criteria.isBuscaPorFecha() == true) {
            FormatterFechaHora formateadorFecha = new FormatterFechaHora(FormatterFechaHora.FORMATO_FECHAHORA_INTERNACIONAL);
            query += " AND f.fecha BETWEEN '" + formateadorFecha.format(criteria.getFechaDesde()) + "' AND '" + formateadorFecha.format(criteria.getFechaHasta()) + "'";
        }
        //Proveedor
        if (criteria.isBuscaPorProveedor() == true) {
            query += " AND f.proveedor = " + criteria.getProveedor().getId_Proveedor();
        }
        //Nro de Factura
        if (criteria.isBuscaPorNumeroFactura() == true) {
            query += " AND f.numSerie = " + criteria.getNumSerie() + " AND f.numFactura = " + criteria.getNumFactura();
        }
        //Inpagas
        if (criteria.isBuscarSoloInpagas() == true) {
            query += " AND f.pagada = false";
        }
        //Pagas
        if (criteria.isBuscaSoloPagadas() == true) {
            query += " AND f.pagada = true";
        }
        query += " ORDER BY f.fecha DESC";
        TypedQuery<Double> typedQuery = em.createQuery(query, Double.class);
        typedQuery.setParameter("empresa", criteria.getEmpresa());
        //si es 0, recupera TODOS los registros
        if (criteria.getCantRegistros() != 0) {
            typedQuery.setMaxResults(criteria.getCantRegistros());
        }
        return (typedQuery.getSingleResult() == null) ? 0.0 : typedQuery.getSingleResult();
    }

    @Override
    public double calcularIVA_Venta(BusquedaFacturaVentaCriteria criteria, TipoDeComprobante[] tipoComprobante) {
        String query = "SELECT SUM(f.iva_105_neto + f.iva_21_neto) FROM FacturaVenta f WHERE f.empresa = :empresa AND f.eliminada = false";        
        //Fecha
        if (criteria.isBuscaPorFecha() == true) {
            FormatterFechaHora formateadorFecha = new FormatterFechaHora(FormatterFechaHora.FORMATO_FECHAHORA_INTERNACIONAL);
            query += " AND f.fecha BETWEEN '" + formateadorFecha.format(criteria.getFechaDesde())
                    + "' AND '" + formateadorFecha.format(criteria.getFechaHasta()) + "'";
        }
        //Cliente
        if (criteria.isBuscaCliente() == true) {
            query += " AND f.cliente = " + criteria.getCliente().getId_Cliente();
        }
        //Tipo de Factura
        if (criteria.isBuscaPorTipoComprobante() == true) {
            query += " AND f.tipoComprobante = " + "\'" + criteria.getTipoComprobante() + "\'";
        }
        for (int i = 0; i < tipoComprobante.length; i++) {
            if (i == 0) {
                query += " AND ( f.tipoComprobante = \'" + tipoComprobante[i] + "\'";
            } else {
                query += " OR f.tipoComprobante = \'" + tipoComprobante[i] + "\'";
            }
        }
        query += " )";
        //Usuario
        if (criteria.isBuscaUsuario() == true) {
            query += " AND f.usuario = " + criteria.getUsuario().getId_Usuario();
        }
        if (criteria.isBuscaViajante() == true) {
            query += " AND f.cliente.viajante = " + criteria.getViajante().getId_Usuario();
        }
        //Nro de Factura
        if (criteria.isBuscaPorNumeroFactura() == true) {
            query += " AND f.numSerie = " + criteria.getNumSerie() + " AND f.numFactura = " + criteria.getNumFactura();
        }
        //Pedido
        if (criteria.isBuscarPorPedido() == true) {
            query += " AND f.pedido.nroPedido = " + criteria.getNroPedido();
        }
        //Inpagas
        if (criteria.isBuscaSoloImpagas() == true) {
            query += " AND f.pagada = false";
        }
        //Pagas
        if (criteria.isBuscaSoloPagadas() == true) {
            query += " AND f.pagada = true";
        }
        query += " ORDER BY f.fecha DESC";
        TypedQuery<Double> typedQuery = em.createQuery(query, Double.class);
        typedQuery.setParameter("empresa", criteria.getEmpresa());
        //si es 0, recupera TODOS los registros
        if (criteria.getCantRegistros() != 0) {
            typedQuery.setMaxResults(criteria.getCantRegistros());
        }
        return (typedQuery.getSingleResult() == null) ? 0.0 : typedQuery.getSingleResult();
    }

    @Override
    public double calcularIVA_Compra(BusquedaFacturaCompraCriteria criteria, TipoDeComprobante[] tipoComprobante) {
        String query = "SELECT SUM(f.iva_105_neto + f.iva_21_neto) FROM FacturaCompra f WHERE f.empresa = :empresa AND f.eliminada = false";
        //Fecha Factura
        if (criteria.isBuscaPorFecha() == true) {
            FormatterFechaHora formateadorFecha = new FormatterFechaHora(FormatterFechaHora.FORMATO_FECHAHORA_INTERNACIONAL);
            query += " AND f.fecha BETWEEN '" + formateadorFecha.format(criteria.getFechaDesde())
                    + "' AND '" + formateadorFecha.format(criteria.getFechaHasta()) + "'";
        }
        //Proveedor
        if (criteria.isBuscaPorProveedor() == true) {
            query += " AND f.proveedor = " + criteria.getProveedor().getId_Proveedor();
        }
        for (int i = 0; i < tipoComprobante.length; i++) {
            if (i == 0) {
                query += " AND ( f.tipoComprobante = \'" + tipoComprobante[i] + "\'";  
            } else {
                query += " OR f.tipoComprobante = \'" + tipoComprobante[i] + "\'";
            }
        }
        query += " )";
        //Nro de Factura
        if (criteria.isBuscaPorNumeroFactura() == true) {
            query += " AND f.numSerie = " + criteria.getNumSerie() + " AND f.numFactura = " + criteria.getNumFactura();
        }
        //Inpagas
        if (criteria.isBuscarSoloInpagas() == true) {
            query += " AND f.pagada = false";
        }
        //Pagas
        if (criteria.isBuscaSoloPagadas() == true) {
            query += " AND f.pagada = true";
        }
        query += " ORDER BY f.fecha DESC";
        TypedQuery<Double> typedQuery = em.createQuery(query, Double.class);
        typedQuery.setParameter("empresa", criteria.getEmpresa());
        //si es 0, recupera TODOS los registros
        if (criteria.getCantRegistros() != 0) {
            typedQuery.setMaxResults(criteria.getCantRegistros());
        }
        return (typedQuery.getSingleResult() == null) ? 0.0 : typedQuery.getSingleResult();
    }

    @Override
    public double calcularGananciaTotal(BusquedaFacturaVentaCriteria criteria) {
        String query = "SELECT SUM(r.ganancia_neto * r.cantidad) FROM FacturaVenta f LEFT JOIN f.renglones r WHERE f.empresa = :empresa AND f.eliminada = false";
        //Fecha
        if (criteria.isBuscaPorFecha() == true) {
            FormatterFechaHora formateadorFecha = new FormatterFechaHora(FormatterFechaHora.FORMATO_FECHAHORA_INTERNACIONAL);
            query += " AND f.fecha BETWEEN '" + formateadorFecha.format(criteria.getFechaDesde())
                    + "' AND '" + formateadorFecha.format(criteria.getFechaHasta()) + "'";
        }
        //Cliente
        if (criteria.isBuscaCliente() == true) {
            query += " AND f.cliente = " + criteria.getCliente().getId_Cliente();
        }
        //Tipo de Factura
        if (criteria.isBuscaPorTipoComprobante() == true) {
            query += " AND f.tipoComprobante = " + "\'" + criteria.getTipoComprobante() + "\'";
        }
        //Usuario
        if (criteria.isBuscaUsuario() == true) {
            query += " AND f.usuario = " + criteria.getUsuario().getId_Usuario();
        }
        if (criteria.isBuscaViajante() == true) {
            query += " AND f.cliente.viajante = " + criteria.getViajante().getId_Usuario();
        }
        //Nro de Factura
        if (criteria.isBuscaPorNumeroFactura() == true) {
            query += " AND f.numSerie = " + criteria.getNumSerie() + " AND f.numFactura = " + criteria.getNumFactura();
        }
        //Pedido
        if (criteria.isBuscarPorPedido() == true) {
            query += " AND f.pedido.nroPedido = " + criteria.getNroPedido();
        }
        //Inpagas
        if (criteria.isBuscaSoloImpagas() == true) {
            query += " AND f.pagada = false";
        }
        //Pagas
        if (criteria.isBuscaSoloPagadas() == true) {
            query += " AND f.pagada = true";
        }
        query += " ORDER BY f.fecha DESC";
        TypedQuery<Double> typedQuery = em.createQuery(query, Double.class);
        typedQuery.setParameter("empresa", criteria.getEmpresa());
        //si es 0, recupera TODOS los registros
        if (criteria.getCantRegistros() != 0) {
            typedQuery.setMaxResults(criteria.getCantRegistros());
        }
        return (typedQuery.getSingleResult() == null) ? 0.0 : typedQuery.getSingleResult();
    }

    @Override
    public List<FacturaVenta> buscarFacturasVenta(BusquedaFacturaVentaCriteria criteria) {
        String query = "SELECT f FROM FacturaVenta f WHERE f.empresa = :empresa AND f.eliminada = false";
        //Fecha
        if (criteria.isBuscaPorFecha() == true) {
            FormatterFechaHora formateadorFecha = new FormatterFechaHora(FormatterFechaHora.FORMATO_FECHAHORA_INTERNACIONAL);
            query += " AND f.fecha BETWEEN '" + formateadorFecha.format(criteria.getFechaDesde()) + "' AND '" + formateadorFecha.format(criteria.getFechaHasta()) + "'";
        }
        //Cliente
        if (criteria.isBuscaCliente() == true) {
            query += " AND f.cliente = " + criteria.getCliente().getId_Cliente();
        }
        //Tipo de Factura
        if (criteria.isBuscaPorTipoComprobante()== true) {
            query += " AND f.tipoComprobante = " + "\'" + criteria.getTipoComprobante() + "\'";
        }
        //Usuario
        if (criteria.isBuscaUsuario() == true) {
            query += " AND f.usuario = " + criteria.getUsuario().getId_Usuario();
        }
        if (criteria.isBuscaViajante() == true) {
            query += " AND f.cliente.viajante = " + criteria.getViajante().getId_Usuario();
        }
        //Nro de Factura
        if (criteria.isBuscaPorNumeroFactura() == true) {
            query += " AND f.numSerie = " + criteria.getNumSerie() + " AND f.numFactura = " + criteria.getNumFactura();
        }
        //Pedido
        if (criteria.isBuscarPorPedido() == true) {
            query += " AND f.pedido.nroPedido = " + criteria.getNroPedido();
        }
        //Inpagas
        if (criteria.isBuscaSoloImpagas() == true) {
            query += " AND f.pagada = false";
        }
        //Pagas
        if (criteria.isBuscaSoloPagadas() == true) {
            query += " AND f.pagada = true";
        }
        query += " ORDER BY f.fecha DESC";
        TypedQuery<FacturaVenta> typedQuery = em.createQuery(query, FacturaVenta.class);
        typedQuery.setParameter("empresa", criteria.getEmpresa());
        //si es 0, recupera TODOS los registros
        if (criteria.getCantRegistros() != 0) {
            typedQuery.setMaxResults(criteria.getCantRegistros());
        }
        return typedQuery.getResultList();
    }

    @Override
    public List<FacturaCompra> buscarFacturasCompra(BusquedaFacturaCompraCriteria criteria) {
        String query = "SELECT f FROM FacturaCompra f WHERE f.empresa = :empresa AND f.eliminada = false";
        //Fecha Factura
        if (criteria.isBuscaPorFecha() == true) {
            FormatterFechaHora formateadorFecha = new FormatterFechaHora(FormatterFechaHora.FORMATO_FECHAHORA_INTERNACIONAL);
            query += " AND f.fecha BETWEEN '" + formateadorFecha.format(criteria.getFechaDesde()) + "' AND '" + formateadorFecha.format(criteria.getFechaHasta()) + "'";
        }
        //Proveedor
        if (criteria.isBuscaPorProveedor() == true) {
            query += " AND f.proveedor = " + criteria.getProveedor().getId_Proveedor();
        }
        //Nro de Factura
        if (criteria.isBuscaPorNumeroFactura() == true) {
            query += " AND f.numSerie = " + criteria.getNumSerie() + " AND f.numFactura = " + criteria.getNumFactura();
        }
        //Inpagas
        if (criteria.isBuscarSoloInpagas() == true) {
            query += " AND f.pagada = false";
        }
        //Pagas
        if (criteria.isBuscaSoloPagadas() == true) {
            query += " AND f.pagada = true";
        }
        query += " ORDER BY f.fecha DESC";
        TypedQuery<FacturaCompra> typedQuery = em.createQuery(query, FacturaCompra.class);
        typedQuery.setParameter("empresa", criteria.getEmpresa());
        //si es 0, recupera TODOS los registros
        if (criteria.getCantRegistros() != 0) {
            typedQuery.setMaxResults(criteria.getCantRegistros());
        }
        return typedQuery.getResultList();
    }

}
