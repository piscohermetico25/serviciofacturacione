package com.nextia.serviciofacturacione.model.common;

/**
 * Representa la información del cliente o receptor del comprobante electrónico
 */
public class Cliente {
    private String tipoDocumento;
    private String numeroDocumento;
    private String razonSocial;
    private String direccion;
    private String email;
    private String telefono;
    
    // Constructores
    public Cliente() {
    }
    
    public Cliente(String tipoDocumento, String numeroDocumento, String razonSocial) {
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.razonSocial = razonSocial;
    }
    
    // Getters y setters
    public String getTipoDocumento() {
        return tipoDocumento;
    }
    
    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
    
    public String getNumeroDocumento() {
        return numeroDocumento;
    }
    
    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
