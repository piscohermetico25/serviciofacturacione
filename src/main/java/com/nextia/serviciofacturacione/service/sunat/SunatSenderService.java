package com.nextia.serviciofacturacione.service.sunat;

/**
 * Interfaz para el servicio de envío de documentos electrónicos a SUNAT
 * Basado en el manual del programador: https://cpe.sunat.gob.pe/sites/default/files/inline-files/manual_programador%20%281%29.pdf
 */
public interface SunatSenderService {
    
    /**
     * Envía un archivo ZIP a SUNAT mediante el método sendBill
     * @param nombreArchivo Nombre del archivo ZIP
     * @param contenidoZip Contenido del archivo ZIP
     * @param ruc RUC del emisor
     * @param usuarioSol Usuario SOL
     * @param claveSol Clave SOL
     * @return Array de bytes con la respuesta CDR de SUNAT
     */
    byte[] enviarArchivo(String nombreArchivo, byte[] contenidoZip, String ruc, String usuarioSol, String claveSol);
    
    /**
     * Consulta el estado de un documento en SUNAT mediante el método getStatus
     * @param ruc RUC del emisor
     * @param tipoDocumento Tipo de documento (01=Factura, 03=Boleta, 07=Nota de Crédito, 08=Nota de Débito)
     * @param serie Serie del documento
     * @param numero Número del documento
     * @param usuarioSol Usuario SOL
     * @param claveSol Clave SOL
     * @return Array de bytes con la respuesta de SUNAT
     * @deprecated Usar consultarCdr en su lugar según el manual del programador de SUNAT
     */
    @Deprecated(since = "1.1.0", forRemoval = true)
    byte[] consultarEstado(String ruc, String tipoDocumento, String serie, String numero, 
                          String usuarioSol, String claveSol);
    
    /**
     * Envía un resumen diario a SUNAT mediante el método sendSummary
     * @param nombreArchivo Nombre del archivo ZIP
     * @param contenidoZip Contenido del archivo ZIP
     * @param ruc RUC del emisor
     * @param usuarioSol Usuario SOL
     * @param claveSol Clave SOL
     * @return Ticket de recepción del resumen
     */
    String enviarResumen(String nombreArchivo, byte[] contenidoZip, String ruc, String usuarioSol, String claveSol);
    
    /**
     * Envía un lote de comprobantes a SUNAT mediante el método sendPack
     * @param nombreArchivo Nombre del archivo ZIP
     * @param contenidoZip Contenido del archivo ZIP
     * @param ruc RUC del emisor
     * @param usuarioSol Usuario SOL
     * @param claveSol Clave SOL
     * @return Ticket de recepción del lote
     */
    String enviarLote(String nombreArchivo, byte[] contenidoZip, String ruc, String usuarioSol, String claveSol);
    
    /**
     * Consulta el estado de un ticket de resumen o lote en SUNAT mediante el método getStatus
     * @param numeroTicket Número de ticket generado por SUNAT
     * @param ruc RUC del emisor
     * @param usuarioSol Usuario SOL
     * @param claveSol Clave SOL
     * @return Array de bytes con la respuesta CDR de SUNAT
     */
    byte[] consultarTicket(String numeroTicket, String ruc, String usuarioSol, String claveSol);
    
    /**
     * Consulta el CDR de un documento en SUNAT mediante el método getStatusCdr
     * @param ruc RUC del emisor
     * @param tipoDocumento Tipo de documento (01=Factura, 03=Boleta, 07=Nota de Crédito, 08=Nota de Débito)
     * @param serie Serie del documento
     * @param numero Número del documento
     * @param usuarioSol Usuario SOL
     * @param claveSol Clave SOL
     * @return Array de bytes con la respuesta CDR de SUNAT
     */
    byte[] consultarCdr(String ruc, String tipoDocumento, String serie, String numero, 
                       String usuarioSol, String claveSol);
}
