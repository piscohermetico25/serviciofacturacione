package com.nextia.serviciofacturacione.service.baja;

import com.nextia.serviciofacturacione.dto.BajaDocumentosRequest;
import com.nextia.serviciofacturacione.model.CdrResponse;
import com.nextia.serviciofacturacione.model.common.Emisor;

public interface BajaDocumentosService {

    byte[] generarXml(String nombreArchivo, Emisor emisor, BajaDocumentosRequest request);
    public CdrResponse enviarBajaDocumentos(BajaDocumentosRequest request, Emisor emisor);

    public CdrResponse consultarEstado(Emisor emisor, String tipoDocumento, String serie, String numero);
}
