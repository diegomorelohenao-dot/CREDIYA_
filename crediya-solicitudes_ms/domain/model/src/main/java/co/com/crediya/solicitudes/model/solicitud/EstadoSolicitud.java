package co.com.crediya.solicitudes.model.solicitud;

public enum EstadoSolicitud {
    PENDIENTE_REVISION("Pendiente de revisión"),
    REVISION_MANUAL("Revisión manual"),
    APROBADA("Aprobada"),
    RECHAZADA("Rechazada");

    private final String descripcion;

    EstadoSolicitud(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean requiereRevisionManual() {
        return this == PENDIENTE_REVISION || this == REVISION_MANUAL || this == RECHAZADA;
    }
}
