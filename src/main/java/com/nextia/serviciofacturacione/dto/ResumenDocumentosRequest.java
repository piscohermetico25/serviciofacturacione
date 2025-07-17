package com.nextia.serviciofacturacione.dto;

import java.util.List;

import com.nextia.serviciofacturacione.model.common.CabeceraResumen;
import com.nextia.serviciofacturacione.model.common.DetalleResumen;

public class ResumenDocumentosRequest {
    private CabeceraResumen cabecera;
    private List<DetalleResumen> detalle;

    public CabeceraResumen getCabecera() { return cabecera; }
    public void setCabecera(CabeceraResumen cabecera) { this.cabecera = cabecera; }
    public List<DetalleResumen> getDetalle() { return detalle; }
    public void setDetalle(List<DetalleResumen> detalle) { this.detalle = detalle; }


}

