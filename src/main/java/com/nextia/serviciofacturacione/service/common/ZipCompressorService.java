package com.nextia.serviciofacturacione.service.common;


public interface ZipCompressorService {
    

    byte[] comprimirXml(String nombreArchivo, byte[] contenido);
    

    byte[] descomprimirZip(byte[] contenidoZip);
    

    byte[] descomprimirZip(byte[] contenidoZip, String nombreArchivo);
}
