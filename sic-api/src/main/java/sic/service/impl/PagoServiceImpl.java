package sic.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.persistence.EntityNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sic.modelo.Factura;
import sic.modelo.FacturaCompra;
import sic.modelo.FacturaVenta;
import sic.modelo.FormaDePago;
import sic.modelo.Pago;
import sic.repository.IPagoRepository;
import sic.service.IFacturaService;
import sic.service.IPagoService;
import sic.service.BusinessServiceException;
import sic.util.Utilidades;

@Service
public class PagoServiceImpl implements IPagoService {

    private final IPagoRepository pagoRepository;
    private final IFacturaService facturaService;
    private static final Logger LOGGER = Logger.getLogger(PagoServiceImpl.class.getPackage().getName());

    @Autowired
    public PagoServiceImpl(IPagoRepository pagoRepository,
            IFacturaService facturaService) {

        this.pagoRepository = pagoRepository;
        this.facturaService = facturaService;
    }

    @Override
    public Pago getPagoPorId(long idPago) {
        Pago pago = this.pagoRepository.getPagoPorId(idPago);
        if (pago == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_pago_inexistente_eliminado"));
        }
        return pago;
    }
    
    @Override
    public List<Pago> getPagosDeLaFactura(long idFactura) {
        return this.pagoRepository.getPagosDeLaFactura(idFactura);
    }

    @Override
    public double getSaldoAPagar(Factura factura) {
        double saldo = factura.getTotal();
        for (Pago pago : this.getPagosDeLaFactura(factura.getId_Factura())) {
            saldo = saldo - pago.getMonto();
        }
        if (saldo < 0) {
            saldo = 0.0;
        }
        return saldo;
    }

    @Override
    public List<Pago> getPagosEntreFechasYFormaDePago(long id_Empresa, long id_FormaDePago, Date desde, Date hasta) {
        return pagoRepository.getPagosEntreFechasYFormaDePago(id_Empresa, id_FormaDePago, desde, hasta);
    }

    @Override
    public long getSiguienteNroPago(Long idEmpresa) {
        return 1 + pagoRepository.getMayorNroPago(idEmpresa);
    }
    
    @Override
    @Transactional
    public Pago guardar(Pago pago) {
        this.validarOperacion(pago);
        pago.setNroPago(this.getSiguienteNroPago(pago.getEmpresa().getId_Empresa()));
        pago = pagoRepository.guardar(pago);
        facturaService.actualizarFacturaEstadoPagada(pago.getFactura());
        LOGGER.warn("El Pago " + pago + " se guardó correctamente.");
        return pago;
    }

    @Override
    @Transactional
    public void eliminar(long idPago) {
        Pago pago = this.getPagoPorId(idPago);
        if (pago == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_pago_inexistente_eliminado"));
        }
        pago.setEliminado(true);
        pagoRepository.actualizar(pago);
        facturaService.actualizarFacturaEstadoPagada(pago.getFactura());
        LOGGER.warn("El Pago " + pago + " se eliminó correctamente.");
    }

    @Override
    public double calcularTotalAdeudadoFacturasVenta(List<FacturaVenta> facturasVenta) {
        List<Factura> facturas = new ArrayList<>();
        facturas.addAll(facturasVenta);
        return this.calcularTotalAdeudadoFacturas(facturas);
    }

    @Override
    public double calcularTotalAdeudadoFacturasCompra(List<FacturaCompra> facturasCompra) {
        List<Factura> facturas = new ArrayList<>();
        facturas.addAll(facturasCompra);
        return this.calcularTotalAdeudadoFacturas(facturas);
    }

    @Override
    public double calcularTotalAdeudadoFacturas(List<Factura> facturas) {
        double total = 0.0;
        for (Factura factura : facturas) {
            total += this.getSaldoAPagar(factura);
        }
        return total;
    }

    @Override
    @Transactional
    public void pagarMultiplesFacturas(List<Factura> facturas, double monto, FormaDePago formaDePago, String nota, Date fechaYHora) {
        if (monto <= this.calcularTotalAdeudadoFacturas(facturas)) {
            List<Factura> facturasOrdenadas = facturaService.ordenarFacturasPorFechaAsc(facturas);
            for (Factura factura : facturasOrdenadas) {
                if (monto > 0.0) {
                    factura.setPagos(this.getPagosDeLaFactura(factura.getId_Factura()));
                    Pago nuevoPago = new Pago();
                    nuevoPago.setFormaDePago(formaDePago);
                    nuevoPago.setFactura(factura);
                    nuevoPago.setFecha(fechaYHora);
                    nuevoPago.setEmpresa(factura.getEmpresa());
                    nuevoPago.setNota(nota);
                    double saldoAPagar = this.getSaldoAPagar(factura);
                    if (saldoAPagar <= monto) {
                        monto = monto - saldoAPagar;
                        // Se utiliza round por un problema de presicion de la maquina ej: 828.65 - 614.0 = 214.64999...
                        monto = Math.round(monto * 100.0) / 100.0;
                        nuevoPago.setMonto(saldoAPagar);
                    } else {
                        nuevoPago.setMonto(monto);
                        monto = 0.0;
                    }
                    this.guardar(nuevoPago);
                }
            }
        } else {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_pago_mayorADeuda_monto"));
        }
    }

    @Override
    public void validarOperacion(Pago pago) {
        //Requeridos
        if (pago.getMonto() <= 0) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_pago_mayorQueCero_monto"));
        }
        if (pago.getFecha() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_pago_fecha_vacia"));
        }
        if (pago.getFactura() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_pago_factura_vacia"));
        }
        if (pago.getEmpresa() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_pago_empresa_vacia"));
        }
        if (pago.getFormaDePago() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_pago_formaDePago_vacia"));
        }
        if (pago.getFactura().isPagada() == true) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_pagada"));
        }
        if (Utilidades.round(pago.getMonto(), 2) > Utilidades.round(this.getSaldoAPagar(pago.getFactura()), 2)) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_pago_mayorADeuda_monto"));
        }
    }

}
