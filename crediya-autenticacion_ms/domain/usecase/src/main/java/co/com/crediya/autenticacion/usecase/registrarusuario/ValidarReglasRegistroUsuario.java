package co.com.crediya.autenticacion.usecase.registrarusuario;

import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.model.usuario.gateways.ReglaRegistroUsuario;
import co.com.crediya.autenticacion.model.usuario.gateways.UsuarioValidator;

import java.util.List;

public class ValidarReglasRegistroUsuario implements UsuarioValidator {
    private final List<ReglaRegistroUsuario> reglas;

    public ValidarReglasRegistroUsuario(List<ReglaRegistroUsuario> reglas) {
        this.reglas = List.copyOf(reglas);
    }

    @Override
    public void validar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Los datos del usuario son obligatorios.");
        }

        reglas.forEach(regla -> regla.validar(usuario));
    }
}
