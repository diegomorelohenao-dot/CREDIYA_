package co.com.crediya.autenticacion.usecase.registrarusuario;

import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.model.usuario.gateways.ReglaRegistroUsuario;

public class ValidarDatosObligatoriosUsuario implements ReglaRegistroUsuario {
    @Override
    public void validar(Usuario usuario) {
        validarTexto(usuario.getNombres(), "nombres");
        validarTexto(usuario.getApellidos(), "apellidos");
        validarTexto(usuario.getDocumento(), "documento");
        validarTexto(usuario.getDireccion(), "dirección");
        validarTexto(usuario.getTelefono(), "teléfono");
        validarTexto(usuario.getCorreoElectronico(), "correo electrónico");

        if (usuario.getFechaNacimiento() == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");
        }
        if (usuario.getSalarioBase() == null) {
            throw new IllegalArgumentException("El salario base es obligatorio.");
        }
    }

    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio.");
        }
    }
}
