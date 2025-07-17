package com.nextia.serviciofacturacione.controller;

import com.nextia.serviciofacturacione.dto.BajaDocumentosRequest;
import com.nextia.serviciofacturacione.dto.CdrResponseDto;
import com.nextia.serviciofacturacione.dto.ComprobanteRequest;
import com.nextia.serviciofacturacione.dto.ResumenDocumentosRequest;
import com.nextia.serviciofacturacione.model.CdrResponse;
import com.nextia.serviciofacturacione.model.common.Emisor;
import com.nextia.serviciofacturacione.service.ComprobanteService;
import com.nextia.serviciofacturacione.service.emisor.EmisorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Base64;

@RestController
@AllArgsConstructor
@RequestMapping("/api/comprobantes")
public class ComprobanteController {
    
    private final ComprobanteService comprobanteService;
    private final EmisorService emisorService;
    
    @PostMapping("/factura")
    public ResponseEntity<CdrResponseDto> enviarFactura(@RequestBody ComprobanteRequest request) {
        Emisor emisor = emisorService.obtenerEmisor();
        CdrResponse respuesta = comprobanteService.enviarFactura(request, emisor);
        CdrResponseDto responseDto = convertirRespuesta(respuesta);
        return ResponseEntity.ok(responseDto);
    }
    

    @PostMapping("/boleta")
    public ResponseEntity<CdrResponseDto> enviarBoleta(@RequestBody ComprobanteRequest request) {
        Emisor emisor = emisorService.obtenerEmisor();
        CdrResponse respuesta = comprobanteService.enviarFactura(request, emisor);
        CdrResponseDto responseDto = convertirRespuesta(respuesta);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/nota-credito")
    public ResponseEntity<CdrResponseDto> enviarNotaCredito(@RequestBody ComprobanteRequest request) {
        Emisor emisor = emisorService.obtenerEmisor();
        CdrResponse respuesta = comprobanteService.enviarNotaCredito(request, emisor);
        CdrResponseDto responseDto = convertirRespuesta(respuesta);
        return ResponseEntity.ok(responseDto);
    }
    
    @PostMapping("/nota-debito")
    public ResponseEntity<CdrResponseDto> enviarNotaDebito(@RequestBody ComprobanteRequest request) {
     
        Emisor emisor = emisorService.obtenerEmisor();
        CdrResponse respuesta = comprobanteService.enviarNotaDebito(request, emisor);
        CdrResponseDto responseDto = convertirRespuesta(respuesta);
        return ResponseEntity.ok(responseDto);
    }
    
    @PostMapping("/resumen-documentos")
    public ResponseEntity<CdrResponseDto> enviarResumenDocumentos(@RequestBody ResumenDocumentosRequest request) {
        Emisor emisor = emisorService.obtenerEmisor();
        CdrResponse respuesta = comprobanteService.enviarResumenDocumentos(request, emisor);
        CdrResponseDto responseDto = convertirRespuesta(respuesta);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/baja-documentos")
    public ResponseEntity<CdrResponseDto> enviarBajaDocumentos(@RequestBody BajaDocumentosRequest request) {
        Emisor emisor = emisorService.obtenerEmisor();
        CdrResponse respuesta = comprobanteService.enviarBajaDocumentos(request, emisor);
        CdrResponseDto responseDto = convertirRespuesta(respuesta);
        return ResponseEntity.ok(responseDto);
    }

    private CdrResponseDto convertirRespuesta(CdrResponse respuesta) {
        CdrResponseDto dto = new CdrResponseDto();
        dto.setCodigoSunat(respuesta.getCodigo());
        dto.setDescripcion(respuesta.getDescripcion());
        dto.setNotas(respuesta.getNotas());
        dto.setNombreArchivoCdr(respuesta.getNombreArchivo());
        dto.setExito(respuesta.isExito());
        
        if (respuesta.getArchivoCdr() != null) {
            dto.setCdrBase64(Base64.getEncoder().encodeToString(respuesta.getArchivoCdr()));
        }
        
        return dto;
    }
}
