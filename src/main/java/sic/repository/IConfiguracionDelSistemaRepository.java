package sic.repository;

import sic.modelo.ConfiguracionDelSistema;
import sic.modelo.Empresa;

public interface IConfiguracionDelSistemaRepository {

    void actualizar(ConfiguracionDelSistema cds);

    ConfiguracionDelSistema getConfiguracionDelSistemaPorEmpresa(Empresa empresa);

    ConfiguracionDelSistema getConfiguracionDelSistemaPorId(long id_ConfiguracionDelSistema);

    void guardar(ConfiguracionDelSistema cds);

    void guardarXMLconDOM(String pathEtiqueta, String valor) throws XMLException;

    void leerXMLconDOM() throws XMLException;
    
}
