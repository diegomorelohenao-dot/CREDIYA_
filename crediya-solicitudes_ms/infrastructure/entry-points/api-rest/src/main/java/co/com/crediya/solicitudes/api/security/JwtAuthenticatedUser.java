package co.com.crediya.solicitudes.api.security;

import co.com.crediya.solicitudes.model.solicitud.RolSolicitante;

public record JwtAuthenticatedUser(Long id, String correoElectronico, String rol) {

    /** Convierte el claim "role" del JWT al tipo del dominio. */
    public RolSolicitante rolDominio() {
        return RolSolicitante.valueOf(rol);
    }
}
