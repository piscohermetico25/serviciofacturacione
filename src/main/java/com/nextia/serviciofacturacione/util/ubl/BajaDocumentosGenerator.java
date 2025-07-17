package com.nextia.serviciofacturacione.util.ubl;

import com.nextia.serviciofacturacione.model.common.CabeceraResumen;
import com.nextia.serviciofacturacione.model.common.DetalleResumen;
import com.nextia.serviciofacturacione.model.common.Emisor;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BajaDocumentosGenerator {
    
    public String crearXMLBajaDocumentos(Emisor emisor, CabeceraResumen cabecera, List<DetalleResumen> detalle, String nombreXml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        // Namespaces
        final String NS_VOIDED = "urn:sunat:names:specification:ubl:peru:schema:xsd:VoidedDocuments-1";
        final String NS_CAC = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2";
        final String NS_CBC = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2";
        final String NS_DS = "http://www.w3.org/2000/09/xmldsig#";
        final String NS_EXT = "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2";
        final String NS_SAC = "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1";
        final String NS_XSI = "http://www.w3.org/2001/XMLSchema-instance";

        Element voided = doc.createElementNS(NS_VOIDED, "VoidedDocuments");
        voided.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:cac", NS_CAC);
        voided.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:cbc", NS_CBC);
        voided.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ds", NS_DS);
        voided.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ext", NS_EXT);
        voided.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:sac", NS_SAC);
        voided.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", NS_XSI);
        doc.appendChild(voided);

        // ext:UBLExtensions
        Element extUBLExtensions = doc.createElementNS(NS_EXT, "ext:UBLExtensions");
        Element extUBLExtension = doc.createElementNS(NS_EXT, "ext:UBLExtension");
        Element extExtensionContent = doc.createElementNS(NS_EXT, "ext:ExtensionContent");
        extUBLExtension.appendChild(extExtensionContent);
        extUBLExtensions.appendChild(extUBLExtension);
        voided.appendChild(extUBLExtensions);

        // cbc:UBLVersionID
        Element ublVersion = doc.createElementNS(NS_CBC, "cbc:UBLVersionID");
        ublVersion.setTextContent("2.0");
        voided.appendChild(ublVersion);

        // cbc:CustomizationID
        Element customizationID = doc.createElementNS(NS_CBC, "cbc:CustomizationID");
        customizationID.setTextContent("1.0");
        voided.appendChild(customizationID);

        // cbc:ID
        Element id = doc.createElementNS(NS_CBC, "cbc:ID");
        id.setTextContent(cabecera.getTipoDoc() + "-" + cabecera.getSerie() + "-" + cabecera.getCorrelativo());
        voided.appendChild(id);

        // cbc:ReferenceDate
        Element referenceDate = doc.createElementNS(NS_CBC, "cbc:ReferenceDate");
        referenceDate.setTextContent(cabecera.getFechaEmision().format(DateTimeFormatter.ISO_DATE));
        voided.appendChild(referenceDate);

        // cbc:IssueDate
        Element issueDate = doc.createElementNS(NS_CBC, "cbc:IssueDate");
        issueDate.setTextContent(cabecera.getFechaEnvio().format(DateTimeFormatter.ISO_DATE));
        voided.appendChild(issueDate);

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
        voided.appendChild(signature);

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
        voided.appendChild(accountingSupplierParty);

        // sac:VoidedDocumentsLine (por cada detalle)
        for (DetalleResumen v : detalle) {
            Element voidedLine = doc.createElementNS(NS_SAC, "sac:VoidedDocumentsLine");
            // cbc:LineID
            Element lineID = doc.createElementNS(NS_CBC, "cbc:LineID");
            lineID.setTextContent(String.valueOf(v.getItem()));
            voidedLine.appendChild(lineID);
            // cbc:DocumentTypeCode
            Element documentTypeCode = doc.createElementNS(NS_CBC, "cbc:DocumentTypeCode");
            documentTypeCode.setTextContent(v.getTipoDoc());
            voidedLine.appendChild(documentTypeCode);
            // sac:DocumentSerialID
            Element documentSerialID = doc.createElementNS(NS_SAC, "sac:DocumentSerialID");
            documentSerialID.setTextContent(v.getSerie());
            voidedLine.appendChild(documentSerialID);
            // sac:DocumentNumberID
            Element documentNumberID = doc.createElementNS(NS_SAC, "sac:DocumentNumberID");
            documentNumberID.setTextContent(v.getCorrelativo());
            voidedLine.appendChild(documentNumberID);
            // sac:VoidReasonDescription
            Element voidReasonDescription = doc.createElementNS(NS_SAC, "sac:VoidReasonDescription");
            CDATASection motivoCdata = doc.createCDATASection(v.getNombreAfectacion() != null ? v.getNombreAfectacion().trim() : "");
            voidReasonDescription.appendChild(motivoCdata);
            voidedLine.appendChild(voidReasonDescription);
            voided.appendChild(voidedLine);
        }

        // Guardar XML con UTF-8

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
}
