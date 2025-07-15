package com.nextia.serviciofacturacione.service.factura;

import com.nextia.serviciofacturacione.exception.FacturacionException;
import com.nextia.serviciofacturacione.model.CdrResponse;
import com.nextia.serviciofacturacione.model.Factura;
import com.nextia.serviciofacturacione.service.common.CdrProcessorService;
import com.nextia.serviciofacturacione.service.common.SunatSenderService;
import com.nextia.serviciofacturacione.service.common.XmlSignerService;
import com.nextia.serviciofacturacione.service.common.ZipCompressorService;
import com.nextia.serviciofacturacione.util.UblGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Implementación del servicio para gestión de facturas electrónicas
 */
@Service
public class FacturaServiceImpl implements FacturaService {
    
    private static final Logger log = LoggerFactory.getLogger(FacturaServiceImpl.class);
    private static final String TIPO_DOCUMENTO = "01"; // 01 = Factura
    
    private final UblGenerator ublGenerator;
    private final XmlSignerService xmlSignerService;
    private final ZipCompressorService zipCompressorService;
    private final SunatSenderService sunatSenderService;
    private final CdrProcessorService cdrProcessorService;
    
    public FacturaServiceImpl(UblGenerator ublGenerator,
                             XmlSignerService xmlSignerService,
                             ZipCompressorService zipCompressorService,
                             SunatSenderService sunatSenderService,
                             CdrProcessorService cdrProcessorService) {
        this.ublGenerator = ublGenerator;
        this.xmlSignerService = xmlSignerService;
        this.zipCompressorService = zipCompressorService;
        this.sunatSenderService = sunatSenderService;
        this.cdrProcessorService = cdrProcessorService;
    }
    
    @Override
    public byte[] generarXml(Factura factura) {
        try {
            log.info("Generando XML para factura: {}-{}", factura.getSerie(), factura.getCorrelativo());
            String xmlContent = ublGenerator.generateFacturaXml(factura);
            return xmlContent.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error al generar XML de factura: {}", e.getMessage(), e);
            throw new FacturacionException("Error al generar XML de factura: " + e.getMessage(), e);
        }
    }
    
    @Override
    public CdrResponse enviarFactura(Factura factura, String ruc, String usuarioSol, String claveSol) {
        try {
            log.info("Iniciando proceso de envío de factura: {}-{}", factura.getSerie(), factura.getCorrelativo());
            
            // Paso 1: Generar XML
            byte[] xml = generarXml(factura);
            
            // Paso 2: Firmar XML
            byte[] xmlFirmado = xmlSignerService.firmarXml(xml);
            
            // Paso 3: Comprimir XML firmado a ZIP
            String nombreArchivo = generarNombreArchivo(ruc, TIPO_DOCUMENTO, factura.getSerie(), factura.getCorrelativo());
            byte[] zip = zipCompressorService.comprimirXml(nombreArchivo + ".xml", xmlFirmado);
            
            // Paso 4: Enviar ZIP a SUNAT (usando credenciales inyectadas)
            byte[] cdrZip = sunatSenderService.enviarArchivo(nombreArchivo + ".zip", zip);
            
            // Paso 5: Procesar ZIP de CDR
            CdrResponse respuesta = cdrProcessorService.procesarZip(cdrZip);
            log.info("Factura {}-{} enviada. Respuesta SUNAT: {}", 
                    factura.getSerie(), factura.getCorrelativo(), respuesta.getCodigo());
            
            return respuesta;
        } catch (Exception e) {
            log.error("Error al enviar factura a SUNAT", e);
            return new CdrResponse("9999", "Error al enviar factura: " + e.getMessage());
        }
    }
    
    @Override
    public CdrResponse consultarEstado(String ruc, String tipoDocumento, String serie, String numero, 
                                      String usuarioSol, String claveSol) {
        try {
            log.info("Consultando estado de factura: {}-{}", serie, numero);
            // Usar método actualizado con credenciales inyectadas
            byte[] respuestaBytes = sunatSenderService.consultarEstado(tipoDocumento, serie, numero);
            CdrResponse respuesta = cdrProcessorService.procesarXml(respuestaBytes);
            log.info("Consulta de estado de factura {}-{} completada. Respuesta SUNAT: {}", 
                    serie, numero, respuesta.getCodigo());
            
            return respuesta;
        } catch (FacturacionException e) {
            log.error("Error al consultar estado de factura", e);
            return new CdrResponse("9999", "Error al consultar estado de factura: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error al consultar estado de factura", e);
            throw new FacturacionException("Error al consultar estado de factura: " + e.getMessage(), e);
        }
    }
    
    /**
     * Genera el nombre del archivo según el estándar de SUNAT
     * Formato: [RUC]-[TIPO_DOCUMENTO]-[SERIE]-[NUMERO]
     */
    private String generarNombreArchivo(String ruc, String tipoDocumento, String serie, String numero) {
        return String.format("%s-%s-%s-%s", ruc, tipoDocumento, serie, numero);
    }
}
