package co.com.crediya.autenticacion.api;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

/**
 * Contrato de error HTTP compartido por ambos microservicios:
 * {@code { timestamp, status, message, details }}.
 */
public record ApiError(Instant timestamp, int status, String message, Map<String, String> details) {

    public static ApiError of(HttpStatus status, String message) {
        return of(status, message, Map.of());
    }

    public static ApiError of(HttpStatus status, String message, Map<String, String> details) {
        return new ApiError(Instant.now(), status.value(), message, details);
    }
}
