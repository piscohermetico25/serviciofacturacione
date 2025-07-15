package com.nextia.serviciofacturacione.service;

import com.nextia.serviciofacturacione.model.Boleta;
import com.nextia.serviciofacturacione.model.CdrResponse;
import com.nextia.serviciofacturacione.model.Factura;
import com.nextia.serviciofacturacione.model.NotaCredito;
import com.nextia.serviciofacturacione.model.NotaDebito;

/**
 * Servicio principal para la gestión de comprobantes electrónicos
 * Orquesta el flujo completo para todos los tipos de documentos
 */
public interface ComprobanteService {

    /**
     * Envía una factura electrónica a SUNAT
     * 
     * @param factura Datos de la factura
     * @param ruc RUC del emisor
     * @param usuarioSol Usuario SOL
     * @param claveSol Clave SOL
     * @return Respuesta CDR de SUNAT
     */
    CdrResponse enviarFactura(Factura factura, String ruc, String usuarioSol, String claveSol);
    
    /**
     * Envía una boleta electrónica a SUNAT
     * 
     * @param boleta Datos de la boleta
     * @param ruc RUC del emisor
     * @param usuarioSol Usuario SOL
     * @param claveSol Clave SOL
     * @return Respuesta CDR de SUNAT
     */
    CdrResponse enviarBoleta(Boleta boleta, String ruc, String usuarioSol, String claveSol);
    
    /**
     * Envía una nota de crédito electrónica a SUNAT
     * 
     * @param notaCredito Datos de la nota de crédito
     * @param ruc RUC del emisor
     * @param usuarioSol Usuario SOL
     * @param claveSol Clave SOL
     * @return Respuesta CDR de SUNAT
     */
    CdrResponse enviarNotaCredito(NotaCredito notaCredito, String ruc, String usuarioSol, String claveSol);
    
    /**
     * Envía una nota de débito electrónica a SUNAT
     * 
     * @param notaDebito Datos de la nota de débito
     * @param ruc RUC del emisor
     * @param usuarioSol Usuario SOL
     * @param claveSol Clave SOL
     * @return Respuesta CDR de SUNAT
     */
    CdrResponse enviarNotaDebito(NotaDebito notaDebito, String ruc, String usuarioSol, String claveSol);
    
    /**
     * Consulta el estado de un comprobante electrónico
     * 
     * @param ruc RUC del emisor
     * @param tipoDocumento Tipo de documento (01=Factura, 03=Boleta, 07=Nota de Crédito, 08=Nota de Débito)
     * @param serie Serie del comprobante
     * @param numero Número del comprobante
     * @param usuarioSol Usuario SOL
     * @param claveSol Clave SOL
     * @return Respuesta CDR de SUNAT
     */
    CdrResponse consultarEstado(String ruc, String tipoDocumento, String serie, String numero, 
                               String usuarioSol, String claveSol);
}
