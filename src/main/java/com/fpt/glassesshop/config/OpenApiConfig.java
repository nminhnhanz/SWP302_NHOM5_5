package com.fpt.glassesshop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Glasses Shop API")
                        .version("1.0")
                        .description("API documentation for the Glasses Shop SWP project.")
                        .contact(new Contact()
                                .name("SWP Team")
                                .email("support@glassesshop.com")));
    }
}
