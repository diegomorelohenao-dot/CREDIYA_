package co.com.crediya.autenticacion.model.usuario.gateways;

import co.com.crediya.autenticacion.model.usuario.Usuario;

public interface ReglaRegistroUsuario {
    void validar(Usuario usuario);
}
