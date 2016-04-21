package sic.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "caja")
@NamedQueries({
    @NamedQuery(name = "Caja.getUltimaCaja",
            query = "SELECT c FROM Caja c WHERE c.empresa.id_Empresa = :id_Empresa AND c.eliminada = false ORDER BY c.fechaApertura DESC"),
    @NamedQuery(name = "Caja.buscarCajaPorID",
            query = "SELECT c FROM Caja c WHERE c.id_Caja = :id_caja AND c.empresa.id_Empresa = :id_Empresa ORDER BY c.fechaApertura ASC"),
    @NamedQuery(name = "Caja.getUltimoNumeroDeCaja",
            query = "SELECT max(c.nroCaja) FROM Caja c WHERE c.empresa.id_Empresa = :id_Empresa"),
    @NamedQuery(name = "Caja.getCajas",
            query = "SELECT c FROM Caja c WHERE c.empresa.id_Empresa = :id_Empresa AND c.eliminada = false AND c.fechaApertura BETWEEN :desde AND :hasta ORDER BY c.fechaApertura DESC")
})
@Data
public class Caja implements Serializable {

    @Id
    @GeneratedValue
    private long id_Caja;

    private int nroCaja;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaApertura;

    @Column(nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCierre;

    @ManyToOne
    @JoinColumn(name = "id_Empresa", referencedColumnName = "id_Empresa")
    private Empresa empresa;

    @OneToOne
    @JoinColumn(name = "id_Usuario", referencedColumnName = "id_Usuario")
    private Usuario usuarioAbreCaja;

    @OneToOne
    @JoinColumn(name = "id_UsuarioCierra", referencedColumnName = "id_Usuario")
    private Usuario usuarioCierraCaja;

    @Column(nullable = false)
    private String observacion;

    private boolean cerrada;

    private double saldoInicial;

    private double saldoFinal;

    private double saldoReal;

    private boolean eliminada;

}