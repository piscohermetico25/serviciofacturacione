package com.nextia.serviciofacturacione.service.common;

import com.nextia.serviciofacturacione.model.CdrResponse;

/**
 * Interfaz para el servicio de procesamiento de respuestas CDR de SUNAT
 */
public interface CdrProcessorService {
    
    /**
     * Procesa un archivo ZIP de CDR y extrae la información relevante
     * @param contenidoZip Contenido del archivo ZIP de CDR
     * @return Objeto con la información procesada del CDR
     */
    CdrResponse procesarZip(byte[] contenidoZip);
    
    /**
     * Procesa un archivo XML de CDR y extrae la información relevante
     * @param contenidoXml Contenido del archivo XML de CDR
     * @return Objeto con la información procesada del CDR
     */
    CdrResponse procesarXml(byte[] contenidoXml);
    
    /**
     * Extrae el código de respuesta de un CDR
     * @param contenidoXml Contenido del archivo XML de CDR
     * @return Código de respuesta
     */
    String extraerCodigoRespuesta(byte[] contenidoXml);
    
    /**
     * Extrae la descripción de respuesta de un CDR
     * @param contenidoXml Contenido del archivo XML de CDR
     * @return Descripción de respuesta
     */
    String extraerDescripcionRespuesta(byte[] contenidoXml);
    
    /**
     * Extrae las notas adicionales de un CDR
     * @param contenidoXml Contenido del archivo XML de CDR
     * @return Lista de notas adicionales
     */
    java.util.List<String> extraerNotas(byte[] contenidoXml);
}
