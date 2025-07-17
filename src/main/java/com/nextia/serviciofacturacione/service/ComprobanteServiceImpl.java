package com.nextia.serviciofacturacione.service;

import com.nextia.serviciofacturacione.dto.BajaDocumentosRequest;
import com.nextia.serviciofacturacione.dto.FacturaRequest;
import com.nextia.serviciofacturacione.dto.ResumenDocumentosRequest;
import com.nextia.serviciofacturacione.model.Boleta;
import com.nextia.serviciofacturacione.model.CdrResponse;
import com.nextia.serviciofacturacione.model.NotaCredito;
import com.nextia.serviciofacturacione.model.NotaDebito;
import com.nextia.serviciofacturacione.model.common.Emisor;
import com.nextia.serviciofacturacione.service.resumen.ResumenDocumentosService;
import com.nextia.serviciofacturacione.service.baja.BajaDocumentosService;
import com.nextia.serviciofacturacione.service.boleta.BoletaService;
import com.nextia.serviciofacturacione.service.factura.FacturaService;
import com.nextia.serviciofacturacione.service.nota.NotaCreditoService;
import com.nextia.serviciofacturacione.service.nota.NotaDebitoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio principal para gestión de comprobantes electrónicos
 * Orquesta el flujo completo para todos los tipos de documentos
 */
@Service
public class ComprobanteServiceImpl implements ComprobanteService {

    private static final Logger log = LoggerFactory.getLogger(ComprobanteServiceImpl.class);
    
    @Autowired
    private FacturaService facturaService;
    
    @Autowired
    private BoletaService boletaService;
    
    @Autowired
    private NotaCreditoService notaCreditoService;
    
    @Autowired
    private NotaDebitoService notaDebitoService;

    @Autowired
    private ResumenDocumentosService resumenDocumentosService;

    @Autowired
    private BajaDocumentosService bajaDocumentosService;
    
    
    @Override
    public CdrResponse enviarFactura(FacturaRequest facturaRequest, Emisor emisor) {
        try {
            log.info("Iniciando proceso de envío de factura: {}-{}", facturaRequest.getComprobante().getSerie(), facturaRequest.getComprobante().getCorrelativo());
            CdrResponse respuesta = facturaService.enviarFactura(facturaRequest, emisor);
            log.info("Proceso de envío de factura completado. Código SUNAT: {}", respuesta.getCodigo());
            return respuesta;
        } catch (Exception e) {
            log.error("Error en el proceso de envío de factura", e);
            CdrResponse errorResponse = new CdrResponse("9999", "Error en el proceso de envío de factura: " + e.getMessage());
            return errorResponse;
        }
    }

    @Override
    public CdrResponse enviarBoleta(Boleta boleta, String ruc, String usuarioSol, String claveSol) {
        try {
            log.info("Iniciando proceso de envío de boleta: {}-{}", boleta.getSerie(), boleta.getCorrelativo());
            CdrResponse respuesta = boletaService.enviarBoleta(boleta, ruc, usuarioSol, claveSol);
            log.info("Proceso de envío de boleta completado. Código SUNAT: {}", respuesta.getCodigo());
            return respuesta;
        } catch (Exception e) {
            log.error("Error en el proceso de envío de boleta", e);
            CdrResponse errorResponse = new CdrResponse("9999", "Error en el proceso de envío de boleta: " + e.getMessage());
            return errorResponse;
        }
    }

    @Override
    public CdrResponse enviarNotaCredito(NotaCredito notaCredito, String ruc, String usuarioSol, String claveSol) {
        try {
            log.info("Iniciando proceso de envío de nota de crédito: {}-{}", notaCredito.getSerie(), notaCredito.getCorrelativo());
            CdrResponse respuesta = notaCreditoService.enviarNotaCredito(notaCredito, ruc, usuarioSol, claveSol);
            log.info("Proceso de envío de nota de crédito completado. Código SUNAT: {}", respuesta.getCodigo());
            return respuesta;
        } catch (Exception e) {
            log.error("Error en el proceso de envío de nota de crédito", e);
            CdrResponse errorResponse = new CdrResponse("9999", "Error en el proceso de envío de nota de crédito: " + e.getMessage());
            return errorResponse;
        }
    }

