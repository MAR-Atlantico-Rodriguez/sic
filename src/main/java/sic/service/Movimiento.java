package sic.service;

/**
 * Describe las distintas operaciones comerciales que se pueden realizar.
 */
public enum Movimiento {

    /**
     * Para el caso en que se este realizando un compra.
     */
    COMPRA,
    /**
     * Para el caso en que se este realizando una venta.
     */
    VENTA,
    /**
     * Para el caso en que se realize un gasto.
     */
    GASTO,
    /**
     * Para el caso en que se este realizando un pedido.
     */
    PEDIDO;
}
