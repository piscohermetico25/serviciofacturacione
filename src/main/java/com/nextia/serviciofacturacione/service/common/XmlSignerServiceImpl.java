package com.nextia.serviciofacturacione.service.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.nextia.serviciofacturacione.util.CertificadoUtil;

import javax.xml.XMLConstants;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.nextia.serviciofacturacione.exception.FacturacionException;

/**
 * Implementación del servicio para firma digital de documentos XML según especificaciones SUNAT.
 */
@Service
public class XmlSignerServiceImpl implements XmlSignerService {

    private static final Logger log = LoggerFactory.getLogger(XmlSignerServiceImpl.class);

    // Constantes para los namespaces y nombres de elementos XML
    private static final String NAMESPACE_UBL_EXTENSIONS = "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2";
    private static final String ELEMENTO_UBL_EXTENSIONS = "ext:UBLExtensions";
    private static final String ELEMENTO_UBL_EXTENSION = "ext:UBLExtension";
    private static final String ELEMENTO_EXTENSION_CONTENT = "ext:ExtensionContent";
    
    // Constantes para características de seguridad XML
    private static final String FEATURE_DISALLOW_DOCTYPE = "http://apache.org/xml/features/disallow-doctype-decl";
    private static final String FEATURE_EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    private static final String FEATURE_EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";

    private final String rutaCertificado;
    private final String aliasCertificado;
    private final String passwordCertificado;
    private final ResourceLoader resourceLoader;

    /**
     * Constructor con inyección de dependencias
     */
    public XmlSignerServiceImpl(
            @Value("${sunat.certificado.ruta}") String rutaCertificado,
            @Value("${sunat.certificado.alias}") String aliasCertificado,
            @Value("${sunat.certificado.password}") String passwordCertificado,
            ResourceLoader resourceLoader) {
        this.rutaCertificado = rutaCertificado;
        this.aliasCertificado = aliasCertificado;
        this.passwordCertificado = passwordCertificado;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public byte[] firmarXml(byte[] xml, PrivateKey clavePrivada, X509Certificate certificado) {
        try {
            log.info("Iniciando proceso de firma digital de XML según estándar SUNAT");

            // Crear el documento DOM desde el XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true); // Importante para XPath y firma XML
            
            // Configurar seguridad para prevenir XXE
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            dbf.setFeature(FEATURE_DISALLOW_DOCTYPE, true);
            dbf.setFeature(FEATURE_EXTERNAL_GENERAL_ENTITIES, false);
            dbf.setFeature(FEATURE_EXTERNAL_PARAMETER_ENTITIES, false);
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);
            dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            
            log.debug("DocumentBuilderFactory configurado con opciones de seguridad para prevenir XXE");
            Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xml));

            // Buscar o crear el nodo UBLExtensions donde se colocará la firma
            Element rootElement = doc.getDocumentElement();
            log.info("Elemento raíz del documento: {}", rootElement.getNodeName());

            // Buscar o crear el nodo UBLExtensions donde se colocará la firma
            Element extensionsElement = null;
            Element extensionContentElement = null;

            // Buscar el elemento UBLExtensions existente
            NodeList extensionsList = rootElement.getElementsByTagName(ELEMENTO_UBL_EXTENSIONS);
            if (extensionsList.getLength() > 0) {
                extensionsElement = (Element) extensionsList.item(0);
                log.info("Encontrado elemento {} existente", ELEMENTO_UBL_EXTENSIONS);
                extensionContentElement = buscarOCrearExtensionContent(doc, extensionsElement);
            } else {
                // Crear estructura completa si no existe
                extensionsElement = doc.createElementNS(NAMESPACE_UBL_EXTENSIONS, ELEMENTO_UBL_EXTENSIONS);
                Element extensionElement = doc.createElementNS(NAMESPACE_UBL_EXTENSIONS, ELEMENTO_UBL_EXTENSION);
                extensionContentElement = doc.createElementNS(NAMESPACE_UBL_EXTENSIONS, ELEMENTO_EXTENSION_CONTENT);

                extensionElement.appendChild(extensionContentElement);
                extensionsElement.appendChild(extensionElement);

                // Insertar al inicio del documento (como primer hijo del elemento raíz)
                if (rootElement.getFirstChild() != null) {
                    rootElement.insertBefore(extensionsElement, rootElement.getFirstChild());
                } else {
                    rootElement.appendChild(extensionsElement);
                }
                log.info("Creada estructura completa {} para firma", ELEMENTO_UBL_EXTENSIONS);
            }

            // Verificar que el extensionContentElement no sea nulo
            if (extensionContentElement == null) {
                throw new FacturacionException("No se pudo encontrar o crear el elemento ext:ExtensionContent para la firma");
            }

            // Crear el contexto de firma en el ExtensionContent
            DOMSignContext dsc = new DOMSignContext(clavePrivada, extensionContentElement);

            // Establecer el prefijo de la firma
            dsc.setDefaultNamespacePrefix("ds");

            // Crear la fábrica de firmas XML
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

