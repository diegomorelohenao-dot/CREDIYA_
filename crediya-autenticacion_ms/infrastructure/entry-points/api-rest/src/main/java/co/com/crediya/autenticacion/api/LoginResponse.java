package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.model.usuario.RolUsuario;
import co.com.crediya.autenticacion.usecase.login.LoginResult;

public record LoginResponse(String accessToken, String tokenType, UsuarioAutenticado usuario) {
    public static LoginResponse from(LoginResult result) {
        return new LoginResponse(result.accessToken(), "Bearer",
                new UsuarioAutenticado(result.usuarioId(), result.correoElectronico(), result.rol()));
    }

    public record UsuarioAutenticado(Long id, String correoElectronico, RolUsuario rol) {
    }
}