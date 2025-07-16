package com.nextia.serviciofacturacione.util.ubl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.nextia.serviciofacturacione.exception.UblGenerationException;
import com.nextia.serviciofacturacione.model.FacturaData;
import com.nextia.serviciofacturacione.model.FacturaData.FacturaLineaData;

/**
 * Clase especializada en la generación de documentos XML UBL para facturas
 */
public class FacturaGenerator implements UblDocumentGenerator {

    // Namespaces utilizados en el documento XML
    private static final String NS_CAC = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2";
    private static final String NS_CBC = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2";
    private static final String NS_EXT = "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2";
    private static final String NS_INVOICE = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2";
    
    // Constantes para elementos XML comunes
    private static final String ELEMENT_ID = "cbc:ID";
    private static final String ATTR_CURRENCY_ID = "currencyID";

    /**
     * Genera el documento XML para una factura
     * 
     * @param document Documento XML base
     * @param data Datos de la factura
     * @throws UblGenerationException si ocurre algún error durante la generación
     */
    public void generate(Document document, Object data) throws UblGenerationException {
        try {
            FacturaData facturaData;
            if (data instanceof FacturaData facturaDataCast) {
                facturaData = facturaDataCast;
            } else {
                // Si no se proporciona un objeto FacturaData, usamos datos de ejemplo
                facturaData = crearFacturaEjemplo();
            }
            
            // Crear el elemento raíz Invoice con los namespaces correspondientes
            Element invoiceElement = document.createElementNS(NS_INVOICE, "Invoice");
            document.appendChild(invoiceElement);
            
            // Agregar los namespaces al elemento raíz
            invoiceElement.setAttribute("xmlns:cac", NS_CAC);
            invoiceElement.setAttribute("xmlns:cbc", NS_CBC);
            invoiceElement.setAttribute("xmlns:ext", NS_EXT);
            invoiceElement.setAttribute("xmlns", NS_INVOICE);
            
            // Generar las secciones del documento
            generateUBLExtensions(document, invoiceElement);
            generateDocumentHeader(document, invoiceElement, facturaData);
            generateSupplierInfo(document, invoiceElement, facturaData);
            generateCustomerInfo(document, invoiceElement, facturaData);
            generateMonetaryTotal(document, invoiceElement, facturaData);
            generateInvoiceLines(document, invoiceElement, facturaData);
        } catch (Exception e) {
            throw new UblGenerationException("Error al generar documento de factura", e);
        }
    }
    
    /**
     * Crea una factura de ejemplo para pruebas
     * 
     * @return FacturaData con datos de ejemplo
     */
    private FacturaData crearFacturaEjemplo() {
        FacturaData factura = new FacturaData();
        factura.setNumeroFactura("F001-00000001");
        factura.setFechaEmision(java.time.LocalDate.now());
        factura.setMoneda("PEN");
        factura.setRucEmisor("10413425722");
        factura.setRazonSocialEmisor("EMPRESA DE PRUEBA S.A.C.");
        factura.setTipoDocumentoEmisor("6");
        factura.setRucReceptor("20987654321");
        factura.setRazonSocialReceptor("CLIENTE CORPORATIVO S.A.");
        factura.setTipoDocumentoReceptor("6");
        factura.setMontoTotal("4500.00");
        
        // Primera línea
        FacturaLineaData linea1 = new FacturaLineaData();
        linea1.setId("1");
        linea1.setCantidad("2.00");
        linea1.setMontoLinea("4000.00");
        linea1.setPrecioUnitario("1694.92");
        linea1.setDescripcion("Laptop HP Pavilion");
        linea1.setMontoImpuesto("610.17");
        linea1.setTipoPrecio("01");
        factura.addLinea(linea1);
        
        // Segunda línea
        FacturaLineaData linea2 = new FacturaLineaData();
        linea2.setId("2");
        linea2.setCantidad("1.00");
        linea2.setMontoLinea("500.00");
        linea2.setPrecioUnitario("423.73");
        linea2.setDescripcion("Servicio de instalación de software");
        linea2.setMontoImpuesto("76.27");
        linea2.setTipoPrecio("01");
        factura.addLinea(linea2);
        
        return factura;
    }
    