            // Crear la referencia al documento completo (elemento raíz)
            // Según SUNAT: Se debe firmar todo el documento completo, todo el contenido del elemento raíz
            // NOTA: SHA1 es requerido por SUNAT aunque sea considerado débil en la actualidad
            Reference ref = fac.newReference(
                    "", // Referencia vacía para firmar todo el documento
                    fac.newDigestMethod(DigestMethod.SHA1, null),
                    Collections.singletonList(
                            fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
                    null,
                    null);

            // Crear el método de canonicalización
            // NOTA: Se usa canonicalización inclusiva según requerimientos de SUNAT
            CanonicalizationMethod cm = fac.newCanonicalizationMethod(
                    CanonicalizationMethod.INCLUSIVE,
                    (C14NMethodParameterSpec) null);

            // Crear el método de firma
            SignatureMethod sm = fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null);

            // Crear SignedInfo
            SignedInfo si = fac.newSignedInfo(cm, sm, Collections.singletonList(ref));

            // Crear KeyInfo con el certificado
            // Según SUNAT: Se debe incluir el certificado completo en la firma
            KeyInfoFactory kif = fac.getKeyInfoFactory();
            List<Object> x509Content = new ArrayList<>();
            // Incluir el DN del certificado
            x509Content.add(certificado.getSubjectX500Principal().getName());
            // Incluir el certificado completo
            x509Content.add(certificado);
            X509Data xd = kif.newX509Data(x509Content);
            KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));

            // Crear y firmar la firma
            XMLSignature signature = fac.newXMLSignature(si, ki);
            signature.sign(dsc);

            // Convertir el documento firmado a bytes
            TransformerFactory tf = TransformerFactory.newInstance();
            // Configurar seguridad para prevenir XXE
            configurarSeguridadTransformer(tf);

            Transformer trans = tf.newTransformer();

            // Mantener la codificación original del documento
            trans.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            trans.setOutputProperty(OutputKeys.INDENT, "no");

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            trans.transform(new DOMSource(doc), new StreamResult(bos));

            log.info("XML firmado correctamente según estándar SUNAT");
            return bos.toByteArray();

        } catch (ParserConfigurationException e) {
            log.error("Error en la configuración del parser XML: {}", e.getMessage(), e);
            throw new FacturacionException("Error en la configuración del parser XML: " + e.getMessage(), e);
        } catch (SAXException e) {
            log.error("Error al parsear el XML: {}", e.getMessage(), e);
            throw new FacturacionException("Error al parsear el XML: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("Error de E/S al procesar el XML: {}", e.getMessage(), e);
            throw new FacturacionException("Error de E/S al procesar el XML: " + e.getMessage(), e);
        } catch (MarshalException | XMLSignatureException e) {
            log.error("Error en la firma XML: {}", e.getMessage(), e);
            throw new FacturacionException("Error en la firma XML: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error inesperado al firmar XML: {}", e.getMessage(), e);
            throw new FacturacionException("Error inesperado al firmar XML: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca o crea un elemento ExtensionContent vacío para la firma dentro de UBLExtensions
     * 
     * @param doc Documento XML
     * @param extensionsElement Elemento UBLExtensions
     * @return Elemento ExtensionContent donde se colocará la firma
     */
    private Element buscarOCrearExtensionContent(Document doc, Element extensionsElement) {
        // Buscar el elemento UBLExtension para la firma
        NodeList extensionNodeList = extensionsElement.getElementsByTagName(ELEMENTO_UBL_EXTENSION);
        
        // Verificar si ya existe un UBLExtension con ExtensionContent vacío
        for (int i = 0; i < extensionNodeList.getLength(); i++) {
            Element extensionElement = (Element) extensionNodeList.item(i);
            NodeList extensionContentList = extensionElement.getElementsByTagName(ELEMENTO_EXTENSION_CONTENT);
            
            if (extensionContentList.getLength() > 0) {
                Element contentElement = (Element) extensionContentList.item(0);
                // Si el ExtensionContent está vacío, lo usamos para la firma
                if (!contentElement.hasChildNodes()) {
                    log.info("Encontrado elemento {} vacío para firma", ELEMENTO_EXTENSION_CONTENT);
                    return contentElement;
                }
            }
        }
        
        // Si no se encontró un UBLExtension con ExtensionContent vacío, creamos uno nuevo
        Element newExtensionElement = doc.createElementNS(NAMESPACE_UBL_EXTENSIONS, ELEMENTO_UBL_EXTENSION);
        Element extensionContentElement = doc.createElementNS(NAMESPACE_UBL_EXTENSIONS, ELEMENTO_EXTENSION_CONTENT);
        newExtensionElement.appendChild(extensionContentElement);
        extensionsElement.appendChild(newExtensionElement);
        log.info("Creado nuevo elemento {} con {} para firma", ELEMENTO_UBL_EXTENSION, ELEMENTO_EXTENSION_CONTENT);
        
        return extensionContentElement;
    }
    
    /**
     * Carga un KeyStore desde una ruta de archivo
     * 
     * @param rutaArchivo Ruta del archivo de certificado
     * @param password Contraseña del certificado
     * @return KeyStore cargado
     * @throws Exception Si ocurre algún error al cargar el certificado
     */
    private KeyStore cargarKeyStore(String rutaArchivo, char[] password) throws Exception {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        
        if (rutaArchivo.startsWith("classpath:")) {
            // Cargar desde el classpath usando ResourceLoader
            Resource resource = resourceLoader.getResource(rutaArchivo);
            log.info("¿Recurso existe? {}", resource.exists());
            if (!resource.exists()) {
                throw new FacturacionException("El certificado digital no existe en la ruta: " + rutaArchivo);
            }
            try (InputStream is = resource.getInputStream()) {
                ks.load(is, password);
            }
        } else {
            // Cargar desde una ruta absoluta del sistema de archivos
            File file = new File(rutaArchivo);
            log.info("¿Archivo existe? {}", file.exists());
            if (!file.exists()) {
                throw new FacturacionException("El certificado digital no existe en la ruta: " + rutaArchivo);
            }
            try (FileInputStream fis = new FileInputStream(rutaArchivo)) {
                ks.load(fis, password);
            }
        }
        
        return ks;
    }
    

    @Override
    public byte[] firmarXml(byte[] xml) {
        try {
            log.info("Firmando XML con certificado por defecto");
            
            log.info("Intentando cargar certificado desde: {}", rutaCertificado);
            log.info("Usando alias: {}", aliasCertificado);
            // Usar formato condicional para evitar operaciones costosas si el nivel de log no está habilitado
            if (log.isInfoEnabled()) {
                log.info("Usando password: {}", passwordCertificado.substring(0, Math.min(2, passwordCertificado.length())) + "***");
            }
            
            // Cargar el certificado y la clave privada desde el keystore
            KeyStore ks = cargarKeyStore(rutaCertificado, passwordCertificado.toCharArray());
            
            // Verificar el certificado y obtener la clave privada y el certificado usando la clase utilitaria
            Object[] resultado = CertificadoUtil.verificarCertificado(ks, aliasCertificado, passwordCertificado.toCharArray());
            PrivateKey privateKey = (PrivateKey) resultado[0];
            X509Certificate cert = (X509Certificate) resultado[1];
            
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
            
            // Configurar seguridad para prevenir XXE
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            dbf.setFeature(FEATURE_DISALLOW_DOCTYPE, true);
            dbf.setFeature(FEATURE_EXTERNAL_GENERAL_ENTITIES, false);
            dbf.setFeature(FEATURE_EXTERNAL_PARAMETER_ENTITIES, false);
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);
            dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            
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
            // Configurar validación segura
            valContext.setProperty("org.jcp.xml.dsig.secureValidation", Boolean.TRUE);
            
            XMLSignature signature = fac.unmarshalXMLSignature(valContext);
            
            boolean valid = signature.validate(valContext);
            log.info("Resultado de verificación de firma: {}", valid ? "válida" : "inválida");
            
            return valid;
        } catch (ParserConfigurationException e) {
            log.error("Error en la configuración del parser XML: {}", e.getMessage(), e);
            throw new FacturacionException("Error en la configuración del parser XML: " + e.getMessage(), e);
        } catch (SAXException e) {
            log.error("Error al parsear el XML: {}", e.getMessage(), e);
            throw new FacturacionException("Error al parsear el XML: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("Error de E/S al procesar el XML: {}", e.getMessage(), e);
            throw new FacturacionException("Error de E/S al procesar el XML: " + e.getMessage(), e);
        } catch (MarshalException | XMLSignatureException e) {
            log.error("Error en la firma XML: {}", e.getMessage(), e);
            throw new FacturacionException("Error en la firma XML: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error inesperado al verificar firma XML: {}", e.getMessage(), e);
            throw new FacturacionException("Error inesperado al verificar firma XML: " + e.getMessage(), e);
        }
    }
    
    /**
     * Configura las opciones de seguridad para el TransformerFactory
     * para prevenir vulnerabilidades XXE (XML External Entity)
     * 
     * @param tf TransformerFactory a configurar
     * @throws FacturacionException si ocurre un error crítico al configurar la seguridad
     */
    private void configurarSeguridadTransformer(TransformerFactory tf) {
        // Deshabilitar acceso a entidades externas
        try {
            // Prevenir ataques XXE deshabilitando acceso a DTDs externas
            tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            // Prevenir acceso a hojas de estilo externas
            tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            // Habilitar procesamiento seguro según OWASP
            tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            
            log.debug("Configuración de seguridad aplicada correctamente al TransformerFactory");
        } catch (IllegalArgumentException e) {
            // Algunos TransformerFactory no soportan estos atributos pero no es crítico
            log.warn("No se pudo configurar atributo de seguridad para TransformerFactory: {}", e.getMessage());
        } catch (TransformerConfigurationException e) {
            // Error crítico al configurar características de seguridad
            log.error("Error crítico al configurar seguridad para TransformerFactory: {}", e.getMessage(), e);
            throw new FacturacionException("Error al configurar seguridad XML: " + e.getMessage(), e);
        }
    }
    
}
