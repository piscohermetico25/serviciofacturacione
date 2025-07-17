package com.nextia.serviciofacturacione.service.common;

import java.nio.charset.StandardCharsets;

import com.nextia.serviciofacturacione.dto.BajaDocumentosRequest;
import com.nextia.serviciofacturacione.dto.ComprobanteRequest;
import com.nextia.serviciofacturacione.dto.ResumenDocumentosRequest;
import com.nextia.serviciofacturacione.exception.FacturacionException;
import com.nextia.serviciofacturacione.model.common.Emisor;

import com.nextia.serviciofacturacione.util.UblGenerator;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeneracionXmlServiceImpl implements GeneracionXmlService {

    @Autowired
    private UblGenerator ublGenerator;

    private static final Logger log = LoggerFactory.getLogger(GeneracionXmlServiceImpl.class);

    public byte[] generarXml(String tipoDocumento, String nombreArchivo, Emisor emisor,
            ComprobanteRequest comprobanteRequest) {
        try {
            log.info("Generando XML para factura: {}-{}", comprobanteRequest.getComprobante().getSerie(),
                    comprobanteRequest.getComprobante().getCorrelativo());
            String xmlContent = "";
            switch (tipoDocumento) {
                case "01":
                    xmlContent = ublGenerator.generateFacturaXml(nombreArchivo, emisor, comprobanteRequest.getCliente(),
                            comprobanteRequest.getComprobante(), comprobanteRequest.getDetalle());
                    break;
                case "03":
                    xmlContent = ublGenerator.generateFacturaXml(nombreArchivo, emisor, comprobanteRequest.getCliente(),
                            comprobanteRequest.getComprobante(), comprobanteRequest.getDetalle());
                    break;
                case "07":
                    xmlContent = ublGenerator.generateNotaCreditoXml(nombreArchivo, emisor,
                            comprobanteRequest.getCliente(), comprobanteRequest.getComprobante(),
                            comprobanteRequest.getDetalle());
                    break;
                case "08":
                    xmlContent = ublGenerator.generateNotaDebitoXml(nombreArchivo, emisor,
                            comprobanteRequest.getCliente(), comprobanteRequest.getComprobante(),
                            comprobanteRequest.getDetalle());
                    break;
                default:
                    log.error("Tipo de documento no soportado: {}", tipoDocumento);
                    return new byte[0];
            }
            return xmlContent.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            String mensaje = String.format("Error al generar XML de factura %s-%s: %s",
                    comprobanteRequest.getComprobante().getSerie(),
                    comprobanteRequest.getComprobante().getCorrelativo(), e.getMessage());
            log.error(mensaje, e);
            throw new FacturacionException(mensaje, e);
        }
    }

    public byte[] generarXml(String nombreArchivo, Emisor emisor, ResumenDocumentosRequest comprobanteRequest) {
        try {
            log.info("Generando XML para factura: {}-{}", comprobanteRequest.getCabecera().getSerie(),
                    comprobanteRequest.getCabecera().getCorrelativo());
            String xmlContent = "";

            xmlContent = ublGenerator.generateResumenDocumentosXml(nombreArchivo, emisor, comprobanteRequest);
            return xmlContent.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            String mensaje = String.format("Error al generar XML de factura %s-%s: %s",
                    comprobanteRequest.getCabecera().getSerie(), comprobanteRequest.getCabecera().getCorrelativo(),
                    e.getMessage());
            log.error(mensaje, e);
            throw new FacturacionException(mensaje, e);
        }
    }

    public byte[] generarXml(String nombreArchivo, Emisor emisor, BajaDocumentosRequest comprobanteRequest) {
        try {
            log.info("Generando XML para factura: {}-{}", comprobanteRequest.getCabecera().getSerie(),
                    comprobanteRequest.getCabecera().getCorrelativo());
            String xmlContent = "";

            xmlContent = ublGenerator.generateBajaDocumentosXml(nombreArchivo, emisor, comprobanteRequest);
            return xmlContent.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            String mensaje = String.format("Error al generar XML de factura %s-%s: %s",
                    comprobanteRequest.getCabecera().getSerie(), comprobanteRequest.getCabecera().getCorrelativo(),
                    e.getMessage());
            log.error(mensaje, e);
            throw new FacturacionException(mensaje, e);
        }
    }

}
