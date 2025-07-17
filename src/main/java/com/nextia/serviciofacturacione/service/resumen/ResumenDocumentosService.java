package com.nextia.serviciofacturacione.service.resumen;

import com.nextia.serviciofacturacione.dto.ResumenDocumentosRequest;
import com.nextia.serviciofacturacione.model.CdrResponse;
import com.nextia.serviciofacturacione.model.common.Emisor;

public interface ResumenDocumentosService {

    byte[] generarXml(String nombreArchivo, Emisor emisor, ResumenDocumentosRequest request);
    public CdrResponse enviarResumenDocumentos(ResumenDocumentosRequest request, Emisor emisor);

    public CdrResponse consultarEstado(Emisor emisor, String tipoDocumento, String serie, String numero);
}
