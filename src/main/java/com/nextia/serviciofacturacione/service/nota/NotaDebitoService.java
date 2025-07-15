package com.nextia.serviciofacturacione.service.nota;

import com.nextia.serviciofacturacione.model.CdrResponse;
import com.nextia.serviciofacturacione.model.NotaDebito;

/**
 * Interfaz para el servicio de gestión de notas de débito electrónicas
 */
public interface NotaDebitoService {
    
    /**
     * Genera el XML UBL 2.1 para una nota de débito
     * @param notaDebito Objeto con los datos de la nota de débito
     * @return Array de bytes con el contenido del XML
     */
    byte[] generarXml(NotaDebito notaDebito);
    
    /**
     * Envía una nota de débito a SUNAT
     * @param notaDebito Objeto con los datos de la nota de débito
     * @param ruc RUC del emisor
     * @param usuarioSol Usuario SOL
     * @param claveSol Clave SOL
     * @return Respuesta del CDR de SUNAT
     */
    CdrResponse enviarNotaDebito(NotaDebito notaDebito, String ruc, String usuarioSol, String claveSol);
    
    /**
     * Consulta el estado de una nota de débito en SUNAT
     * @param ruc RUC del emisor
     * @param tipoDocumento Tipo de documento (08 para nota de débito)
     * @param serie Serie de la nota de débito
     * @param numero Número de la nota de débito
     * @param usuarioSol Usuario SOL
     * @param claveSol Clave SOL
     * @return Respuesta con el estado del documento
     */
    CdrResponse consultarEstado(String ruc, String tipoDocumento, String serie, String numero, 
                                String usuarioSol, String claveSol);
}
