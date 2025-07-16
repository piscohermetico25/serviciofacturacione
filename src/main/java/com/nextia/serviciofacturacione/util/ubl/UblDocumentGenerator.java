package com.nextia.serviciofacturacione.util.ubl;

import org.w3c.dom.Document;

/**
 * Interfaz base para todos los generadores de documentos UBL
 */
public interface UblDocumentGenerator {
    
    /**
     * Genera un documento XML UBL
     * 
     * @param document Documento XML base
     * @param data Datos para la generación del documento
     * @throws Exception si ocurre algún error durante la generación
     */
    void generate(Document document, Object data) throws Exception;
}
