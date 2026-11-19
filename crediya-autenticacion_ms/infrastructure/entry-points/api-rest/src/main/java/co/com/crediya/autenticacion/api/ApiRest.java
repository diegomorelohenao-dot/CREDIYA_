package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.usecase.registrarusuario.RegistrarUsuarioCommand;
import co.com.crediya.autenticacion.usecase.registrarusuario.RegistrarUsuarioUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Registro de usuarios")
public class ApiRest {
    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Registrar cliente", description = "Registra un usuario público con rol CLIENTE.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado."),
            @ApiResponse(responseCode = "400", description = "La solicitud contiene datos no válidos."),
            @ApiResponse(responseCode = "409", description = "El documento o correo ya está registrado.")
    })
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroUsuarioRequest request) {
        var usuario = registrarUsuarioUseCase.registrarUsuario(
                RegistrarUsuarioCommand.publico(request.toDomain(), request.password()));
        log.info("Usuario cliente registrado con id={}", usuario.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.from(usuario));
    }
}