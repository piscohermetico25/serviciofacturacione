package com.nextia.serviciofacturacione.service.sunat;

import com.nextia.serviciofacturacione.model.common.Emisor;

/**
 * Interfaz para el servicio de envío de documentos electrónicos a SUNAT
 * Basado en el manual del programador: https://cpe.sunat.gob.pe/sites/default/files/inline-files/manual_programador%20%281%29.pdf
 */
public interface SunatSenderService {
    
    byte[] enviarArchivo(String nombreArchivo, byte[] contenidoZip);
    
    byte[] consultarEstado(Emisor emisor, String tipoDocumento, String serie, String numero);
    
    String enviarResumen(String nombreArchivo, byte[] contenidoZip);
    
    String enviarLote(String nombreArchivo, byte[] contenidoZip);
    
    byte[] consultarTicket(String numeroTicket);
    
    byte[] consultarCdr(String tipoDocumento, String serie, String numero);
    byte[] consultarEstadoAR(String tipoDocumento, String serie, String numero) ;
    

}
