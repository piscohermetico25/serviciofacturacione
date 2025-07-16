package com.nextia.serviciofacturacione.util;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.nextia.serviciofacturacione.exception.UblGenerationException;
import com.nextia.serviciofacturacione.util.ubl.UblDocumentGenerator;
import com.nextia.serviciofacturacione.util.ubl.FacturaGenerator;

/**
 * Clase que orquesta la generación de documentos XML UBL para diferentes tipos de comprobantes
 */
@Component
public class UblGenerator {
    
    private final XmlSignatureService xmlSignatureService;
    
    public UblGenerator(XmlSignatureService xmlSignatureService) {
        this.xmlSignatureService = xmlSignatureService;
    }
    
    /**
     * Genera el XML para una factura
     * 
     * @param facturaData Datos de la factura
     * @return String con el XML generado
     * @throws UblGenerationException si ocurre algún error durante la generación
     */
    public String generateFacturaXml(Object facturaData) throws UblGenerationException {
        UblDocumentGenerator facturaGenerator = new FacturaGenerator();
        try {
            Document document = createDocument();
            
            // Delegar la generación del documento a la clase especializada
            facturaGenerator.generate(document, facturaData);
            
            // Convertir el documento a String
            String xmlSinFirma = documentToString(document);
            
            // Firmar el documento XML
            return xmlSignatureService.signXml(xmlSinFirma);
        } catch (Exception e) {
            throw new UblGenerationException("Error al generar XML de factura", e);
        }
    }
    
    /**
     * Genera el XML para una boleta
     * 
     * @param boletaData Datos de la boleta
     * @return String con el XML generado
     * @throws UblGenerationException si ocurre algún error durante la generación
     */
    public String generateBoletaXml(Object boletaData) throws UblGenerationException {
        // Esta funcionalidad se implementará cuando se cree el generador de boletas
        throw new UnsupportedOperationException("Generación de boleta no implementada aún");
    }
    
    /**
     * Genera el XML para una nota de crédito
     * 
     * @param notaCreditoData Datos de la nota de crédito
     * @return String con el XML generado
     * @throws UblGenerationException si ocurre algún error durante la generación
     */
    public String generateNotaCreditoXml(Object notaCreditoData) throws UblGenerationException {
        // Esta funcionalidad se implementará cuando se cree el generador de notas de crédito
        throw new UnsupportedOperationException("Generación de nota de crédito no implementada aún");
    }
    
    /**
     * Genera el XML para una nota de débito
     * 
     * @param notaDebitoData Datos de la nota de débito
     * @return String con el XML generado
     * @throws UblGenerationException si ocurre algún error durante la generación
     */
    public String generateNotaDebitoXml(Object notaDebitoData) throws UblGenerationException {
        // Esta funcionalidad se implementará cuando se cree el generador de notas de débito
        throw new UnsupportedOperationException("Generación de nota de débito no implementada aún");
    }
    
    /**
     * Crea un documento XML seguro con protección contra ataques XXE
     * 
     * @return Documento XML
     * @throws UblGenerationException si ocurre algún error durante la creación
     */
    protected Document createDocument() throws UblGenerationException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            
            // Deshabilitar el acceso a entidades externas para prevenir ataques XXE
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);
            
            // Configurar FEATURE_SECURE_PROCESSING
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            
            // Configurar atributos de seguridad adicionales
            dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            
            DocumentBuilder db = dbf.newDocumentBuilder();
            
            // Establecer un EntityResolver restrictivo que rechaza todas las entidades externas
            db.setEntityResolver((publicId, systemId) -> new InputSource(new StringReader(""))); 
            
            return db.newDocument();
        } catch (ParserConfigurationException e) {
            throw new UblGenerationException("Error al crear documento XML", e);
        }
    }
    
    /**
     * Convierte un documento XML a String con protección contra ataques XXE
     * 
     * @param doc Documento XML
     * @return String representación del documento XML
     * @throws TransformerException si ocurre algún error durante la transformación
     */
    private String documentToString(Document doc) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        
        // Deshabilitar el acceso a entidades externas para prevenir ataques XXE
        try {
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            // Configurar seguridad adicional para prevenir ataques XXE
            transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (Exception e) {
            // Algunos atributos pueden no ser soportados en todas las implementaciones
            // pero intentamos aplicar la máxima seguridad posible
        }
        
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        
        return writer.toString();
    }
}