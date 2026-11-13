package co.com.crediya.autenticacion.model.usuario.exceptions;

public class UsuarioYaRegistradoException extends RuntimeException {
    public UsuarioYaRegistradoException() {
        super("Ya existe un usuario con ese correo electrónico o documento.");
    }
}