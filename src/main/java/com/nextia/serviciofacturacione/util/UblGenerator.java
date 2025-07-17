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

import com.nextia.serviciofacturacione.dto.BajaDocumentosRequest;
import com.nextia.serviciofacturacione.dto.ResumenDocumentosRequest;
import com.nextia.serviciofacturacione.exception.UblGenerationException;
import com.nextia.serviciofacturacione.model.common.Cliente;
import com.nextia.serviciofacturacione.model.common.Comprobante;
import com.nextia.serviciofacturacione.model.common.Detalle;
import com.nextia.serviciofacturacione.model.common.Emisor;
import com.nextia.serviciofacturacione.util.ubl.UblDocumentGenerator;
import com.nextia.serviciofacturacione.util.ubl.BajaDocumentosGenerator;
import com.nextia.serviciofacturacione.util.ubl.FacturaGenerator;
import com.nextia.serviciofacturacione.util.ubl.ResumenDocumentosGenerator;

import java.util.Map;
import java.util.List;

/**
 * Clase que orquesta la generación de documentos XML UBL para diferentes tipos de comprobantes
 */
@Component
public class UblGenerator {
    
    
    

    public String generateFacturaXml(String nombreXml, Emisor emisor, Cliente cliente, Comprobante comprobante, List<Detalle> detalle) throws UblGenerationException {
        try {
            
  
            FacturaGenerator facturaGenerator = new FacturaGenerator();
            String xml = facturaGenerator.crearXMLFactura(nombreXml, emisor, cliente, comprobante, detalle);
            // Devuelve el path del archivo generado
            return xml;
        } catch (Exception e) {
            throw new UblGenerationException("Error al generar XML de factura", e);
        }
    }
    
    public String generateResumenDocumentosXml(String nombreXml, Emisor emisor, ResumenDocumentosRequest resumenDocumentosRequest) throws UblGenerationException {
        try {
            
            ResumenDocumentosGenerator resumenDocumentosGenerator = new ResumenDocumentosGenerator();
            String xml = resumenDocumentosGenerator.crearXMLResumenDocumentos(nombreXml,emisor, resumenDocumentosRequest.getCabecera(), resumenDocumentosRequest.getDetalle());
            // Devuelve el path del archivo generado
            return xml;
        } catch (Exception e) {
            throw new UblGenerationException("Error al generar XML de resumen de documentos", e);
        }
    }
    
    public String generateBajaDocumentosXml(String nombreXml, Emisor emisor, BajaDocumentosRequest bajaDocumentosRequest) throws UblGenerationException {
        try {
            
            BajaDocumentosGenerator bajaDocumentosGenerator = new BajaDocumentosGenerator();
            String xml = bajaDocumentosGenerator.crearXMLBajaDocumentos(emisor, bajaDocumentosRequest.getCabecera(), bajaDocumentosRequest.getDetalle(), nombreXml);
            // Devuelve el path del archivo generado
            return xml;
        } catch (Exception e) {
            throw new UblGenerationException("Error al generar XML de baja de documentos", e);
        }
    }
    
}