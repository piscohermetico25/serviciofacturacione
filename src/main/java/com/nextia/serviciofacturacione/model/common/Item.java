package com.nextia.serviciofacturacione.model.common;

import java.math.BigDecimal;

/**
 * Representa un ítem o línea de detalle en un comprobante electrónico
 */
public class Item {
    private String codigo;
    private String descripcion;
    private String unidadMedida;
    private BigDecimal cantidad;
    private BigDecimal valorUnitario;
    private BigDecimal precioUnitario;
    private BigDecimal descuento;
    private BigDecimal igv;
    private BigDecimal isc;
    private BigDecimal otrosTributos;
    private BigDecimal importeTotal;
    private String tipoAfectacionIGV;
    private String codigoTipoAfectacionIGV;
    
    // Constructores
    public Item() {
    }
    
    public Item(String codigo, String descripcion, BigDecimal cantidad, BigDecimal precioUnitario) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }
    
    // Getters y setters
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getUnidadMedida() {
        return unidadMedida;
    }
    
    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }
    
    public BigDecimal getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }
    
    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }
    
    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }
    
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
    public BigDecimal getDescuento() {
        return descuento;
    }
    
    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }
    
    public BigDecimal getIgv() {
        return igv;
    }
    
    public void setIgv(BigDecimal igv) {
        this.igv = igv;
    }
    
    public BigDecimal getIsc() {
        return isc;
    }
    
    public void setIsc(BigDecimal isc) {
        this.isc = isc;
    }
    
    public BigDecimal getOtrosTributos() {
        return otrosTributos;
    }
    
    public void setOtrosTributos(BigDecimal otrosTributos) {
        this.otrosTributos = otrosTributos;
    }
    
    public BigDecimal getImporteTotal() {
        return importeTotal;
    }
    
    public void setImporteTotal(BigDecimal importeTotal) {
        this.importeTotal = importeTotal;
    }
    
    public String getTipoAfectacionIGV() {
        return tipoAfectacionIGV;
    }
    
    public void setTipoAfectacionIGV(String tipoAfectacionIGV) {
        this.tipoAfectacionIGV = tipoAfectacionIGV;
    }
    
    public String getCodigoTipoAfectacionIGV() {
        return codigoTipoAfectacionIGV;
    }
    
    public void setCodigoTipoAfectacionIGV(String codigoTipoAfectacionIGV) {
        this.codigoTipoAfectacionIGV = codigoTipoAfectacionIGV;
    }
}
