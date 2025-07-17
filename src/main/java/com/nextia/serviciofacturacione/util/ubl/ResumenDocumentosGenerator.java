package com.nextia.serviciofacturacione.util.ubl;

import com.nextia.serviciofacturacione.model.common.CabeceraResumen;
import com.nextia.serviciofacturacione.model.common.DetalleResumen;
import com.nextia.serviciofacturacione.model.common.Emisor;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ResumenDocumentosGenerator {

    
    String UbLVersion="2.0";
    String CustomizationID="1.1";
    public static String crearXMLResumenDocumentos(String nombreXml,Emisor emisor, CabeceraResumen cabecera, List<DetalleResumen> detalle) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        // Namespaces
        final String NS_SUMMARY = "urn:sunat:names:specification:ubl:peru:schema:xsd:SummaryDocuments-1";
        final String NS_CAC = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2";
        final String NS_CBC = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2";
        final String NS_DS = "http://www.w3.org/2000/09/xmldsig#";
        final String NS_EXT = "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2";
        final String NS_SAC = "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1";
        final String NS_QDT = "urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2";
        final String NS_UDT = "urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2";

        Element summary = doc.createElementNS(NS_SUMMARY, "SummaryDocuments");
        summary.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:cac", NS_CAC);
        summary.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:cbc", NS_CBC);
        summary.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ds", NS_DS);
        summary.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ext", NS_EXT);
        summary.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:sac", NS_SAC);
        summary.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:qdt", NS_QDT);
        summary.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:udt", NS_UDT);
        doc.appendChild(summary);

        // ext:UBLExtensions
        Element extUBLExtensions = doc.createElementNS(NS_EXT, "ext:UBLExtensions");
        Element extUBLExtension = doc.createElementNS(NS_EXT, "ext:UBLExtension");
        Element extExtensionContent = doc.createElementNS(NS_EXT, "ext:ExtensionContent");
        extUBLExtension.appendChild(extExtensionContent);
        extUBLExtensions.appendChild(extUBLExtension);
        summary.appendChild(extUBLExtensions);

        // cbc:UBLVersionID
        Element ublVersion = doc.createElementNS(NS_CBC, "cbc:UBLVersionID");
        ublVersion.setTextContent("2.0");
        summary.appendChild(ublVersion);

        // cbc:CustomizationID
        Element customizationID = doc.createElementNS(NS_CBC, "cbc:CustomizationID");
        customizationID.setTextContent("1.1");
        summary.appendChild(customizationID);

        // cbc:ID
        Element id = doc.createElementNS(NS_CBC, "cbc:ID");
        id.setTextContent(cabecera.getTipoDoc() + "-" + cabecera.getSerie() + "-" + cabecera.getCorrelativo());
        summary.appendChild(id);

        // cbc:ReferenceDate
        Element referenceDate = doc.createElementNS(NS_CBC, "cbc:ReferenceDate");
        referenceDate.setTextContent(cabecera.getFechaEmision().format(DateTimeFormatter.ISO_DATE));
        summary.appendChild(referenceDate);

        // cbc:IssueDate
        Element issueDate = doc.createElementNS(NS_CBC, "cbc:IssueDate");
        issueDate.setTextContent(cabecera.getFechaEnvio().format(DateTimeFormatter.ISO_DATE));
        summary.appendChild(issueDate);

        // cac:Signature
        Element signature = doc.createElementNS(NS_CAC, "cac:Signature");
        Element signatureID = doc.createElementNS(NS_CBC, "cbc:ID");
        signatureID.setTextContent(cabecera.getTipoDoc() + "-" + cabecera.getSerie() + "-" + cabecera.getCorrelativo());
        signature.appendChild(signatureID);
        Element signatoryParty = doc.createElementNS(NS_CAC, "cac:SignatoryParty");
        Element partyIdentification = doc.createElementNS(NS_CAC, "cac:PartyIdentification");
        Element partyID = doc.createElementNS(NS_CBC, "cbc:ID");
        partyID.setTextContent(emisor.getRuc().trim());
        partyIdentification.appendChild(partyID);
        signatoryParty.appendChild(partyIdentification);
        Element partyName = doc.createElementNS(NS_CAC, "cac:PartyName");
        Element name = doc.createElementNS(NS_CBC, "cbc:Name");
        CDATASection razonSocialCdata = doc.createCDATASection(emisor.getRazonSocial().trim());
        name.appendChild(razonSocialCdata);
        partyName.appendChild(name);
        signatoryParty.appendChild(partyName);
        signature.appendChild(signatoryParty);
        Element digitalSignatureAttachment = doc.createElementNS(NS_CAC, "cac:DigitalSignatureAttachment");
        Element externalReference = doc.createElementNS(NS_CAC, "cac:ExternalReference");
        Element uri = doc.createElementNS(NS_CBC, "cbc:URI");
        uri.setTextContent(cabecera.getTipoDoc() + "-" + cabecera.getSerie() + "-" + cabecera.getCorrelativo());
        externalReference.appendChild(uri);
        digitalSignatureAttachment.appendChild(externalReference);
        signature.appendChild(digitalSignatureAttachment);
        summary.appendChild(signature);

        // cac:AccountingSupplierParty
        Element accountingSupplierParty = doc.createElementNS(NS_CAC, "cac:AccountingSupplierParty");
        Element customerAssignedAccountID = doc.createElementNS(NS_CBC, "cbc:CustomerAssignedAccountID");
        customerAssignedAccountID.setTextContent(emisor.getRuc().trim());
        accountingSupplierParty.appendChild(customerAssignedAccountID);
        Element additionalAccountID = doc.createElementNS(NS_CBC, "cbc:AdditionalAccountID");
        additionalAccountID.setTextContent(emisor.getTipoDoc());
        accountingSupplierParty.appendChild(additionalAccountID);
        Element party = doc.createElementNS(NS_CAC, "cac:Party");
        Element partyLegalEntity = doc.createElementNS(NS_CAC, "cac:PartyLegalEntity");
        Element registrationName = doc.createElementNS(NS_CBC, "cbc:RegistrationName");
        CDATASection regNameCdata = doc.createCDATASection(emisor.getRazonSocial().trim());
        registrationName.appendChild(regNameCdata);
        partyLegalEntity.appendChild(registrationName);
        party.appendChild(partyLegalEntity);
        accountingSupplierParty.appendChild(party);
        summary.appendChild(accountingSupplierParty);

        // sac:SummaryDocumentsLine (por cada detalle)
        for (DetalleResumen v : detalle) {
            Element summaryLine = doc.createElementNS(NS_SAC, "sac:SummaryDocumentsLine");
            // cbc:LineID
            Element lineID = doc.createElementNS(NS_CBC, "cbc:LineID");
            lineID.setTextContent(String.valueOf(v.getItem()));
            summaryLine.appendChild(lineID);
            // cbc:DocumentTypeCode
            Element documentTypeCode = doc.createElementNS(NS_CBC, "cbc:DocumentTypeCode");
            documentTypeCode.setTextContent(v.getTipoDoc());
            summaryLine.appendChild(documentTypeCode);
            // cbc:ID
            Element idDoc = doc.createElementNS(NS_CBC, "cbc:ID");
            idDoc.setTextContent(v.getSerie() + "-" + v.getCorrelativo());
            summaryLine.appendChild(idDoc);
            // cac:Status
            Element status = doc.createElementNS(NS_CAC, "cac:Status");
            Element conditionCode = doc.createElementNS(NS_CBC, "cbc:ConditionCode");
            conditionCode.setTextContent(String.valueOf(v.getCondicion()));
            status.appendChild(conditionCode);
            summaryLine.appendChild(status);
            // sac:TotalAmount
            Element totalAmount = doc.createElementNS(NS_SAC, "sac:TotalAmount");
            totalAmount.setAttribute("currencyID", v.getMoneda());
            totalAmount.setTextContent(formatBigDecimal(v.getImporteTotal()));
            summaryLine.appendChild(totalAmount);
            // sac:BillingPayment
            Element billingPayment = doc.createElementNS(NS_SAC, "sac:BillingPayment");
            Element paidAmount = doc.createElementNS(NS_CBC, "cbc:PaidAmount");
            paidAmount.setAttribute("currencyID", v.getMoneda());
            paidAmount.setTextContent(formatBigDecimal(v.getValorTotal()));
            billingPayment.appendChild(paidAmount);
            Element instructionID = doc.createElementNS(NS_CBC, "cbc:InstructionID");
            instructionID.setTextContent(v.getTipoTotal());
            billingPayment.appendChild(instructionID);
            summaryLine.appendChild(billingPayment);
            // cac:TaxTotal
            Element taxTotal = doc.createElementNS(NS_CAC, "cac:TaxTotal");
            Element taxAmount = doc.createElementNS(NS_CBC, "cbc:TaxAmount");
            taxAmount.setAttribute("currencyID", v.getMoneda());
            taxAmount.setTextContent(formatBigDecimal(v.getIgvTotal()));
            taxTotal.appendChild(taxAmount);
            // Si el código de afectación no es 1000, agregar TaxSubtotal extra
            if (!"1000".equals(v.getCodigoAfectacion())) {
                Element taxSubtotalExtra = doc.createElementNS(NS_CAC, "cac:TaxSubtotal");
                Element taxAmountExtra = doc.createElementNS(NS_CBC, "cbc:TaxAmount");
                taxAmountExtra.setAttribute("currencyID", v.getMoneda());
                taxAmountExtra.setTextContent(formatBigDecimal(v.getIgvTotal()));
                taxSubtotalExtra.appendChild(taxAmountExtra);
                Element taxCategoryExtra = doc.createElementNS(NS_CAC, "cac:TaxCategory");
                Element taxSchemeExtra = doc.createElementNS(NS_CAC, "cac:TaxScheme");
                Element idExtra = doc.createElementNS(NS_CBC, "cbc:ID");
                idExtra.setTextContent(v.getCodigoAfectacion());
                taxSchemeExtra.appendChild(idExtra);
                Element nameExtra = doc.createElementNS(NS_CBC, "cbc:Name");
                nameExtra.setTextContent(v.getNombreAfectacion());
                taxSchemeExtra.appendChild(nameExtra);
                Element typeCodeExtra = doc.createElementNS(NS_CBC, "cbc:TaxTypeCode");
                typeCodeExtra.setTextContent(v.getTipoAfectacion());
                taxSchemeExtra.appendChild(typeCodeExtra);
                taxCategoryExtra.appendChild(taxSchemeExtra);
                taxSubtotalExtra.appendChild(taxCategoryExtra);
                taxTotal.appendChild(taxSubtotalExtra);
            }
            // TaxSubtotal IGV
            Element taxSubtotal = doc.createElementNS(NS_CAC, "cac:TaxSubtotal");
            Element taxAmountSub = doc.createElementNS(NS_CBC, "cbc:TaxAmount");
            taxAmountSub.setAttribute("currencyID", v.getMoneda());
            taxAmountSub.setTextContent(formatBigDecimal(v.getIgvTotal()));
            taxSubtotal.appendChild(taxAmountSub);
            Element taxCategory = doc.createElementNS(NS_CAC, "cac:TaxCategory");
            Element taxScheme = doc.createElementNS(NS_CAC, "cac:TaxScheme");
            Element idIgv = doc.createElementNS(NS_CBC, "cbc:ID");
            idIgv.setTextContent("1000");
            taxScheme.appendChild(idIgv);
            Element nameIgv = doc.createElementNS(NS_CBC, "cbc:Name");
            nameIgv.setTextContent("IGV");
            taxScheme.appendChild(nameIgv);
            Element typeCodeIgv = doc.createElementNS(NS_CBC, "cbc:TaxTypeCode");
            typeCodeIgv.setTextContent("VAT");
            taxScheme.appendChild(typeCodeIgv);
            taxCategory.appendChild(taxScheme);
            taxSubtotal.appendChild(taxCategory);
            taxTotal.appendChild(taxSubtotal);
            summaryLine.appendChild(taxTotal);
            summary.appendChild(summaryLine);
        }

        // Guardar XML con UTF-8
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

    private static String formatBigDecimal(BigDecimal value) {
        if (value == null) return "0.00";
        return value.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
    }
}
