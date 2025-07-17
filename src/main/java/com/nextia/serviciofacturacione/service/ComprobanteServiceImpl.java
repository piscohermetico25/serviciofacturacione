package com.nextia.serviciofacturacione.service;

import com.nextia.serviciofacturacione.dto.BajaDocumentosRequest;
import com.nextia.serviciofacturacione.dto.ComprobanteRequest;
import com.nextia.serviciofacturacione.dto.ResumenDocumentosRequest;
import com.nextia.serviciofacturacione.exception.FacturacionException;
import com.nextia.serviciofacturacione.model.CdrResponse;
import com.nextia.serviciofacturacione.model.common.Emisor;
import com.nextia.serviciofacturacione.service.sunat.SunatSenderService;
import com.nextia.serviciofacturacione.service.common.CdrProcessorService;
import com.nextia.serviciofacturacione.service.common.GeneracionXmlService;
import com.nextia.serviciofacturacione.service.common.XmlSignerService;
import com.nextia.serviciofacturacione.service.common.ZipCompressorService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ComprobanteServiceImpl implements ComprobanteService {

    private static final Logger log = LoggerFactory.getLogger(ComprobanteServiceImpl.class);

    @Autowired
    private GeneracionXmlService generacionXmlService;

    @Autowired
    private XmlSignerService xmlSignerService;

    @Autowired
    private ZipCompressorService zipCompressorService;

    @Autowired
    private SunatSenderService sunatSenderService;

    @Autowired
    private CdrProcessorService cdrProcessorService;

    @Override
    public CdrResponse enviarFactura(ComprobanteRequest comprobanteRequest, Emisor emisor) {
        try {

            String TIPO_DOCUMENTO = "01";
            // Paso 01 - Generar nombre del archivo
            String nombreArchivo = generarNombreArchivo(emisor.getRuc(), TIPO_DOCUMENTO,
                    comprobanteRequest.getComprobante().getSerie(),
                    comprobanteRequest.getComprobante().getCorrelativo());

            // Paso 02 - Generar XML
            byte[] xml;
            try {
                xml = generacionXmlService.generarXml(TIPO_DOCUMENTO, nombreArchivo, emisor, comprobanteRequest);
            } catch (FacturacionException e) {
                log.error("Error al generar XML: {}", e.getMessage(), e);
                return new CdrResponse("9999", "Error al generar XML: " + e.getMessage());
            }

            // Paso 03 - Guardar XML
            try {
                guardarXmlEnDescargas(xml, nombreArchivo + ".xml");
            } catch (FacturacionException e) {
                log.error("Error al guardar XML: {}", e.getMessage(), e);
                return new CdrResponse("9999", "Error al guardar XML: " + e.getMessage());
            }

            // Paso 04 - Firmar XML
            byte[] xmlFirmado = xmlSignerService.firmarXml(xml);

            // Paso 05 - Comprimir XML firmado a ZIP
            byte[] zip = zipCompressorService.comprimirXml(nombreArchivo + ".xml", xmlFirmado);

            // Paso 06 - Enviar ZIP a SUNAT (usando credenciales inyectadas)
            byte[] cdrZip = sunatSenderService.enviarArchivo(nombreArchivo + ".zip", zip);

            // Paso 07 - Procesar ZIP de CDR
            CdrResponse respuesta = cdrProcessorService.procesarZip(cdrZip);
            log.info("Factura {}-{} enviada. Respuesta SUNAT: {}",
                    comprobanteRequest.getComprobante().getSerie(),
                    comprobanteRequest.getComprobante().getCorrelativo(), respuesta.getCodigo());

            return respuesta;
        } catch (Exception e) {
            log.error("Error en el proceso de envío de factura", e);
            CdrResponse errorResponse = new CdrResponse("9999",
                    "Error en el proceso de envío de factura: " + e.getMessage());
            return errorResponse;
        }
    }

    @Override
    public CdrResponse enviarBoleta(ComprobanteRequest comprobanteRequest, Emisor emisor) {
        try {

            String TIPO_DOCUMENTO = "03";
            // Paso 01 - Generar nombre del archivo
            String nombreArchivo = generarNombreArchivo(emisor.getRuc(), TIPO_DOCUMENTO,
                    comprobanteRequest.getComprobante().getSerie(),
                    comprobanteRequest.getComprobante().getCorrelativo());

            // Paso 02 - Generar XML
            byte[] xml;
            try {
                xml = generacionXmlService.generarXml(TIPO_DOCUMENTO, nombreArchivo, emisor, comprobanteRequest);
            } catch (FacturacionException e) {
                log.error("Error al generar XML: {}", e.getMessage(), e);
                return new CdrResponse("9999", "Error al generar XML: " + e.getMessage());
            }

            // Paso 03 - Guardar XML
            try {
                guardarXmlEnDescargas(xml, nombreArchivo + ".xml");
            } catch (FacturacionException e) {
                log.error("Error al guardar XML: {}", e.getMessage(), e);
                return new CdrResponse("9999", "Error al guardar XML: " + e.getMessage());
            }

            // Paso 04 - Firmar XML
            byte[] xmlFirmado = xmlSignerService.firmarXml(xml);

            // Paso 05 - Comprimir XML firmado a ZIP
            byte[] zip = zipCompressorService.comprimirXml(nombreArchivo + ".xml", xmlFirmado);

            // Paso 06 - Enviar ZIP a SUNAT (usando credenciales inyectadas)
            byte[] cdrZip = sunatSenderService.enviarArchivo(nombreArchivo + ".zip", zip);

            // Paso 07 - Procesar ZIP de CDR
            CdrResponse respuesta = cdrProcessorService.procesarZip(cdrZip);
            log.info("Factura {}-{} enviada. Respuesta SUNAT: {}",
                    comprobanteRequest.getComprobante().getSerie(),
                    comprobanteRequest.getComprobante().getCorrelativo(), respuesta.getCodigo());

            return respuesta;
        } catch (Exception e) {
            log.error("Error en el proceso de envío de factura", e);
            CdrResponse errorResponse = new CdrResponse("9999",
                    "Error en el proceso de envío de factura: " + e.getMessage());
            return errorResponse;
        }
    }

    @Override
    public CdrResponse enviarNotaCredito(ComprobanteRequest comprobanteRequest, Emisor emisor) {
        try {

            String TIPO_DOCUMENTO = "07";
            // Paso 01 - Generar nombre del archivo
            String nombreArchivo = generarNombreArchivo(emisor.getRuc(), TIPO_DOCUMENTO,
                    comprobanteRequest.getComprobante().getSerie(),
                    comprobanteRequest.getComprobante().getCorrelativo());

            // Paso 02 - Generar XML
            byte[] xml;
            try {
                xml = generacionXmlService.generarXml(TIPO_DOCUMENTO, nombreArchivo, emisor, comprobanteRequest);
            } catch (FacturacionException e) {
                log.error("Error al generar XML: {}", e.getMessage(), e);
                return new CdrResponse("9999", "Error al generar XML: " + e.getMessage());
            }

            // Paso 03 - Guardar XML
            try {
                guardarXmlEnDescargas(xml, nombreArchivo + ".xml");
            } catch (FacturacionException e) {
                log.error("Error al guardar XML: {}", e.getMessage(), e);
                return new CdrResponse("9999", "Error al guardar XML: " + e.getMessage());
            }

            // Paso 04 - Firmar XML
            byte[] xmlFirmado = xmlSignerService.firmarXml(xml);

            // Paso 05 - Comprimir XML firmado a ZIP
            byte[] zip = zipCompressorService.comprimirXml(nombreArchivo + ".xml", xmlFirmado);

            // Paso 06 - Enviar ZIP a SUNAT (usando credenciales inyectadas)
            byte[] cdrZip = sunatSenderService.enviarArchivo(nombreArchivo + ".zip", zip);

            // Paso 07 - Procesar ZIP de CDR
            CdrResponse respuesta = cdrProcessorService.procesarZip(cdrZip);
            log.info("Factura {}-{} enviada. Respuesta SUNAT: {}",
                    comprobanteRequest.getComprobante().getSerie(),
                    comprobanteRequest.getComprobante().getCorrelativo(), respuesta.getCodigo());

            return respuesta;
        } catch (Exception e) {
            log.error("Error en el proceso de envío de factura", e);
            CdrResponse errorResponse = new CdrResponse("9999",
                    "Error en el proceso de envío de factura: " + e.getMessage());
            return errorResponse;
        }
    }

    @Override
    public CdrResponse enviarNotaDebito(ComprobanteRequest comprobanteRequest, Emisor emisor) {
        try {

            String TIPO_DOCUMENTO = "08";
            // Paso 01 - Generar nombre del archivo
            String nombreArchivo = generarNombreArchivo(emisor.getRuc(), TIPO_DOCUMENTO,
                    comprobanteRequest.getComprobante().getSerie(),
                    comprobanteRequest.getComprobante().getCorrelativo());

            // Paso 02 - Generar XML
            byte[] xml;
            try {
                xml = generacionXmlService.generarXml(TIPO_DOCUMENTO, nombreArchivo, emisor, comprobanteRequest);
            } catch (FacturacionException e) {
                log.error("Error al generar XML: {}", e.getMessage(), e);
                return new CdrResponse("9999", "Error al generar XML: " + e.getMessage());
            }

            // Paso 03 - Guardar XML
            try {
                guardarXmlEnDescargas(xml, nombreArchivo + ".xml");
            } catch (FacturacionException e) {
                log.error("Error al guardar XML: {}", e.getMessage(), e);
                return new CdrResponse("9999", "Error al guardar XML: " + e.getMessage());
            }

            // Paso 04 - Firmar XML
            byte[] xmlFirmado = xmlSignerService.firmarXml(xml);

            // Paso 05 - Comprimir XML firmado a ZIP
            byte[] zip = zipCompressorService.comprimirXml(nombreArchivo + ".xml", xmlFirmado);

            // Paso 06 - Enviar ZIP a SUNAT (usando credenciales inyectadas)
            byte[] cdrZip = sunatSenderService.enviarArchivo(nombreArchivo + ".zip", zip);

            // Paso 07 - Procesar ZIP de CDR
            CdrResponse respuesta = cdrProcessorService.procesarZip(cdrZip);
            log.info("Factura {}-{} enviada. Respuesta SUNAT: {}",
                    comprobanteRequest.getComprobante().getSerie(),
                    comprobanteRequest.getComprobante().getCorrelativo(), respuesta.getCodigo());

            return respuesta;
        } catch (Exception e) {
            log.error("Error en el proceso de envío de factura", e);
            CdrResponse errorResponse = new CdrResponse("9999",
                    "Error en el proceso de envío de factura: " + e.getMessage());
            return errorResponse;
        }
    }

    @Override
    public CdrResponse enviarResumenDocumentos(ResumenDocumentosRequest request, Emisor emisor) {
        try {

            String TIPO_DOCUMENTO = "00";
            // Paso 01 - Generar nombre del archivo
            String nombreArchivo = generarNombreArchivo(emisor.getRuc(), TIPO_DOCUMENTO,
                    request.getCabecera().getSerie(),
                    request.getCabecera().getCorrelativo());

            // Paso 02 - Generar XML
            byte[] xml;
            try {
                xml = generacionXmlService.generarXml(nombreArchivo, emisor, request);
            } catch (FacturacionException e) {
                log.error("Error al generar XML: {}", e.getMessage(), e);
                return new CdrResponse("9999", "Error al generar XML: " + e.getMessage());
            }

            // Paso 03 - Guardar XML
            try {
                guardarXmlEnDescargas(xml, nombreArchivo + ".xml");
            } catch (FacturacionException e) {
                log.error("Error al guardar XML: {}", e.getMessage(), e);
                return new CdrResponse("9999", "Error al guardar XML: " + e.getMessage());
            }

            // Paso 04 - Firmar XML
            byte[] xmlFirmado = xmlSignerService.firmarXml(xml);

            // Paso 05 - Comprimir XML firmado a ZIP
            byte[] zip = zipCompressorService.comprimirXml(nombreArchivo + ".xml", xmlFirmado);

            // Paso 06 - Enviar ZIP a SUNAT (usando credenciales inyectadas)
            byte[] cdrZip = sunatSenderService.enviarArchivo(nombreArchivo + ".zip", zip);

            // Paso 07 - Procesar ZIP de CDR
            CdrResponse respuesta = cdrProcessorService.procesarZip(cdrZip);
            log.info("Factura {}-{} enviada. Respuesta SUNAT: {}",
                    request.getCabecera().getSerie(),
                    request.getCabecera().getCorrelativo(), respuesta.getCodigo());

            return respuesta;
        } catch (Exception e) {
            log.error("Error en el proceso de envío de factura", e);
            CdrResponse errorResponse = new CdrResponse("9999",
                    "Error en el proceso de envío de factura: " + e.getMessage());
            return errorResponse;
        }
    }

    @Override
    public CdrResponse enviarBajaDocumentos(BajaDocumentosRequest request, Emisor emisor) {
        try {

            String TIPO_DOCUMENTO = "00";
            // Paso 01 - Generar nombre del archivo
            String nombreArchivo = generarNombreArchivo(emisor.getRuc(), TIPO_DOCUMENTO,
                    request.getCabecera().getSerie(),
                    request.getCabecera().getCorrelativo());

            // Paso 02 - Generar XML
            byte[] xml;
            try {
                xml = generacionXmlService.generarXml(nombreArchivo, emisor, request);
            } catch (FacturacionException e) {
                log.error("Error al generar XML: {}", e.getMessage(), e);
                return new CdrResponse("9999", "Error al generar XML: " + e.getMessage());
            }

            // Paso 03 - Guardar XML
            try {
                guardarXmlEnDescargas(xml, nombreArchivo + ".xml");
            } catch (FacturacionException e) {
                log.error("Error al guardar XML: {}", e.getMessage(), e);
                return new CdrResponse("9999", "Error al guardar XML: " + e.getMessage());
            }

            // Paso 04 - Firmar XML
            byte[] xmlFirmado = xmlSignerService.firmarXml(xml);

            // Paso 05 - Comprimir XML firmado a ZIP
            byte[] zip = zipCompressorService.comprimirXml(nombreArchivo + ".xml", xmlFirmado);

            // Paso 06 - Enviar ZIP a SUNAT (usando credenciales inyectadas)
            byte[] cdrZip = sunatSenderService.enviarArchivo(nombreArchivo + ".zip", zip);

            // Paso 07 - Procesar ZIP de CDR
            CdrResponse respuesta = cdrProcessorService.procesarZip(cdrZip);
            log.info("Factura {}-{} enviada. Respuesta SUNAT: {}",
                    request.getCabecera().getSerie(),
                    request.getCabecera().getCorrelativo(), respuesta.getCodigo());

            return respuesta;
        } catch (Exception e) {
            log.error("Error en el proceso de envío de factura", e);
            CdrResponse errorResponse = new CdrResponse("9999",
                    "Error en el proceso de envío de factura: " + e.getMessage());
            return errorResponse;
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

    private String generarNombreArchivo(String ruc, String tipoDocumento, String serie, String numero) {
        return String.format("%s-%s-%s-%s", ruc, tipoDocumento, serie, numero);
    }

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
