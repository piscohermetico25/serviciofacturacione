package com.nextia.serviciofacturacione.service;

import com.nextia.serviciofacturacione.dto.BajaDocumentosRequest;
import com.nextia.serviciofacturacione.dto.FacturaRequest;
import com.nextia.serviciofacturacione.dto.ResumenDocumentosRequest;
import com.nextia.serviciofacturacione.model.Boleta;
import com.nextia.serviciofacturacione.model.CdrResponse;
import com.nextia.serviciofacturacione.model.NotaCredito;
import com.nextia.serviciofacturacione.model.NotaDebito;
import com.nextia.serviciofacturacione.model.common.Emisor;

/**
 * Servicio principal para la gestión de comprobantes electrónicos
 * Orquesta el flujo completo para todos los tipos de documentos
 */
public interface ComprobanteService {


    CdrResponse enviarFactura(FacturaRequest facturaRequest, Emisor emisor);
    

    CdrResponse enviarBoleta(Boleta boleta, String ruc, String usuarioSol, String claveSol);
    

    CdrResponse enviarNotaCredito(NotaCredito notaCredito, String ruc, String usuarioSol, String claveSol);
    

    CdrResponse enviarNotaDebito(NotaDebito notaDebito, String ruc, String usuarioSol, String claveSol);

    CdrResponse enviarResumenDocumentos(ResumenDocumentosRequest request, Emisor emisor);

    CdrResponse enviarBajaDocumentos(BajaDocumentosRequest request, Emisor emisor);

    CdrResponse consultarEstado(Emisor emisor, String tipoDocumento, String serie, String numero);
}
