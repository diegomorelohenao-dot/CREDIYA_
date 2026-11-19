package co.com.crediya.solicitudes.model.solicitud;

/** Roles que puede tener el usuario autenticado que consume este microservicio. */
public enum RolSolicitante {
    CLIENTE,
    ASESOR,
    ADMIN;

    public boolean puedeListarParaRevision() {
        return this == ASESOR || this == ADMIN;
    }
}