    /**
     * Genera la sección UBLExtensions que contiene la firma digital
     * 
     * @param document Documento XML
     * @param invoiceElement Elemento raíz Invoice
     */
    private void generateUBLExtensions(Document document, Element invoiceElement) {
        Element extUBLExtensions = document.createElementNS(NS_EXT, "ext:UBLExtensions");
        invoiceElement.appendChild(extUBLExtensions);
        
        Element extUBLExtension = document.createElementNS(NS_EXT, "ext:UBLExtension");
        extUBLExtensions.appendChild(extUBLExtension);
        
        Element extExtensionContent = document.createElementNS(NS_EXT, "ext:ExtensionContent");
        extUBLExtension.appendChild(extExtensionContent);
        
        // No agregamos la firma aquí, solo preparamos la estructura
        // La firma será agregada por XmlSignatureService después de generar el documento
    }
    
    /**
     * Genera la cabecera del documento
     * 
     * @param document Documento XML
     * @param invoiceElement Elemento raíz de la factura
     * @param facturaData Datos de la factura
     * @throws UblGenerationException si ocurre algún error durante la generación
     */
    private void generateDocumentHeader(Document document, Element invoiceElement, FacturaData facturaData) throws UblGenerationException {
        try {
            // UBLVersionID
            Element ublVersionID = document.createElementNS(NS_CBC, "cbc:UBLVersionID");
            ublVersionID.setTextContent("2.1");
            invoiceElement.appendChild(ublVersionID);
            
            // CustomizationID
            Element customizationID = document.createElementNS(NS_CBC, "cbc:CustomizationID");
            customizationID.setTextContent("2.0");
            invoiceElement.appendChild(customizationID);
            
            // ID (número de factura)
            Element id = document.createElementNS(NS_CBC, ELEMENT_ID);
            id.setTextContent(facturaData.getNumeroFactura());
            invoiceElement.appendChild(id);
            
            // IssueDate (fecha de emisión)
            Element issueDate = document.createElementNS(NS_CBC, "cbc:IssueDate");
            LocalDate fechaEmision = facturaData.getFechaEmision();
            issueDate.setTextContent(fechaEmision.format(DateTimeFormatter.ISO_DATE));
            invoiceElement.appendChild(issueDate);
            
            // DocumentCurrencyCode (moneda)
            Element documentCurrencyCode = document.createElementNS(NS_CBC, "cbc:DocumentCurrencyCode");
            documentCurrencyCode.setTextContent(facturaData.getMoneda());
            invoiceElement.appendChild(documentCurrencyCode);
        } catch (Exception e) {
            throw new UblGenerationException("Error al generar cabecera del documento", e);
        }
    }
    
    /**
     * Genera la información del proveedor (emisor de la factura)
     * 
     * @param document Documento XML
     * @param invoiceElement Elemento raíz Invoice
     * @param facturaData Datos de la factura
     * @throws UblGenerationException si ocurre algún error durante la generación
     */
    private void generateSupplierInfo(Document document, Element invoiceElement, FacturaData facturaData) throws UblGenerationException {
        try {
            Element accountingSupplierParty = document.createElementNS(NS_CAC, "cac:AccountingSupplierParty");
            invoiceElement.appendChild(accountingSupplierParty);
            
            Element party = document.createElementNS(NS_CAC, "cac:Party");
            accountingSupplierParty.appendChild(party);
            
            // PartyIdentification (RUC del emisor)
            Element partyIdentification = document.createElementNS(NS_CAC, "cac:PartyIdentification");
            party.appendChild(partyIdentification);
            
            Element partyID = document.createElementNS(NS_CBC, ELEMENT_ID);
            partyID.setAttribute("schemeID", facturaData.getTipoDocumentoEmisor()); // 6 = RUC
            partyID.setTextContent(facturaData.getRucEmisor());
            partyIdentification.appendChild(partyID);
            
            // PartyName (Nombre comercial)
            Element partyName = document.createElementNS(NS_CAC, "cac:PartyName");
            party.appendChild(partyName);
            
            Element name = document.createElementNS(NS_CBC, "cbc:Name");
            name.setTextContent(facturaData.getRazonSocialEmisor());
            partyName.appendChild(name);
            
            // PartyLegalEntity (Razón social)
            Element partyLegalEntity = document.createElementNS(NS_CAC, "cac:PartyLegalEntity");
            party.appendChild(partyLegalEntity);
            
            Element registrationName = document.createElementNS(NS_CBC, "cbc:RegistrationName");
            registrationName.setTextContent(facturaData.getRazonSocialEmisor());
            partyLegalEntity.appendChild(registrationName);
        } catch (Exception e) {
            throw new UblGenerationException("Error al generar información del proveedor", e);
        }
    }
    
