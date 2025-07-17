package com.nextia.serviciofacturacione.model.common;

import java.math.BigDecimal;

public class DetalleResumen {
     private int item;
    private String tipoDoc; // 03 (Boleta)
    private String serie;
    private String correlativo;
    private int condicion; // 1 (Adicionar), 2 (Modificar), 3 (Anulado)
    private String moneda;
    private BigDecimal importeTotal;
    private BigDecimal valorTotal;
    private String tipoTotal; // 01 (Gravado), 02 (Exonerado), 03 (Inafecto)
    private BigDecimal igvTotal;
    private String codigoAfectacion;
    private String nombreAfectacion;
    private String tipoAfectacion;
    public int getItem() {
        return item;
    }
    public void setItem(int item) {
        this.item = item;
    }
    public String getTipoDoc() {
        return tipoDoc;
    }
    public void setTipoDoc(String tipoDoc) {
        this.tipoDoc = tipoDoc;
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
    public int getCondicion() {
        return condicion;
    }
    public void setCondicion(int condicion) {
        this.condicion = condicion;
    }
    public String getMoneda() {
        return moneda;
    }
    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }
    public BigDecimal getImporteTotal() {
        return importeTotal;
    }
    public void setImporteTotal(BigDecimal importeTotal) {
        this.importeTotal = importeTotal;
    }
    public BigDecimal getValorTotal() {
        return valorTotal;
    }
    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
    public String getTipoTotal() {
        return tipoTotal;
    }
    public void setTipoTotal(String tipoTotal) {
        this.tipoTotal = tipoTotal;
    }
    public BigDecimal getIgvTotal() {
        return igvTotal;
    }
    public void setIgvTotal(BigDecimal igvTotal) {
        this.igvTotal = igvTotal;
    }
    public String getCodigoAfectacion() {
        return codigoAfectacion;
    }
    public void setCodigoAfectacion(String codigoAfectacion) {
        this.codigoAfectacion = codigoAfectacion;
    }
    public String getNombreAfectacion() {
        return nombreAfectacion;
    }
    public void setNombreAfectacion(String nombreAfectacion) {
        this.nombreAfectacion = nombreAfectacion;
    }
    public String getTipoAfectacion() {
        return tipoAfectacion;
    }
    public void setTipoAfectacion(String tipoAfectacion) {
        this.tipoAfectacion = tipoAfectacion;
    }


    
    
}
