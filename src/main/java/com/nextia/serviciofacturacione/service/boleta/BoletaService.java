package com.nextia.serviciofacturacione.service.boleta;

import com.nextia.serviciofacturacione.model.Boleta;
import com.nextia.serviciofacturacione.model.CdrResponse;

/**
 * Interfaz para el servicio de gestión de boletas electrónicas
 */
public interface BoletaService {
    
    /**
     * Genera el XML UBL 2.1 para una boleta
     * @param boleta Objeto con los datos de la boleta
     * @return Array de bytes con el contenido del XML
     */
    byte[] generarXml(Boleta boleta);
    
    /**
     * Envía una boleta a SUNAT
     * @param boleta Objeto con los datos de la boleta
     * @param ruc RUC del emisor
     * @param usuarioSol Usuario SOL
     * @param claveSol Clave SOL
     * @return Respuesta del CDR de SUNAT
     */
    CdrResponse enviarBoleta(Boleta boleta, String ruc, String usuarioSol, String claveSol);
    
    /**
     * Consulta el estado de una boleta en SUNAT
     * @param ruc RUC del emisor
     * @param tipoDocumento Tipo de documento (03 para boleta)
     * @param serie Serie de la boleta
     * @param numero Número de la boleta
     * @param usuarioSol Usuario SOL
     * @param claveSol Clave SOL
     * @return Respuesta con el estado del documento
     */
    CdrResponse consultarEstado(String ruc, String tipoDocumento, String serie, String numero, 
                                String usuarioSol, String claveSol);
}
