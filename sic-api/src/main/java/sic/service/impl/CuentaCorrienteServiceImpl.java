package sic.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.persistence.EntityNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sic.modelo.BusquedaFacturaVentaCriteria;
import sic.modelo.CuentaCorriente;
import sic.modelo.FacturaVenta;
import sic.repository.CuentaCorrienteRepository;
import sic.service.BusinessServiceException;
import sic.service.ICuentaCorrienteService;
import sic.service.IEmpresaService;
import sic.service.IFacturaService;
import sic.service.INotaService;
import sic.service.IPagoService;

@Service
public class CuentaCorrienteServiceImpl implements ICuentaCorrienteService {
    
    private final CuentaCorrienteRepository cuentaCorrienteRepository;
    private final IEmpresaService empresaService;
    private final IFacturaService facturaService;
    private final IPagoService pagoService;
    private final INotaService notaService;
    private static final Logger LOGGER = Logger.getLogger(CuentaCorrienteServiceImpl.class.getPackage().getName()); 
    
    @Autowired
    public CuentaCorrienteServiceImpl(CuentaCorrienteRepository cuentaCorrienteRepository, IEmpresaService empresaService,
                IFacturaService facturaService, IPagoService pagoService, INotaService notaService) {
                this.cuentaCorrienteRepository = cuentaCorrienteRepository;
                this.facturaService = facturaService;
                this.pagoService = pagoService;
                this.notaService = notaService;
                this.empresaService = empresaService;
    }

    @Override
    public void actualizar(CuentaCorriente cuentaCorriente) {
        cuentaCorrienteRepository.save(cuentaCorriente);
    }

    @Override
    public void eliminar(Long idCuentaCorriente) {
        CuentaCorriente cuentaCorriente = this.getCuentaCorrientePorID(idCuentaCorriente);
        if (cuentaCorriente == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_cuenta_corriente_no_existente"));
        }
        cuentaCorriente.setEliminada(true);
        this.actualizar(cuentaCorriente);
    }

    @Override
    public CuentaCorriente getCuentaCorrientePorID(Long idCuentaCorriente) {
        CuentaCorriente cuentaCorriente = cuentaCorrienteRepository.findOne(idCuentaCorriente);
        if (cuentaCorriente == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_cuenta_corriente_no_existente"));
        }
        cuentaCorriente.setSaldo(this.getSaldoCuentaCorriente(cuentaCorriente));
        return cuentaCorriente;
    }

    @Override
    public List<CuentaCorriente> getCuentasCorrientes(long idEmpresa, Date fechaApertura) {
        return cuentaCorrienteRepository.findAllByFechaAperturaAndEmpresaAndEliminada(fechaApertura, empresaService.getEmpresaPorId(idEmpresa), false);
    }

    @Override
    public CuentaCorriente getUltimaCuentaCorriente(long idEmpresa) {
        return cuentaCorrienteRepository.findTopByEmpresaAndEliminadaOrderByFechaAperturaDesc(empresaService.getEmpresaPorId(idEmpresa), false);
    }

    @Override
    public int getUltimoNumeroDeCuentaCorriente(long idEmpresa) {
        CuentaCorriente cuentaCorriente = this.getUltimaCuentaCorriente(idEmpresa);
         if (cuentaCorriente == null) {
            return 0;
        } else {
            return cuentaCorriente.getNroCuentaCorriente();
        }
    }

    @Override
    public CuentaCorriente guardar(CuentaCorriente cuentaCorriente) {
        cuentaCorriente.setFechaApertura(cuentaCorriente.getCliente().getFechaAlta());
        cuentaCorriente.setNroCuentaCorriente(this.getUltimoNumeroDeCuentaCorriente(cuentaCorriente.getEmpresa().getId_Empresa()) + 1);
        this.validarCuentaCorriente(cuentaCorriente);
        cuentaCorriente = cuentaCorrienteRepository.save(cuentaCorriente);
        LOGGER.warn("La Cuenta Corriente " + cuentaCorriente + " se guardó correctamente." );
        return cuentaCorriente;
    }

    @Override
    public void validarCuentaCorriente(CuentaCorriente cuentaCorriente) {
        //Entrada de Datos
        //Requeridos
        if (cuentaCorriente.getFechaApertura() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_cuenta_corriente_fecha_vacia"));
        }
        if (cuentaCorriente.getEmpresa() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_caja_empresa_vacia"));
        }
        if (cuentaCorriente.getCliente() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_cliente_vacio"));
        }
        //Duplicados        
        if (cuentaCorrienteRepository.findById(cuentaCorriente.getId_CuentaCorriente()) != null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_caja_duplicada"));
        }        
    }

    @Override
    public double getSaldoCuentaCorriente(CuentaCorriente cuentaCorriente) {
        BusquedaFacturaVentaCriteria criteria = BusquedaFacturaVentaCriteria
                .builder()
                .buscaCliente(true)
                .cliente(cuentaCorriente.getCliente())
                .empresa(cuentaCorriente.getEmpresa())
                .fechaDesde(cuentaCorriente.getFechaApertura())
                .fechaHasta(new Date())
                .buscaPorFecha(true)
                .build();
        List<FacturaVenta> facturasVenta = this.facturaService.buscarFacturaVenta(criteria);
        double saldoFacturas = this.pagoService.calcularTotalAdeudadoFacturasVenta(facturasVenta);
        double saldoNotas = this.notaService.getSaldoNotas(cuentaCorriente.getCliente().getId_Cliente(), cuentaCorriente.getEmpresa().getId_Empresa());
        return saldoNotas - saldoFacturas;
    }
        
}
