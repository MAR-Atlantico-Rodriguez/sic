package sic.service;

import java.util.Date;
import java.util.List;
import sic.modelo.CuentaCorriente;

public interface ICuentaCorrienteService {
       
      void actualizar(CuentaCorriente cuentaCorriente);

      void eliminar(Long idCuentaCorriente);

      CuentaCorriente getCuentaCorrientePorID(Long idCuentaCorriente);

      List<CuentaCorriente> getCuentasCorrientes(long idEmpresa, Date fechaApertura);

      CuentaCorriente getUltimaCuentaCorriente(long idEmpresa);

      int getUltimoNumeroDeCuentaCorriente(long idEmpresa);

      CuentaCorriente guardar(CuentaCorriente cuentaCorriente);

      void validarCuentaCorriente(CuentaCorriente cuentaCorriente);
  
      double getSaldoCuentaCorriente(CuentaCorriente cuentaCorriente);

}
