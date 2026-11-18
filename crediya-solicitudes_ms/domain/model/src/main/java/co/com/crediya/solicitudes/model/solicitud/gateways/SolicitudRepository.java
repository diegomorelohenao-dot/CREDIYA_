package co.com.crediya.solicitudes.model.solicitud.gateways;

import co.com.crediya.solicitudes.model.solicitud.Solicitud;

public interface SolicitudRepository {
    Solicitud save(Solicitud solicitud);
}