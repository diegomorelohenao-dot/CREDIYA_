package co.com.crediya.autenticacion.usecase.registrarusuario;

import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.model.usuario.gateways.ReglaRegistroUsuario;

import java.time.LocalDate;

public class ValidarEdadMinima implements ReglaRegistroUsuario {
    private final int edadMinima;

    public ValidarEdadMinima(int edadMinima) {
        this.edadMinima = edadMinima;
    }

    @Override
    public void validar(Usuario usuario) {
        LocalDate fechaNacimiento = usuario.getFechaNacimiento();
        if (fechaNacimiento != null && fechaNacimiento.isAfter(LocalDate.now().minusYears(edadMinima))) {
            throw new IllegalArgumentException("El usuario debe tener al menos " + edadMinima + " años.");
        }
    }
}
