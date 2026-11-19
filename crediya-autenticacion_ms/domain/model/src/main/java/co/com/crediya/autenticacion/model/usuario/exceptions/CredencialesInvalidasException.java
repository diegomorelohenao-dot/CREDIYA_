package co.com.crediya.autenticacion.model.usuario.exceptions;

public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException() {
        super("Correo electrónico o contraseña inválidos.");
    }
}