package com.nextia.serviciofacturacione.exception;

/**
 * Excepción personalizada para el sistema de facturación electrónica
 * Permite encapsular errores específicos del proceso de facturación
 */
public class FacturacionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor con mensaje de error
     * 
     * @param message Mensaje descriptivo del error
     */
    public FacturacionException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje de error y causa original
     * 
     * @param message Mensaje descriptivo del error
     * @param cause Excepción original que causó el error
     */
    public FacturacionException(String message, Throwable cause) {
        super(message, cause);
    }
}
