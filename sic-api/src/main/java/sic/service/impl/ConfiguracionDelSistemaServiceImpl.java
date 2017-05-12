package sic.service.impl;

import java.util.ResourceBundle;
import javax.persistence.EntityNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sic.modelo.ConfiguracionDelSistema;
import sic.modelo.Empresa;
import sic.service.IConfiguracionDelSistemaService;
import sic.repository.ConfiguracionDelSistemaRepository;
import sic.service.BusinessServiceException;

@Service
public class ConfiguracionDelSistemaServiceImpl implements IConfiguracionDelSistemaService {

    private final ConfiguracionDelSistemaRepository configuracionRepository; 
    private static final Logger LOGGER = Logger.getLogger(ConfiguracionDelSistemaServiceImpl.class.getPackage().getName());

    @Autowired
    public ConfiguracionDelSistemaServiceImpl(ConfiguracionDelSistemaRepository configuracionRepository) {
        this.configuracionRepository = configuracionRepository;           
    }

    @Override
    public ConfiguracionDelSistema getConfiguracionDelSistemaPorId(long id_ConfiguracionDelSistema) {
        ConfiguracionDelSistema cds = configuracionRepository.findOne(id_ConfiguracionDelSistema); 
        if (cds == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_cds_no_existente"));
        }
        return cds;        
    }

    @Override
    public ConfiguracionDelSistema getConfiguracionDelSistemaPorEmpresa(Empresa empresa) {        
        return configuracionRepository.findByEmpresa(empresa);        
    }

    @Override
    @Transactional
    public ConfiguracionDelSistema guardar(ConfiguracionDelSistema cds) {    
        this.validarCds(cds);
        cds = configuracionRepository.save(cds);        
        LOGGER.warn("La Configuracion del Sistema " + cds + " se guardó correctamente." );
        return cds;
    }

    @Override
    @Transactional
    public void actualizar(ConfiguracionDelSistema cds) {    
        this.validarCds(cds);
        configuracionRepository.save(cds);        
    }
    
    @Override
    public void validarCds(ConfiguracionDelSistema cds) {
        if (cds.isFacturaElectronicaHabilitada()) {
            if (cds.getCertificadoAfip() == null) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_cds_certificado_vacio"));
            }
            if (cds.getFirmanteCertificadoAfip().isEmpty()) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_cds_firmante_vacio"));
            }
            if (cds.getNroPuntoDeVentaAfip() <= 0) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_cds_punto_venta_incorrecto"));
            }
            if (cds.getPasswordCertificadoAfip() == null || cds.getPasswordCertificadoAfip().isEmpty()) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_cds_password_vacio"));
            }
        }
    }
}
