package com.nextia.serviciofacturacione.service.common;

import com.nextia.serviciofacturacione.dto.BajaDocumentosRequest;
import com.nextia.serviciofacturacione.dto.ComprobanteRequest;
import com.nextia.serviciofacturacione.dto.ResumenDocumentosRequest;
import com.nextia.serviciofacturacione.model.common.Emisor;

public interface GeneracionXmlService {

    public byte[] generarXml(String tipoDocumento, String nombreArchivo, Emisor emisor, ComprobanteRequest comprobanteRequest);
    public byte[] generarXml(String nombreArchivo, Emisor emisor, ResumenDocumentosRequest comprobanteRequest);
    public byte[] generarXml(String nombreArchivo, Emisor emisor, BajaDocumentosRequest comprobanteRequest);
    
}
