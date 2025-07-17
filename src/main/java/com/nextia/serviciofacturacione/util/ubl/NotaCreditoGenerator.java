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
import java.io.File;
import java.util.List;

public class NotaCreditoGenerator {
    /**
     * Genera un XML UBL tipo CreditNote siguiendo la lógica PHP proporcionada.
     * @param nombreXml Nombre del archivo de salida (sin extensión)
     * @param emisor Datos del emisor
     * @param cliente Datos del cliente
     * @param comprobante Datos del comprobante
     * @param detalle Lista de items
     * @throws Exception Si ocurre un error en la generación del XML
     */
    public String crearXMLNotaCredito(String nombreXml, Emisor emisor, Cliente cliente, Comprobante comprobante, List<Detalle> detalle) throws Exception {
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

        // Elemento raíz CreditNote
        Element creditNote = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2", "CreditNote");
        creditNote.setAttribute("xmlns:cac", "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
        creditNote.setAttribute("xmlns:cbc", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");
        creditNote.setAttribute("xmlns:ds", "http://www.w3.org/2000/09/xmldsig#");
        creditNote.setAttribute("xmlns:ext", "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2");
        doc.appendChild(creditNote);

        // UBLExtensions
        Element extUBLExtensions = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2", "ext:UBLExtensions");
        Element extUBLExtension = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2", "ext:UBLExtension");
        Element extExtensionContent = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2", "ext:ExtensionContent");
        extUBLExtension.appendChild(extExtensionContent);
        extUBLExtensions.appendChild(extUBLExtension);
        creditNote.appendChild(extUBLExtensions);

        // Versiones UBL y Customization
        Element ublVersionID = doc.createElementNS(null, "cbc:UBLVersionID");
        ublVersionID.setTextContent("2.1");
        creditNote.appendChild(ublVersionID);
        Element customizationID = doc.createElementNS(null, "cbc:CustomizationID");
        customizationID.setTextContent("2.0");
        creditNote.appendChild(customizationID);

        // ID y fechas
        Element id = doc.createElementNS(null, "cbc:ID");
        id.setTextContent(comprobante.getSerie() + "-" + comprobante.getCorrelativo());
        creditNote.appendChild(id);
        Element issueDate = doc.createElementNS(null, "cbc:IssueDate");
        issueDate.setTextContent(comprobante.getFechaEmision().toString());
        creditNote.appendChild(issueDate);
        Element issueTime = doc.createElementNS(null, "cbc:IssueTime");
        issueTime.setTextContent("00:00:01");
        creditNote.appendChild(issueTime);
        Element note = doc.createElementNS(null, "cbc:Note");
        note.setAttribute("languageLocaleID", "1000");
        note.appendChild(doc.createCDATASection(comprobante.getTotalTexto()));
        creditNote.appendChild(note);
        Element currency = doc.createElementNS(null, "cbc:DocumentCurrencyCode");
        currency.setTextContent(comprobante.getMoneda());
        creditNote.appendChild(currency);

        // DiscrepancyResponse
        Element discrepancy = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:DiscrepancyResponse");
        Element refId = doc.createElementNS(null, "cbc:ReferenceID");
        refId.setTextContent(comprobante.getSerieRef() + "-" + comprobante.getCorrelativoRef());
        discrepancy.appendChild(refId);
        Element respCode = doc.createElementNS(null, "cbc:ResponseCode");
        respCode.setTextContent(comprobante.getCodMotivo());
        discrepancy.appendChild(respCode);
        Element desc = doc.createElementNS(null, "cbc:Description");
        desc.setTextContent(comprobante.getDescripcionMotivo());
        discrepancy.appendChild(desc);
        creditNote.appendChild(discrepancy);

        // BillingReference
        Element billingRef = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:BillingReference");
        Element invoiceDocRef = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:InvoiceDocumentReference");
        Element invoiceId = doc.createElementNS(null, "cbc:ID");
        invoiceId.setTextContent(comprobante.getSerieRef() + "-" + comprobante.getCorrelativoRef());
        invoiceDocRef.appendChild(invoiceId);
        Element invoiceTypeCode = doc.createElementNS(null, "cbc:DocumentTypeCode");
        invoiceTypeCode.setTextContent(comprobante.getTipoDocRef());
        invoiceDocRef.appendChild(invoiceTypeCode);
        billingRef.appendChild(invoiceDocRef);
        creditNote.appendChild(billingRef);

        // Signature
        Element signature = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:Signature");
        Element sigId = doc.createElementNS(null, "cbc:ID");
        sigId.setTextContent(emisor.getRuc());
        signature.appendChild(sigId);
        Element sigNote = doc.createElementNS(null, "cbc:Note");
        sigNote.appendChild(doc.createCDATASection(emisor.getNombreComercial()));
        signature.appendChild(sigNote);
        Element signatoryParty = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:SignatoryParty");
        Element partyId = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:PartyIdentification");
        Element partyIdValue = doc.createElementNS(null, "cbc:ID");
        partyIdValue.setTextContent(emisor.getRuc());
        partyId.appendChild(partyIdValue);
        signatoryParty.appendChild(partyId);
        Element partyName = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:PartyName");
        Element partyNameValue = doc.createElementNS(null, "cbc:Name");
        partyNameValue.appendChild(doc.createCDATASection(emisor.getRazonSocial()));
        partyName.appendChild(partyNameValue);
        signatoryParty.appendChild(partyName);
        signature.appendChild(signatoryParty);
        Element digitalSigAtt = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:DigitalSignatureAttachment");
        Element extRef = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:ExternalReference");
        Element uri = doc.createElementNS(null, "cbc:URI");
        uri.setTextContent("#SIGN-EMPRESA");
        extRef.appendChild(uri);
        digitalSigAtt.appendChild(extRef);
        signature.appendChild(digitalSigAtt);
        creditNote.appendChild(signature);

        // AccountingSupplierParty (Emisor)
        Element accSupplier = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:AccountingSupplierParty");
        Element accSupplierParty = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:Party");
        Element accSupplierPartyId = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:PartyIdentification");
        Element accSupplierPartyIdValue = doc.createElementNS(null, "cbc:ID");
        accSupplierPartyIdValue.setAttribute("schemeID", emisor.getTipoDoc());
        accSupplierPartyIdValue.setTextContent(emisor.getRuc());
        accSupplierPartyId.appendChild(accSupplierPartyIdValue);
        accSupplierParty.appendChild(accSupplierPartyId);
        Element accSupplierPartyName = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:PartyName");
        Element accSupplierPartyNameValue = doc.createElementNS(null, "cbc:Name");
        accSupplierPartyNameValue.appendChild(doc.createCDATASection(emisor.getNombreComercial()));
        accSupplierPartyName.appendChild(accSupplierPartyNameValue);
        accSupplierParty.appendChild(accSupplierPartyName);
        Element accSupplierLegalEntity = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:PartyLegalEntity");
        Element accSupplierRegName = doc.createElementNS(null, "cbc:RegistrationName");
        accSupplierRegName.appendChild(doc.createCDATASection(emisor.getRazonSocial()));
        accSupplierLegalEntity.appendChild(accSupplierRegName);
        Element accSupplierRegAddr = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:RegistrationAddress");
        Element accSupplierUbigeo = doc.createElementNS(null, "cbc:ID");
        accSupplierUbigeo.setTextContent(emisor.getUbigeo());
        accSupplierRegAddr.appendChild(accSupplierUbigeo);
        Element accSupplierAddrType = doc.createElementNS(null, "cbc:AddressTypeCode");
        accSupplierAddrType.setTextContent("0000");
        accSupplierRegAddr.appendChild(accSupplierAddrType);
        Element accSupplierCitySub = doc.createElementNS(null, "cbc:CitySubdivisionName");
        accSupplierCitySub.setTextContent("NONE");
        accSupplierRegAddr.appendChild(accSupplierCitySub);
        Element accSupplierCity = doc.createElementNS(null, "cbc:CityName");
        accSupplierCity.setTextContent(emisor.getProvincia());
        accSupplierRegAddr.appendChild(accSupplierCity);
        Element accSupplierCountrySub = doc.createElementNS(null, "cbc:CountrySubentity");
        accSupplierCountrySub.setTextContent(emisor.getDepartamento());
        accSupplierRegAddr.appendChild(accSupplierCountrySub);
        Element accSupplierDistrict = doc.createElementNS(null, "cbc:District");
        accSupplierDistrict.setTextContent(emisor.getDistrito());
        accSupplierRegAddr.appendChild(accSupplierDistrict);
        Element accSupplierAddrLine = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:AddressLine");
        Element accSupplierAddrLineValue = doc.createElementNS(null, "cbc:Line");
        accSupplierAddrLineValue.appendChild(doc.createCDATASection(emisor.getDireccion()));
        accSupplierAddrLine.appendChild(accSupplierAddrLineValue);
        accSupplierRegAddr.appendChild(accSupplierAddrLine);
        Element accSupplierCountry = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:Country");
        Element accSupplierCountryId = doc.createElementNS(null, "cbc:IdentificationCode");
        accSupplierCountryId.setTextContent(emisor.getPais());
        accSupplierCountry.appendChild(accSupplierCountryId);
        accSupplierRegAddr.appendChild(accSupplierCountry);
        accSupplierLegalEntity.appendChild(accSupplierRegAddr);
        accSupplierParty.appendChild(accSupplierLegalEntity);
        accSupplier.appendChild(accSupplierParty);
        creditNote.appendChild(accSupplier);

        // AccountingCustomerParty (Cliente)
        Element accCustomer = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:AccountingCustomerParty");
        Element accCustomerParty = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:Party");
        Element accCustomerPartyId = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:PartyIdentification");
        Element accCustomerPartyIdValue = doc.createElementNS(null, "cbc:ID");
        accCustomerPartyIdValue.setAttribute("schemeID", cliente.getTipoDoc());
        accCustomerPartyIdValue.setTextContent(cliente.getRuc());
        accCustomerPartyId.appendChild(accCustomerPartyIdValue);
        accCustomerParty.appendChild(accCustomerPartyId);
        Element accCustomerLegalEntity = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:PartyLegalEntity");
        Element accCustomerRegName = doc.createElementNS(null, "cbc:RegistrationName");
        accCustomerRegName.appendChild(doc.createCDATASection(cliente.getRazonSocial()));
        accCustomerLegalEntity.appendChild(accCustomerRegName);
        // Dirección solo si existe
        if (cliente.getDireccion() != null && !cliente.getDireccion().isEmpty()) {
            Element accCustomerRegAddr = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:RegistrationAddress");
            Element accCustomerAddrLine = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:AddressLine");
            Element accCustomerAddrLineValue = doc.createElementNS(null, "cbc:Line");
            accCustomerAddrLineValue.appendChild(doc.createCDATASection(cliente.getDireccion()));
            accCustomerAddrLine.appendChild(accCustomerAddrLineValue);
            accCustomerRegAddr.appendChild(accCustomerAddrLine);
            Element accCustomerCountry = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:Country");
            Element accCustomerCountryId = doc.createElementNS(null, "cbc:IdentificationCode");
            accCustomerCountryId.setTextContent(cliente.getPais());
            accCustomerCountry.appendChild(accCustomerCountryId);
            accCustomerRegAddr.appendChild(accCustomerCountry);
            accCustomerLegalEntity.appendChild(accCustomerRegAddr);
        }
        accCustomerParty.appendChild(accCustomerLegalEntity);
        accCustomer.appendChild(accCustomerParty);
        creditNote.appendChild(accCustomer);

        // TaxTotal
        Element taxTotal = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:TaxTotal");
        Element taxAmount = doc.createElementNS(null, "cbc:TaxAmount");
        taxAmount.setAttribute("currencyID", comprobante.getMoneda());
        taxAmount.setTextContent(comprobante.getIgv().toPlainString());
        taxTotal.appendChild(taxAmount);

        // TaxSubtotal (Gravadas)
        if (comprobante.getTotalOpGravadas() != null && comprobante.getTotalOpGravadas().compareTo(java.math.BigDecimal.ZERO) > 0) {
            Element taxSubtotal = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:TaxSubtotal");
            Element taxableAmount = doc.createElementNS(null, "cbc:TaxableAmount");
            taxableAmount.setAttribute("currencyID", comprobante.getMoneda());
            taxableAmount.setTextContent(comprobante.getTotalOpGravadas().toPlainString());
            taxSubtotal.appendChild(taxableAmount);
            Element subTaxAmount = doc.createElementNS(null, "cbc:TaxAmount");
            subTaxAmount.setAttribute("currencyID", comprobante.getMoneda());
            subTaxAmount.setTextContent(comprobante.getIgv().toPlainString());
            taxSubtotal.appendChild(subTaxAmount);
            Element taxCategory = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:TaxCategory");
            Element taxScheme = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:TaxScheme");
            Element taxSchemeId = doc.createElementNS(null, "cbc:ID");
            taxSchemeId.setTextContent("1000");
            taxScheme.appendChild(taxSchemeId);
            Element taxSchemeName = doc.createElementNS(null, "cbc:Name");
            taxSchemeName.setTextContent("IGV");
            taxScheme.appendChild(taxSchemeName);
            Element taxTypeCode = doc.createElementNS(null, "cbc:TaxTypeCode");
            taxTypeCode.setTextContent("VAT");
            taxScheme.appendChild(taxTypeCode);
            taxCategory.appendChild(taxScheme);
            taxSubtotal.appendChild(taxCategory);
            taxTotal.appendChild(taxSubtotal);
        }

        // TaxSubtotal (Exoneradas)
        if (comprobante.getTotalOpExoneradas() != null && comprobante.getTotalOpExoneradas().compareTo(java.math.BigDecimal.ZERO) > 0) {
            Element taxSubtotal = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:TaxSubtotal");
            Element taxableAmount = doc.createElementNS(null, "cbc:TaxableAmount");
            taxableAmount.setAttribute("currencyID", comprobante.getMoneda());
            taxableAmount.setTextContent(comprobante.getTotalOpExoneradas().toPlainString());
            taxSubtotal.appendChild(taxableAmount);
            Element subTaxAmount = doc.createElementNS(null, "cbc:TaxAmount");
            subTaxAmount.setAttribute("currencyID", comprobante.getMoneda());
            subTaxAmount.setTextContent("0.00");
            taxSubtotal.appendChild(subTaxAmount);
            Element taxCategory = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:TaxCategory");
            Element taxCatId = doc.createElementNS(null, "cbc:ID");
            taxCatId.setAttribute("schemeID", "UN/ECE 5305");
            taxCatId.setAttribute("schemeName", "Tax Category Identifier");
            taxCatId.setAttribute("schemeAgencyName", "United Nations Economic Commission for Europe");
            taxCatId.setTextContent("E");
            taxCategory.appendChild(taxCatId);
            Element taxScheme = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:TaxScheme");
            Element taxSchemeId = doc.createElementNS(null, "cbc:ID");
            taxSchemeId.setAttribute("schemeID", "UN/ECE 5153");
            taxSchemeId.setAttribute("schemeAgencyID", "6");
            taxSchemeId.setTextContent("9997");
            taxScheme.appendChild(taxSchemeId);
            Element taxSchemeName = doc.createElementNS(null, "cbc:Name");
            taxSchemeName.setTextContent("EXO");
            taxScheme.appendChild(taxSchemeName);
            Element taxTypeCode = doc.createElementNS(null, "cbc:TaxTypeCode");
            taxTypeCode.setTextContent("VAT");
            taxScheme.appendChild(taxTypeCode);
            taxCategory.appendChild(taxScheme);
            taxSubtotal.appendChild(taxCategory);
            taxTotal.appendChild(taxSubtotal);
        }

        // TaxSubtotal (Inafectas)
        if (comprobante.getTotalOpInafectas() != null && comprobante.getTotalOpInafectas().compareTo(java.math.BigDecimal.ZERO) > 0) {
            Element taxSubtotal = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:TaxSubtotal");
            Element taxableAmount = doc.createElementNS(null, "cbc:TaxableAmount");
            taxableAmount.setAttribute("currencyID", comprobante.getMoneda());
            taxableAmount.setTextContent(comprobante.getTotalOpInafectas().toPlainString());
            taxSubtotal.appendChild(taxableAmount);
            Element subTaxAmount = doc.createElementNS(null, "cbc:TaxAmount");
            subTaxAmount.setAttribute("currencyID", comprobante.getMoneda());
            subTaxAmount.setTextContent("0.00");
            taxSubtotal.appendChild(subTaxAmount);
            Element taxCategory = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:TaxCategory");
            Element taxCatId = doc.createElementNS(null, "cbc:ID");
            taxCatId.setAttribute("schemeID", "UN/ECE 5305");
            taxCatId.setAttribute("schemeName", "Tax Category Identifier");
            taxCatId.setAttribute("schemeAgencyName", "United Nations Economic Commission for Europe");
            taxCatId.setTextContent("E");
            taxCategory.appendChild(taxCatId);
            Element taxScheme = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:TaxScheme");
            Element taxSchemeId = doc.createElementNS(null, "cbc:ID");
            taxSchemeId.setAttribute("schemeID", "UN/ECE 5153");
            taxSchemeId.setAttribute("schemeAgencyID", "6");
            taxSchemeId.setTextContent("9998");
            taxScheme.appendChild(taxSchemeId);
            Element taxSchemeName = doc.createElementNS(null, "cbc:Name");
            taxSchemeName.setTextContent("INA");
            taxScheme.appendChild(taxSchemeName);
            Element taxTypeCode = doc.createElementNS(null, "cbc:TaxTypeCode");
            taxTypeCode.setTextContent("FRE");
            taxScheme.appendChild(taxTypeCode);
            taxCategory.appendChild(taxScheme);
            taxSubtotal.appendChild(taxCategory);
            taxTotal.appendChild(taxSubtotal);
        }
        creditNote.appendChild(taxTotal);

        // LegalMonetaryTotal
        Element legalMonetaryTotal = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:LegalMonetaryTotal");
        Element payableAmount = doc.createElementNS(null, "cbc:PayableAmount");
        payableAmount.setAttribute("currencyID", comprobante.getMoneda());
        payableAmount.setTextContent(comprobante.getTotal().toPlainString());
        legalMonetaryTotal.appendChild(payableAmount);
        creditNote.appendChild(legalMonetaryTotal);

        // CreditNoteLine
        for (Detalle v : detalle) {
            Element creditNoteLine = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:CreditNoteLine");
            Element lineId = doc.createElementNS(null, "cbc:ID");
            lineId.setTextContent(String.valueOf(v.getItem()));
            creditNoteLine.appendChild(lineId);
            Element creditedQty = doc.createElementNS(null, "cbc:CreditedQuantity");
            creditedQty.setAttribute("unitCode", v.getUnidad());
            creditedQty.setTextContent(v.getCantidad().toPlainString());
            creditNoteLine.appendChild(creditedQty);
            Element lineExtAmount = doc.createElementNS(null, "cbc:LineExtensionAmount");
            lineExtAmount.setAttribute("currencyID", comprobante.getMoneda());
            lineExtAmount.setTextContent(v.getValorTotal().toPlainString());
            creditNoteLine.appendChild(lineExtAmount);
            // PricingReference
            Element pricingRef = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:PricingReference");
            Element altCondPrice = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:AlternativeConditionPrice");
            Element priceAmount = doc.createElementNS(null, "cbc:PriceAmount");
            priceAmount.setAttribute("currencyID", comprobante.getMoneda());
            priceAmount.setTextContent(v.getPrecioUnitario().toPlainString());
            altCondPrice.appendChild(priceAmount);
            Element priceTypeCode = doc.createElementNS(null, "cbc:PriceTypeCode");
            priceTypeCode.setTextContent(v.getTipoPrecio());
            altCondPrice.appendChild(priceTypeCode);
            pricingRef.appendChild(altCondPrice);
            creditNoteLine.appendChild(pricingRef);
            // TaxTotal
            Element lineTaxTotal = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:TaxTotal");
            Element lineTaxAmount = doc.createElementNS(null, "cbc:TaxAmount");
            lineTaxAmount.setAttribute("currencyID", comprobante.getMoneda());
            lineTaxAmount.setTextContent(v.getIgv().toPlainString());
            lineTaxTotal.appendChild(lineTaxAmount);
            Element lineTaxSubtotal = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:TaxSubtotal");
            Element lineTaxableAmount = doc.createElementNS(null, "cbc:TaxableAmount");
            lineTaxableAmount.setAttribute("currencyID", comprobante.getMoneda());
            lineTaxableAmount.setTextContent(v.getValorTotal().toPlainString());
            lineTaxSubtotal.appendChild(lineTaxableAmount);
            Element lineSubTaxAmount = doc.createElementNS(null, "cbc:TaxAmount");
            lineSubTaxAmount.setAttribute("currencyID", comprobante.getMoneda());
            lineSubTaxAmount.setTextContent(v.getIgv().toPlainString());
            lineTaxSubtotal.appendChild(lineSubTaxAmount);
            Element lineTaxCategory = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:TaxCategory");
            Element percent = doc.createElementNS(null, "cbc:Percent");
            percent.setTextContent(v.getPorcentajeIgv().toPlainString());
            lineTaxCategory.appendChild(percent);
            Element taxExemptionReasonCode = doc.createElementNS(null, "cbc:TaxExemptionReasonCode");
            taxExemptionReasonCode.setTextContent(v.getCodigoAfectacionAlt());
            lineTaxCategory.appendChild(taxExemptionReasonCode);
            Element lineTaxScheme = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:TaxScheme");
            Element lineTaxSchemeId = doc.createElementNS(null, "cbc:ID");
            lineTaxSchemeId.setTextContent(v.getCodigoAfectacion());
            lineTaxScheme.appendChild(lineTaxSchemeId);
            Element lineTaxSchemeName = doc.createElementNS(null, "cbc:Name");
            lineTaxSchemeName.setTextContent(v.getNombreAfectacion());
            lineTaxScheme.appendChild(lineTaxSchemeName);
            Element lineTaxTypeCode = doc.createElementNS(null, "cbc:TaxTypeCode");
            lineTaxTypeCode.setTextContent(v.getTipoAfectacion());
            lineTaxScheme.appendChild(lineTaxTypeCode);
            lineTaxCategory.appendChild(lineTaxScheme);
            lineTaxSubtotal.appendChild(lineTaxCategory);
            lineTaxTotal.appendChild(lineTaxSubtotal);
            creditNoteLine.appendChild(lineTaxTotal);
            // Item
            Element item = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:Item");
            Element descItem = doc.createElementNS(null, "cbc:Description");
            descItem.appendChild(doc.createCDATASection(v.getDescripcion()));
            item.appendChild(descItem);
            Element sellersItemId = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:SellersItemIdentification");
            Element sellersItemIdValue = doc.createElementNS(null, "cbc:ID");
            sellersItemIdValue.setTextContent(v.getCodigo());
            sellersItemId.appendChild(sellersItemIdValue);
            item.appendChild(sellersItemId);
            creditNoteLine.appendChild(item);
            // Price
            Element price = doc.createElementNS("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", "cac:Price");
            Element priceAmount2 = doc.createElementNS(null, "cbc:PriceAmount");
            priceAmount2.setAttribute("currencyID", comprobante.getMoneda());
            priceAmount2.setTextContent(v.getValorUnitario().toPlainString());
            price.appendChild(priceAmount2);
            creditNoteLine.appendChild(price);
            creditNote.appendChild(creditNoteLine);
        }

        // Guardar el archivo XML
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(nombreXml + ".XML"));
        transformer.transform(source, result);

        return nombreXml + ".XML";
    }
}
