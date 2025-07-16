package com.nextia.serviciofacturacione.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de datos para la generación de facturas
 */
public class FacturaData {
    
    private String numeroFactura;
    private LocalDate fechaEmision;
    private String moneda;
    private String rucEmisor;
    private String razonSocialEmisor;
    private String tipoDocumentoEmisor;
    private String rucReceptor;
    private String razonSocialReceptor;
    private String tipoDocumentoReceptor;
    private String montoTotal;
    private List<FacturaLineaData> lineas;
    
    public FacturaData() {
        this.lineas = new ArrayList<>();
    }
    
    // Getters y setters
    
    public String getNumeroFactura() {
        return numeroFactura;
    }
    
    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }
    
    public LocalDate getFechaEmision() {
        return fechaEmision;
    }
    
    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
    
    public String getMoneda() {
        return moneda;
    }
    
    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }
    
    public String getRucEmisor() {
        return rucEmisor;
    }
    
    public void setRucEmisor(String rucEmisor) {
        this.rucEmisor = rucEmisor;
    }
    
    public String getRazonSocialEmisor() {
        return razonSocialEmisor;
    }
    
    public void setRazonSocialEmisor(String razonSocialEmisor) {
        this.razonSocialEmisor = razonSocialEmisor;
    }
    
    public String getTipoDocumentoEmisor() {
        return tipoDocumentoEmisor;
    }
    
    public void setTipoDocumentoEmisor(String tipoDocumentoEmisor) {
        this.tipoDocumentoEmisor = tipoDocumentoEmisor;
    }
    
    public String getRucReceptor() {
        return rucReceptor;
    }
    
    public void setRucReceptor(String rucReceptor) {
        this.rucReceptor = rucReceptor;
    }
    
    public String getRazonSocialReceptor() {
        return razonSocialReceptor;
    }
    
    public void setRazonSocialReceptor(String razonSocialReceptor) {
        this.razonSocialReceptor = razonSocialReceptor;
    }
    
    public String getTipoDocumentoReceptor() {
        return tipoDocumentoReceptor;
    }
    
    public void setTipoDocumentoReceptor(String tipoDocumentoReceptor) {
        this.tipoDocumentoReceptor = tipoDocumentoReceptor;
    }
    
    public String getMontoTotal() {
        return montoTotal;
    }
    
    public void setMontoTotal(String montoTotal) {
        this.montoTotal = montoTotal;
    }
    
    public List<FacturaLineaData> getLineas() {
        return lineas;
    }
    
    public void setLineas(List<FacturaLineaData> lineas) {
        this.lineas = lineas;
    }
    
    public void addLinea(FacturaLineaData linea) {
        this.lineas.add(linea);
    }
    
    /**
     * Clase interna para representar una línea de factura
     */
    public static class FacturaLineaData {
        private String id;
        private String cantidad;
        private String montoLinea;
        private String precioUnitario;
        private String descripcion;
        private String montoImpuesto;
        private String tipoPrecio;
        
        // Getters y setters
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getCantidad() {
            return cantidad;
        }
        
        public void setCantidad(String cantidad) {
            this.cantidad = cantidad;
        }
        
        public String getMontoLinea() {
            return montoLinea;
        }
        
        public void setMontoLinea(String montoLinea) {
            this.montoLinea = montoLinea;
        }
        
        public String getPrecioUnitario() {
            return precioUnitario;
        }
        
        public void setPrecioUnitario(String precioUnitario) {
            this.precioUnitario = precioUnitario;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
        
        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getMontoImpuesto() {
            return montoImpuesto;
        }
        
        public void setMontoImpuesto(String montoImpuesto) {
            this.montoImpuesto = montoImpuesto;
        }
        
        public String getTipoPrecio() {
            return tipoPrecio;
        }
        
        public void setTipoPrecio(String tipoPrecio) {
            this.tipoPrecio = tipoPrecio;
        }
    }
}
