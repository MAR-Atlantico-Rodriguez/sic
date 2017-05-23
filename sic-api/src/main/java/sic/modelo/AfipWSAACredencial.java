package sic.modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AfipWSAACredencial {
    
    private String token;
    private String sign;
    private long cuit;
    
}
