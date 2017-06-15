package sic.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "caja")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id_CuentaCorriente"})
public class CuentaCorriente implements Serializable {
    
    @Id
    @GeneratedValue
    private long id_CuentaCorriente;
    
    @Column(nullable = false)
    private int nroCuentaCorriente;
    
    private boolean eliminada;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaApertura;
            
    @ManyToOne // o ONE to ONE??
    @JoinColumn(name = "id_Cliente", referencedColumnName = "id_Cliente")
    private Cliente cliente;
    
    @ManyToOne
    @JoinColumn(name = "id_Empresa", referencedColumnName = "id_Empresa")
    private Empresa empresa;
    
    @Transient
    private double saldo;
    
}
