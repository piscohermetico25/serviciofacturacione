package com.nextia.serviciofacturacione.model.common;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Comprobante {
    
    // Datos comunes
    private String serie;
    private String correlativo;
    private LocalDate fechaEmision;
    private String tipoDoc; // Catálogo 01: 01 (Factura), 07 (NC), 08 (ND)
    private String moneda; // Catálogo 02: PEN, USD
    private String totalTexto; // El monto total en letras
    private BigDecimal total;
    private BigDecimal igv;
    private BigDecimal totalOpGravadas;
    private BigDecimal totalOpInafectas;
    private BigDecimal totalOpExoneradas;
    private BigDecimal totalOpGratuita;

    // Datos de Forma de Pago (para Factura)
    private Integer formaPagoActivo; // 1 para Crédito, otro valor para Contado
    private Integer numeroCuota;
    private Integer diasCuotasVentas;
    private BigDecimal formaPagoMontoApagarPorMes;
    
    // Datos de Referencia (para Notas de Crédito/Débito)
    private String serieRef;
    private String correlativoRef;
    private String tipoDocRef;
    private String codMotivo; // Catálogo 09 (NC) o 10 (ND)
    private String descripcionMotivo;
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
    public LocalDate getFechaEmision() {
        return fechaEmision;
    }
    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
    public String getTipoDoc() {
        return tipoDoc;
    }
    public void setTipoDoc(String tipoDoc) {
        this.tipoDoc = tipoDoc;
    }
    public String getMoneda() {
        return moneda;
    }
    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }
    public String getTotalTexto() {
        return totalTexto;
    }
    public void setTotalTexto(String totalTexto) {
        this.totalTexto = totalTexto;
    }
    public BigDecimal getTotal() {
        return total;
    }
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    public BigDecimal getIgv() {
        return igv;
    }
    public void setIgv(BigDecimal igv) {
        this.igv = igv;
    }
    public BigDecimal getTotalOpGravadas() {
        return totalOpGravadas;
    }
    public void setTotalOpGravadas(BigDecimal totalOpGravadas) {
        this.totalOpGravadas = totalOpGravadas;
    }
    public BigDecimal getTotalOpInafectas() {
        return totalOpInafectas;
    }
    public void setTotalOpInafectas(BigDecimal totalOpInafectas) {
        this.totalOpInafectas = totalOpInafectas;
    }
    public BigDecimal getTotalOpExoneradas() {
        return totalOpExoneradas;
    }
    public void setTotalOpExoneradas(BigDecimal totalOpExoneradas) {
        this.totalOpExoneradas = totalOpExoneradas;
    }
    public BigDecimal getTotalOpGratuita() {
        return totalOpGratuita;
    }
    public void setTotalOpGratuita(BigDecimal totalOpGratuita) {
        this.totalOpGratuita = totalOpGratuita;
    }
    public int getFormaPagoActivo() {
        return formaPagoActivo;
    }
    public void setFormaPagoActivo(int formaPagoActivo) {
        this.formaPagoActivo = formaPagoActivo;
    }
    public Integer getNumeroCuota() {
        return numeroCuota;
    }
    public void setNumeroCuota(Integer numeroCuota) {
        this.numeroCuota = numeroCuota;
    }
    public Integer getDiasCuotasVentas() {
        return diasCuotasVentas;
    }
    public void setDiasCuotasVentas(Integer diasCuotasVentas) {
        this.diasCuotasVentas = diasCuotasVentas;
    }
    public BigDecimal getFormaPagoMontoApagarPorMes() {
        return formaPagoMontoApagarPorMes;
    }
    public void setFormaPagoMontoApagarPorMes(BigDecimal formaPagoMontoApagarPorMes) {
        this.formaPagoMontoApagarPorMes = formaPagoMontoApagarPorMes;
    }
    public String getSerieRef() {
        return serieRef;
    }
    public void setSerieRef(String serieRef) {
        this.serieRef = serieRef;
    }
    public String getCorrelativoRef() {
        return correlativoRef;
    }
    public void setCorrelativoRef(String correlativoRef) {
        this.correlativoRef = correlativoRef;
    }
    public String getTipoDocRef() {
        return tipoDocRef;
    }
    public void setTipoDocRef(String tipoDocRef) {
        this.tipoDocRef = tipoDocRef;
    }
    public String getCodMotivo() {
        return codMotivo;
    }
    public void setCodMotivo(String codMotivo) {
        this.codMotivo = codMotivo;
    }
    public String getDescripcionMotivo() {
        return descripcionMotivo;
    }
    public void setDescripcionMotivo(String descripcionMotivo) {
        this.descripcionMotivo = descripcionMotivo;
    }

    

    
    
}
