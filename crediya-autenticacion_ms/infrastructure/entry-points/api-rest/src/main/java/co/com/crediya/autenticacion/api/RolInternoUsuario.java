package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.model.usuario.RolUsuario;

/**
 * Roles que pueden solicitarse al endpoint autenticado de registro.
 * La jerarquía real se valida en el dominio con el rol del usuario autenticado.
 */
public enum RolInternoUsuario {
    CLIENTE,
    ASESOR,
    ADMIN;

    public RolUsuario toDomain() {
        return RolUsuario.valueOf(name());
    }
}
