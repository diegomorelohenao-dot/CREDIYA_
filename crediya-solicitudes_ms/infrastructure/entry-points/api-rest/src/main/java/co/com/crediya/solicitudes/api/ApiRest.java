package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.usecase.registrarsolicitud.RegistrarSolicitudUseCase;
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

@Tag(name = "Solicitudes", description = "Registro de solicitudes de préstamo")
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/solicitud", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ApiRest {
    private final RegistrarSolicitudUseCase registrarSolicitudUseCase;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Registrar solicitud de préstamo",
            description = "Registra una solicitud con estado inicial pendiente de revisión.")
    public ResponseEntity<SolicitudResponse> registrar(@Valid @RequestBody SolicitudRequest request) {
        log.info("Recepción de solicitud de préstamo");
        var solicitud = registrarSolicitudUseCase.execute(SolicitudApiMapper.toDomain(request));
        log.info("Solicitud de préstamo registrada con id={}", solicitud.getId());
        return ResponseEntity.status(201).body(SolicitudApiMapper.toResponse(solicitud));
    }
}