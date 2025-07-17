package com.nextia.serviciofacturacione.controller;

import com.nextia.serviciofacturacione.dto.BoletaRequest;
import com.nextia.serviciofacturacione.dto.CdrResponseDto;
import com.nextia.serviciofacturacione.dto.FacturaRequest;
import com.nextia.serviciofacturacione.dto.NotaCreditoRequest;
import com.nextia.serviciofacturacione.dto.NotaDebitoRequest;
import com.nextia.serviciofacturacione.model.Boleta;
import com.nextia.serviciofacturacione.model.CdrResponse;
import com.nextia.serviciofacturacione.model.NotaCredito;
import com.nextia.serviciofacturacione.model.NotaDebito;
import com.nextia.serviciofacturacione.model.common.Emisor;
import com.nextia.serviciofacturacione.service.ComprobanteService;
import com.nextia.serviciofacturacione.service.emisor.EmisorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

/**
 * Controlador REST para la gestión de comprobantes electrónicos
 */
@RestController
@RequestMapping("/api/comprobantes")
public class ComprobanteController {

    private static final Logger log = LoggerFactory.getLogger(ComprobanteController.class);
    
    @Autowired
    private ComprobanteService comprobanteService;

    @Autowired
    private EmisorService emisorService;
    
    /**
     * Endpoint para enviar una factura electrónica a SUNAT
     */
    @PostMapping("/factura")
    public ResponseEntity<CdrResponseDto> enviarFactura(@RequestBody FacturaRequest request) {
        log.info("Recibida solicitud para enviar factura: {}-{}", request.getComprobante().getSerie(), request.getComprobante().getCorrelativo());

        //crear un servicio para obtener el emisor y los datos del emisor obtenerlos del properties
   

        Emisor emisor = emisorService.obtenerEmisor();

        // Enviar factura a SUNAT
        CdrResponse respuesta = comprobanteService.enviarFactura(request, emisor);
        
        // Convertir respuesta a DTO
        CdrResponseDto responseDto = convertirRespuesta(respuesta);
        
        return ResponseEntity.ok(responseDto);
    }
    
    /**
     * Endpoint para enviar una boleta electrónica a SUNAT
     */
    @PostMapping("/boleta")
    public ResponseEntity<CdrResponseDto> enviarBoleta(@RequestBody BoletaRequest request) {
        log.info("Recibida solicitud para enviar boleta: {}-{}", request.getSerie(), request.getNumero());
        
        // Convertir DTO a modelo
        Boleta boleta = new Boleta();
        boleta.setTipoDocumento("03");
        boleta.setSerie(request.getSerie());
        boleta.setCorrelativo(request.getNumero());
        boleta.setFechaEmision(request.getFechaEmision());
        boleta.setHoraEmision(request.getHoraEmision());
        boleta.setMoneda(request.getMoneda());
        boleta.setEmisor(request.getEmisor());
        boleta.setReceptor(request.getCliente());
        boleta.setDetalles(request.getItems());
        boleta.setTotales(request.getTotales());
        
        // Enviar boleta a SUNAT
        CdrResponse respuesta = comprobanteService.enviarBoleta(boleta, request.getRuc(), 
                                                              request.getUsuarioSol(), request.getClaveSol());
        
        // Convertir respuesta a DTO
        CdrResponseDto responseDto = convertirRespuesta(respuesta);
        
        return ResponseEntity.ok(responseDto);
    }
    
