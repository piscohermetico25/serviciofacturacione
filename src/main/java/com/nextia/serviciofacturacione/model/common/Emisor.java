package com.nextia.serviciofacturacione.model.common;

/**
 * Representa la información del emisor del comprobante electrónico
 */
public class Emisor {
    private String ruc;
    private String tipoDoc; // Catálogo 06: RUC
    private String razonSocial;
    private String nombreComercial;
    private String direccion;
    private String pais; // Catálogo 04: PE
    private String ubigeo;
    private String departamento;
    private String provincia;
    private String distrito;
    private String usuarioSol;
    private String claveSol;

    public String getUsuarioSol() {
        return usuarioSol;
    }
    public void setUsuarioSol(String usuarioSol) {
        this.usuarioSol = usuarioSol;
    }
    public String getClaveSol() {
        return claveSol;
    }
    public void setClaveSol(String claveSol) {
        this.claveSol = claveSol;
    }
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
    public String getNombreComercial() {
        return nombreComercial;
    }
    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
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
    public String getUbigeo() {
        return ubigeo;
    }
    public void setUbigeo(String ubigeo) {
        this.ubigeo = ubigeo;
    }
    public String getDepartamento() {
        return departamento;
    }
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
    public String getProvincia() {
        return provincia;
    }
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
    public String getDistrito() {
        return distrito;
    }
    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }
   

    


}
