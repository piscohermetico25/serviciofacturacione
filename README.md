# Servicio de Facturación Electrónica SUNAT

Este proyecto es un servicio REST en Java con Spring Boot para el envío de boletas, facturas, notas de crédito, notas de débito y tickets, siguiendo las especificaciones del manual de la SUNAT.

## Requisitos
- Java 17 o superior
- Maven 3.6+

## Ejecución

```bash
mvn spring-boot:run
```

## Endpoints principales
- `/api/boletas`  
- `/api/facturas`  
- `/api/notas-credito`  
- `/api/notas-debito`  
- `/api/tickets`  

La estructura de los comprobantes sigue el [Manual de Programador SUNAT](https://cpe.sunat.gob.pe/sites/default/files/inline-files/manual_programador%20%281%29.pdf).

## Documentación interactiva

Accede a la documentación Swagger en:  
`http://localhost:8080/swagger-ui.html`

---

**Desarrollado por Nextia Corp**
