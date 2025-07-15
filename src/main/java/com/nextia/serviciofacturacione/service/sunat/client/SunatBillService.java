package com.nextia.serviciofacturacione.service.sunat.client;

/**
 * Interfaz para el servicio web de SUNAT según el manual del programador
 * https://cpe.sunat.gob.pe/sites/default/files/inline-files/manual_programador%20%281%29.pdf
 */
public interface SunatBillService {
    
    
    /**
     * Envía un comprobante a SUNAT (factura, boleta, nota de crédito, nota de débito)
     * 
     * @param fileName Nombre del archivo ZIP
     * @param contentFile Contenido del archivo ZIP
     * @return Respuesta de SUNAT en formato ZIP con el CDR
     */
    byte[] sendBill(String fileName, byte[] contentFile);
    
    /**
     * Envía un resumen diario o comunicación de baja a SUNAT
     * 
     * @param fileName Nombre del archivo ZIP
     * @param contentFile Contenido del archivo ZIP
     * @return Número de ticket asignado por SUNAT
     */
    String sendSummary(String fileName, byte[] contentFile);
    
    /**
     * Envía un lote de varios comprobantes a SUNAT
     * 
     * @param fileName Nombre del archivo ZIP
     * @param contentFile Contenido del archivo ZIP
     * @return Número de ticket asignado por SUNAT
     */
    String sendPack(String fileName, byte[] contentFile);
    
    /**
     * Consulta el estado de un ticket (para resúmenes o packs)
     * 
     * @param ticket Número de ticket generado por SUNAT
     * @return Respuesta de SUNAT en formato ZIP con el CDR
     */
    byte[] getStatus(String ticket);
    
    /**
     * Consulta el CDR por tipo, serie y número de comprobante
     * 
     * @param rucComprobante RUC del emisor
     * @param tipoComprobante Tipo de comprobante
     * @param serieComprobante Serie del comprobante
     * @param numeroComprobante Número del comprobante
     * @return Respuesta de SUNAT en formato ZIP con el CDR
     */
    byte[] getStatusCdr(String rucComprobante, String tipoComprobante, String serieComprobante, String numeroComprobante);
    
    /**
     * Consulta el estado de un comprobante por RUC, tipo, serie y número
     * 
     * @param rucComprobante RUC del emisor
     * @param tipoComprobante Tipo de comprobante
     * @param serieComprobante Serie del comprobante
     * @param numeroComprobante Número del comprobante
     * @return Respuesta de SUNAT con el estado del comprobante
     */
    byte[] getStatusAR(String rucComprobante, String tipoComprobante, String serieComprobante, String numeroComprobante);
}
