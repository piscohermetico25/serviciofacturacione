package com.nextia.serviciofacturacione.service.sunat;

import com.nextia.serviciofacturacione.exception.FacturacionException;
import com.nextia.serviciofacturacione.service.common.SunatSenderService;
import com.nextia.serviciofacturacione.service.sunat.client.PasswordCallbackHandler;
import com.nextia.serviciofacturacione.service.sunat.client.SunatBillService;

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

import jakarta.xml.ws.soap.SOAPFaultException;
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
    
    public SunatSenderServiceImpl(
            @Value("${sunat.service.url}") String sunatServiceUrl,
            @Value("${sunat.service.timeout}") int connectionTimeout,
            @Value("${sunat.ruc}") String ruc,
            @Value("${sunat.usuario.sol}") String usuarioSol,
            @Value("${sunat.clave.sol}") String claveSol) {
        this.sunatServiceUrl = sunatServiceUrl;
        this.connectionTimeout = connectionTimeout;
        this.ruc = ruc;
        this.usuarioSol = usuarioSol;
        this.claveSol = claveSol;
    }

    /**
     * Crea y configura un cliente SOAP para los servicios de SUNAT
     * 
     * @return Servicio SOAP configurado
     */
    private SunatBillService crearClienteSoap() {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setAddress(sunatServiceUrl);
        factory.setServiceClass(SunatBillService.class);
        
        SunatBillService service = (SunatBillService) factory.create();
        Client client = ClientProxy.getClient(service);
        
        // Configurar seguridad WS-Security usando credenciales inyectadas
        configurarSeguridad(client, this.ruc + this.usuarioSol, this.claveSol);
        
        // Configurar timeout
        configurarTimeout(client);
        
        return service;
    }
    
    @Override
    public byte[] enviarArchivo(String nombreArchivo, byte[] contenidoZip) {
        try {
            log.info("Enviando archivo {} a SUNAT", nombreArchivo);
            
            // Crear cliente SOAP
            SunatBillService service = crearClienteSoap();
            
            // Enviar archivo a SUNAT
            byte[] respuesta = service.sendBill(nombreArchivo, contenidoZip);
            
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

    /**
     * Consulta el estado de un documento enviado a SUNAT
     * 
     * @deprecated Usar consultarCdr en su lugar según el manual del programador de SUNAT
     */
    @Override
    @Deprecated(since = "1.1.0", forRemoval = true)
    public byte[] consultarEstado(String tipoDocumento, String serie, String numero) {
        try {
            log.info("Consultando estado del documento {}-{}-{} en SUNAT", tipoDocumento, serie, numero);
            
            // Crear cliente SOAP
            SunatBillService service = crearClienteSoap();
            
            // Consultar estado
            String nombreArchivo = ruc + "-" + tipoDocumento + "-" + serie + "-" + numero;
            byte[] respuesta = service.getStatus(nombreArchivo);
            
            log.info("Estado del documento {}-{}-{} consultado correctamente", tipoDocumento, serie, numero);
            return respuesta;
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
            
            // Crear cliente SOAP
            SunatBillService service = crearClienteSoap();
            
            // Enviar resumen a SUNAT
            String numeroTicket = service.sendSummary(nombreArchivo, contenidoZip);
            
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
            
            // Crear cliente SOAP
            SunatBillService service = crearClienteSoap();
            
            // Consultar ticket
            byte[] respuesta = service.getStatus(numeroTicket);
            
            log.info("Ticket {} consultado correctamente", numeroTicket);
            return respuesta;
        } catch (SOAPFaultException e) {
            log.error("Error SOAP al consultar ticket: {}", e.getMessage(), e);
            throw new FacturacionException("Error al consultar ticket: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error al consultar ticket: {}", e.getMessage(), e);
            throw new FacturacionException("Error al consultar ticket: " + e.getMessage(), e);
        }
    }
    
    /**
     * Configura la seguridad WS-Security para el cliente SOAP
     * 
     * @param client Cliente SOAP a configurar
     * @param usuario Usuario para autenticación
     * @param clave Clave para autenticación
     */
    private void configurarSeguridad(Client client, String usuario, String clave) {
        Map<String, Object> outProps = new HashMap<>();
        outProps.put(ConfigurationConstants.ACTION, ConfigurationConstants.USERNAME_TOKEN);
        outProps.put(ConfigurationConstants.USER, usuario);
        outProps.put(ConfigurationConstants.PASSWORD_TYPE, WSS4JConstants.PW_TEXT);
        outProps.put(ConfigurationConstants.PW_CALLBACK_REF, new PasswordCallbackHandler(usuario, clave));
        
        WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(outProps);
        client.getOutInterceptors().add(wssOut);
    }
    
    /**
     * Configura el timeout para el cliente SOAP
     * 
     * @param client Cliente SOAP a configurar
     */
    private void configurarTimeout(Client client) {
        HTTPConduit conduit = (HTTPConduit) client.getConduit();
        HTTPClientPolicy policy = new HTTPClientPolicy();
        policy.setConnectionTimeout(connectionTimeout);
        policy.setReceiveTimeout(connectionTimeout);
        conduit.setClient(policy);
    }
    
    @Override
    public String enviarLote(String nombreArchivo, byte[] contenidoZip) {
        try {
            log.info("Enviando lote {} a SUNAT", nombreArchivo);
            
            // Crear cliente SOAP
            SunatBillService service = crearClienteSoap();
            
            // Enviar lote a SUNAT y obtener número de ticket
            String numeroTicket = service.sendPack(nombreArchivo, contenidoZip);
            
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
            
            // Crear cliente SOAP
            SunatBillService service = crearClienteSoap();
            
            // Consultar CDR
            byte[] respuesta = service.getStatusCdr(ruc, tipoDocumento, serie, numero);
            
            log.info("CDR del documento {}-{}-{} consultado correctamente", tipoDocumento, serie, numero);
            return respuesta;
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
            
            // Crear cliente SOAP
            SunatBillService service = crearClienteSoap();
            
            // Consultar estado AR
            byte[] respuesta = service.getStatusAR(ruc, tipoDocumento, serie, numero);
            
            log.info("Estado AR del documento {}-{}-{} consultado correctamente", tipoDocumento, serie, numero);
            return respuesta;
        } catch (SOAPFaultException e) {
            log.error("Error SOAP al consultar estado AR: {}", e.getMessage(), e);
            throw new FacturacionException("Error al consultar estado AR: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error al consultar estado AR: {}", e.getMessage(), e);
            throw new FacturacionException("Error al consultar estado AR: " + e.getMessage(), e);
        }
    }
    

}
