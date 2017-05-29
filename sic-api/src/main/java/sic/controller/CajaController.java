package sic.controller;

import java.util.Calendar;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import sic.modelo.BusquedaCajaCriteria;
import sic.modelo.Caja;
import sic.modelo.Usuario;
import sic.service.ICajaService;
import sic.service.IEmpresaService;
import sic.service.IUsuarioService;

@RestController
@RequestMapping("/api/v1")
public class CajaController {
    
    private final ICajaService cajaService;    
    private final IEmpresaService empresaService;
    private final IUsuarioService usuarioService;
    
    @Autowired
    public CajaController(ICajaService cajaService, IEmpresaService empresaService,
                          IUsuarioService usuarioService) {
        this.cajaService = cajaService;        
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
    }
    
    @GetMapping("/cajas/{idCaja}")
    @ResponseStatus(HttpStatus.OK)
    public Caja getCajaPorId(@PathVariable long idCaja) {
        return cajaService.getCajaPorId(idCaja);
    }
    
    @PutMapping("/cajas")
    @ResponseStatus(HttpStatus.OK)
    public void actualizar(@RequestBody Caja caja) {
        cajaService.actualizar(caja);        
    }
    
    @PostMapping("/cajas")
    @ResponseStatus(HttpStatus.CREATED)
    public Caja guardar(@RequestBody Caja caja) {
        return cajaService.guardar(caja);
    }
    
    @DeleteMapping("/cajas/{idCaja}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable long idCaja) {
        cajaService.eliminar(idCaja);
    }
    
    @PutMapping("/cajas/{idCaja}/cierre")
    @ResponseStatus(HttpStatus.OK)
    public Caja cerrarCaja(@PathVariable long idCaja,
                           @RequestParam double monto,
                           @RequestParam long idUsuarioCierre) {
        return cajaService.cerrarCaja(idCaja, monto, idUsuarioCierre, false);
    }
    
    @GetMapping("/cajas/busqueda/criteria")
    @ResponseStatus(HttpStatus.OK)
    public List<Caja> getCajasCriteria(@RequestParam(value = "idEmpresa") long idEmpresa,
                                       @RequestParam(value = "desde", required = false) Long desde,
                                       @RequestParam(value = "hasta", required = false) Long hasta,
                                       @RequestParam(value = "idUsuario", required = false) Long idUsuario) {
        Calendar fechaDesde = Calendar.getInstance();            
        Calendar fechaHasta = Calendar.getInstance();
        if (desde != null && hasta != null) {           
            fechaDesde.setTimeInMillis(desde);
            fechaHasta.setTimeInMillis(hasta);
        }
        Usuario usuario = new Usuario();
        if(idUsuario != null) {
            usuario = usuarioService.getUsuarioPorId(idUsuario);
        }
        BusquedaCajaCriteria criteria = BusquedaCajaCriteria.builder()
                                        .buscaPorFecha((desde != null) && (hasta != null))
                                        .fechaDesde(fechaDesde.getTime())
                                        .fechaHasta(fechaHasta.getTime())
                                        .empresa(empresaService.getEmpresaPorId(idEmpresa))
                                        .cantidadDeRegistros(0)
                                        .buscaPorUsuario(idUsuario != null)
                                        .usuario(usuario)
                                        .build();
        return cajaService.getCajasCriteria(criteria);        
    }
    
    
    @GetMapping("/cajas/{idCaja}/total")
    public double getTotalCaja(@PathVariable long idCaja, 
                               @RequestParam(required = false) boolean soloAfectaCaja) {
        return cajaService.getTotalCaja(cajaService.getCajaPorId(idCaja), soloAfectaCaja);
    }
    
}
