package com.nextia.serviciofacturacione.service.nota;

import com.nextia.serviciofacturacione.model.CdrResponse;
import com.nextia.serviciofacturacione.model.NotaDebito;
import com.nextia.serviciofacturacione.service.common.CdrProcessorService;
import com.nextia.serviciofacturacione.service.common.SunatSenderService;
import com.nextia.serviciofacturacione.service.common.XmlSignerService;
import com.nextia.serviciofacturacione.service.common.ZipCompressorService;
import com.nextia.serviciofacturacione.util.UblGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Implementación del servicio para gestión de notas de débito electrónicas
 */
@Service
public class NotaDebitoServiceImpl implements NotaDebitoService {
    
    private static final Logger log = LoggerFactory.getLogger(NotaDebitoServiceImpl.class);
    private static final String TIPO_DOCUMENTO = "08"; // 08 = Nota de Débito
    
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
    public byte[] generarXml(NotaDebito notaDebito) {
        try {
            log.info("Generando XML para nota de débito: {}-{}", notaDebito.getSerie(), notaDebito.getCorrelativo());
            String xmlContent = ublGenerator.generateNotaDebitoXml(notaDebito);
            return xmlContent.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error al generar XML de nota de débito", e);
            throw new RuntimeException("Error al generar XML de nota de débito: " + e.getMessage(), e);
        }
    }
    
    @Override
    public CdrResponse enviarNotaDebito(NotaDebito notaDebito, String ruc, String usuarioSol, String claveSol) {
        try {
            log.info("Iniciando proceso de envío de nota de débito: {}-{}", notaDebito.getSerie(), notaDebito.getCorrelativo());
            
            // Paso 1: Generar XML
            byte[] xml = generarXml(notaDebito);
            
            // Paso 2: Firmar XML
            byte[] xmlFirmado = xmlSignerService.firmarXml(xml);
            
            // Paso 3: Comprimir XML firmado a ZIP
            String nombreArchivo = generarNombreArchivo(ruc, TIPO_DOCUMENTO, notaDebito.getSerie(), notaDebito.getCorrelativo());
            byte[] zip = zipCompressorService.comprimirXml(nombreArchivo + ".xml", xmlFirmado);
            
            // Paso 4: Enviar ZIP a SUNAT
            byte[] cdrZip = sunatSenderService.enviarArchivo(nombreArchivo + ".zip", zip, ruc, usuarioSol, claveSol);
            
            // Paso 5: Procesar ZIP de CDR
            CdrResponse respuesta = cdrProcessorService.procesarZip(cdrZip);
            log.info("Nota de débito {}-{} enviada. Respuesta SUNAT: {}", 
                    notaDebito.getSerie(), notaDebito.getCorrelativo(), respuesta.getCodigo());
            
            return respuesta;
        } catch (Exception e) {
            log.error("Error al enviar nota de débito a SUNAT", e);
            CdrResponse errorResponse = new CdrResponse("9999", "Error al enviar nota de débito: " + e.getMessage());
            return errorResponse;
        }
    }
    
    @Override
    public CdrResponse consultarEstado(String ruc, String tipoDocumento, String serie, String numero, 
                                      String usuarioSol, String claveSol) {
        try {
            log.info("Consultando estado de nota de débito: {}-{}", serie, numero);
            byte[] respuestaBytes = sunatSenderService.consultarEstado(ruc, tipoDocumento, serie, numero, 
                                                                      usuarioSol, claveSol);
            CdrResponse respuesta = cdrProcessorService.procesarXml(respuestaBytes);
            log.info("Consulta de estado de nota de débito {}-{} completada. Respuesta SUNAT: {}", 
                    serie, numero, respuesta.getCodigo());
            
            return respuesta;
        } catch (Exception e) {
            log.error("Error al consultar estado de nota de débito", e);
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
