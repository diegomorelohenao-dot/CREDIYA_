package co.com.crediya.autenticacion.usecase.registrarusuario;

import co.com.crediya.autenticacion.model.usuario.RolUsuario;
import co.com.crediya.autenticacion.model.usuario.Usuario;

/**
 * @param rol             rol que se asignará al usuario creado
 * @param rolSolicitante  rol de quien ejecuta la operación; null en registro público
 */
public record RegistrarUsuarioCommand(Usuario usuario, String password, RolUsuario rol, RolUsuario rolSolicitante) {

    public static RegistrarUsuarioCommand publico(Usuario usuario, String password) {
        return new RegistrarUsuarioCommand(usuario, password, RolUsuario.CLIENTE, null);
    }

    public static RegistrarUsuarioCommand interno(Usuario usuario, String password,
                                                  RolUsuario rol, RolUsuario rolSolicitante) {
        return new RegistrarUsuarioCommand(usuario, password, rol, rolSolicitante);
    }
}
