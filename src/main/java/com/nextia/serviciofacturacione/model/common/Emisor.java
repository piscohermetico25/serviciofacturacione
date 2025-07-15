package com.nextia.serviciofacturacione.model.common;

/**
 * Representa la información del emisor del comprobante electrónico
 */
public class Emisor {
    private String ruc;
    private String razonSocial;
    private String nombreComercial;
    private String direccion;
    private String ubigeo;
    private String departamento;
    private String provincia;
    private String distrito;
    private String codigoPais;
    private String telefono;
    private String email;
    
    // Constructores
    public Emisor() {
    }
    
    public Emisor(String ruc, String razonSocial) {
        this.ruc = ruc;
        this.razonSocial = razonSocial;
    }
    
    // Getters y setters
    public String getRuc() {
        return ruc;
    }
    
    public void setRuc(String ruc) {
        this.ruc = ruc;
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
    
    public String getCodigoPais() {
        return codigoPais;
    }
    
    public void setCodigoPais(String codigoPais) {
        this.codigoPais = codigoPais;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
}
