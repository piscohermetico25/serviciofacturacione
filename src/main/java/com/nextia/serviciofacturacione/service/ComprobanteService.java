package com.nextia.serviciofacturacione.service;

import com.nextia.serviciofacturacione.dto.BajaDocumentosRequest;
import com.nextia.serviciofacturacione.dto.ComprobanteRequest;
import com.nextia.serviciofacturacione.dto.ResumenDocumentosRequest;
import com.nextia.serviciofacturacione.model.CdrResponse;
import com.nextia.serviciofacturacione.model.common.Emisor;

public interface ComprobanteService {


    CdrResponse enviarFactura(ComprobanteRequest comprobanteRequest, Emisor emisor);
    CdrResponse enviarBoleta(ComprobanteRequest boletaRequest, Emisor emisor);
    CdrResponse enviarNotaCredito(ComprobanteRequest notaCreditoRequest, Emisor emisor);
    CdrResponse enviarNotaDebito(ComprobanteRequest notaDebitoRequest, Emisor emisor);
    CdrResponse enviarResumenDocumentos(ResumenDocumentosRequest request, Emisor emisor);
    CdrResponse enviarBajaDocumentos(BajaDocumentosRequest request, Emisor emisor);
    CdrResponse consultarEstado(Emisor emisor, String tipoDocumento, String serie, String numero);
}
