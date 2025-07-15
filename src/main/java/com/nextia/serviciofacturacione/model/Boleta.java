package com.nextia.serviciofacturacione.model;

import com.nextia.serviciofacturacione.model.common.Cliente;
import com.nextia.serviciofacturacione.model.common.Emisor;
import com.nextia.serviciofacturacione.model.common.Item;
import com.nextia.serviciofacturacione.model.common.Totales;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa una boleta de venta electrónica según el estándar UBL 2.1 de SUNAT
 */
public class Boleta {
    private String tipoDocumento;
    private String serie;
    private String correlativo;
    private String fechaEmision;
    private String horaEmision;
    private String moneda;
    private String formaPago;
    private String observaciones;
    
    private Emisor emisor;
    private Cliente receptor;
    private List<Item> detalles;
    private Totales totales;
    
    // Constructores
    public Boleta() {
        this.detalles = new ArrayList<>();
        this.totales = new Totales();
    }
    
    public Boleta(String serie, String correlativo, String fechaEmision) {
        this.serie = serie;
        this.correlativo = correlativo;
        this.fechaEmision = fechaEmision;
        this.detalles = new ArrayList<>();
        this.totales = new Totales();
    }
    
    // Getters y setters
    public String getTipoDocumento() {
        return tipoDocumento;
    }
    
    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
    
    public String getSerie() {
        return serie;
    }
    
    public void setSerie(String serie) {
        this.serie = serie;
    }
    
    public String getCorrelativo() {
        return correlativo;
    }
    
    public void setCorrelativo(String correlativo) {
        this.correlativo = correlativo;
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
    
    public String getFormaPago() {
        return formaPago;
    }
    
    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public Emisor getEmisor() {
        return emisor;
    }
    
    public void setEmisor(Emisor emisor) {
        this.emisor = emisor;
    }
    
    public Cliente getReceptor() {
        return receptor;
    }
    
    public void setReceptor(Cliente receptor) {
        this.receptor = receptor;
    }
    
    public List<Item> getDetalles() {
        return detalles;
    }
    
    public void setDetalles(List<Item> detalles) {
        this.detalles = detalles;
    }
    
    public void addDetalle(Item detalle) {
        if (this.detalles == null) {
            this.detalles = new ArrayList<>();
        }
        this.detalles.add(detalle);
    }
    
    public Totales getTotales() {
        return totales;
    }
    
    public void setTotales(Totales totales) {
        this.totales = totales;
    }
}