    @Override
    public CdrResponse enviarNotaDebito(NotaDebito notaDebito, String ruc, String usuarioSol, String claveSol) {
        try {
            log.info("Iniciando proceso de envío de nota de débito: {}-{}", notaDebito.getSerie(), notaDebito.getCorrelativo());
            CdrResponse respuesta = notaDebitoService.enviarNotaDebito(notaDebito, ruc, usuarioSol, claveSol);
            log.info("Proceso de envío de nota de débito completado. Código SUNAT: {}", respuesta.getCodigo());
            return respuesta;
        } catch (Exception e) {
            log.error("Error en el proceso de envío de nota de débito", e);
            CdrResponse errorResponse = new CdrResponse("9999", "Error en el proceso de envío de nota de débito: " + e.getMessage());
            return errorResponse;
        }
    }
    
    @Override
    public CdrResponse enviarResumenDocumentos(ResumenDocumentosRequest request, Emisor emisor) {
        try {
            log.info("Iniciando proceso de envío de resumen de documentos: {}-{}", request.getCabecera().getSerie(), request.getCabecera().getCorrelativo());
            CdrResponse respuesta = resumenDocumentosService.enviarResumenDocumentos(request, emisor);
            log.info("Proceso de envío de resumen de documentos completado. Código SUNAT: {}", respuesta.getCodigo());
            return respuesta;
        } catch (Exception e) {
            log.error("Error en el proceso de envío de resumen de documentos", e);
            CdrResponse errorResponse = new CdrResponse("9999", "Error en el proceso de envío de resumen de documentos: " + e.getMessage());
            return errorResponse;
        }
    }

    @Override
    public CdrResponse enviarBajaDocumentos(BajaDocumentosRequest request, Emisor emisor) {
        try {
            log.info("Iniciando proceso de envío de baja de documentos: {}-{}", request.getCabecera().getSerie(), request.getCabecera().getCorrelativo());
            CdrResponse respuesta = bajaDocumentosService.enviarBajaDocumentos(request, emisor);
            log.info("Proceso de envío de baja de documentos completado. Código SUNAT: {}", respuesta.getCodigo());
            return respuesta;
        } catch (Exception e) {
            log.error("Error en el proceso de envío de baja de documentos", e);
            CdrResponse errorResponse = new CdrResponse("9999", "Error en el proceso de envío de baja de documentos: " + e.getMessage());
            return errorResponse;
        }
    }

    @Override
    public CdrResponse consultarEstado(Emisor emisor, String tipoDocumento, String serie, String numero) {
        try {
            log.info("Consultando estado de comprobante: {}-{}-{}", tipoDocumento, serie, numero);
            
            CdrResponse respuesta;
            
            // Seleccionar el servicio adecuado según el tipo de documento
            switch (tipoDocumento) {
                case "01": // Factura
                    respuesta = facturaService.consultarEstado(emisor, tipoDocumento, serie, numero);
                    break;
                case "03": // Boleta
                    respuesta = boletaService.consultarEstado(emisor, tipoDocumento, serie, numero);
                    break;
                case "07": // Nota de Crédito
                    respuesta = notaCreditoService.consultarEstado(emisor, tipoDocumento, serie, numero);
                    break;
                case "08": // Nota de Débito
                    respuesta = notaDebitoService.consultarEstado(emisor, tipoDocumento, serie, numero);
                    break;
                default:
                    log.error("Tipo de documento no soportado: {}", tipoDocumento);
                    return new CdrResponse("9999", "Tipo de documento no soportado: " + tipoDocumento);
            }
            
            log.info("Consulta de estado completada. Código SUNAT: {}", respuesta.getCodigo());
            return respuesta;
        } catch (Exception e) {
            log.error("Error al consultar estado de comprobante", e);
            CdrResponse errorResponse = new CdrResponse("9999", "Error al consultar estado de comprobante: " + e.getMessage());
            return errorResponse;
        }
    }
}
