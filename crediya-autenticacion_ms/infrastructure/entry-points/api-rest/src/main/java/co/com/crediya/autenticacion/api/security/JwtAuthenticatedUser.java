package co.com.crediya.autenticacion.api.security;

import co.com.crediya.autenticacion.model.usuario.RolUsuario;

public record JwtAuthenticatedUser(Long id, String correoElectronico, RolUsuario rol) {
}