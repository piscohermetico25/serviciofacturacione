package com.nextia.serviciofacturacione.service.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Implementación del servicio para compresión y descompresión de archivos ZIP
 */
@Service
public class ZipCompressorServiceImpl implements ZipCompressorService {

    private static final Logger log = LoggerFactory.getLogger(ZipCompressorServiceImpl.class);

    @Override
    public byte[] comprimirXml(String nombreArchivo, byte[] contenido) {
        try {
            log.info("Comprimiendo archivo XML: {}", nombreArchivo);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                ZipEntry entry = new ZipEntry(nombreArchivo);
                zos.putNextEntry(entry);
                zos.write(contenido);
                zos.closeEntry();
            }
            
            log.info("Archivo XML comprimido correctamente");
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Error al comprimir archivo XML", e);
            throw new RuntimeException("Error al comprimir archivo XML: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] descomprimirZip(byte[] contenidoZip) {
        try {
            log.info("Descomprimiendo archivo ZIP");
            ByteArrayInputStream bais = new ByteArrayInputStream(contenidoZip);
            ZipInputStream zis = new ZipInputStream(bais);
            
            ZipEntry entry = zis.getNextEntry();
            if (entry == null) {
                log.error("El archivo ZIP está vacío");
                throw new RuntimeException("El archivo ZIP está vacío");
            }
            
            log.info("Extrayendo archivo: {}", entry.getName());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            
            while ((len = zis.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            
            zis.closeEntry();
            zis.close();
            
            log.info("Archivo descomprimido correctamente");
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Error al descomprimir archivo ZIP", e);
            throw new RuntimeException("Error al descomprimir archivo ZIP: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] descomprimirZip(byte[] contenidoZip, String nombreArchivo) {
        try {
            log.info("Descomprimiendo archivo ZIP, buscando: {}", nombreArchivo);
            ByteArrayInputStream bais = new ByteArrayInputStream(contenidoZip);
            ZipInputStream zis = new ZipInputStream(bais);
            
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals(nombreArchivo)) {
                    log.info("Archivo encontrado: {}", entry.getName());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    
                    while ((len = zis.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }
                    
                    zis.closeEntry();
                    zis.close();
                    
                    log.info("Archivo descomprimido correctamente");
                    return baos.toByteArray();
                }
                zis.closeEntry();
            }
            
            zis.close();
            log.error("No se encontró el archivo {} en el ZIP", nombreArchivo);
            throw new RuntimeException("No se encontró el archivo " + nombreArchivo + " en el ZIP");
        } catch (IOException e) {
            log.error("Error al descomprimir archivo ZIP", e);
            throw new RuntimeException("Error al descomprimir archivo ZIP: " + e.getMessage(), e);
        }
    }
}
