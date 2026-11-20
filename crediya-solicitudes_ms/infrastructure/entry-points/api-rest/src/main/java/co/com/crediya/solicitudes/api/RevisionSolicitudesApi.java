package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.api.security.JwtAuthenticatedUser;
import co.com.crediya.solicitudes.model.exceptions.RolNoAutorizadoException;
import co.com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import co.com.crediya.solicitudes.model.solicitud.FiltroSolicitudes;
import co.com.crediya.solicitudes.usecase.listarsolicitudes.ListarSolicitudesParaRevisionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Revisión de solicitudes", description = "Listado paginado para revisión manual")
@RestController
@RequestMapping(value = "/api/v1/solicitud", produces = MediaType.APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class RevisionSolicitudesApi {

    private final ListarSolicitudesParaRevisionUseCase listarSolicitudesParaRevisionUseCase;

    @GetMapping
    @Operation(summary = "Listar solicitudes para revisión manual",
            description = "Disponible solo para ASESOR y ADMIN. Permite filtrar por estado, tipo de préstamo "
                    + "y documento, e incluye la deuda mensual aprobada de cada solicitante.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido."),
            @ApiResponse(responseCode = "400", description = "Filtros o paginación inválidos."),
            @ApiResponse(responseCode = "403", description = "El rol no puede revisar solicitudes.")
    })
    public ResponseEntity<PaginaResponse<SolicitudRevisionResponse>> listar(
            @RequestParam(name = "estado", required = false) EstadoSolicitud estado,
            @RequestParam(name = "codigoTipoPrestamo", required = false) String codigoTipoPrestamo,
            @RequestParam(name = "documentoCliente", required = false) String documentoCliente,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @AuthenticationPrincipal JwtAuthenticatedUser autenticado) {
        if (autenticado == null) {
            throw new RolNoAutorizadoException("Se requiere un usuario autenticado.");
        }

        var filtro = new FiltroSolicitudes(estado, codigoTipoPrestamo, documentoCliente);
        var pagina = listarSolicitudesParaRevisionUseCase.execute(
                filtro, autenticado.rolDominio(), page, size);

        return ResponseEntity.ok(new PaginaResponse<>(
                pagina.contenido().stream().map(SolicitudRevisionResponse::from).toList(),
                pagina.numeroPagina(), pagina.tamanoPagina(),
                pagina.totalElementos(), pagina.totalPaginas()));
    }
}
