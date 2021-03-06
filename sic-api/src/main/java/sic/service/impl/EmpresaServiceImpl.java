package sic.service.impl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.persistence.EntityNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sic.modelo.ConfiguracionDelSistema;
import sic.modelo.Empresa;
import sic.service.IEmpresaService;
import sic.service.BusinessServiceException;
import sic.modelo.TipoDeOperacion;
import sic.util.Validator;
import sic.repository.EmpresaRepository;
import sic.service.IConfiguracionDelSistemaService;

@Service
public class EmpresaServiceImpl implements IEmpresaService {

    private final EmpresaRepository empresaRepository;
    private final IConfiguracionDelSistemaService configuracionDelSistemaService;    
    private static final Logger LOGGER = Logger.getLogger(EmpresaServiceImpl.class.getPackage().getName());

    @Value("${SIC_STATIC_CONTENT}")
    private String pathStaticContent;
    
    @Autowired
    public EmpresaServiceImpl(EmpresaRepository empresaRepository,
            IConfiguracionDelSistemaService configuracionDelSistemaService) {

        this.empresaRepository = empresaRepository;
        this.configuracionDelSistemaService = configuracionDelSistemaService;
    }
    
    @Override
    public Empresa getEmpresaPorId(Long idEmpresa){
        Empresa empresa = empresaRepository.findOne(idEmpresa);
        if (empresa == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_empresa_no_existente"));
        }
        return empresa;
    }

    @Override
    public List<Empresa> getEmpresas() {
        return empresaRepository.findAllByAndEliminadaOrderByNombreAsc(false);
    }

    @Override
    public Empresa getEmpresaPorNombre(String nombre) {
        return empresaRepository.findByNombreIsAndEliminadaOrderByNombreAsc(nombre, false);
    }

    @Override
    public Empresa getEmpresaPorCUIP(long cuip) {
        return empresaRepository.findByCuipAndEliminada(cuip, false);
    }

    private void validarOperacion(TipoDeOperacion operacion, Empresa empresa) {
        //Entrada de Datos
        if (!Validator.esEmailValido(empresa.getEmail())) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_empresa_email_invalido"));
        }
        //Requeridos
        if (Validator.esVacio(empresa.getNombre())) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_empresa_vacio_nombre"));
        }
        if (Validator.esVacio(empresa.getDireccion())) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_empresa_vacio_direccion"));
        }
        if (empresa.getCondicionIVA() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_empresa_vacio_condicionIVA"));
        }
        if (empresa.getLocalidad() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_empresa_vacio_localidad"));
        }
        //Duplicados
        //Nombre
        Empresa empresaDuplicada = this.getEmpresaPorNombre(empresa.getNombre());
        if (operacion.equals(TipoDeOperacion.ALTA) && empresaDuplicada != null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_empresa_duplicado_nombre"));
        }
        if (operacion.equals(TipoDeOperacion.ACTUALIZACION)) {
            if (empresaDuplicada != null && empresaDuplicada.getId_Empresa() != empresa.getId_Empresa()) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_empresa_duplicado_nombre"));
            }
        }
        //CUIP
        empresaDuplicada = this.getEmpresaPorCUIP(empresa.getCuip());
        if (operacion.equals(TipoDeOperacion.ALTA) && empresaDuplicada != null && empresa.getCuip() != 0) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_empresa_duplicado_cuip"));
        }
        if (operacion.equals(TipoDeOperacion.ACTUALIZACION)) {
            if (empresaDuplicada != null && empresaDuplicada.getId_Empresa() != empresa.getId_Empresa() && empresa.getCuip() != 0) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_empresa_duplicado_cuip"));
            }
        }
    }

    private void crearConfiguracionDelSistema(Empresa empresa) {
        ConfiguracionDelSistema cds = new ConfiguracionDelSistema();
        cds.setUsarFacturaVentaPreImpresa(false);
        cds.setCantidadMaximaDeRenglonesEnFactura(28);
        cds.setFacturaElectronicaHabilitada(false);
        cds.setEmpresa(empresa);
        configuracionDelSistemaService.guardar(cds);
    }

    @Override
    @Transactional
    public Empresa guardar(Empresa empresa) {
        validarOperacion(TipoDeOperacion.ALTA, empresa);
        empresa = empresaRepository.save(empresa);
        crearConfiguracionDelSistema(empresa);
        LOGGER.warn("La Empresa " + empresa + " se guardó correctamente." );
        return empresa;
    }

    @Override
    @Transactional
    public void actualizar(Empresa empresa) {
        validarOperacion(TipoDeOperacion.ACTUALIZACION, empresa);
        empresaRepository.save(empresa);
    }

    @Override
    @Transactional
    public void eliminar(Long idEmpresa) {
        Empresa empresa = this.getEmpresaPorId(idEmpresa);
        if (empresa == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_empresa_no_existente"));
        }
        empresa.setEliminada(true);
        configuracionDelSistemaService.eliminar(configuracionDelSistemaService.getConfiguracionDelSistemaPorEmpresa(empresa));
        empresaRepository.save(empresa);
    }
    
    @Override
    public String guardarLogoEnDisco(byte[] imagen) {
        String filename = String.valueOf(new Date().getTime());
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(pathStaticContent + filename);
            fos.write(imagen);
            fos.close();
        } catch (FileNotFoundException ex) {
            LOGGER.error(ex.getMessage());            
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_empresa_error_logo"));
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_empresa_error_logo"));
        }
        return filename;
    }
}
