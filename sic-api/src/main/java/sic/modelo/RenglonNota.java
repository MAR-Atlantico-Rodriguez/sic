package sic.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "renglonNota")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id_RenglonNota"})
public class RenglonNota {
    
    @Id
    @GeneratedValue
    private long id_RenglonNota;
    
    @Column(nullable = false)
    private long id_ProductoItem;

    @Column(nullable = false)
    private String codigoItem;

    @Column(nullable = false)
    private String descripcionItem;

    @Column(nullable = false)
    private String medidaItem;

    private double descuentoPorcentaje;
    private double descuentoNeto;
    private double gananciaPorcentaje;
    private double gananciaNeto;
    
    @Column(nullable = false)
    private double cantidad;
    
    private String descripcion;
    
    @Column(nullable = false)
    private double precioUnitario;
    
    @Column(nullable = false)
    private double ivaPorcentaje;
    
    @Column(nullable = false)
    private double subTotal; //sin nada
    
    @Column(nullable = false)
    private double subTotalBruto;  //con descuentos y recargos, sin iva
    
    @Column(nullable = false)
    private double importe; //con descuentos, recargos y con iva

}
