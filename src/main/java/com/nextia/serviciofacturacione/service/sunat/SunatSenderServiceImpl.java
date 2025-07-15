package com.nextia.serviciofacturacione.service.sunat;

import com.nextia.serviciofacturacione.exception.FacturacionException;
import com.nextia.serviciofacturacione.service.sunat.SunatSenderService;
import com.nextia.serviciofacturacione.service.sunat.client.PasswordCallbackHandler;
import com.nextia.serviciofacturacione.service.sunat.client.generated.BillService;
import com.nextia.serviciofacturacione.service.sunat.client.generated.StatusResponse;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.common.ConfigurationConstants;
import org.apache.wss4j.common.WSS4JConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.activation.DataHandler;
import jakarta.mail.util.ByteArrayDataSource;
import jakarta.xml.ws.soap.SOAPFaultException;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementación del servicio para el envío de documentos electrónicos a SUNAT
 */
@Service
public class SunatSenderServiceImpl implements SunatSenderService {

    private static final Logger log = LoggerFactory.getLogger(SunatSenderServiceImpl.class);
    
    private final String sunatServiceUrl;
    private final int connectionTimeout;
    private final String ruc;
    private final String usuarioSol;
    private final String claveSol;

    
    private final BillService billService;
    
    public SunatSenderServiceImpl(
            @Value("${sunat.service.url}") String sunatServiceUrl,
            @Value("${sunat.service.timeout}") int connectionTimeout,
            @Value("${sunat.ruc}") String ruc,
            @Value("${sunat.usuario.sol}") String usuarioSol,
            @Value("${sunat.clave.sol}") String claveSol,
            BillService billService) {
        this.sunatServiceUrl = sunatServiceUrl;
        this.connectionTimeout = connectionTimeout;
        this.ruc = ruc;
        this.usuarioSol = usuarioSol;
        this.claveSol = claveSol;
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
    public byte[] consultarEstado(String tipoDocumento, String serie, String numero) {
        try {
            log.info("Consultando estado del documento {}-{}-{} en SUNAT", tipoDocumento, serie, numero);
            
            // Consultar estado
            String nombreArchivo = ruc + "-" + tipoDocumento + "-" + serie + "-" + numero;
            StatusResponse respuesta = billService.getStatus(nombreArchivo);
            
            log.info("Estado del documento {}-{}-{} consultado correctamente", tipoDocumento, serie, numero);
            
            // Convertir la respuesta a bytes - ajusta según la estructura real de StatusResponse
            // Opción 1: Si tiene un método que devuelve byte[]
            // return respuesta.getContent(); 
            
            // Opción 2: Si tiene un método que devuelve String
            // return respuesta.getStatus().getBytes(StandardCharsets.UTF_8);
            
            // Opción 3: Convertir todo el objeto a bytes
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

    @Override
    public byte[] consultarTicket(String numeroTicket) {
        try {
            log.info("Consultando ticket {} en SUNAT", numeroTicket);
            
            
            // Consultar ticket
            StatusResponse respuesta = billService.getStatus(numeroTicket);
            
            log.info("Ticket {} consultado correctamente", numeroTicket);
            return respuesta.getContent();
        } catch (SOAPFaultException e) {
            log.error("Error SOAP al consultar ticket: {}", e.getMessage(), e);
            throw new FacturacionException("Error al consultar ticket: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error al consultar ticket: {}", e.getMessage(), e);
            throw new FacturacionException("Error al consultar ticket: " + e.getMessage(), e);
        }
    }
    

    

    @Override
    public String enviarLote(String nombreArchivo, byte[] contenidoZip) {
        try {
            log.info("Enviando lote {} a SUNAT", nombreArchivo);
            
            // Enviar lote a SUNAT y obtener número de ticket
            String numeroTicket = billService.sendPack(nombreArchivo, new DataHandler(new ByteArrayDataSource(contenidoZip, "application/zip")), null);
            
            log.info("Lote {} enviado correctamente a SUNAT. Número de ticket: {}", nombreArchivo, numeroTicket);
            return numeroTicket;
        } catch (SOAPFaultException e) {
            log.error("Error SOAP al enviar lote a SUNAT: {}", e.getMessage(), e);
            throw new FacturacionException("Error al enviar lote a SUNAT: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error al enviar lote a SUNAT: {}", e.getMessage(), e);
            throw new FacturacionException("Error al enviar lote a SUNAT: " + e.getMessage(), e);
        }
    }
    
    @Override
    public byte[] consultarCdr(String tipoDocumento, String serie, String numero) {
        try {
            log.info("Consultando CDR del documento {}-{}-{} en SUNAT", tipoDocumento, serie, numero);
            
            // Consultar CDR
            StatusResponse respuesta = billService.getStatus(numero);
            
            log.info("CDR del documento {}-{}-{} consultado correctamente", tipoDocumento, serie, numero);
            return respuesta.getContent();
        } catch (SOAPFaultException e) {
            log.error("Error SOAP al consultar CDR: {}", e.getMessage(), e);
            throw new FacturacionException("Error al consultar CDR: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error al consultar CDR: {}", e.getMessage(), e);
            throw new FacturacionException("Error al consultar CDR: " + e.getMessage(), e);
        }
    }
    
    @Override
    public byte[] consultarEstadoAR(String tipoDocumento, String serie, String numero) {
        try {
            log.info("Consultando estado AR del documento {}-{}-{} en SUNAT", tipoDocumento, serie, numero);
            
            // Consultar estado AR
            StatusResponse respuesta = billService.getStatus(numero);
            
            log.info("Estado AR del documento {}-{}-{} consultado correctamente", tipoDocumento, serie, numero);
            return respuesta.getContent();
        } catch (SOAPFaultException e) {
            log.error("Error SOAP al consultar estado AR: {}", e.getMessage(), e);
            throw new FacturacionException("Error al consultar estado AR: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error al consultar estado AR: {}", e.getMessage(), e);
            throw new FacturacionException("Error al consultar estado AR: " + e.getMessage(), e);
        }
    }
    

}
