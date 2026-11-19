package co.com.crediya.solicitudes.model.solicitud.gateways;

import co.com.crediya.solicitudes.model.solicitud.FiltroSolicitudes;
import co.com.crediya.solicitudes.model.solicitud.Pagina;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;

import java.math.BigDecimal;

public interface SolicitudRepository {
    Solicitud save(Solicitud solicitud);

    Pagina<Solicitud> findAll(FiltroSolicitudes filtro, int pagina, int tamano);

    BigDecimal sumarCuotasAprobadas(Long solicitanteId);
}
