package com.nextia.serviciofacturacione.exception;

/**
 * Excepción específica para errores durante la generación de documentos UBL
 */
public class UblGenerationException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor con mensaje de error
     * 
     * @param message Mensaje de error
     */
    public UblGenerationException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje de error y causa
     * 
     * @param message Mensaje de error
     * @param cause Causa de la excepción
     */
    public UblGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
