package com.nextia.serviciofacturacione.service.factura;

import com.nextia.serviciofacturacione.dto.FacturaRequest;
import com.nextia.serviciofacturacione.exception.FacturacionException;
import com.nextia.serviciofacturacione.model.CdrResponse;
import com.nextia.serviciofacturacione.model.common.Emisor;
import com.nextia.serviciofacturacione.service.common.CdrProcessorService;
import com.nextia.serviciofacturacione.service.sunat.SunatSenderService;
import com.nextia.serviciofacturacione.service.common.XmlSignerService;
import com.nextia.serviciofacturacione.service.common.ZipCompressorService;
import com.nextia.serviciofacturacione.util.UblGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    public byte[] generarXml(String nombreArchivo, Emisor emisor, FacturaRequest facturaRequest) {
        try {
            log.info("Generando XML para factura: {}-{}", facturaRequest.getComprobante().getSerie(), facturaRequest.getComprobante().getCorrelativo());
            String xmlContent = ublGenerator.generateFacturaXml(nombreArchivo,emisor, facturaRequest.getCliente(), facturaRequest.getComprobante(), facturaRequest.getDetalle());
            return xmlContent.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            String mensaje = String.format("Error al generar XML de factura %s-%s: %s", 
                    facturaRequest.getComprobante().getSerie(), facturaRequest.getComprobante().getCorrelativo(), e.getMessage());
            log.error(mensaje, e);
            throw new FacturacionException(mensaje, e);
        }
    }
    
    @Override
    public CdrResponse enviarFactura(FacturaRequest facturaRequest, Emisor emisor) {
        try {
            log.info("Iniciando proceso de envío de factura: {}-{}", facturaRequest.getComprobante().getSerie(), facturaRequest.getComprobante().getCorrelativo());
            
            // Generar nombre del archivo
            String nombreArchivo = generarNombreArchivo(emisor.getRuc(), TIPO_DOCUMENTO, facturaRequest.getComprobante().getSerie(), facturaRequest.getComprobante().getCorrelativo());

            // Paso 1: Generar XML
            byte[] xml;
            try {
                xml = generarXml(nombreArchivo, emisor, facturaRequest);
            } catch (FacturacionException e) {
                log.error("Error al generar XML: {}", e.getMessage(), e);
                return new CdrResponse("9999", "Error al generar XML: " + e.getMessage());
            }
            
            
            try {
                guardarXmlEnDescargas(xml, nombreArchivo + ".xml");
            } catch (FacturacionException e) {
                log.error("Error al guardar XML: {}", e.getMessage(), e);
                return new CdrResponse("9999", "Error al guardar XML: " + e.getMessage());
            }
            
            // Paso 2: Firmar XML
            byte[] xmlFirmado = xmlSignerService.firmarXml(xml);
            
            // Paso 3: Comprimir XML firmado a ZIP
            byte[] zip = zipCompressorService.comprimirXml(nombreArchivo + ".xml", xmlFirmado);
            
            // Paso 4: Enviar ZIP a SUNAT (usando credenciales inyectadas)
            byte[] cdrZip = sunatSenderService.enviarArchivo(nombreArchivo + ".zip", zip);
            
            // Paso 5: Procesar ZIP de CDR
            CdrResponse respuesta = cdrProcessorService.procesarZip(cdrZip);
            log.info("Factura {}-{} enviada. Respuesta SUNAT: {}", 
                    facturaRequest.getComprobante().getSerie(), facturaRequest.getComprobante().getCorrelativo(), respuesta.getCodigo());
            
            return respuesta;
        } catch (FacturacionException e) {
            String mensaje = String.format("Error al enviar factura %s-%s a SUNAT: %s", 
                    facturaRequest.getComprobante().getSerie(), facturaRequest.getComprobante().getCorrelativo(), e.getMessage());
            log.error(mensaje, e);
            return new CdrResponse("9999", mensaje);
        } catch (Exception e) {
            String mensaje = String.format("Error inesperado al enviar factura %s-%s a SUNAT: %s", 
                    facturaRequest.getComprobante().getSerie(), facturaRequest.getComprobante().getCorrelativo(), e.getMessage());
            log.error(mensaje, e);
            return new CdrResponse("9999", mensaje);
        }
    }
    
    @Override
    public CdrResponse consultarEstado(Emisor emisor, String tipoDocumento, String serie, String numero) {
        try {
            log.info("Consultando estado de factura: {}-{}", serie, numero);
            // Usar método actualizado con credenciales inyectadas
            byte[] respuestaBytes = sunatSenderService.consultarEstado(emisor, tipoDocumento, serie, numero);
            CdrResponse respuesta = cdrProcessorService.procesarXml(respuestaBytes);
            log.info("Consulta de estado de factura {}-{} completada. Respuesta SUNAT: {}", 
                    serie, numero, respuesta.getCodigo());
            
            return respuesta;
        } catch (FacturacionException e) {
            String mensaje = String.format("Error al consultar estado de factura %s-%s: %s", 
                    serie, numero, e.getMessage());
            log.error(mensaje, e);
            return new CdrResponse("9999", mensaje);
        } catch (Exception e) {
            String mensaje = String.format("Error al consultar estado de factura %s-%s: %s", 
                    serie, numero, e.getMessage());
            log.error(mensaje, e);
            throw new FacturacionException(mensaje, e);
        }
    }
    
    /**
     * Genera el nombre del archivo según el estándar de SUNAT
     * Formato: [RUC]-[TIPO_DOCUMENTO]-[SERIE]-[NUMERO]
     */
    private String generarNombreArchivo(String ruc, String tipoDocumento, String serie, String numero) {
        return String.format("%s-%s-%s-%s", ruc, tipoDocumento, serie, numero);
    }
    
    /**
     * Guarda el archivo XML en la carpeta resources/descargas
     * 
     * @param xml Contenido del archivo XML en bytes
     * @param nombreArchivo Nombre del archivo a guardar
     * @throws FacturacionException Si ocurre un error al guardar el archivo
     */
    private void guardarXmlEnDescargas(byte[] xml, String nombreArchivo) {
        Path rutaDescargas = null;
        Path rutaArchivo = null;
        
        try {
            // Crear la ruta al directorio de descargas
            rutaDescargas = Paths.get("src", "main", "resources", "descargas");
            
            // Verificar si el directorio existe, si no, crearlo
            if (!Files.exists(rutaDescargas)) {
                Files.createDirectories(rutaDescargas);
                log.info("Directorio de descargas creado: {}", rutaDescargas);
            }
            
            // Crear la ruta completa del archivo
            rutaArchivo = rutaDescargas.resolve(nombreArchivo);
            
            // Guardar el archivo
            Files.write(rutaArchivo, xml);
            log.info("Archivo XML guardado exitosamente en: {}", rutaArchivo);
        } catch (IOException e) {
            String mensaje = String.format("Error al guardar el archivo XML '%s' en la ruta '%s': %s", 
                    nombreArchivo, 
                    (rutaDescargas != null ? rutaDescargas : "desconocida"), 
                    e.getMessage());
            log.error(mensaje, e);
            throw new FacturacionException(mensaje, e);
        }
    }
}
