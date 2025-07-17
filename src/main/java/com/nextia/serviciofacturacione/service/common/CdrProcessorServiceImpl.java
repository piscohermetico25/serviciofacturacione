package com.nextia.serviciofacturacione.service.common;

import com.nextia.serviciofacturacione.model.CdrResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Implementación del servicio para procesamiento de respuestas CDR de SUNAT
 */
@Service
public class CdrProcessorServiceImpl implements CdrProcessorService {

    private static final Logger log = LoggerFactory.getLogger(CdrProcessorServiceImpl.class);
    private static final String XPATH_CODIGO = "//cbc:ResponseCode";
    private static final String XPATH_DESCRIPCION = "//cbc:Description";
    private static final String XPATH_NOTAS = "//cbc:Note";


    @Override
    public byte[] unzipCDR(byte[] zipBytes) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
            ZipInputStream zis = new ZipInputStream(bais)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".xml")) {
                    return zis.readAllBytes();
                }
            }
        }
        return null;
    }

    public CdrResponse obtenerRespuestaSUNAT(byte[] xmlBytes) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xmlBytes));

        NodeList responseCode = doc.getElementsByTagName("cbc:ResponseCode");
        NodeList description = doc.getElementsByTagName("cbc:Description");

        String codigo = responseCode.item(0).getTextContent();
        String mensaje = description.item(0).getTextContent();

        return new CdrResponse(codigo, mensaje);
    }


    @Override
    public CdrResponse procesarZip(byte[] contenidoZip) {
        try {

            byte[] xmlBytes = unzipCDR(contenidoZip);
            CdrResponse cdrResponse = obtenerRespuestaSUNAT(xmlBytes);
            cdrResponse.setArchivoCdr(contenidoZip);
            
            return cdrResponse;
        } catch (Exception e) {
            log.error("Error al procesar ZIP de CDR", e);
            return new CdrResponse("9999", "Error al procesar ZIP de CDR: " + e.getMessage());
        }
    }

    @Override
    public CdrResponse procesarXml(byte[] contenidoXml) {
        try {
            log.info("Procesando XML de respuesta CDR");
            
            // Extraer información del XML
            String codigo = extraerCodigoRespuesta(contenidoXml);
            String descripcion = extraerDescripcionRespuesta(contenidoXml);
            List<String> notas = extraerNotas(contenidoXml);
            
            // Crear respuesta
            CdrResponse respuesta = new CdrResponse(codigo, descripcion);
            for (String nota : notas) {
                respuesta.addNota(nota);
            }
            
            log.info("XML de CDR procesado. Código: {}, Descripción: {}", codigo, descripcion);
            return respuesta;
        } catch (Exception e) {
            log.error("Error al procesar XML de CDR", e);
            return new CdrResponse("9999", "Error al procesar XML de CDR: " + e.getMessage());
        }
    }

    @Override
    public String extraerCodigoRespuesta(byte[] contenidoXml) {
        try {
            Document doc = parseXml(contenidoXml);
            XPath xpath = XPathFactory.newInstance().newXPath();
            
            NodeList nodes = (NodeList) xpath.evaluate(XPATH_CODIGO, doc, XPathConstants.NODESET);
            if (nodes.getLength() > 0) {
                return nodes.item(0).getTextContent();
            }
            
            return "9999";
        } catch (Exception e) {
            log.error("Error al extraer código de respuesta", e);
            return "9999";
        }
    }

    @Override
    public String extraerDescripcionRespuesta(byte[] contenidoXml) {
        try {
            Document doc = parseXml(contenidoXml);
            XPath xpath = XPathFactory.newInstance().newXPath();
            
            NodeList nodes = (NodeList) xpath.evaluate(XPATH_DESCRIPCION, doc, XPathConstants.NODESET);
            if (nodes.getLength() > 0) {
                return nodes.item(0).getTextContent();
            }
            
            return "No se pudo extraer la descripción de la respuesta";
        } catch (Exception e) {
            log.error("Error al extraer descripción de respuesta", e);
            return "Error al extraer descripción: " + e.getMessage();
        }
    }

    @Override
    public List<String> extraerNotas(byte[] contenidoXml) {
        List<String> notas = new ArrayList<>();
        
        try {
            Document doc = parseXml(contenidoXml);
            XPath xpath = XPathFactory.newInstance().newXPath();
            
            NodeList nodes = (NodeList) xpath.evaluate(XPATH_NOTAS, doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                notas.add(nodes.item(i).getTextContent());
            }
        } catch (Exception e) {
            log.error("Error al extraer notas de respuesta", e);
        }
        
        return notas;
    }
    
    /**
     * Parsea un XML a un objeto Document
     */
    private Document parseXml(byte[] contenidoXml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        
        // Deshabilitar DTD para prevenir XXE
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        
        return dbf.newDocumentBuilder().parse(new ByteArrayInputStream(contenidoXml));
    }
}
