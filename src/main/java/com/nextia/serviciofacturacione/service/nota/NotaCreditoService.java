package com.nextia.serviciofacturacione.service.nota;

import com.nextia.serviciofacturacione.model.CdrResponse;
import com.nextia.serviciofacturacione.model.NotaCredito;

/**
 * Interfaz para el servicio de gestión de notas de crédito electrónicas
 */
public interface NotaCreditoService {
    
    /**
     * Genera el XML UBL 2.1 para una nota de crédito
     * @param notaCredito Objeto con los datos de la nota de crédito
     * @return Array de bytes con el contenido del XML
     */
    byte[] generarXml(NotaCredito notaCredito);
    
    /**
     * Envía una nota de crédito a SUNAT
     * @param notaCredito Objeto con los datos de la nota de crédito
     * @param ruc RUC del emisor
     * @param usuarioSol Usuario SOL
     * @param claveSol Clave SOL
     * @return Respuesta del CDR de SUNAT
     */
    CdrResponse enviarNotaCredito(NotaCredito notaCredito, String ruc, String usuarioSol, String claveSol);
    
    /**
     * Consulta el estado de una nota de crédito en SUNAT
     * @param ruc RUC del emisor
     * @param tipoDocumento Tipo de documento (07 para nota de crédito)
     * @param serie Serie de la nota de crédito
     * @param numero Número de la nota de crédito
     * @param usuarioSol Usuario SOL
     * @param claveSol Clave SOL
     * @return Respuesta con el estado del documento
     */
    CdrResponse consultarEstado(String ruc, String tipoDocumento, String serie, String numero, 
                                String usuarioSol, String claveSol);
}
