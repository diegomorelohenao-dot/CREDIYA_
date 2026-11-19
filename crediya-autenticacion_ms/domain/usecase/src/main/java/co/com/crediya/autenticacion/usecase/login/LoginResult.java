package co.com.crediya.autenticacion.usecase.login;

import co.com.crediya.autenticacion.model.usuario.RolUsuario;

public record LoginResult(String accessToken, Long usuarioId, String correoElectronico, RolUsuario rol) {
}