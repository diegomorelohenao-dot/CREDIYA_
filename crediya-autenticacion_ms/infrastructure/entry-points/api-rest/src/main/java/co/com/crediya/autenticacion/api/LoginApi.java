package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.usecase.login.LoginCommand;
import co.com.crediya.autenticacion.usecase.login.LoginUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/login", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Inicio de sesión y emisión de token")
public class LoginApi {
    private final LoginUseCase loginUseCase;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Iniciar sesión", description = "Valida credenciales y devuelve un token Bearer.")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Solicitud de inicio de sesión recibida");
        var result = loginUseCase.login(new LoginCommand(request.correoElectronico(), request.password()));
        log.info("Inicio de sesión exitoso para usuario id={}", result.usuarioId());
        return ResponseEntity.ok(LoginResponse.from(result));
    }
}