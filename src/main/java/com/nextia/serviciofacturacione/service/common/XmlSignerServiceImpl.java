package com.nextia.serviciofacturacione.service.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import com.nextia.serviciofacturacione.exception.FacturacionException;

import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementación del servicio para la firma digital de documentos XML
 */
@Service
public class XmlSignerServiceImpl implements XmlSignerService {

    private static final Logger log = LoggerFactory.getLogger(XmlSignerServiceImpl.class);
    
    @Value("${sunat.certificado.ruta}")
    private String rutaCertificado;
    
    @Value("${sunat.certificado.password}")
    private String passwordCertificado;
    
    @Value("${sunat.certificado.alias}")
    private String aliasCertificado;
    
    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public byte[] firmarXml(byte[] xml, PrivateKey clavePrivada, X509Certificate certificado) {
        try {
            log.info("Iniciando proceso de firma digital de XML");
            
            // Crear el documento DOM desde el XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            
            // Deshabilitar DTD para prevenir XXE
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            
            Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xml));
            
            // Buscar o crear el nodo UBLExtensions donde se colocará la firma
            Element rootElement = doc.getDocumentElement();
            Element extensionsElement = null;
            
            NodeList extensionsList = rootElement.getElementsByTagName("ext:UBLExtensions");
            if (extensionsList.getLength() > 0) {
                extensionsElement = (Element) extensionsList.item(0);
            } else {
                extensionsElement = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2", "ext:UBLExtensions");
                if (rootElement.getFirstChild() != null) {
                    rootElement.insertBefore(extensionsElement, rootElement.getFirstChild());
                } else {
                    rootElement.appendChild(extensionsElement);
                }
            }
            
            // Crear el contexto de firma
            DOMSignContext dsc = new DOMSignContext(clavePrivada, extensionsElement);
            
            // Crear la fábrica de firmas XML
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
            
            // Crear la referencia al documento
            Reference ref = fac.newReference(
                    "",
                    fac.newDigestMethod(DigestMethod.SHA1, null),
                    Collections.singletonList(
                            fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
                    null,
                    null);
            
            // Crear el método de canonicalización
            CanonicalizationMethod cm = fac.newCanonicalizationMethod(
                    CanonicalizationMethod.INCLUSIVE,
                    (C14NMethodParameterSpec) null);
            
            // Crear el método de firma
            SignatureMethod sm = fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null);
            
            // Crear SignedInfo
            SignedInfo si = fac.newSignedInfo(cm, sm, Collections.singletonList(ref));
            
            // Crear KeyInfo con el certificado
            KeyInfoFactory kif = fac.getKeyInfoFactory();
            List<Object> x509Content = new ArrayList<>();
            x509Content.add(certificado.getSubjectX500Principal().getName());
            x509Content.add(certificado);
            X509Data xd = kif.newX509Data(x509Content);
            KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));
            
            // Crear y firmar la firma
            XMLSignature signature = fac.newXMLSignature(si, ki);
            signature.sign(dsc);
            
            // Convertir el documento firmado a bytes
            TransformerFactory tf = TransformerFactory.newInstance();
            
            // Configurar seguridad para TransformerFactory
            configurarSeguridadTransformer(tf);
            
            Transformer trans = tf.newTransformer();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            trans.transform(new DOMSource(doc), new StreamResult(bos));
            
            log.info("XML firmado correctamente");
            return bos.toByteArray();
            
        } catch (Exception e) {
            log.error("Error al firmar XML-01: {}", e.getMessage(), e);
            throw new FacturacionException("Error al firmar XML-01: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] firmarXml(byte[] xml) {
        try {
            log.info("Firmando XML con certificado por defecto");
            
            // Cargar el certificado y la clave privada desde el keystore
            KeyStore ks = KeyStore.getInstance("PKCS12");
            
            if (rutaCertificado.startsWith("classpath:")) {
                // Cargar desde el classpath usando ResourceLoader
                Resource resource = resourceLoader.getResource(rutaCertificado);
                try (InputStream is = resource.getInputStream()) {
                    ks.load(is, passwordCertificado.toCharArray());
                }
            } else {
                // Cargar desde una ruta absoluta del sistema de archivos
                try (FileInputStream fis = new FileInputStream(rutaCertificado)) {
                    ks.load(fis, passwordCertificado.toCharArray());
                }
            }
            
            PrivateKey privateKey = (PrivateKey) ks.getKey(aliasCertificado, passwordCertificado.toCharArray());
            X509Certificate cert = (X509Certificate) ks.getCertificate(aliasCertificado);
            
            return firmarXml(xml, privateKey, cert);
        } catch (Exception e) {
            log.error("Error al cargar certificado o firmar XML: {}", e.getMessage(), e);
            throw new FacturacionException("Error al firmar XML con certificado por defecto: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean verificarFirma(byte[] xml, X509Certificate certificado) {
        try {
            log.info("Verificando firma digital de XML");
            
            // Crear el documento DOM desde el XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            
            // Deshabilitar DTD para prevenir XXE
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            
            Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xml));
            
            // Buscar el nodo de firma
            NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
            if (nl.getLength() == 0) {
                log.warn("No se encontró firma en el documento XML");
                return false;
            }
            
            // Crear la fábrica de firmas XML
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
            
            // Verificar la firma
            DOMValidateContext valContext = new DOMValidateContext(certificado.getPublicKey(), nl.item(0));
            XMLSignature signature = fac.unmarshalXMLSignature(valContext);
            
            boolean valid = signature.validate(valContext);
            log.info("Resultado de verificación de firma: {}", valid ? "válida" : "inválida");
            
            return valid;
        } catch (Exception e) {
            log.error("Error al verificar firma XML: {}", e.getMessage(), e);
            throw new FacturacionException("Error al verificar firma XML: " + e.getMessage(), e);
        }
    }
    
    /**
     * Configura la seguridad del TransformerFactory para prevenir ataques XXE
     * 
     * @param tf TransformerFactory a configurar
     */
    private void configurarSeguridadTransformer(TransformerFactory tf) {
        try {
            tf.setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
            tf.setAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet", "");
        } catch (IllegalArgumentException e) {
            // Algunos TransformerFactory no soportan estos atributos
            log.warn("No se pudo configurar seguridad en TransformerFactory: {}", e.getMessage());
        }
    }
}
