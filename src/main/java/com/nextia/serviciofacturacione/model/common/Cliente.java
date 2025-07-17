package com.nextia.serviciofacturacione.model.common;

/**
 * Representa la información del cliente o receptor del comprobante electrónico
 */
public class Cliente {
    private String ruc; // O DNI, etc.
    private String tipoDoc; // Catálogo 06: RUC, DNI, etc.
    private String razonSocial; // O nombre completo si es DNI
    private String direccion;
    private String pais; // Catálogo 04: PE
    public String getRuc() {
        return ruc;
    }
    public void setRuc(String ruc) {
        this.ruc = ruc;
    }
    public String getTipoDoc() {
        return tipoDoc;
    }
    public void setTipoDoc(String tipoDoc) {
        this.tipoDoc = tipoDoc;
    }
    public String getRazonSocial() {
        return razonSocial;
    }
    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    public String getPais() {
        return pais;
    }
    public void setPais(String pais) {
        this.pais = pais;
    }
    

    

}
