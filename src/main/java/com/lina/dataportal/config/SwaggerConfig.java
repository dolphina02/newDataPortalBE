package com.lina.dataportal.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LINA Data Portal API")
                        .description("보험업계 특화 데이터 분석 플랫폼 - LINA 데이터 포털의 백엔드 API 서버")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("LINA Data Portal Team")
                                .email("dataportal-support@lina.co.kr")
                                .url("https://github.com/lina/data-portal-backend"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("개발 서버"),
                        new Server()
                                .url("https://api.dataportal.lina.co.kr")
                                .description("프로덕션 서버")))
                .components(new Components()
                        .addSecuritySchemes("basicAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("Basic Authentication (admin/admin123)")))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
    }
}