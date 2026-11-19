package co.com.crediya.solicitudes.model.solicitud;

public record FiltroSolicitudes(EstadoSolicitud estado, String codigoTipoPrestamo, String documentoCliente) {

    public static FiltroSolicitudes todos() {
        return new FiltroSolicitudes(null, null, null);
    }
}
