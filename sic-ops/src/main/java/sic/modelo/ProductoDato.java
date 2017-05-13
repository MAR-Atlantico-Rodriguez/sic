package sic.modelo;

import java.util.List;
import lombok.Data;

@Data
public class ProductoDato {
    
    List<Producto> productos;
    long cantidadDeProductos;
}
