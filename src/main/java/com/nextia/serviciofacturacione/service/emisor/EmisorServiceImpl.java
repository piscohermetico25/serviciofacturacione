package com.nextia.serviciofacturacione.service.emisor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nextia.serviciofacturacione.model.common.Emisor;

@Service
public class EmisorServiceImpl implements EmisorService {
    

    @Value("${emisor.tipoDocumento}")
    private String tipoDocumento;
    
    @Value("${emisor.ruc}")
    private String ruc;
    
    @Value("${emisor.razonSocial}")
    private String razonSocial;
    
    @Value("${emisor.nombreComercial}")
    private String nombreComercial;
    
    @Value("${emisor.direccion}")
    private String direccion;
    
    @Value("${emisor.pais}")
    private String pais;
    
    @Value("${emisor.departamento}")
    private String departamento;
    
    @Value("${emisor.provincia}")
    private String provincia;
    
    @Value("${emisor.distrito}")
    private String distrito;
    
    @Value("${emisor.ubigeo}")
    private String ubigeo;
    
    @Value("${emisor.usuarioSol}")
    private String usuarioSol;
    
    @Value("${emisor.claveSol}")
    private String claveSol;


    @Override
    public Emisor obtenerEmisor() {
        
        Emisor emisor = new Emisor();
        emisor.setTipoDoc(tipoDocumento);
        emisor.setRuc(ruc);
        emisor.setRazonSocial(razonSocial);
        emisor.setNombreComercial(nombreComercial);
        emisor.setDireccion(direccion);
        emisor.setPais(pais);
        emisor.setDepartamento(departamento);
        emisor.setProvincia(provincia);
        emisor.setDistrito(distrito);
        emisor.setUbigeo(ubigeo);
        emisor.setUsuarioSol(usuarioSol);
        emisor.setClaveSol(claveSol);
        return emisor;
    }
}
