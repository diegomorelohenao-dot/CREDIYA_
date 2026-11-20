package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.api.security.JwtAuthenticatedUser;
import co.com.crediya.autenticacion.usecase.registrarusuario.RegistrarUsuarioCommand;
import co.com.crediya.autenticacion.usecase.registrarusuario.RegistrarUsuarioUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/usuarios/internos", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RegistroUsuarioInternoApi {
    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Registrar usuario interno o cliente",
            description = "ADMIN puede crear ASESOR, ADMIN o CLIENTE; ASESOR únicamente puede crear CLIENTE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado."),
            @ApiResponse(responseCode = "400", description = "Datos inválidos."),
            @ApiResponse(responseCode = "403", description = "El rol autenticado no puede crear el rol solicitado."),
            @ApiResponse(responseCode = "409", description = "Documento o correo ya registrado.")
    })
    public ResponseEntity<UsuarioResponse> registrar(
            @Valid @RequestBody RegistroUsuarioRequest request,
            @RequestParam(name = "rol") RolInternoUsuario rol,
            @AuthenticationPrincipal JwtAuthenticatedUser autenticado) {
        var command = request.toCommand(rol.toDomain(), autenticado.rol());
        var usuario = registrarUsuarioUseCase.registrarUsuario(command);
        log.info("Usuario registrado con id={} rol={} por el usuario {}",
                usuario.getId(), usuario.getRol(), autenticado.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.from(usuario));
    }
}
