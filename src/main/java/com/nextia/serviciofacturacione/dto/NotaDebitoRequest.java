package com.nextia.serviciofacturacione.dto;

import com.nextia.serviciofacturacione.model.common.Cliente;
import com.nextia.serviciofacturacione.model.common.Emisor;
import com.nextia.serviciofacturacione.model.common.Item;
import com.nextia.serviciofacturacione.model.common.Totales;

import java.util.List;

/**
 * DTO para recibir la solicitud de emisión de una nota de débito electrónica
 */
public class NotaDebitoRequest {
    // Datos de autenticación para SUNAT
    private String ruc;
    private String usuarioSol;
    private String claveSol;
    
    // Datos de la nota de débito
    private String serie;
    private String numero;
    private String fechaEmision;
    private String horaEmision;
    private String moneda;
    
    // Datos del documento de referencia
    private String tipoDocumentoRef;
    private String serieDocumentoRef;
    private String numeroDocumentoRef;
    private String fechaDocumentoRef;
    
    // Motivo de la nota de débito
    private String codigoMotivo;
    private String descripcionMotivo;
    
    // Entidades relacionadas
    private Emisor emisor;
    private Cliente cliente;
    private List<Item> items;
    private Totales totales;
    
    // Getters y setters
    public String getRuc() {
        return ruc;
    }
    
    public void setRuc(String ruc) {
        this.ruc = ruc;
    }
    
    public String getUsuarioSol() {
        return usuarioSol;
    }
    
    public void setUsuarioSol(String usuarioSol) {
        this.usuarioSol = usuarioSol;
    }
    
    public String getClaveSol() {
        return claveSol;
    }
    
    public void setClaveSol(String claveSol) {
        this.claveSol = claveSol;
    }
    
    public String getSerie() {
        return serie;
    }
    
    public void setSerie(String serie) {
        this.serie = serie;
    }
    
    public String getNumero() {
        return numero;
    }
    
    public void setNumero(String numero) {
        this.numero = numero;
    }
    
    public String getFechaEmision() {
        return fechaEmision;
    }
    
    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
    
    public String getHoraEmision() {
        return horaEmision;
    }
    
    public void setHoraEmision(String horaEmision) {
        this.horaEmision = horaEmision;
    }
    
    public String getMoneda() {
        return moneda;
    }
    
    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }
    
    public String getTipoDocumentoRef() {
        return tipoDocumentoRef;
    }
    
    public void setTipoDocumentoRef(String tipoDocumentoRef) {
        this.tipoDocumentoRef = tipoDocumentoRef;
    }
    
    public String getSerieDocumentoRef() {
        return serieDocumentoRef;
    }
    
    public void setSerieDocumentoRef(String serieDocumentoRef) {
        this.serieDocumentoRef = serieDocumentoRef;
    }
    
    public String getNumeroDocumentoRef() {
        return numeroDocumentoRef;
    }
    
    public void setNumeroDocumentoRef(String numeroDocumentoRef) {
        this.numeroDocumentoRef = numeroDocumentoRef;
    }
    
    public String getFechaDocumentoRef() {
        return fechaDocumentoRef;
    }
    
    public void setFechaDocumentoRef(String fechaDocumentoRef) {
        this.fechaDocumentoRef = fechaDocumentoRef;
    }
    
    public String getCodigoMotivo() {
        return codigoMotivo;
    }
    
    public void setCodigoMotivo(String codigoMotivo) {
        this.codigoMotivo = codigoMotivo;
    }
    
    public String getDescripcionMotivo() {
        return descripcionMotivo;
    }
    
    public void setDescripcionMotivo(String descripcionMotivo) {
        this.descripcionMotivo = descripcionMotivo;
    }
    
    public Emisor getEmisor() {
        return emisor;
    }
    
    public void setEmisor(Emisor emisor) {
        this.emisor = emisor;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public List<Item> getItems() {
        return items;
    }
    
    public void setItems(List<Item> items) {
        this.items = items;
    }
    
    public Totales getTotales() {
        return totales;
    }
    
    public void setTotales(Totales totales) {
        this.totales = totales;
    }
}
