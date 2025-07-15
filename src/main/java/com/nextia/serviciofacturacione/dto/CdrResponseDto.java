package com.nextia.serviciofacturacione.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para enviar la respuesta CDR al cliente
 */
public class CdrResponseDto {
    private String codigoSunat;
    private String descripcion;
    private List<String> notas;
    private String nombreArchivoCdr;
    private String cdrBase64;
    private boolean exito;
    
    // Constructores
    public CdrResponseDto() {
        this.notas = new ArrayList<>();
        this.exito = false;
    }
    
    public CdrResponseDto(String codigoSunat, String descripcion) {
        this.codigoSunat = codigoSunat;
        this.descripcion = descripcion;
        this.notas = new ArrayList<>();
        this.exito = "0".equals(codigoSunat);
    }
    
    // Getters y setters
    public String getCodigoSunat() {
        return codigoSunat;
    }
    
    public void setCodigoSunat(String codigoSunat) {
        this.codigoSunat = codigoSunat;
        this.exito = "0".equals(codigoSunat);
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public List<String> getNotas() {
        return notas;
    }
    
    public void setNotas(List<String> notas) {
        this.notas = notas;
    }
    
    public void addNota(String nota) {
        if (this.notas == null) {
            this.notas = new ArrayList<>();
        }
        this.notas.add(nota);
    }
    
    public String getNombreArchivoCdr() {
        return nombreArchivoCdr;
    }
    
    public void setNombreArchivoCdr(String nombreArchivoCdr) {
        this.nombreArchivoCdr = nombreArchivoCdr;
    }
    
    public String getCdrBase64() {
        return cdrBase64;
    }
    
    public void setCdrBase64(String cdrBase64) {
        this.cdrBase64 = cdrBase64;
    }
    
    public boolean isExito() {
        return exito;
    }
    
    public void setExito(boolean exito) {
        this.exito = exito;
    }
}
