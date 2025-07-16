package com.nextia.serviciofacturacione.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.nextia.serviciofacturacione.exception.UblGenerationException;

/**
 * Servicio para firmar documentos XML UBL según los requisitos de SUNAT
 */
@Service
public class XmlSignatureService {

    private static final String UTF_8 = StandardCharsets.UTF_8.name();

    @Value("${sunat.certificado.ruta}")
    private String certificadoRuta;

    @Value("${sunat.certificado.password}")
    private String certificadoPassword;

    @Value("${sunat.certificado.alias}")
    private String certificadoAlias;

    private final ResourceLoader resourceLoader;

    public XmlSignatureService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Firma un documento XML UBL
     * 
     * @param xmlContent Contenido XML a firmar
     * @return Documento XML firmado
     * @throws UblGenerationException si ocurre algún error durante la firma
     */
    public String signXml(String xmlContent) throws UblGenerationException {
        try {
            // Cargar el documento XML
            Document document = parseXml(xmlContent);
            
            // Buscar el elemento ExtensionContent donde se insertará la firma
            NodeList extensionContentList = document.getElementsByTagNameNS(
                    "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2", 
                    "ExtensionContent");
            
            if (extensionContentList.getLength() == 0) {
                throw new UblGenerationException("No se encontró el elemento ExtensionContent para insertar la firma");
            }
            
            Element extensionContent = (Element) extensionContentList.item(0);
            
            // Eliminar cualquier firma existente
            NodeList signatureList = extensionContent.getElementsByTagNameNS(
                    XMLSignature.XMLNS, "Signature");
            for (int i = 0; i < signatureList.getLength(); i++) {
                extensionContent.removeChild(signatureList.item(i));
            }
            
            // Cargar el certificado y la clave privada
            KeyStore keyStore = loadKeyStore();
            X509Certificate certificate = (X509Certificate) keyStore.getCertificate(certificadoAlias);
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(certificadoAlias, certificadoPassword.toCharArray());
            
            // Crear la fábrica de firma XML
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
            
            // Crear el contexto de firma
            DOMSignContext dsc = new DOMSignContext(privateKey, extensionContent);
            
            // Especificar el ID para el documento raíz (necesario para la referencia)
            Element rootElement = document.getDocumentElement();
            rootElement.setAttributeNS(null, "Id", "xmldsig-doc-root");
            dsc.setIdAttributeNS(rootElement, null, "Id");
            
            // Crear la referencia al documento
            Reference ref = createReference(fac, "#xmldsig-doc-root");
            
            // Crear SignedInfo
            SignedInfo si = createSignedInfo(fac, ref);
            
            // Crear KeyInfo con el certificado X509
            KeyInfo ki = createKeyInfo(fac, certificate);
            
            // Crear la firma
            XMLSignature signature = fac.newXMLSignature(si, ki);
            
            // Firmar el documento
            signature.sign(dsc);
            
            // Convertir el documento firmado a String
            return documentToString(document);
        } catch (Exception e) {
            throw new UblGenerationException("Error al firmar el documento XML: " + e.getMessage(), e);
        }
    }
    
    /**
     * Carga el almacén de claves (KeyStore) con el certificado digital
     * 
     * @return KeyStore cargado
     * @throws UblGenerationException si ocurre algún error al cargar el KeyStore
     */
    private KeyStore loadKeyStore() throws UblGenerationException {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            Resource resource = resourceLoader.getResource(certificadoRuta);
            try (InputStream is = resource.getInputStream()) {
                keyStore.load(is, certificadoPassword.toCharArray());
                return keyStore;
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            throw new UblGenerationException("Error al cargar el certificado digital: " + e.getMessage(), e);
        }
    }
    
    /**
     * Crea una referencia para la firma XML
     * 
     * @param fac Fábrica de firma XML
     * @param uri URI de la referencia (puede ser vacía o un ID con #)
     * @return Referencia configurada
     * @throws UblGenerationException si ocurre algún error al crear la referencia
     */
    private Reference createReference(XMLSignatureFactory fac, String uri) throws UblGenerationException {
        try {
            // Crear transformaciones
            List<Transform> transforms = new ArrayList<>();
            transforms.add(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
            transforms.add(fac.newTransform(CanonicalizationMethod.INCLUSIVE, (TransformParameterSpec) null));
            
            // Crear referencia con ID único
            String referenceId = "Reference-" + UUID.randomUUID().toString();
            return fac.newReference(
                    uri, // URI para referenciar el elemento a firmar
                    fac.newDigestMethod(DigestMethod.SHA256, null),
                    transforms,
                    null,
                    referenceId);
        } catch (Exception e) {
            throw new UblGenerationException("Error al crear la referencia para la firma: " + e.getMessage(), e);
        }
    }
    
    /**
     * Crea el elemento SignedInfo para la firma XML
     * 
     * @param fac Fábrica de firma XML
     * @param ref Referencia a incluir
     * @return SignedInfo configurado
     * @throws UblGenerationException si ocurre algún error al crear SignedInfo
     */
    private SignedInfo createSignedInfo(XMLSignatureFactory fac, Reference ref) throws UblGenerationException {
        try {
            return fac.newSignedInfo(
                    fac.newCanonicalizationMethod(
                            CanonicalizationMethod.INCLUSIVE,
                            (C14NMethodParameterSpec) null),
                    fac.newSignatureMethod(SignatureMethod.RSA_SHA256, null),
                    Collections.singletonList(ref));
        } catch (Exception e) {
            throw new UblGenerationException("Error al crear SignedInfo para la firma: " + e.getMessage(), e);
        }
    }
    
    /**
     * Crea el elemento KeyInfo con el certificado X509
     * 
     * @param fac Fábrica de firma XML
     * @param certificate Certificado X509
     * @return KeyInfo configurado
     */
    private KeyInfo createKeyInfo(XMLSignatureFactory fac, X509Certificate certificate) {
        KeyInfoFactory kif = fac.getKeyInfoFactory();
        X509Data x509Data = kif.newX509Data(Collections.singletonList(certificate));
        return kif.newKeyInfo(Collections.singletonList(x509Data));
    }
    
    /**
     * Parsea una cadena XML a un objeto Document
     * 
     * @param xmlContent Contenido XML
     * @return Document parseado
     * @throws UblGenerationException si ocurre algún error durante el parseo
     */
    private Document parseXml(String xmlContent) throws UblGenerationException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            
            // Deshabilitar el acceso a entidades externas para prevenir ataques XXE
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);
            
            // Configurar FEATURE_SECURE_PROCESSING
            dbf.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);
            
            // Configurar atributos de seguridad adicionales
            dbf.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD, "");
            dbf.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new UblGenerationException("Error al parsear el XML: " + e.getMessage(), e);
        }
    }
    
    /**
     * Convierte un documento XML a String
     * 
     * @param document Documento XML
     * @return String representación del documento XML
     * @throws TransformerException si ocurre algún error durante la transformación
     */
    private String documentToString(Document document) throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        
        // Deshabilitar el acceso a entidades externas para prevenir ataques XXE
        try {
            tf.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD, "");
            tf.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            tf.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (Exception e) {
            // Algunos atributos pueden no ser soportados en todas las implementaciones
        }
        
        Transformer transformer = tf.newTransformer();
        
        // Configurar la salida XML para que sea compatible con SUNAT
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, UTF_8);
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "no");
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.METHOD, "xml");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        try {
            return baos.toString(UTF_8);
        } catch (IOException e) {
            throw new TransformerException("Error al convertir el documento a String: " + e.getMessage(), e);
        }
    }
}
