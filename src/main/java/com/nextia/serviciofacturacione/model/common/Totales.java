package com.nextia.serviciofacturacione.model.common;

import java.math.BigDecimal;

/**
 * Representa los totales de un comprobante electrónico
 */
public class Totales {
    private BigDecimal gravado;
    private BigDecimal exonerado;
    private BigDecimal inafecto;
    private BigDecimal gratuito;
    private BigDecimal igv;
    private BigDecimal isc;
    private BigDecimal otrosTributos;
    private BigDecimal descuentoGlobal;
    private BigDecimal totalVenta;
    
    // Constructores
    public Totales() {
        this.gravado = BigDecimal.ZERO;
        this.exonerado = BigDecimal.ZERO;
        this.inafecto = BigDecimal.ZERO;
        this.gratuito = BigDecimal.ZERO;
        this.igv = BigDecimal.ZERO;
        this.isc = BigDecimal.ZERO;
        this.otrosTributos = BigDecimal.ZERO;
        this.descuentoGlobal = BigDecimal.ZERO;
        this.totalVenta = BigDecimal.ZERO;
    }
    
    // Getters y setters
    public BigDecimal getGravado() {
        return gravado;
    }
    
    public void setGravado(BigDecimal gravado) {
        this.gravado = gravado;
    }
    
    public BigDecimal getExonerado() {
        return exonerado;
    }
    
    public void setExonerado(BigDecimal exonerado) {
        this.exonerado = exonerado;
    }
    
    public BigDecimal getInafecto() {
        return inafecto;
    }
    
    public void setInafecto(BigDecimal inafecto) {
        this.inafecto = inafecto;
    }
    
    public BigDecimal getGratuito() {
        return gratuito;
    }
    
    public void setGratuito(BigDecimal gratuito) {
        this.gratuito = gratuito;
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
    
    public BigDecimal getDescuentoGlobal() {
        return descuentoGlobal;
    }
    
    public void setDescuentoGlobal(BigDecimal descuentoGlobal) {
        this.descuentoGlobal = descuentoGlobal;
    }
    
    public BigDecimal getTotalVenta() {
        return totalVenta;
    }
    
    public void setTotalVenta(BigDecimal totalVenta) {
        this.totalVenta = totalVenta;
    }
    
    /**
     * Calcula el total de la venta sumando los montos gravados, exonerados, inafectos
     * y los impuestos correspondientes
     * @return Monto total de la venta
     */
    public BigDecimal calcularTotalVenta() {
        BigDecimal total = BigDecimal.ZERO;
        total = total.add(gravado != null ? gravado : BigDecimal.ZERO);
        total = total.add(exonerado != null ? exonerado : BigDecimal.ZERO);
        total = total.add(inafecto != null ? inafecto : BigDecimal.ZERO);
        total = total.add(igv != null ? igv : BigDecimal.ZERO);
        total = total.add(isc != null ? isc : BigDecimal.ZERO);
        total = total.add(otrosTributos != null ? otrosTributos : BigDecimal.ZERO);
        total = total.subtract(descuentoGlobal != null ? descuentoGlobal : BigDecimal.ZERO);
        
        return total;
    }
}
