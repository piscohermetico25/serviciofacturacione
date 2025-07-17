package com.nextia.serviciofacturacione.dto;

import com.nextia.serviciofacturacione.model.common.Cliente;
import com.nextia.serviciofacturacione.model.common.Comprobante;
import com.nextia.serviciofacturacione.model.common.Detalle;
import java.util.List;

/**
 * DTO para recibir la solicitud de emisión de una factura electrónica
 */
public class ComprobanteRequest {
   
    Cliente cliente;
    Comprobante comprobante;
    List<Detalle> detalle;

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Comprobante getComprobante() {
        return comprobante;
    }

    public void setComprobante(Comprobante comprobante) {
        this.comprobante = comprobante;
    }

    public List<Detalle> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<Detalle> detalle) {
        this.detalle = detalle;
    }
}
