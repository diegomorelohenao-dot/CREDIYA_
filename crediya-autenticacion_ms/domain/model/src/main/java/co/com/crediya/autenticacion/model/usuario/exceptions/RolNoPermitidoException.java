package co.com.crediya.autenticacion.model.usuario.exceptions;

public class RolNoPermitidoException extends RuntimeException {
    public RolNoPermitidoException(String mensaje) {
        super(mensaje);
    }
}
