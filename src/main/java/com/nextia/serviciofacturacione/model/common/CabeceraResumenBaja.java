package com.nextia.serviciofacturacione.model.common;

import java.time.LocalDate;

public class CabeceraResumenBaja {
       private String tipoDoc; // RC (Resumen), RA (Baja)
    private String serie; // Formato YYYYMMDD
    private String correlativo; // Correlativo del día (1, 2, 3...)
    private LocalDate fechaEmision; // Fecha de los comprobantes que se informan
    private LocalDate fechaEnvio; // Fecha en que se envía el resumen

    // Getters y Setters
    public String getTipoDoc() { return tipoDoc; }
    public void setTipoDoc(String tipoDoc) { this.tipoDoc = tipoDoc; }

    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }

    public String getCorrelativo() { return correlativo; }
    public void setCorrelativo(String correlativo) { this.correlativo = correlativo; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public LocalDate getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDate fechaEnvio) { this.fechaEnvio = fechaEnvio; }
}
