package com.nextia.serviciofacturacione.model;

import com.nextia.serviciofacturacione.model.common.Cliente;
import com.nextia.serviciofacturacione.model.common.Emisor;
import com.nextia.serviciofacturacione.model.common.Detalle;
import com.nextia.serviciofacturacione.model.common.Totales;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa una nota de crédito electrónica según el estándar UBL 2.1 de SUNAT
 */
public class NotaCredito {
    private String tipoDocumento;
    private String serie;
    private String correlativo;
    private String fechaEmision;
    private String horaEmision;
    private String moneda;
    
    // Datos del documento de referencia
    private String tipoDocumentoRef;
    private String serieDocumentoRef;
    private String correlativoDocumentoRef;
    private String fechaDocumentoRef;
    
    // Motivo de la nota de crédito
    private String codigoMotivo;
    private String descripcionMotivo;
    
    private Emisor emisor;
    private Cliente receptor;
    private List<Detalle> detalles;
    private Totales totales;
    
    // Constructores
    public NotaCredito() {
        this.detalles = new ArrayList<>();
        this.totales = new Totales();
    }
    
    public NotaCredito(String serie, String correlativo, String fechaEmision) {
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
    
    public String getCorrelativoDocumentoRef() {
        return correlativoDocumentoRef;
    }
    
    public void setCorrelativoDocumentoRef(String correlativoDocumentoRef) {
        this.correlativoDocumentoRef = correlativoDocumentoRef;
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
    
    public Cliente getReceptor() {
        return receptor;
    }
    
    public void setReceptor(Cliente receptor) {
        this.receptor = receptor;
    }
    
    public List<Detalle> getDetalles() {
        return detalles;
    }
    
    public void setDetalles(List<Detalle> detalles) {
        this.detalles = detalles;
    }
    
    public void addDetalle(Detalle detalle) {
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
    
    /**
     * Obtiene el motivo de la nota de crédito
     * @return Descripción del motivo
     */
    public String getMotivo() {
        return this.descripcionMotivo;
    }
}
