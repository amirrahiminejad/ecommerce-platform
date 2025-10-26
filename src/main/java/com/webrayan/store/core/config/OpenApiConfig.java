package com.webrayan.store.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * تنظیمات OpenAPI/Swagger برای مستندسازی API
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .servers(createServersList())
                .components(createComponents())
                .addSecurityItem(createSecurityRequirement());
    }

    private Info createApiInfo() {
        return new Info()
                .title("Iran ECommerce API")
                .description("مستندات کامل API بازار بین‌المللی ایران\n\n" +
                           "این API شامل تمام endpoint های مربوط به:\n" +
                           "- مدیریت کاربران و احراز هویت\n" +
                           "- مدیریت آگهی‌ها و محصولات\n" +
                           "- سیستم فروش و سفارشات\n" +
                           "- مدیریت دسته‌بندی‌ها\n" +
                           "- سیستم مالی و پرداخت\n\n" +
                           "**نحوه استفاده از Authentication:**\n" +
                           "1. ابتدا از endpoint `/api/auth/login` برای ورود استفاده کنید\n" +
                           "2. Token دریافتی را در header Authorization با پیشوند Bearer قرار دهید\n" +
                           "3. مثال: `Authorization: Bearer YOUR_JWT_TOKEN`")
                .version("1.0.0")
                .contact(createContact())
                .license(createLicense());
    }

    private Contact createContact() {
        return new Contact()
                .name("WebRayan Team")
                .email("info@webrayan.com")
                .url("https://webrayan.com");
    }

    private License createLicense() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }

    private List<Server> createServersList() {
        String serverUrl = String.format("http://localhost:%s%s", serverPort, contextPath);
        
        Server localServer = new Server()
                .url(serverUrl)
                .description("Development Server");
                
        Server productionServer = new Server()
                .url("https://api.iran-bazaar.com")
                .description("Production Server");

        return List.of(localServer, productionServer);
    }

    private Components createComponents() {
        return new Components()
                .addSecuritySchemes("bearerAuth", createSecurityScheme());
    }

    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Token مورد نیاز برای دسترسی به API های محافظت شده");
    }

    private SecurityRequirement createSecurityRequirement() {
        return new SecurityRequirement().addList("bearerAuth");
    }
}
