package com.nextia.serviciofacturacione.util;

import org.springframework.stereotype.Component;
import com.nextia.serviciofacturacione.dto.BajaDocumentosRequest;
import com.nextia.serviciofacturacione.dto.ResumenDocumentosRequest;
import com.nextia.serviciofacturacione.exception.UblGenerationException;
import com.nextia.serviciofacturacione.model.common.Cliente;
import com.nextia.serviciofacturacione.model.common.Comprobante;
import com.nextia.serviciofacturacione.model.common.Detalle;
import com.nextia.serviciofacturacione.model.common.Emisor;
import com.nextia.serviciofacturacione.util.ubl.BajaDocumentosGenerator;
import com.nextia.serviciofacturacione.util.ubl.FacturaGenerator;
import com.nextia.serviciofacturacione.util.ubl.NotaCreditoGenerator;
import com.nextia.serviciofacturacione.util.ubl.NotaDebitoGenerator;
import com.nextia.serviciofacturacione.util.ubl.ResumenDocumentosGenerator;
import java.util.List;


@Component
public class UblGenerator {

    public String generateFacturaXml(String nombreXml, Emisor emisor, Cliente cliente, Comprobante comprobante, List<Detalle> detalle) throws UblGenerationException {
        try {
            FacturaGenerator facturaGenerator = new FacturaGenerator();
            return facturaGenerator.crearXMLFactura(nombreXml, emisor, cliente, comprobante, detalle);
        } catch (Exception e) {
            throw new UblGenerationException("Error al generar XML de factura", e);
        }
    }

    public String generateNotaCreditoXml(String nombreXml, Emisor emisor, Cliente cliente, Comprobante comprobante, List<Detalle> detalle) throws UblGenerationException {
        try {
            NotaCreditoGenerator notaCreditoGenerator = new NotaCreditoGenerator();
            return notaCreditoGenerator.crearXMLNotaCredito(nombreXml, emisor, cliente, comprobante, detalle);
        } catch (Exception e) {
            throw new UblGenerationException("Error al generar XML de nota de crédito", e);
        }

    }
    
    public String generateNotaDebitoXml(String nombreXml, Emisor emisor, Cliente cliente, Comprobante comprobante, List<Detalle> detalle) throws UblGenerationException {
        try {
            NotaDebitoGenerator notaDebitoGenerator = new NotaDebitoGenerator();
            return notaDebitoGenerator.crearXMLNotaDebito(nombreXml, emisor, cliente, comprobante, detalle);
        } catch (Exception e) {
            throw new UblGenerationException("Error al generar XML de nota de débito", e);
        }
    }

    
    public String generateResumenDocumentosXml(String nombreXml, Emisor emisor, ResumenDocumentosRequest resumenDocumentosRequest) throws UblGenerationException {
        try {
            ResumenDocumentosGenerator resumenDocumentosGenerator = new ResumenDocumentosGenerator();
            return resumenDocumentosGenerator.crearXMLResumenDocumentos(nombreXml,emisor, resumenDocumentosRequest.getCabecera(), resumenDocumentosRequest.getDetalle());
        } catch (Exception e) {
            throw new UblGenerationException("Error al generar XML de resumen de documentos", e);
        }
    }
    
    public String generateBajaDocumentosXml(String nombreXml, Emisor emisor, BajaDocumentosRequest bajaDocumentosRequest) throws UblGenerationException {
        try {
            
            BajaDocumentosGenerator bajaDocumentosGenerator = new BajaDocumentosGenerator();
            return bajaDocumentosGenerator.crearXMLBajaDocumentos(emisor, bajaDocumentosRequest.getCabecera(), bajaDocumentosRequest.getDetalle(), nombreXml);
        } catch (Exception e) {
            throw new UblGenerationException("Error al generar XML de baja de documentos", e);
        }
    }
    
}