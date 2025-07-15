package com.nextia.serviciofacturacione.service.nota;

import com.nextia.serviciofacturacione.model.CdrResponse;
import com.nextia.serviciofacturacione.model.NotaCredito;
import com.nextia.serviciofacturacione.service.common.CdrProcessorService;
import com.nextia.serviciofacturacione.service.sunat.SunatSenderService;
import com.nextia.serviciofacturacione.service.common.XmlSignerService;
import com.nextia.serviciofacturacione.service.common.ZipCompressorService;
import com.nextia.serviciofacturacione.util.UblGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Implementación del servicio para gestión de notas de crédito electrónicas
 */
@Service
public class NotaCreditoServiceImpl implements NotaCreditoService {
    
    private static final Logger log = LoggerFactory.getLogger(NotaCreditoServiceImpl.class);
    private static final String TIPO_DOCUMENTO = "07"; // 07 = Nota de Crédito
    
    @Autowired
    private UblGenerator ublGenerator;
    
    @Autowired
    private XmlSignerService xmlSignerService;
    
    @Autowired
    private ZipCompressorService zipCompressorService;
    
    @Autowired
    private SunatSenderService sunatSenderService;
    
    @Autowired
    private CdrProcessorService cdrProcessorService;
    
    @Override
    public byte[] generarXml(NotaCredito notaCredito) {
        try {
            log.info("Generando XML para nota de crédito: {}-{}", notaCredito.getSerie(), notaCredito.getCorrelativo());
            String xmlContent = ublGenerator.generateNotaCreditoXml(notaCredito);
            return xmlContent.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error al generar XML de nota de crédito", e);
            throw new RuntimeException("Error al generar XML de nota de crédito: " + e.getMessage(), e);
        }
    }
    
    @Override
    public CdrResponse enviarNotaCredito(NotaCredito notaCredito, String ruc, String usuarioSol, String claveSol) {
        try {
            log.info("Iniciando proceso de envío de nota de crédito: {}-{}", notaCredito.getSerie(), notaCredito.getCorrelativo());
            
            // Paso 1: Generar XML
            byte[] xml = generarXml(notaCredito);
            
            // Paso 2: Firmar XML
            byte[] xmlFirmado = xmlSignerService.firmarXml(xml);
            
            // Paso 3: Comprimir XML firmado a ZIP
            String nombreArchivo = generarNombreArchivo(ruc, TIPO_DOCUMENTO, notaCredito.getSerie(), notaCredito.getCorrelativo());
            byte[] zip = zipCompressorService.comprimirXml(nombreArchivo + ".xml", xmlFirmado);
            
            // Paso 4: Enviar ZIP a SUNAT (usando credenciales inyectadas)
            byte[] cdrZip = sunatSenderService.enviarArchivo(nombreArchivo + ".zip", zip);
            
            // Paso 5: Procesar ZIP de CDR
            CdrResponse respuesta = cdrProcessorService.procesarZip(cdrZip);
            log.info("Nota de crédito {}-{} enviada. Respuesta SUNAT: {}", 
                    notaCredito.getSerie(), notaCredito.getCorrelativo(), respuesta.getCodigo());
            
            return respuesta;
        } catch (Exception e) {
            log.error("Error al enviar nota de crédito a SUNAT", e);
            CdrResponse errorResponse = new CdrResponse("9999", "Error al enviar nota de crédito: " + e.getMessage());
            return errorResponse;
        }
    }
    
    @Override
    public CdrResponse consultarEstado(String ruc, String tipoDocumento, String serie, String numero, 
                                      String usuarioSol, String claveSol) {
        try {
            log.info("Consultando estado de nota de crédito: {}-{}", serie, numero);
            // Usar método actualizado con credenciales inyectadas
            byte[] respuestaBytes = sunatSenderService.consultarEstado(tipoDocumento, serie, numero);
            CdrResponse respuesta = cdrProcessorService.procesarXml(respuestaBytes);
            log.info("Consulta de estado de nota de crédito {}-{} completada. Respuesta SUNAT: {}", 
                    serie, numero, respuesta.getCodigo());
            
            return respuesta;
        } catch (Exception e) {
            log.error("Error al consultar estado de nota de crédito", e);
            CdrResponse errorResponse = new CdrResponse("9999", "Error al consultar estado: " + e.getMessage());
            return errorResponse;
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
