package com.nextia.serviciofacturacione.service.common;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * Interfaz para el servicio de firma digital de documentos XML
 */
public interface XmlSignerService {
    
    /**
     * Firma digitalmente un documento XML
     * @param xml Contenido del XML a firmar
     * @param clavePrivada Clave privada para la firma
     * @param certificado Certificado digital X509
     * @return XML firmado
     */
    byte[] firmarXml(byte[] xml, PrivateKey clavePrivada, X509Certificate certificado);
    
    /**
     * Firma digitalmente un documento XML utilizando la configuración por defecto
     * @param xml Contenido del XML a firmar
     * @return XML firmado
     */
    byte[] firmarXml(byte[] xml);
    
    /**
     * Verifica la firma digital de un documento XML
     * @param xml Contenido del XML firmado
     * @param certificado Certificado digital X509 para verificar la firma
     * @return true si la firma es válida, false en caso contrario
     */
    boolean verificarFirma(byte[] xml, X509Certificate certificado);
}