    /**
     * Genera la información del cliente (receptor de la factura)
     * 
     * @param document Documento XML
     * @param invoiceElement Elemento raíz Invoice
     * @param facturaData Datos de la factura
     * @throws UblGenerationException si ocurre algún error durante la generación
     */
    private void generateCustomerInfo(Document document, Element invoiceElement, FacturaData facturaData) throws UblGenerationException {
        try {
            Element accountingCustomerParty = document.createElementNS(NS_CAC, "cac:AccountingCustomerParty");
            invoiceElement.appendChild(accountingCustomerParty);
            
            Element party = document.createElementNS(NS_CAC, "cac:Party");
            accountingCustomerParty.appendChild(party);
            
            // PartyIdentification (RUC del receptor)
            Element partyIdentification = document.createElementNS(NS_CAC, "cac:PartyIdentification");
            party.appendChild(partyIdentification);
            
            Element partyID = document.createElementNS(NS_CBC, ELEMENT_ID);
            partyID.setAttribute("schemeID", facturaData.getTipoDocumentoReceptor()); // 6 = RUC
            partyID.setTextContent(facturaData.getRucReceptor());
            partyIdentification.appendChild(partyID);
            
            // PartyLegalEntity (Razón social del receptor)
            Element partyLegalEntity = document.createElementNS(NS_CAC, "cac:PartyLegalEntity");
            party.appendChild(partyLegalEntity);
            
            Element registrationName = document.createElementNS(NS_CBC, "cbc:RegistrationName");
            registrationName.setTextContent(facturaData.getRazonSocialReceptor());
            partyLegalEntity.appendChild(registrationName);
        } catch (Exception e) {
            throw new UblGenerationException("Error al generar información del cliente", e);
        }
    }
    
    /**
     * Genera la sección de totales monetarios
     * 
     * @param document Documento XML
     * @param invoiceElement Elemento raíz Invoice
     * @param facturaData Datos de la factura
     * @throws UblGenerationException si ocurre algún error durante la generación
     */
    private void generateMonetaryTotal(Document document, Element invoiceElement, FacturaData facturaData) throws UblGenerationException {
        try {
            Element legalMonetaryTotal = document.createElementNS(NS_CAC, "cac:LegalMonetaryTotal");
            invoiceElement.appendChild(legalMonetaryTotal);
            
            Element payableAmount = document.createElementNS(NS_CBC, "cbc:PayableAmount");
            payableAmount.setAttribute(ATTR_CURRENCY_ID, facturaData.getMoneda());
            payableAmount.setTextContent(facturaData.getMontoTotal());
            legalMonetaryTotal.appendChild(payableAmount);
        } catch (Exception e) {
            throw new UblGenerationException("Error al generar totales monetarios", e);
        }
    }
    
    /**
     * Genera las líneas de la factura
     * 
     * @param document Documento XML
     * @param invoiceElement Elemento raíz Invoice
     * @param facturaData Datos de la factura
     * @throws UblGenerationException si ocurre algún error durante la generación
     */
    private void generateInvoiceLines(Document document, Element invoiceElement, FacturaData facturaData) throws UblGenerationException {
        try {
            // Generar cada línea de factura
            for (FacturaLineaData lineData : facturaData.getLineas()) {
                generateInvoiceLine(document, invoiceElement, lineData, facturaData.getMoneda());
            }
        } catch (Exception e) {
            throw new UblGenerationException("Error al generar líneas de factura", e);
        }
    }
    