    /**
     * Endpoint para enviar una nota de crédito electrónica a SUNAT
     */
    @PostMapping("/nota-credito")
    public ResponseEntity<CdrResponseDto> enviarNotaCredito(@RequestBody NotaCreditoRequest request) {
        log.info("Recibida solicitud para enviar nota de crédito: {}-{}", request.getSerie(), request.getNumero());
        
        // Convertir DTO a modelo
        NotaCredito notaCredito = new NotaCredito();
        notaCredito.setTipoDocumento("07");
        notaCredito.setSerie(request.getSerie());
        notaCredito.setCorrelativo(request.getNumero());
        notaCredito.setFechaEmision(request.getFechaEmision());
        notaCredito.setHoraEmision(request.getHoraEmision());
        notaCredito.setMoneda(request.getMoneda());
        notaCredito.setTipoDocumentoRef(request.getTipoDocumentoRef());
        notaCredito.setSerieDocumentoRef(request.getSerieDocumentoRef());
        notaCredito.setCorrelativoDocumentoRef(request.getNumeroDocumentoRef());
        notaCredito.setFechaDocumentoRef(request.getFechaDocumentoRef());
        notaCredito.setCodigoMotivo(request.getCodigoMotivo());
        notaCredito.setDescripcionMotivo(request.getDescripcionMotivo());
        notaCredito.setEmisor(request.getEmisor());
        notaCredito.setReceptor(request.getCliente());
        notaCredito.setDetalles(request.getItems());
        notaCredito.setTotales(request.getTotales());
        
        // Enviar nota de crédito a SUNAT
        CdrResponse respuesta = comprobanteService.enviarNotaCredito(notaCredito, request.getRuc(), 
                                                                   request.getUsuarioSol(), request.getClaveSol());
        
        // Convertir respuesta a DTO
        CdrResponseDto responseDto = convertirRespuesta(respuesta);
        
        return ResponseEntity.ok(responseDto);
    }
    
    /**
     * Endpoint para enviar una nota de débito electrónica a SUNAT
     */
    @PostMapping("/nota-debito")
    public ResponseEntity<CdrResponseDto> enviarNotaDebito(@RequestBody NotaDebitoRequest request) {
        log.info("Recibida solicitud para enviar nota de débito: {}-{}", request.getSerie(), request.getNumero());
        
        // Convertir DTO a modelo
        NotaDebito notaDebito = new NotaDebito();
        notaDebito.setTipoDocumento("08");
        notaDebito.setSerie(request.getSerie());
        notaDebito.setCorrelativo(request.getNumero());
        notaDebito.setFechaEmision(request.getFechaEmision());
        notaDebito.setHoraEmision(request.getHoraEmision());
        notaDebito.setMoneda(request.getMoneda());
        notaDebito.setTipoDocumentoRef(request.getTipoDocumentoRef());
        notaDebito.setSerieDocumentoRef(request.getSerieDocumentoRef());
        notaDebito.setCorrelativoDocumentoRef(request.getNumeroDocumentoRef());
        notaDebito.setFechaDocumentoRef(request.getFechaDocumentoRef());
        notaDebito.setCodigoMotivo(request.getCodigoMotivo());
        notaDebito.setDescripcionMotivo(request.getDescripcionMotivo());
        notaDebito.setEmisor(request.getEmisor());
        notaDebito.setReceptor(request.getCliente());
        notaDebito.setDetalles(request.getItems());
        notaDebito.setTotales(request.getTotales());
        
        // Enviar nota de débito a SUNAT
        CdrResponse respuesta = comprobanteService.enviarNotaDebito(notaDebito, request.getRuc(), 
                                                                  request.getUsuarioSol(), request.getClaveSol());
        
        // Convertir respuesta a DTO
        CdrResponseDto responseDto = convertirRespuesta(respuesta);
        
        return ResponseEntity.ok(responseDto);
    }
    
    /**
     * Convierte la respuesta del modelo a DTO
     */
    private CdrResponseDto convertirRespuesta(CdrResponse respuesta) {
        CdrResponseDto dto = new CdrResponseDto();
        dto.setCodigoSunat(respuesta.getCodigo());
        dto.setDescripcion(respuesta.getDescripcion());
        dto.setNotas(respuesta.getNotas());
        dto.setNombreArchivoCdr(respuesta.getNombreArchivo());
        dto.setExito(respuesta.isExito());
        
        // Convertir archivo CDR a Base64 si existe
        if (respuesta.getArchivoCdr() != null) {
            dto.setCdrBase64(Base64.getEncoder().encodeToString(respuesta.getArchivoCdr()));
        }
        
        return dto;
    }
}
