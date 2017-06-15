package sic.modelo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    
@Entity
@Table(name = "notaDeCredito")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaDeCredito implements Serializable {

    @Id
    @GeneratedValue
    private long id_NotaDeCredito;
    
    @Column(nullable = false)
    private long nroNotaDeCredito;
    
    private boolean eliminada;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoDeComprobante tipoDeComprobante;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    
    @ManyToOne
    @JoinColumn(name = "id_Cliente", referencedColumnName = "id_Cliente")
    private Cliente cliente;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_NotaDeCredito")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<FacturaVenta> facturasVenta;
    
    private String descripcion;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_NotaDeCredito")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<RenglonNota> renglonNota;
    
    @Column(nullable = false)
    private double subTotal;
    
    @Column(nullable = false)
    private double ivaPorcentaje;

    @Column(nullable = false)
    private double ivaNeto;
    
    @Column(nullable = false)
    private double total;
    
}
