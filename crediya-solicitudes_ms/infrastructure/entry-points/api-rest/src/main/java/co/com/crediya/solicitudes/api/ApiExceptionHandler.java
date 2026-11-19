package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.model.exceptions.PlazoPrestamoInvalidoException;
import co.com.crediya.solicitudes.model.exceptions.RolNoAutorizadoException;
import co.com.crediya.solicitudes.model.exceptions.TipoPrestamoNoEncontradoException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleInvalidRequest(MethodArgumentNotValidException exception) {
        Map<String, String> details = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> details.putIfAbsent(error.getField(), error.getDefaultMessage()));

        log.warn("Solicitud rechazada por errores de validación: {}", details);
        return error(HttpStatus.BAD_REQUEST, "La solicitud contiene datos inválidos.", details);
    }

    @ExceptionHandler({ConstraintViolationException.class, HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiError> handleMalformedRequest(Exception exception) {
        log.warn("Solicitud rechazada por formato o parámetros inválidos: {}", exception.getMessage());
        return error(HttpStatus.BAD_REQUEST, "La solicitud tiene un formato inválido.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBusinessValidation(IllegalArgumentException exception) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(RolNoAutorizadoException.class)
    public ResponseEntity<ApiError> handleRolNoAutorizado(RolNoAutorizadoException exception) {
        return error(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(TipoPrestamoNoEncontradoException.class)
    public ResponseEntity<ApiError> handleTipoPrestamoNoEncontrado(TipoPrestamoNoEncontradoException exception) {
        log.warn("Solicitud rechazada: {}", exception.getMessage());
        return error(HttpStatus.BAD_REQUEST, exception.getMessage(),
                Map.of("codigoTipoPrestamo", exception.getMessage()));
    }

    @ExceptionHandler(PlazoPrestamoInvalidoException.class)
    public ResponseEntity<ApiError> handlePlazoInvalido(PlazoPrestamoInvalidoException exception) {
        log.warn("Solicitud rechazada por plazo inválido: {}", exception.getMessage());
        return error(HttpStatus.BAD_REQUEST, exception.getMessage(),
                Map.of("plazoMeses", exception.getMessage()));
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(Exception exception) {
        return error(HttpStatus.NOT_FOUND, "El recurso solicitado no existe.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedError(Exception exception) {
        log.error("Error interno al procesar la solicitud de préstamo.", exception);
        return error(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno. Intenta nuevamente más tarde.");
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String message) {
        return error(status, message, Map.of());
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String message, Map<String, String> details) {
        return ResponseEntity.status(status)
                .body(new ApiError(Instant.now(), status.value(), message, details));
    }
}
