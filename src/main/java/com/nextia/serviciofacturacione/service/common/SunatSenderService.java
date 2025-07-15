package com.nextia.serviciofacturacione.service.common;

/**
 * Interfaz para el servicio de envío de documentos electrónicos a SUNAT
 * Basado en el manual del programador: https://cpe.sunat.gob.pe/sites/default/files/inline-files/manual_programador%20%281%29.pdf
 */
public interface SunatSenderService {
    
    /**
     * Envía un archivo ZIP a SUNAT mediante el método sendBill
     * @param nombreArchivo Nombre del archivo ZIP
     * @param contenidoZip Contenido del archivo ZIP
     * @return Array de bytes con la respuesta CDR de SUNAT
     */
    byte[] enviarArchivo(String nombreArchivo, byte[] contenidoZip);
    
    /**
     * Consulta el estado de un documento en SUNAT mediante el método getStatus
     * @param tipoDocumento Tipo de documento (01=Factura, 03=Boleta, 07=Nota de Crédito, 08=Nota de Débito)
     * @param serie Serie del documento
     * @param numero Número del documento
     * @return Array de bytes con la respuesta de SUNAT
     * @deprecated Usar consultarCdr en su lugar según el manual del programador de SUNAT
     */
    @Deprecated(since = "1.1.0", forRemoval = true)
    byte[] consultarEstado(String tipoDocumento, String serie, String numero);
    
    /**
     * Envía un resumen diario a SUNAT mediante el método sendSummary
     * @param nombreArchivo Nombre del archivo ZIP
     * @param contenidoZip Contenido del archivo ZIP
     * @return Número de ticket generado por SUNAT
     */
    String enviarResumen(String nombreArchivo, byte[] contenidoZip);
    
    /**
     * Envía un lote de comprobantes a SUNAT mediante el método sendPack
     * @param nombreArchivo Nombre del archivo ZIP
     * @param contenidoZip Contenido del archivo ZIP
     * @return Número de ticket generado por SUNAT
     */
    String enviarLote(String nombreArchivo, byte[] contenidoZip);
    
    /**
     * Consulta el estado de un ticket de resumen o lote en SUNAT mediante el método getStatus
     * @param numeroTicket Número de ticket generado por SUNAT
     * @return Array de bytes con la respuesta CDR de SUNAT
     */
    byte[] consultarTicket(String numeroTicket);
    
    /**
     * Consulta el CDR de un documento en SUNAT mediante el método getStatusCdr
     * @param tipoDocumento Tipo de documento (01=Factura, 03=Boleta, 07=Nota de Crédito, 08=Nota de Débito)
     * @param serie Serie del documento
     * @param numero Número del documento
     * @return Array de bytes con la respuesta CDR de SUNAT
     */
    byte[] consultarCdr(String tipoDocumento, String serie, String numero);
    
    /**
     * Consulta el estado de un comprobante en SUNAT mediante el método getStatusAR
     * @param tipoDocumento Tipo de documento (01=Factura, 03=Boleta, 07=Nota de Crédito, 08=Nota de Débito)
     * @param serie Serie del documento
     * @param numero Número del documento
     * @return Array de bytes con la respuesta de estado de SUNAT
     */
    byte[] consultarEstadoAR(String tipoDocumento, String serie, String numero);
}
