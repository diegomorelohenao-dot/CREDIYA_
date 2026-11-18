package co.com.crediya.solicitudes.model.exceptions;

public class TipoPrestamoNoEncontradoException extends RuntimeException {
    public TipoPrestamoNoEncontradoException(String code) {
        super("El tipo de préstamo seleccionado no existe o no está activo: " + code);
    }
}