package com.cok.couriertracking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI courierTrackingOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Development server");


        Contact contact = new Contact();
        contact.setEmail("cokan.task@gmail.com");
        contact.setUrl("https://cantask.dev/");

        Info info = new Info()
                .title("Courier Tracking API")
                .version("1.0.0")
                .contact(contact)
                .description("RESTful API for tracking courier locations and store visits");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }

}
