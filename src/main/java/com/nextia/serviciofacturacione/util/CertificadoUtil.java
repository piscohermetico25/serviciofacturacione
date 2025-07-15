package com.nextia.serviciofacturacione.util;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nextia.serviciofacturacione.exception.FacturacionException;

/**
 * Clase utilitaria para la gestión y validación de certificados digitales
 * utilizados en la facturación electrónica SUNAT.
 */
public class CertificadoUtil {

    private static final Logger log = LoggerFactory.getLogger(CertificadoUtil.class);
    
    /**
     * Constructor privado para evitar instanciación de esta clase utilitaria
     */
    private CertificadoUtil() {
        // Clase utilitaria no debe ser instanciada
    }

    /**
     * Verifica si el certificado cumple con los requisitos de SUNAT
     * 
     * @param ks KeyStore que contiene el certificado
     * @param alias Alias del certificado
     * @param password Contraseña del certificado
     * @return Arreglo con la clave privada y el certificado
     * @throws FacturacionException Si ocurre algún error en la verificación
     */
    public static Object[] verificarCertificado(KeyStore ks, String alias, char[] password) {
        try {
            // Verificar que el alias exista en el keystore
            Enumeration<String> aliases = ks.aliases();
            log.info("Aliases disponibles en el keystore:");
            boolean aliasEncontrado = false;
            while (aliases.hasMoreElements()) {
                String currentAlias = aliases.nextElement();
                log.info(" - {}", currentAlias);
                if (currentAlias.equals(alias)) {
                    aliasEncontrado = true;
                }
            }
            
            if (!aliasEncontrado) {
                throw new FacturacionException("El alias '" + alias + "' no existe en el certificado. Verifique la configuración.");
            }
            
            // Obtener la clave privada
            PrivateKey privateKey = (PrivateKey) ks.getKey(alias, password);
            if (privateKey == null) {
                throw new FacturacionException("No se pudo obtener la clave privada con el alias '" + alias + "'. Verifique la contraseña y el alias.");
            }
            
            // Verificar que la longitud de la clave privada sea de al menos 1024 bits
            int keySize = ((java.security.interfaces.RSAKey) privateKey).getModulus().bitLength();
            log.info("Tamaño de la clave privada: {} bits", keySize);
            if (keySize < 1024) {
                throw new FacturacionException("La longitud de la clave privada es menor a 1024 bits. SUNAT requiere una longitud mínima de 1024 bits.");
            }
            
            // Obtener el certificado
            X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
            if (cert == null) {
                throw new FacturacionException("No se pudo obtener el certificado con el alias '" + alias + "'.");
            }
            
            // Verificar que el certificado sea X.509 v3
            if (cert.getVersion() != 3) {
                throw new FacturacionException("El certificado no es X.509 v3. SUNAT requiere certificados X.509 v3.");
            }
            
            // Verificar que el certificado contenga el RUC en el campo OU
            String dn = cert.getSubjectX500Principal().getName();
            log.info("DN del certificado: {}", dn);
            
            // Verificar la validez del certificado
            cert.checkValidity();
            log.info("Certificado válido hasta: {}", cert.getNotAfter());
            
            return new Object[]{privateKey, cert};
        } catch (Exception e) {
            log.error("Error al verificar el certificado: {}", e.getMessage(), e);
            throw new FacturacionException("Error al verificar el certificado: " + e.getMessage(), e);
        }
    }
    
    /**
     * Verifica si un certificado es válido para su uso con SUNAT
     * 
     * @param certificado Certificado a verificar
     * @return true si el certificado es válido, false en caso contrario
     */
    public static boolean esCertificadoValido(X509Certificate certificado) {
        try {
            // Verificar que el certificado sea X.509 v3
            if (certificado.getVersion() != 3) {
                log.warn("El certificado no es X.509 v3. SUNAT requiere certificados X.509 v3.");
                return false;
            }
            
            // Verificar la validez del certificado
            certificado.checkValidity();
            
            // Verificar que la clave pública tenga al menos 1024 bits
            int keySize = ((java.security.interfaces.RSAKey) certificado.getPublicKey()).getModulus().bitLength();
            if (keySize < 1024) {
                log.warn("La longitud de la clave pública es menor a 1024 bits. SUNAT requiere una longitud mínima de 1024 bits.");
                return false;
            }
            
            return true;
        } catch (Exception e) {
            log.warn("Error al verificar el certificado: {}", e.getMessage(), e);
            return false;
        }
    }
}
