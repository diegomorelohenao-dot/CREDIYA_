package co.com.crediya.autenticacion.api.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Expone el esquema Bearer JWT en Swagger UI para poder autenticar los
 * endpoints internos desde el botón "Authorize".
 */
@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Token JWT obtenido en POST /api/v1/login"
)
public class OpenApiConfig {
}
