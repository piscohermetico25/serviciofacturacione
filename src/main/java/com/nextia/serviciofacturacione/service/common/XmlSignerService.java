package com.nextia.serviciofacturacione.service.common;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * Interfaz para el servicio de firma digital de documentos XML
 */
public interface XmlSignerService {
    

    byte[] firmarXml(byte[] xml, PrivateKey clavePrivada, X509Certificate certificado);
    

    byte[] firmarXml(byte[] xml);
    

    boolean verificarFirma(byte[] xml, X509Certificate certificado);
}
