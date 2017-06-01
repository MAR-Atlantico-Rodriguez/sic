package sic.modelo;

/**
 * Describe los distintos tipos de comprobante.
 */

public enum TipoDeComprobante {
    
    FACTURA_A("Factura A"),
    
    FACTURA_B("Factura B"),
    
    FACTURA_C("Factura C"),
    
    FACTURA_X("Factura X"),
    
    FACTURA_Y("Factura Y"),
    
    PEDIDO("Pedido"), 
    
    PRESUPUESTO("Presupuesto");
    
    private final String text;

    private TipoDeComprobante(final String text) {
        this.text = text;
    }
    
    @Override
    public String toString() {
        return text;
    }
}
