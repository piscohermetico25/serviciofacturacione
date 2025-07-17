package com.nextia.serviciofacturacione.service.sunat;

import com.nextia.serviciofacturacione.exception.FacturacionException;
import com.nextia.serviciofacturacione.model.common.Emisor;
import com.nextia.serviciofacturacione.service.sunat.client.generated.BillService;
import com.nextia.serviciofacturacione.service.sunat.client.generated.StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.activation.DataHandler;
import jakarta.mail.util.ByteArrayDataSource;
import jakarta.xml.ws.soap.SOAPFaultException;


/**
 * Implementación del servicio para el envío de documentos electrónicos a SUNAT
 */
@Service
public class SunatSenderServiceImpl implements SunatSenderService {

    private static final Logger log = LoggerFactory.getLogger(SunatSenderServiceImpl.class);
    
    private final String sunatServiceUrl;
    private final int connectionTimeout;
    private final BillService billService;
    
    public SunatSenderServiceImpl(
            @Value("${sunat.service.url}") String sunatServiceUrl,
            @Value("${sunat.service.timeout}") int connectionTimeout,
            BillService billService) {
        this.sunatServiceUrl = sunatServiceUrl;
        this.connectionTimeout = connectionTimeout;
        this.billService = billService;
    }

 
    
    @Override
    public byte[] enviarArchivo(String nombreArchivo, byte[] contenidoZip) {
        try {
            log.info("Enviando archivo {} a SUNAT", nombreArchivo);
            
            // Crear cliente SOAP

            
            // Enviar archivo a SUNAT
            byte[] respuesta = billService.sendBill(nombreArchivo,  new DataHandler(new ByteArrayDataSource(contenidoZip, "application/zip")) , null);    
            
            log.info("Archivo {} enviado correctamente a SUNAT", nombreArchivo);
            return respuesta;
        } catch (SOAPFaultException e) {
            log.error("Error SOAP al enviar archivo a SUNAT: {}", e.getMessage(), e);
            throw new FacturacionException("Error al enviar archivo a SUNAT: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error al enviar archivo a SUNAT: {}", e.getMessage(), e);
            throw new FacturacionException("Error al enviar archivo a SUNAT: " + e.getMessage(), e);
        }
    }


    @Override
    public byte[] consultarEstado(Emisor emisor, String tipoDocumento, String serie, String numero) {
        try {
            log.info("Consultando estado del documento {}-{}-{} en SUNAT", tipoDocumento, serie, numero);
            
            // Consultar estado
            String nombreArchivo = emisor.getRuc() + "-" + tipoDocumento + "-" + serie + "-" + numero;
            StatusResponse respuesta = billService.getStatus(nombreArchivo);
            
            log.info("Estado del documento {}-{}-{} consultado correctamente", tipoDocumento, serie, numero);
            

            return respuesta.getContent();
        } catch (SOAPFaultException e) {
            log.error("Error SOAP al consultar estado: {}", e.getMessage(), e);
            throw new FacturacionException("Error al consultar estado: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error al consultar estado: {}", e.getMessage(), e);
            throw new FacturacionException("Error al consultar estado: " + e.getMessage(), e);
        }
    }

    @Override
    public String enviarResumen(String nombreArchivo, byte[] contenidoZip) {
        try {
            log.info("Enviando resumen {} a SUNAT", nombreArchivo);
            
            
            // Enviar resumen a SUNAT
            String numeroTicket = billService.sendSummary(nombreArchivo, new DataHandler(new ByteArrayDataSource(contenidoZip, "application/zip")), null);
            
            log.info("Resumen {} enviado correctamente a SUNAT. Ticket: {}", nombreArchivo, numeroTicket);
            return numeroTicket;
        } catch (SOAPFaultException e) {
            log.error("Error SOAP al enviar resumen a SUNAT: {}", e.getMessage(), e);
            throw new FacturacionException("Error al enviar resumen a SUNAT: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error al enviar resumen a SUNAT: {}", e.getMessage(), e);
            throw new FacturacionException("Error al enviar resumen a SUNAT: " + e.getMessage(), e);
        }
    }

 
}
