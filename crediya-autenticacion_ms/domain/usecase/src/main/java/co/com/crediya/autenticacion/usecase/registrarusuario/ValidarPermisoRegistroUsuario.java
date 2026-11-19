package co.com.crediya.autenticacion.usecase.registrarusuario;

import co.com.crediya.autenticacion.model.usuario.RolUsuario;
import co.com.crediya.autenticacion.model.usuario.exceptions.RolNoPermitidoException;

public class ValidarPermisoRegistroUsuario {

    public void validar(RolUsuario rolSolicitante, RolUsuario rolObjetivo) {
        if (rolSolicitante != null && !rolSolicitante.puedeRegistrar(rolObjetivo)) {
            throw new RolNoPermitidoException(
                    "El rol " + rolSolicitante.name() + " no puede registrar usuarios con rol "
                            + rolObjetivo.name() + ".");
        }
    }
}
