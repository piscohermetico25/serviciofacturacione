package com.nextia.serviciofacturacione.util;

import com.nextia.serviciofacturacione.model.Boleta;
import com.nextia.serviciofacturacione.model.Factura;
import com.nextia.serviciofacturacione.model.NotaCredito;
import com.nextia.serviciofacturacione.model.NotaDebito;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * Generador de documentos XML en formato UBL 2.1 para SUNAT
 * Esta clase se encarga de generar los XML para facturas, boletas, notas de crédito y débito
 */
@Component
public class UblGenerator {

    private static final Logger log = LoggerFactory.getLogger(UblGenerator.class);
    
    private static final String UBL_VERSION = "2.1";
    private static final String CUSTOMIZATION_ID = "2.0";
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Genera el XML UBL 2.1 para una factura electrónica
     * 
     * @param factura Datos de la factura
     * @return String con el contenido XML
     */
    public String generateFacturaXml(Factura factura) {
        try {
            log.info("Generando XML UBL para factura: {}-{}", factura.getSerie(), factura.getCorrelativo());
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            
            // Crear elemento raíz Invoice
            Element rootElement = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "Invoice");
            doc.appendChild(rootElement);
            
            // Agregar namespaces
            addNamespaces(rootElement);
            
            // Agregar elementos UBL comunes
            addUblVersionElement(doc, rootElement);
            addCustomizationIDElement(doc, rootElement);
            
            // Agregar elementos específicos de factura
            addInvoiceIDElement(doc, rootElement, factura.getSerie() + "-" + factura.getCorrelativo());
            addIssueDateElement(doc, rootElement, factura.getFechaEmision());
            addIssueTimeElement(doc, rootElement, factura.getHoraEmision());
            addDocumentCurrencyCodeElement(doc, rootElement, factura.getMoneda());
            
            // Agregar información del emisor
            addSupplierPartyElement(doc, rootElement, factura.getEmisor());
            
            // Agregar información del cliente
            addCustomerPartyElement(doc, rootElement, factura.getReceptor());
            
            // Agregar detalle de ítems
            addInvoiceLinesElement(doc, rootElement, factura);
            
            // Agregar totales
            addTaxTotalElement(doc, rootElement, factura);
            addLegalMonetaryTotalElement(doc, rootElement, factura);
            
            // Convertir a String
            return documentToString(doc);
            
        } catch (Exception e) {
            log.error("Error al generar XML UBL para factura", e);
            throw new RuntimeException("Error al generar XML UBL para factura: " + e.getMessage(), e);
        }
    }
    
    /**
     * Genera el XML UBL 2.1 para una boleta electrónica
     * 
     * @param boleta Datos de la boleta
     * @return String con el contenido XML
     */
    public String generateBoletaXml(Boleta boleta) {
        try {
            log.info("Generando XML UBL para boleta: {}-{}", boleta.getSerie(), boleta.getCorrelativo());
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            
            // Crear elemento raíz Invoice (mismo elemento que factura, pero con serie B)
            Element rootElement = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "Invoice");
            doc.appendChild(rootElement);
            
            // Agregar namespaces
            addNamespaces(rootElement);
            
            // Agregar elementos UBL comunes
            addUblVersionElement(doc, rootElement);
            addCustomizationIDElement(doc, rootElement);
            
            // Agregar elementos específicos de boleta
            addInvoiceIDElement(doc, rootElement, boleta.getSerie() + "-" + boleta.getCorrelativo());
            addIssueDateElement(doc, rootElement, boleta.getFechaEmision());
            addIssueTimeElement(doc, rootElement, boleta.getHoraEmision());
            addDocumentCurrencyCodeElement(doc, rootElement, boleta.getMoneda());
            
            // Agregar información del emisor
            addSupplierPartyElement(doc, rootElement, boleta.getEmisor());
            
            // Agregar información del cliente
            addCustomerPartyElement(doc, rootElement, boleta.getReceptor());
            
            // Agregar detalle de ítems
            addInvoiceLinesElement(doc, rootElement, boleta);
            
            // Agregar totales
            addTaxTotalElement(doc, rootElement, boleta);
            addLegalMonetaryTotalElement(doc, rootElement, boleta);
            
            // Convertir a String
            return documentToString(doc);
            
        } catch (Exception e) {
            log.error("Error al generar XML UBL para boleta", e);
            throw new RuntimeException("Error al generar XML UBL para boleta: " + e.getMessage(), e);
        }
    }
    
    /**
     * Genera el XML UBL 2.1 para una nota de crédito electrónica
     * 
     * @param notaCredito Datos de la nota de crédito
     * @return String con el contenido XML
     */
    public String generateNotaCreditoXml(NotaCredito notaCredito) {
        try {
            log.info("Generando XML UBL para nota de crédito: {}-{}", notaCredito.getSerie(), notaCredito.getCorrelativo());
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            
            // Crear elemento raíz CreditNote
            Element rootElement = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2", "CreditNote");
            doc.appendChild(rootElement);
            
            // Agregar namespaces
            addNamespaces(rootElement);
            
            // Agregar elementos UBL comunes
            addUblVersionElement(doc, rootElement);
            addCustomizationIDElement(doc, rootElement);
            
            // Agregar elementos específicos de nota de crédito
            addCreditNoteIDElement(doc, rootElement, notaCredito.getSerie() + "-" + notaCredito.getCorrelativo());
            addIssueDateElement(doc, rootElement, notaCredito.getFechaEmision());
            addIssueTimeElement(doc, rootElement, notaCredito.getHoraEmision());
            addDocumentCurrencyCodeElement(doc, rootElement, notaCredito.getMoneda());
            
            // Agregar información del documento de referencia
            addBillingReferenceElement(doc, rootElement, notaCredito);
            
            // Agregar información del motivo
            addDiscrepancyResponseElement(doc, rootElement, notaCredito);
            
            // Agregar información del emisor
            addSupplierPartyElement(doc, rootElement, notaCredito.getEmisor());
            
            // Agregar información del cliente
            addCustomerPartyElement(doc, rootElement, notaCredito.getReceptor());
            
            // Agregar detalle de ítems
            addCreditNoteLineElement(doc, rootElement, notaCredito);
            
            // Agregar totales
            addTaxTotalElement(doc, rootElement, notaCredito);
            addLegalMonetaryTotalElement(doc, rootElement, notaCredito);
            
            // Convertir a String
            return documentToString(doc);
            
        } catch (Exception e) {
            log.error("Error al generar XML UBL para nota de crédito", e);
            throw new RuntimeException("Error al generar XML UBL para nota de crédito: " + e.getMessage(), e);
        }
    }
    
    /**
     * Genera el XML UBL 2.1 para una nota de débito electrónica
     * 
     * @param notaDebito Datos de la nota de débito
     * @return String con el contenido XML
     */
    public String generateNotaDebitoXml(NotaDebito notaDebito) {
        try {
            log.info("Generando XML UBL para nota de débito: {}-{}", notaDebito.getSerie(), notaDebito.getCorrelativo());
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            
            // Crear elemento raíz DebitNote
            Element rootElement = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:DebitNote-2", "DebitNote");
            doc.appendChild(rootElement);
            
            // Agregar namespaces
            addNamespaces(rootElement);
            
            // Agregar elementos UBL comunes
            addUblVersionElement(doc, rootElement);
            addCustomizationIDElement(doc, rootElement);
            
            // Agregar elementos específicos de nota de débito
            addDebitNoteIDElement(doc, rootElement, notaDebito.getSerie() + "-" + notaDebito.getCorrelativo());
            addIssueDateElement(doc, rootElement, notaDebito.getFechaEmision());
            addIssueTimeElement(doc, rootElement, notaDebito.getHoraEmision());
            addDocumentCurrencyCodeElement(doc, rootElement, notaDebito.getMoneda());
            
            // Agregar información del documento de referencia
            addBillingReferenceElement(doc, rootElement, notaDebito);
            
            // Agregar información del motivo
            addDiscrepancyResponseElement(doc, rootElement, notaDebito);
            
            // Agregar información del emisor
            addSupplierPartyElement(doc, rootElement, notaDebito.getEmisor());
            
            // Agregar información del cliente
            addCustomerPartyElement(doc, rootElement, notaDebito.getReceptor());
            
            // Agregar detalle de ítems
            addDebitNoteLineElement(doc, rootElement, notaDebito);
            
            // Agregar totales
            addTaxTotalElement(doc, rootElement, notaDebito);
            addLegalMonetaryTotalElement(doc, rootElement, notaDebito);
            
            // Convertir a String
            return documentToString(doc);
            
        } catch (Exception e) {
            log.error("Error al generar XML UBL para nota de débito", e);
            throw new RuntimeException("Error al generar XML UBL para nota de débito: " + e.getMessage(), e);
        }
    }
    
    // Métodos auxiliares para la generación de elementos XML
    
    private void addNamespaces(Element rootElement) {
        rootElement.setAttribute("xmlns:cac", "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
        rootElement.setAttribute("xmlns:cbc", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");
        rootElement.setAttribute("xmlns:ccts", "urn:un:unece:uncefact:documentation:2");
        rootElement.setAttribute("xmlns:ds", "http://www.w3.org/2000/09/xmldsig#");
        rootElement.setAttribute("xmlns:ext", "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2");
        rootElement.setAttribute("xmlns:qdt", "urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2");
        rootElement.setAttribute("xmlns:udt", "urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2");
        rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
    }
    
    private void addUblVersionElement(Document doc, Element parent) {
        Element ublVersionID = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", "cbc:UBLVersionID");
        ublVersionID.setTextContent(UBL_VERSION);
        parent.appendChild(ublVersionID);
    }
    
    private void addCustomizationIDElement(Document doc, Element parent) {
        Element customizationID = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", "cbc:CustomizationID");
        customizationID.setTextContent(CUSTOMIZATION_ID);
        parent.appendChild(customizationID);
    }
    
    private void addInvoiceIDElement(Document doc, Element parent, String id) {
        Element invoiceID = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", "cbc:ID");
        invoiceID.setTextContent(id);
        parent.appendChild(invoiceID);
    }
    
    private void addCreditNoteIDElement(Document doc, Element parent, String id) {
        Element creditNoteID = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", "cbc:ID");
        creditNoteID.setTextContent(id);
        parent.appendChild(creditNoteID);
    }
    
    private void addDebitNoteIDElement(Document doc, Element parent, String id) {
        Element debitNoteID = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", "cbc:ID");
        debitNoteID.setTextContent(id);
        parent.appendChild(debitNoteID);
    }
    
    private void addIssueDateElement(Document doc, Element parent, String date) {
        Element issueDate = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", "cbc:IssueDate");
        issueDate.setTextContent(date);
        parent.appendChild(issueDate);
    }
    
    private void addIssueTimeElement(Document doc, Element parent, String time) {
        Element issueTime = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", "cbc:IssueTime");
        issueTime.setTextContent(time);
        parent.appendChild(issueTime);
    }
    
    private void addDocumentCurrencyCodeElement(Document doc, Element parent, String currencyCode) {
        Element documentCurrencyCode = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", "cbc:DocumentCurrencyCode");
        documentCurrencyCode.setTextContent(currencyCode);
        parent.appendChild(documentCurrencyCode);
    }
    
    @Value("${emisor.tipoDocumento}")
    private String emisorTipoDocumento;
    
    @Value("${emisor.ruc}")
    private String emisorRuc;
    
    @Value("${emisor.razonSocial}")
    private String emisorRazonSocial;
    
    @Value("${emisor.nombreComercial}")
    private String emisorNombreComercial;
    
    @Value("${emisor.direccion}")
    private String emisorDireccion;
    
    @Value("${emisor.pais}")
    private String emisorPais;
    
    @Value("${emisor.departamento}")
    private String emisorDepartamento;
    
    @Value("${emisor.provincia}")
    private String emisorProvincia;
    
    @Value("${emisor.distrito}")
    private String emisorDistrito;
    
    @Value("${emisor.ubigeo}")
    private String emisorUbigeo;
    
    private void addSupplierPartyElement(Document doc, Element parent, Object emisor) {
        Element accountingSupplierParty = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:AccountingSupplierParty");
        parent.appendChild(accountingSupplierParty);
        
        // Party
        Element party = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:Party");
        accountingSupplierParty.appendChild(party);
        
        // PartyIdentification
        Element partyIdentification = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:PartyIdentification");
        party.appendChild(partyIdentification);
        
        Element id = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", "cbc:ID");
        id.setAttribute("schemeID", emisorTipoDocumento);
        id.setTextContent(emisorRuc);
        partyIdentification.appendChild(id);
        
        // PartyName
        Element partyName = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:PartyName");
        party.appendChild(partyName);
        
        Element name = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", "cbc:Name");
        name.setTextContent(emisorNombreComercial);
        partyName.appendChild(name);
        
        // PartyLegalEntity
        Element partyLegalEntity = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:PartyLegalEntity");
        party.appendChild(partyLegalEntity);
        
        Element registrationName = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", "cbc:RegistrationName");
        registrationName.setTextContent(emisorRazonSocial);
        partyLegalEntity.appendChild(registrationName);
        
        // RegistrationAddress
        Element registrationAddress = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:RegistrationAddress");
        partyLegalEntity.appendChild(registrationAddress);
        
        Element addressId = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", "cbc:ID");
        addressId.setTextContent(emisorUbigeo);
        registrationAddress.appendChild(addressId);
        
        Element addressTypeCode = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", "cbc:AddressTypeCode");
        addressTypeCode.setTextContent("0000");
        registrationAddress.appendChild(addressTypeCode);
        
        Element cityName = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", "cbc:CityName");
        cityName.setTextContent(emisorProvincia);
        registrationAddress.appendChild(cityName);
        
        Element countrySubentity = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", "cbc:CountrySubentity");
        countrySubentity.setTextContent(emisorDepartamento);
        registrationAddress.appendChild(countrySubentity);
        
        Element district = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", "cbc:District");
        district.setTextContent(emisorDistrito);
        registrationAddress.appendChild(district);
        
        Element addressLine = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:AddressLine");
        registrationAddress.appendChild(addressLine);
        
        Element line = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", "cbc:Line");
        line.setTextContent(emisorDireccion);
        addressLine.appendChild(line);
        
        Element country = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:Country");
        registrationAddress.appendChild(country);
        
        Element identificationCode = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", "cbc:IdentificationCode");
        identificationCode.setTextContent(emisorPais);
        country.appendChild(identificationCode);
    }
    
    private void addCustomerPartyElement(Document doc, Element parent, Object receptor) {
        // Implementación simplificada - en un caso real se extraerían los datos del receptor
        Element accountingCustomerParty = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:AccountingCustomerParty");
        parent.appendChild(accountingCustomerParty);
        
        // Aquí se agregarían todos los elementos del receptor según UBL 2.1
        // Por simplicidad, solo se muestra la estructura básica
    }
    
    private void addBillingReferenceElement(Document doc, Element parent, Object documento) {
        // Implementación simplificada - en un caso real se extraerían los datos del documento de referencia
        Element billingReference = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:BillingReference");
        parent.appendChild(billingReference);
        
        // Aquí se agregarían todos los elementos de la referencia según UBL 2.1
        // Por simplicidad, solo se muestra la estructura básica
    }
    
    private void addDiscrepancyResponseElement(Document doc, Element parent, Object documento) {
        // Implementación simplificada - en un caso real se extraerían los datos del motivo
        Element discrepancyResponse = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:DiscrepancyResponse");
        parent.appendChild(discrepancyResponse);
        
        // Aquí se agregarían todos los elementos del motivo según UBL 2.1
        // Por simplicidad, solo se muestra la estructura básica
    }
    
    private void addInvoiceLinesElement(Document doc, Element parent, Object documento) {
        // Implementación simplificada - en un caso real se iteraría sobre los ítems
        Element invoiceLine = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:InvoiceLine");
        parent.appendChild(invoiceLine);
        
        // Aquí se agregarían todos los elementos de cada ítem según UBL 2.1
        // Por simplicidad, solo se muestra la estructura básica
    }
    
    private void addCreditNoteLineElement(Document doc, Element parent, Object documento) {
        // Implementación simplificada - en un caso real se iteraría sobre los ítems
        Element creditNoteLine = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:CreditNoteLine");
        parent.appendChild(creditNoteLine);
        
        // Aquí se agregarían todos los elementos de cada ítem según UBL 2.1
        // Por simplicidad, solo se muestra la estructura básica
    }
    
    private void addDebitNoteLineElement(Document doc, Element parent, Object documento) {
        // Implementación simplificada - en un caso real se iteraría sobre los ítems
        Element debitNoteLine = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:DebitNoteLine");
        parent.appendChild(debitNoteLine);
        
        // Aquí se agregarían todos los elementos de cada ítem según UBL 2.1
        // Por simplicidad, solo se muestra la estructura básica
    }
    
    private void addTaxTotalElement(Document doc, Element parent, Object documento) {
        // Implementación simplificada - en un caso real se calcularían los impuestos
        Element taxTotal = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:TaxTotal");
        parent.appendChild(taxTotal);
        
        // Aquí se agregarían todos los elementos de impuestos según UBL 2.1
        // Por simplicidad, solo se muestra la estructura básica
    }
    
    private void addLegalMonetaryTotalElement(Document doc, Element parent, Object documento) {
        // Implementación simplificada - en un caso real se calcularían los totales
        Element legalMonetaryTotal = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:LegalMonetaryTotal");
        parent.appendChild(legalMonetaryTotal);
        
        // Aquí se agregarían todos los elementos de totales según UBL 2.1
        // Por simplicidad, solo se muestra la estructura básica
    }
    
    private String documentToString(Document doc) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            log.error("Error al convertir documento XML a String", e);
            throw new RuntimeException("Error al convertir documento XML a String: " + e.getMessage(), e);
        }
    }
}
