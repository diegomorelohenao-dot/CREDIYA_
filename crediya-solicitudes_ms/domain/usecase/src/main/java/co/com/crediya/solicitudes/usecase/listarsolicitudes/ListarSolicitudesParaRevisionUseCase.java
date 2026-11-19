package co.com.crediya.solicitudes.usecase.listarsolicitudes;

import co.com.crediya.solicitudes.model.exceptions.RolNoAutorizadoException;
import co.com.crediya.solicitudes.model.solicitud.FiltroSolicitudes;
import co.com.crediya.solicitudes.model.solicitud.Pagina;
import co.com.crediya.solicitudes.model.solicitud.RolSolicitante;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.SolicitudRevision;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ListarSolicitudesParaRevisionUseCase {
    private static final int TAMANO_MAXIMO_PAGINA = 100;

    private final SolicitudRepository solicitudRepository;

    public Pagina<SolicitudRevision> execute(FiltroSolicitudes filtro, RolSolicitante rol,
                                             int pagina, int tamano) {
        validarAcceso(rol);
        validarPaginacion(pagina, tamano);

        Pagina<Solicitud> encontradas = solicitudRepository.findAll(filtro, pagina, tamano);
        List<SolicitudRevision> contenido = encontradas.contenido().stream()
                .map(solicitud -> SolicitudRevision.from(
                        solicitud,
                        solicitudRepository.sumarCuotasAprobadas(solicitud.getSolicitanteId())))
                .toList();

        return new Pagina<>(contenido, encontradas.numeroPagina(), encontradas.tamanoPagina(),
                encontradas.totalElementos(), encontradas.totalPaginas());
    }

    private void validarAcceso(RolSolicitante rol) {
        if (rol == null || !rol.puedeListarParaRevision()) {
            throw new RolNoAutorizadoException("Solo un asesor o administrador puede revisar solicitudes.");
        }
    }

    private void validarPaginacion(int pagina, int tamano) {
        if (pagina < 0 || tamano < 1 || tamano > TAMANO_MAXIMO_PAGINA) {
            throw new IllegalArgumentException(
                    "La página debe ser >= 0 y el tamaño debe estar entre 1 y " + TAMANO_MAXIMO_PAGINA + ".");
        }
    }
}
