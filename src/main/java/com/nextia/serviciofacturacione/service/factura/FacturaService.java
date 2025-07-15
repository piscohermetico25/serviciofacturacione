package com.nextia.serviciofacturacione.service.factura;

import com.nextia.serviciofacturacione.model.CdrResponse;
import com.nextia.serviciofacturacione.model.Factura;

/**
 * Interfaz para el servicio de gestión de facturas electrónicas
 */
public interface FacturaService {
    
    /**
     * Genera el XML UBL 2.1 para una factura
     * @param factura Objeto con los datos de la factura
     * @return Array de bytes con el contenido del XML
     */
    byte[] generarXml(Factura factura);
    
    /**
     * Envía una factura a SUNAT
     * @param factura Objeto con los datos de la factura
     * @param ruc RUC del emisor
     * @param usuarioSol Usuario SOL
     * @param claveSol Clave SOL
     * @return Respuesta del CDR de SUNAT
     */
    CdrResponse enviarFactura(Factura factura, String ruc, String usuarioSol, String claveSol);
    
    /**
     * Consulta el estado de una factura en SUNAT
     * @param ruc RUC del emisor
     * @param tipoDocumento Tipo de documento (01 para factura)
     * @param serie Serie de la factura
     * @param numero Número de la factura
     * @param usuarioSol Usuario SOL
     * @param claveSol Clave SOL
     * @return Respuesta con el estado del documento
     */
    CdrResponse consultarEstado(String ruc, String tipoDocumento, String serie, String numero, 
                                String usuarioSol, String claveSol);
}
