package com.nextia.serviciofacturacione.util.ubl;

import org.w3c.dom.*;

import com.nextia.serviciofacturacione.model.common.Cliente;
import com.nextia.serviciofacturacione.model.common.Comprobante;
import com.nextia.serviciofacturacione.model.common.Detalle;
import com.nextia.serviciofacturacione.model.common.Emisor;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.StringWriter;
import java.util.List;


public class FacturaGenerator {
    /**
     * Genera un XML UBL tipo Invoice siguiendo la lógica PHP proporcionada.
     * @param nombreXml Nombre del archivo de salida (sin extensión)
     * @param emisor Datos del emisor (Map<String, String>)
     * @param cliente Datos del cliente (Map<String, String>)
     * @param comprobante Datos del comprobante (Map<String, Object>)
     * @param detalle Lista de items (List<Map<String, Object>>)
     * @throws Exception Si ocurre un error en la generación del XML
     */

     String UbLVersion="2.1";
     String CustomizationID="2.0";

    public String crearXMLFactura(String nombreXml, Emisor emisor, Cliente cliente, Comprobante comprobante, List<Detalle> detalle) throws Exception {
        // Configuración segura para evitar XXE
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);

        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        // Elemento raíz Invoice
        Element invoice = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "Invoice");
        invoice.setAttribute("xmlns:cac", "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
        invoice.setAttribute("xmlns:cbc", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");
        invoice.setAttribute("xmlns:ds", "http://www.w3.org/2000/09/xmldsig#");
        invoice.setAttribute("xmlns:ext", "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2");
        doc.appendChild(invoice);

        // UBLExtensions
        Element extUBLExtensions = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2", "ext:UBLExtensions");
        Element extUBLExtension = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2", "ext:UBLExtension");
        Element extExtensionContent = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2", "ext:ExtensionContent");
        extUBLExtension.appendChild(extExtensionContent);
        extUBLExtensions.appendChild(extUBLExtension);
        invoice.appendChild(extUBLExtensions);

        // UBLVersionID, CustomizationID, ID, IssueDate, IssueTime, DueDate, InvoiceTypeCode, Note, DocumentCurrencyCode
        appendTextElement(doc, invoice, "cbc:UBLVersionID", UbLVersion);
        appendTextElement(doc, invoice, "cbc:CustomizationID", CustomizationID);
        appendTextElement(doc, invoice, "cbc:ID", comprobante.getSerie() + "-" + comprobante.getCorrelativo());
        appendTextElement(doc, invoice, "cbc:IssueDate", comprobante.getFechaEmision().toString());
        appendTextElement(doc, invoice, "cbc:IssueTime", "00:00:00");
        appendTextElement(doc, invoice, "cbc:DueDate", comprobante.getFechaEmision().toString());
        Element invoiceTypeCode = doc.createElement("cbc:InvoiceTypeCode");
        invoiceTypeCode.setAttribute("listID", "0101");
        invoiceTypeCode.setTextContent(comprobante.getTipoDoc());
        invoice.appendChild(invoiceTypeCode);
        Element note = doc.createElement("cbc:Note");
        note.setAttribute("languageLocaleID", "1000");
        note.appendChild(doc.createCDATASection(comprobante.getTotalTexto()));
        invoice.appendChild(note);
        appendTextElement(doc, invoice, "cbc:DocumentCurrencyCode", comprobante.getMoneda());

        // Firma digital (Signature)
        Element signature = doc.createElement("cac:Signature");
        appendTextElement(doc, signature, "cbc:ID", emisor.getRuc());
        Element sigNote = doc.createElement("cbc:Note");
        sigNote.appendChild(doc.createCDATASection(emisor.getNombreComercial()));
        signature.appendChild(sigNote);
        Element signatoryParty = doc.createElement("cac:SignatoryParty");
        Element partyId = doc.createElement("cac:PartyIdentification");
        appendTextElement(doc, partyId, "cbc:ID", emisor.getRuc());
        signatoryParty.appendChild(partyId);
        Element partyName = doc.createElement("cac:PartyName");
        Element name = doc.createElement("cbc:Name");
        name.appendChild(doc.createCDATASection(emisor.getRazonSocial()));
        partyName.appendChild(name);
        signatoryParty.appendChild(partyName);
        signature.appendChild(signatoryParty);
        Element digitalSignatureAttachment = doc.createElement("cac:DigitalSignatureAttachment");
        Element externalReference = doc.createElement("cac:ExternalReference");
        appendTextElement(doc, externalReference, "cbc:URI", "#SIGN-EMPRESA");
        digitalSignatureAttachment.appendChild(externalReference);
        signature.appendChild(digitalSignatureAttachment);
        invoice.appendChild(signature);

        // AccountingSupplierParty
        Element supplierParty = doc.createElement("cac:AccountingSupplierParty");
        Element supplierPartyParty = doc.createElement("cac:Party");
        Element supplierPartyId = doc.createElement("cac:PartyIdentification");
        Element supplierId = doc.createElement("cbc:ID");
        supplierId.setAttribute("schemeID", emisor.getTipoDoc());
        supplierId.setTextContent(emisor.getRuc());
        supplierPartyId.appendChild(supplierId);
        supplierPartyParty.appendChild(supplierPartyId);
        Element supplierPartyName = doc.createElement("cac:PartyName");
        Element supplierName = doc.createElement("cbc:Name");
        supplierName.appendChild(doc.createCDATASection(emisor.getNombreComercial()));
        supplierPartyName.appendChild(supplierName);
        supplierPartyParty.appendChild(supplierPartyName);
        Element supplierLegalEntity = doc.createElement("cac:PartyLegalEntity");
        Element supplierRegName = doc.createElement("cbc:RegistrationName");
        supplierRegName.appendChild(doc.createCDATASection(emisor.getRazonSocial()));
        supplierLegalEntity.appendChild(supplierRegName);
        Element supplierRegAddress = doc.createElement("cac:RegistrationAddress");
        appendTextElement(doc, supplierRegAddress, "cbc:ID", emisor.getUbigeo());
        appendTextElement(doc, supplierRegAddress, "cbc:AddressTypeCode", "0000");
        appendTextElement(doc, supplierRegAddress, "cbc:CitySubdivisionName", "NONE");
        appendTextElement(doc, supplierRegAddress, "cbc:CityName", emisor.getProvincia());
        appendTextElement(doc, supplierRegAddress, "cbc:CountrySubentity", emisor.getDepartamento());
        appendTextElement(doc, supplierRegAddress, "cbc:District", emisor.getDistrito());
        Element supplierAddressLine = doc.createElement("cac:AddressLine");
        Element supplierLine = doc.createElement("cbc:Line");
        supplierLine.appendChild(doc.createCDATASection(emisor.getDireccion()));
        supplierAddressLine.appendChild(supplierLine);
        supplierRegAddress.appendChild(supplierAddressLine);
        Element supplierCountry = doc.createElement("cac:Country");
        appendTextElement(doc, supplierCountry, "cbc:IdentificationCode", emisor.getPais());
        supplierRegAddress.appendChild(supplierCountry);
        supplierLegalEntity.appendChild(supplierRegAddress);
        supplierPartyParty.appendChild(supplierLegalEntity);
        supplierParty.appendChild(supplierPartyParty);
        invoice.appendChild(supplierParty);

        // AccountingCustomerParty
        Element customerParty = doc.createElement("cac:AccountingCustomerParty");
        Element customerPartyParty = doc.createElement("cac:Party");
        Element customerPartyId = doc.createElement("cac:PartyIdentification");
        Element customerId = doc.createElement("cbc:ID");
        customerId.setAttribute("schemeID", cliente.getTipoDoc());
        customerId.setTextContent(cliente.getRuc());
        customerPartyId.appendChild(customerId);
        customerPartyParty.appendChild(customerPartyId);
        Element customerLegalEntity = doc.createElement("cac:PartyLegalEntity");
        Element customerRegName = doc.createElement("cbc:RegistrationName");
        customerRegName.appendChild(doc.createCDATASection(cliente.getRazonSocial()));
        customerLegalEntity.appendChild(customerRegName);
        if ("6".equals(cliente.getTipoDoc())) {
            Element customerRegAddress = doc.createElement("cac:RegistrationAddress");
            Element customerAddressLine = doc.createElement("cac:AddressLine");
            Element customerLine = doc.createElement("cbc:Line");
            customerLine.appendChild(doc.createCDATASection(cliente.getDireccion()));
            customerAddressLine.appendChild(customerLine);
            customerRegAddress.appendChild(customerAddressLine);
            Element customerCountry = doc.createElement("cac:Country");
            appendTextElement(doc, customerCountry, "cbc:IdentificationCode", cliente.getPais());
            customerRegAddress.appendChild(customerCountry);
            customerLegalEntity.appendChild(customerRegAddress);
        }
        customerPartyParty.appendChild(customerLegalEntity);
        customerParty.appendChild(customerPartyParty);
        invoice.appendChild(customerParty);

        // PaymentTerms
        if (comprobante.getFormaPagoActivo() == 1) {
            Element paymentTerms = doc.createElement("cac:PaymentTerms");
            appendTextElement(doc, paymentTerms, "cbc:ID", "FormaPago");
            appendTextElement(doc, paymentTerms, "cbc:PaymentMeansID", "Credito");
            appendTextElement(doc, paymentTerms, "cbc:Amount", comprobante.getTotal().toString()).setAttribute("currencyID", comprobante.getMoneda());
            invoice.appendChild(paymentTerms);
            int numeroCuota = comprobante.getNumeroCuota();
            int diasCuotas = comprobante.getDiasCuotasVentas();
            String fechaEmision = comprobante.getFechaEmision().toString();
            for (int i = 1; i <= numeroCuota; i++) {
                Element cuota = doc.createElement("cac:PaymentTerms");
                appendTextElement(doc, cuota, "cbc:ID", "FormaPago");
                appendTextElement(doc, cuota, "cbc:PaymentMeansID", String.format("Cuota%03d", i));
                appendTextElement(doc, cuota, "cbc:Amount", comprobante.getFormaPagoMontoApagarPorMes().toString()).setAttribute("currencyID", comprobante.getMoneda());
                // Calcular fecha de pago
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                java.util.Date date = sdf.parse(fechaEmision);
                long ms = date.getTime() + (long) diasCuotas * i * 24 * 60 * 60 * 1000;
                String fechaPago = sdf.format(new java.util.Date(ms));
                appendTextElement(doc, cuota, "cbc:PaymentDueDate", fechaPago);
                invoice.appendChild(cuota);
            }
        } else {
            Element paymentTerms = doc.createElement("cac:PaymentTerms");
            appendTextElement(doc, paymentTerms, "cbc:ID", "FormaPago");
            appendTextElement(doc, paymentTerms, "cbc:PaymentMeansID", "Contado");
            invoice.appendChild(paymentTerms);
        }

        // TaxTotal y TaxSubtotals
        Element taxTotal = doc.createElement("cac:TaxTotal");
        appendTextElement(doc, taxTotal, "cbc:TaxAmount", comprobante.getIgv().toString()).setAttribute("currencyID", comprobante.getMoneda());
        // TaxSubtotal: gravadas
        Element taxSubtotalGrav = doc.createElement("cac:TaxSubtotal");
        appendTextElement(doc, taxSubtotalGrav, "cbc:TaxableAmount", comprobante.getTotalOpGravadas().toString()).setAttribute("currencyID", comprobante.getMoneda());
        appendTextElement(doc, taxSubtotalGrav, "cbc:TaxAmount", comprobante.getIgv().toString()).setAttribute("currencyID", comprobante.getMoneda());
        Element taxCategoryGrav = doc.createElement("cac:TaxCategory");
        Element taxSchemeGrav = doc.createElement("cac:TaxScheme");
        appendTextElement(doc, taxSchemeGrav, "cbc:ID", "1000");
        appendTextElement(doc, taxSchemeGrav, "cbc:Name", "IGV");
        appendTextElement(doc, taxSchemeGrav, "cbc:TaxTypeCode", "VAT");
        taxCategoryGrav.appendChild(taxSchemeGrav);
        taxSubtotalGrav.appendChild(taxCategoryGrav);
        taxTotal.appendChild(taxSubtotalGrav);
        // TaxSubtotal: inafectas
        if (Double.parseDouble(comprobante.getTotalOpInafectas().toString()) > 0) {
            Element taxSubtotalIna = doc.createElement("cac:TaxSubtotal");
            appendTextElement(doc, taxSubtotalIna, "cbc:TaxableAmount", comprobante.getTotalOpInafectas().toString()).setAttribute("currencyID", comprobante.getMoneda());
            appendTextElement(doc, taxSubtotalIna, "cbc:TaxAmount", "0.00").setAttribute("currencyID", comprobante.getMoneda());
            Element taxCategoryIna = doc.createElement("cac:TaxCategory");
            Element taxSchemeIna = doc.createElement("cac:TaxScheme");
            Element idIna = doc.createElement("cbc:ID");
            idIna.setAttribute("schemeID", "UN/ECE 5305");
            idIna.setAttribute("schemeName", "Tax Category Identifier");
            idIna.setAttribute("schemeAgencyName", "United Nations Economic Commission for Europe");
            idIna.setTextContent("E");
            taxCategoryIna.appendChild(idIna);
            Element idSchemeIna = doc.createElement("cbc:ID");
            idSchemeIna.setAttribute("schemeID", "UN/ECE 5153");
            idSchemeIna.setAttribute("schemeAgencyID", "6");
            idSchemeIna.setTextContent("9997");
            taxSchemeIna.appendChild(idSchemeIna);
            appendTextElement(doc, taxSchemeIna, "cbc:Name", "EXO");
            appendTextElement(doc, taxSchemeIna, "cbc:TaxTypeCode", "VAT");
            taxCategoryIna.appendChild(taxSchemeIna);
            taxSubtotalIna.appendChild(taxCategoryIna);
            taxTotal.appendChild(taxSubtotalIna);
        }
        // TaxSubtotal: gratuita
        if (Double.parseDouble(comprobante.getTotalOpGratuita().toString()) > 0) {
            Element taxSubtotalGra = doc.createElement("cac:TaxSubtotal");
            appendTextElement(doc, taxSubtotalGra, "cbc:TaxableAmount", comprobante.getTotalOpGratuita().toString()).setAttribute("currencyID", comprobante.getMoneda());
            appendTextElement(doc, taxSubtotalGra, "cbc:TaxAmount", "0.00").setAttribute("currencyID", comprobante.getMoneda());
            Element taxCategoryGra = doc.createElement("cac:TaxCategory");
            Element taxSchemeGra = doc.createElement("cac:TaxScheme");
            Element idGra = doc.createElement("cbc:ID");
            idGra.setAttribute("schemeName", "Codigo de tributos");
            idGra.setAttribute("schemeAgencyName", "PE:SUNAT");
            idGra.setAttribute("schemeURI", "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo05");
            idGra.setTextContent("9996");
            taxSchemeGra.appendChild(idGra);
            appendTextElement(doc, taxSchemeGra, "cbc:Name", "GRA");
            appendTextElement(doc, taxSchemeGra, "cbc:TaxTypeCode", "FRE");
            taxCategoryGra.appendChild(taxSchemeGra);
            taxSubtotalGra.appendChild(taxCategoryGra);
            taxTotal.appendChild(taxSubtotalGra);
        }
        // TaxSubtotal: exoneradas
        if (Double.parseDouble(comprobante.getTotalOpExoneradas().toString()) > 0) {
            Element taxSubtotalExo = doc.createElement("cac:TaxSubtotal");
            appendTextElement(doc, taxSubtotalExo, "cbc:TaxableAmount", comprobante.getTotalOpExoneradas().toString()).setAttribute("currencyID", comprobante.getMoneda());
            appendTextElement(doc, taxSubtotalExo, "cbc:TaxAmount", "0.00").setAttribute("currencyID", comprobante.getMoneda());
            Element taxCategoryExo = doc.createElement("cac:TaxCategory");
            Element taxSchemeExo = doc.createElement("cac:TaxScheme");
            
            Element idExo = doc.createElement("cbc:ID");
            idExo.setAttribute("schemeID", "UN/ECE 5305");
            idExo.setAttribute("schemeName", "Tax Category Identifier");
            idExo.setAttribute("schemeAgencyName", "United Nations Economic Commission for Europe");
            idExo.setTextContent("E");
            taxCategoryExo.appendChild(idExo);
            Element idSchemeExo = doc.createElement("cbc:ID");
            idSchemeExo.setAttribute("schemeID", "UN/ECE 5153");
            idSchemeExo.setAttribute("schemeAgencyID", "6");
            //idSchemeExo.setTextContent("9998");
            idSchemeExo.setTextContent("9997");
            taxSchemeExo.appendChild(idSchemeExo);
            //appendTextElement(doc, taxSchemeExo, "cbc:Name", "INA");
            //appendTextElement(doc, taxSchemeExo, "cbc:TaxTypeCode", "FRE");
            appendTextElement(doc, taxSchemeExo, "cbc:Name", "EXO");
            appendTextElement(doc, taxSchemeExo, "cbc:TaxTypeCode", "VAT");
            taxCategoryExo.appendChild(taxSchemeExo);
            taxSubtotalExo.appendChild(taxCategoryExo);
            taxTotal.appendChild(taxSubtotalExo);
        }
        invoice.appendChild(taxTotal);

        // LegalMonetaryTotal
        double totalAntesImpuestos = Double.parseDouble(comprobante.getTotalOpGravadas().toString()) +
                Double.parseDouble(comprobante.getTotalOpExoneradas().toString()) +
                Double.parseDouble(comprobante.getTotalOpInafectas().toString());
        Element legalMonetaryTotal = doc.createElement("cac:LegalMonetaryTotal");
        appendTextElement(doc, legalMonetaryTotal, "cbc:LineExtensionAmount", String.valueOf(totalAntesImpuestos)).setAttribute("currencyID", comprobante.getMoneda());
        appendTextElement(doc, legalMonetaryTotal, "cbc:TaxInclusiveAmount", comprobante.getTotal().toString()).setAttribute("currencyID", comprobante.getMoneda());
        appendTextElement(doc, legalMonetaryTotal, "cbc:PayableAmount", comprobante.getTotal().toString()).setAttribute("currencyID", comprobante.getMoneda());
        invoice.appendChild(legalMonetaryTotal);

        // InvoiceLine (detalle)
        for (Detalle v : detalle) {
            Element invoiceLine = doc.createElement("cac:InvoiceLine");
            appendTextElement(doc, invoiceLine, "cbc:ID", v.getItem().toString());
            Element invoicedQuantity = appendTextElement(doc, invoiceLine, "cbc:InvoicedQuantity", v.getCantidad().toString());
            invoicedQuantity.setAttribute("unitCode", v.getUnidad());
            appendTextElement(doc, invoiceLine, "cbc:LineExtensionAmount", v.getValorTotal().toString()).setAttribute("currencyID", comprobante.getMoneda());
            // PricingReference
            Element pricingReference = doc.createElement("cac:PricingReference");
            Element alternativeConditionPrice = doc.createElement("cac:AlternativeConditionPrice");
            appendTextElement(doc, alternativeConditionPrice, "cbc:PriceAmount", v.getPrecioUnitario().toString()).setAttribute("currencyID", comprobante.getMoneda());
            appendTextElement(doc, alternativeConditionPrice, "cbc:PriceTypeCode", v.getTipoPrecio());
            pricingReference.appendChild(alternativeConditionPrice);
            invoiceLine.appendChild(pricingReference);
            // TaxTotal
            Element lineTaxTotal = doc.createElement("cac:TaxTotal");
            appendTextElement(doc, lineTaxTotal, "cbc:TaxAmount", v.getIgv().toString()).setAttribute("currencyID", comprobante.getMoneda());
            Element lineTaxSubtotal = doc.createElement("cac:TaxSubtotal");
            appendTextElement(doc, lineTaxSubtotal, "cbc:TaxableAmount", v.getValorTotal().toString()).setAttribute("currencyID", comprobante.getMoneda());
            appendTextElement(doc, lineTaxSubtotal, "cbc:TaxAmount", v.getIgv().toString()).setAttribute("currencyID", comprobante.getMoneda());
            Element lineTaxCategory = doc.createElement("cac:TaxCategory");
            appendTextElement(doc, lineTaxCategory, "cbc:Percent", v.getPorcentajeIgv().toString());
            appendTextElement(doc, lineTaxCategory, "cbc:TaxExemptionReasonCode", v.getCodigoAfectacionAlt());
            Element lineTaxScheme = doc.createElement("cac:TaxScheme");
            appendTextElement(doc, lineTaxScheme, "cbc:ID", v.getCodigoAfectacion());
            appendTextElement(doc, lineTaxScheme, "cbc:Name", v.getNombreAfectacion());
            appendTextElement(doc, lineTaxScheme, "cbc:TaxTypeCode", v.getTipoAfectacion());
            lineTaxCategory.appendChild(lineTaxScheme);
            lineTaxSubtotal.appendChild(lineTaxCategory);
            lineTaxTotal.appendChild(lineTaxSubtotal);
            invoiceLine.appendChild(lineTaxTotal);
            // Item
            Element item = doc.createElement("cac:Item");
            Element description = doc.createElement("cbc:Description");
            description.appendChild(doc.createCDATASection(v.getDescripcion()));
            item.appendChild(description);
            Element sellersItemIdentification = doc.createElement("cac:SellersItemIdentification");
            appendTextElement(doc, sellersItemIdentification, "cbc:ID", v.getCodigo());
            item.appendChild(sellersItemIdentification);
            invoiceLine.appendChild(item);
            // Price
            Element price = doc.createElement("cac:Price");
            appendTextElement(doc, price, "cbc:PriceAmount", v.getValorUnitario().toString()).setAttribute("currencyID", comprobante.getMoneda());
            invoiceLine.appendChild(price);
            invoice.appendChild(invoiceLine);
        }

        // Guardar XML en archivo

        // Al final, transformar el Document a String
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        String xmlString = writer.toString();

        // (Opcional) Guardar el archivo en disco
        // transformer.transform(new DOMSource(doc), new StreamResult(new File(nombreXml)));

        return xmlString;
    }

    // Método auxiliar para crear elementos de texto
    private Element appendTextElement(Document doc, Element parent, String tag, String value) {
        Element elem = doc.createElement(tag);
        if (value != null) elem.setTextContent(value);
        parent.appendChild(elem);
        return elem;
    }

}

