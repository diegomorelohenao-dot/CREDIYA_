package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.api.security.JwtAuthenticatedUser;
import co.com.crediya.solicitudes.usecase.registrarsolicitud.RegistrarSolicitudUseCase;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Solicitudes", description = "Registro de solicitudes de préstamo")
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/solicitud", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ApiRest {
    private final RegistrarSolicitudUseCase registrarSolicitudUseCase;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Registrar solicitud de préstamo",
            description = "Registra una solicitud con estado inicial pendiente de revisión. "
                    + "El solicitante se deriva del JWT, no del cuerpo.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitud registrada."),
            @ApiResponse(responseCode = "400", description = "Datos inválidos."),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido.")
    })
    public ResponseEntity<SolicitudResponse> registrar(
            @Valid @RequestBody SolicitudRequest request,
            @AuthenticationPrincipal JwtAuthenticatedUser autenticado) {
        var solicitud = registrarSolicitudUseCase.execute(SolicitudApiMapper.toDomain(request, autenticado));
        log.info("Solicitud de préstamo registrada con id={} para el solicitante {}",
                solicitud.getId(), autenticado.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(SolicitudApiMapper.toResponse(solicitud));
    }
}
