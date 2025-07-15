# Servicio de Facturación Electrónica SUNAT

Este proyecto implementa un servicio REST en Java con Spring Boot para la emisión y envío de comprobantes electrónicos a SUNAT (Superintendencia Nacional de Administración Tributaria de Perú), siguiendo las especificaciones del manual de integración de facturación electrónica.

## Características

- Emisión de comprobantes electrónicos:
  - Facturas (01)
  - Boletas de venta (03)
  - Notas de crédito (07)
  - Notas de débito (08)
- Generación de XML según estándar UBL 2.1
- Firma digital de documentos XML
- Compresión en formato ZIP
- Comunicación con servicios web SOAP de SUNAT
- Procesamiento de respuestas CDR (Constancia de Recepción)
- Consulta de estado de comprobantes

## Requisitos

- Java 21 o superior
- Maven 3.6+
- Certificado digital en formato PFX/P12

## Configuración

### Propiedades de la aplicación

El archivo `application.properties` contiene la configuración principal:

```properties
# URL del servicio beta de SUNAT
sunat.service.url.beta=https://e-beta.sunat.gob.pe/ol-ti-itcpfegem-beta/billService

# URL del servicio de producción de SUNAT
sunat.service.url.prod=https://e-factura.sunat.gob.pe/ol-ti-itcpfegem/billService

# URL activa (cambiar entre beta y prod según el entorno)
sunat.service.url=${sunat.service.url.beta}

# Timeout para conexiones con SUNAT (en milisegundos)
sunat.service.timeout=60000

# Credenciales para SUNAT
sunat.ruc=20123456789
sunat.usuario.sol=MODDATOS
sunat.clave.sol=MODDATOS

# Configuración del certificado digital
sunat.certificado.ruta=classpath:certificado/certificado.pfx
sunat.certificado.password=password
sunat.certificado.alias=alias
```

### Certificado Digital

1. Obtener un certificado digital:
   - Para pruebas: Descargar certificado de prueba desde [Portal SUNAT](https://orientacion.sunat.gob.pe/images/imagenes/contenido/comprobantes/certificados-digitales-prueba-set.zip)
   - Para producción: Adquirir de un proveedor autorizado por INDECOPI

2. Colocar el certificado en:
   ```
   src/main/resources/certificado/certificado.pfx
   ```

3. Actualizar las propiedades en `application.properties` con la contraseña y alias correctos

## Ejecución

```bash
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080/serviciofacturacione`

## Endpoints principales

- `POST /api/facturas` - Emisión de facturas electrónicas
- `POST /api/boletas` - Emisión de boletas de venta electrónicas
- `POST /api/notas-credito` - Emisión de notas de crédito electrónicas
- `POST /api/notas-debito` - Emisión de notas de débito electrónicas
- `GET /api/consulta/{ruc}/{tipoDoc}/{serie}/{numero}` - Consulta de estado de comprobantes

## Documentación de la API

Accede a la documentación OpenAPI (Swagger UI) en:
`http://localhost:8080/serviciofacturacione/swagger-ui.html`

## Estructura del proyecto

El proyecto sigue una arquitectura modular con clara separación de responsabilidades:

- **Controllers**: Exponen los endpoints REST
- **Services**: Implementan la lógica de negocio
  - Servicios específicos por documento (FacturaService, BoletaService, etc.)
  - Servicios comunes (XmlSignerService, ZipCompressorService, etc.)
- **Models**: Representan las entidades del dominio
- **DTOs**: Objetos para transferencia de datos en la API

## Referencias

- [Manual de Programador SUNAT](https://cpe.sunat.gob.pe/sites/default/files/inline-files/manual_programador%20%281%29.pdf)
- [Portal de Facturación Electrónica SUNAT](https://cpe.sunat.gob.pe/)

---

**Desarrollado por Nextia Corp**
