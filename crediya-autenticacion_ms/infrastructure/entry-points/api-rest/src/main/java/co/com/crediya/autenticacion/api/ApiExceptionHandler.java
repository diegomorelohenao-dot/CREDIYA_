package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.model.usuario.exceptions.CredencialesInvalidasException;
import co.com.crediya.autenticacion.model.usuario.exceptions.RolNoPermitidoException;
import co.com.crediya.autenticacion.model.usuario.exceptions.UsuarioYaRegistradoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleInvalidRequest(MethodArgumentNotValidException exception) {
        Map<String, String> details = new LinkedHashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            details.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        log.warn("Registro rechazado por errores de validación: {}", details);
        return error(HttpStatus.BAD_REQUEST, "La solicitud contiene datos inválidos.", details);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiError> handleMalformedRequest(Exception exception) {
        return error(HttpStatus.BAD_REQUEST, "La solicitud tiene un formato o parámetro inválido.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBusinessValidation(IllegalArgumentException exception) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(CredencialesInvalidasException exception) {
        log.warn("Intento de autenticación rechazado por credenciales inválidas.");
        return error(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler({RolNoPermitidoException.class})
    public ResponseEntity<ApiError> handleRolNoPermitido(RolNoPermitidoException exception) {
        return error(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(UsuarioYaRegistradoException.class)
    public ResponseEntity<ApiError> handleDuplicateUser(UsuarioYaRegistradoException exception) {
        return error(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedError(Exception exception) {
        log.error("Error inesperado al procesar la solicitud.", exception);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno.");
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String message) {
        return error(status, message, Map.of());
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String message, Map<String, String> details) {
        return ResponseEntity.status(status).body(ApiError.of(status, message, details));
    }
}
