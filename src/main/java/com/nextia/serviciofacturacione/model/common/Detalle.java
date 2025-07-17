package com.nextia.serviciofacturacione.model.common;

import java.math.BigDecimal;

/**
 * Representa un ítem o línea de detalle en un comprobante electrónico
 */
public class Detalle {
   
    private Integer item;
    private String codigo; // Código del producto
    private String descripcion;
    private String unidad; // Catálogo 03: NIU, ZZ, etc.
    private BigDecimal cantidad;
    private BigDecimal valorUnitario; // Precio sin IGV
    private BigDecimal precioUnitario; // Precio con IGV (o sin, dependiendo del tipo)
    private String tipoPrecio; // Catálogo 16: 01 (Precio unitario), 02 (Valor referencial)
    private BigDecimal valorTotal; // = cantidad * valorUnitario
    private BigDecimal igv;
    private BigDecimal porcentajeIgv; // Ej: 18.00
    
    // Datos de Afectación de IGV
    private String codigoAfectacion; // Catálogo 07
    private String codigoAfectacionAlt; // TaxExemptionReasonCode (Catálogo 07)
    private String nombreAfectacion; // "IGV"
    private String tipoAfectacion; // "VAT"
    public Integer getItem() {
        return item;
    }
    public void setItem(Integer item) {
        this.item = item;
    }
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
    public String getUnidad() {
        return unidad;
    }
    public void setUnidad(String unidad) {
        this.unidad = unidad;
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
    public String getTipoPrecio() {
        return tipoPrecio;
    }
    public void setTipoPrecio(String tipoPrecio) {
        this.tipoPrecio = tipoPrecio;
    }
    public BigDecimal getValorTotal() {
        return valorTotal;
    }
    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
    public BigDecimal getIgv() {
        return igv;
    }
    public void setIgv(BigDecimal igv) {
        this.igv = igv;
    }
    public BigDecimal getPorcentajeIgv() {
        return porcentajeIgv;
    }
    public void setPorcentajeIgv(BigDecimal porcentajeIgv) {
        this.porcentajeIgv = porcentajeIgv;
    }
    public String getCodigoAfectacion() {
        return codigoAfectacion;
    }
    public void setCodigoAfectacion(String codigoAfectacion) {
        this.codigoAfectacion = codigoAfectacion;
    }
    public String getCodigoAfectacionAlt() {
        return codigoAfectacionAlt;
    }
    public void setCodigoAfectacionAlt(String codigoAfectacionAlt) {
        this.codigoAfectacionAlt = codigoAfectacionAlt;
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
