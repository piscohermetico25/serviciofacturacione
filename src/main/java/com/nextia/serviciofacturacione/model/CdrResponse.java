package com.nextia.serviciofacturacione.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa la respuesta CDR (Constancia de Recepción) de SUNAT
 */
public class CdrResponse {
    private String id;
    private String codigo;
    private String descripcion;
    private List<String> notas;
    private String nombreArchivo;
    private byte[] archivoCdr;
    private boolean exito;
    
    // Constructores
    public CdrResponse() {
        this.notas = new ArrayList<>();
        this.exito = false;
    }
    
    public CdrResponse(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.notas = new ArrayList<>();
        this.exito = "0".equals(codigo);
    }
    
    // Getters y setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
        this.exito = "0".equals(codigo);
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
    
    public String getNombreArchivo() {
        return nombreArchivo;
    }
    
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }
    
    public byte[] getArchivoCdr() {
        return archivoCdr;
    }
    
    public void setArchivoCdr(byte[] archivoCdr) {
        this.archivoCdr = archivoCdr;
    }
    
    public boolean isExito() {
        return exito;
    }
    
    public void setExito(boolean exito) {
        this.exito = exito;
    }
}
