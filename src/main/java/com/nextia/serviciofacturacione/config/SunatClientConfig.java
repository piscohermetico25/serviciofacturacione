package com.nextia.serviciofacturacione.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.nextia.serviciofacturacione.service.sunat.client.generated.BillService;
import com.nextia.serviciofacturacione.service.sunat.client.generated.BillService_Service;
import jakarta.xml.ws.BindingProvider;
import java.util.Map;

/**
 * Configuración del cliente SOAP para los servicios de SUNAT
 */
@Configuration
public class SunatClientConfig {

 private static final String SUNAT_ENDPOINT = "https://e-beta.sunat.gob.pe/ol-ti-itcpfegem-beta/billService";

    private static final String SUNAT_USERNAME = "20123456789MODDATOS"; // tu RUC + usuario secundario
    private static final String SUNAT_PASSWORD = "CLAVE123";            // tu clave SOL

    @Bean
    public BillService sunatBillServiceClient() {
        BillService_Service service = new BillService_Service();
        BillService port = service.getBillServicePort();

        // Agregar autenticación WS-Security (UsernameToken en encabezado SOAP)
        Map<String, Object> requestContext = ((BindingProvider) port).getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, SUNAT_ENDPOINT);
        requestContext.put(BindingProvider.USERNAME_PROPERTY, SUNAT_USERNAME);
        requestContext.put(BindingProvider.PASSWORD_PROPERTY, SUNAT_PASSWORD);

        return port;
    }

}