    /**
     * Genera una línea de la factura
     * 
     * @param document Documento XML
     * @param invoiceElement Elemento raíz Invoice
     * @param lineData Datos de la línea
     * @param moneda Moneda de la factura
     * @throws UblGenerationException si ocurre algún error durante la generación
     */
    private void generateInvoiceLine(Document document, Element invoiceElement, FacturaLineaData lineData, String moneda) throws UblGenerationException {
        try {
            Element invoiceLine = document.createElementNS(NS_CAC, "cac:InvoiceLine");
            invoiceElement.appendChild(invoiceLine);
            
            // ID de línea
            Element lineID = document.createElementNS(NS_CBC, ELEMENT_ID);
            lineID.setTextContent(lineData.getId());
            invoiceLine.appendChild(lineID);
            
            // Cantidad
            Element invoicedQuantity = document.createElementNS(NS_CBC, "cbc:InvoicedQuantity");
            invoicedQuantity.setAttribute("unitCode", "NIU");
            invoicedQuantity.setTextContent(lineData.getCantidad());
            invoiceLine.appendChild(invoicedQuantity);
            
            // Monto total de línea
            Element lineExtensionAmount = document.createElementNS(NS_CBC, "cbc:LineExtensionAmount");
            lineExtensionAmount.setAttribute(ATTR_CURRENCY_ID, moneda);
            lineExtensionAmount.setTextContent(lineData.getMontoLinea());
            invoiceLine.appendChild(lineExtensionAmount);
            
            // Referencia de precio
            Element pricingReference = document.createElementNS(NS_CAC, "cac:PricingReference");
            invoiceLine.appendChild(pricingReference);
            Element alternativeConditionPrice = document.createElementNS(NS_CAC, "cac:AlternativeConditionPrice");
            pricingReference.appendChild(alternativeConditionPrice);
            
            // Precio unitario con impuestos
            Element priceAmount = document.createElementNS(NS_CBC, "cbc:PriceAmount");
            priceAmount.setAttribute(ATTR_CURRENCY_ID, moneda);
            priceAmount.setTextContent(lineData.getPrecioUnitario());
            alternativeConditionPrice.appendChild(priceAmount);
            
            // Tipo de precio
            Element priceTypeCode = document.createElementNS(NS_CBC, "cbc:PriceTypeCode");
            priceTypeCode.setTextContent(lineData.getTipoPrecio());
            alternativeConditionPrice.appendChild(priceTypeCode);
            
            // Total de impuestos
            Element taxTotal = document.createElementNS(NS_CAC, "cac:TaxTotal");
            invoiceLine.appendChild(taxTotal);
            
            // Monto total de impuesto para la línea
            Element taxTotalAmount = document.createElementNS(NS_CBC, "cbc:TaxAmount");
            taxTotalAmount.setAttribute(ATTR_CURRENCY_ID, moneda);
            taxTotalAmount.setTextContent(lineData.getMontoImpuesto());
            taxTotal.appendChild(taxTotalAmount);
            
            Element taxSubtotal = document.createElementNS(NS_CAC, "cac:TaxSubtotal");
            taxTotal.appendChild(taxSubtotal);
            
            // Monto de impuesto en el subtotal
            Element taxAmount = document.createElementNS(NS_CBC, "cbc:TaxAmount");
            taxAmount.setAttribute(ATTR_CURRENCY_ID, moneda);
            taxAmount.setTextContent(lineData.getMontoImpuesto());
            taxSubtotal.appendChild(taxAmount);
            
            // Información del ítem
            Element item = document.createElementNS(NS_CAC, "cac:Item");
            invoiceLine.appendChild(item);
            
            // Descripción del ítem
            Element description = document.createElementNS(NS_CBC, "cbc:Description");
            description.setTextContent(lineData.getDescripcion());
            item.appendChild(description);
            
            // Precio unitario sin impuestos
            Element price = document.createElementNS(NS_CAC, "cac:Price");
            invoiceLine.appendChild(price);
            
            Element unitPriceAmount = document.createElementNS(NS_CBC, "cbc:PriceAmount");
            unitPriceAmount.setAttribute(ATTR_CURRENCY_ID, moneda);
            unitPriceAmount.setTextContent(lineData.getPrecioUnitario());
            price.appendChild(unitPriceAmount);
        } catch (Exception e) {
            throw new UblGenerationException("Error al generar línea de factura", e);
        }
    }
}
