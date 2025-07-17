package com.nextia.serviciofacturacione.model.common;

public class DetalleBaja {
    private int item;
    private String tipoDoc; // 01 (Factura), 03 (Boleta), etc.
    private String serie;
    private String correlativo;
    private String motivo;

    // Getters y Setters
    public int getItem() { return item; }
    public void setItem(int item) { this.item = item; }

    public String getTipoDoc() { return tipoDoc; }
    public void setTipoDoc(String tipoDoc) { this.tipoDoc = tipoDoc; }

    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }

    public String getCorrelativo() { return correlativo; }
    public void setCorrelativo(String correlativo) { this.correlativo = correlativo; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
