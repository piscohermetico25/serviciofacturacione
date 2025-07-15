package com.nextia.serviciofacturacione.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;

import java.net.UnknownHostException;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        Server activeServer;
        try {
            String hostAddress = java.net.InetAddress.getLocalHost().getHostAddress();
            
            if (hostAddress.equals("127.0.0.1") || hostAddress.equals("localhost")) {
                activeServer = new Server()
                    .url("http://localhost:8080/serviciofacturacione")
                    .description("Servidor Local");
            } else if (hostAddress.equals("10.100.3.88")) {
                activeServer = new Server()
                    .url("http://10.100.3.88:8080/serviciofacturacione")
                    .description("Servidor de Desarrollo");
            } else {
                // Servidor por defecto en caso de que no coincida con ninguna IP conocida
                activeServer = new Server()
                    .url("http://localhost:8080/serviciofacturacione")
                    .description("Servidor por defecto");
            }
        } catch (UnknownHostException e) {
            // En caso de error, usar servidor local por defecto
            activeServer = new Server()
                .url("http://localhost:8080/serviciofacturacione")
                .description("Servidor Local (por defecto)");
        }

        Contact contact = new Contact()
                .name("NextIaCorp")
                .url("https://www.nextiacorp.com")
                .email("soporte@nextiacorp.com");

        License license = new License()
                .name("Propiedad del NextIaCorp")
                .url("https://www.nextiacorp.com");

        Info info = new Info()
                .title("API de Servicio de Facturación")
                .version("1.0.0")
                .contact(contact)
                .description("API para Servicio de Facturación")
                .license(license);

        // Configuración del esquema de seguridad Bearer
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Ingrese el token de la aplicación en el formato: Bearer <token>");

        // Añadir el requerimiento de seguridad global
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("Bearer Authentication");

        return new OpenAPI()
                .info(info)
                .servers(List.of(activeServer))
                .components(new Components().addSecuritySchemes("Bearer Authentication", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}
