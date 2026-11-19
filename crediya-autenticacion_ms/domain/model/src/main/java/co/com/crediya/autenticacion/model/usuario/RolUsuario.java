package co.com.crediya.autenticacion.model.usuario;

/**
 * Roles del sistema, ordenados de menor a mayor jerarquía.
 * La política de quién puede registrar a quién vive aquí para que no dependa
 * de Spring Security ni del adaptador REST.
 */
public enum RolUsuario {
    CLIENTE(1),
    ASESOR(2),
    ADMIN(3);

    private final int jerarquia;

    RolUsuario(int jerarquia) {
        this.jerarquia = jerarquia;
    }

    public int getJerarquia() {
        return jerarquia;
    }

    public String getAuthority() {
        return "ROLE_" + name();
    }

    /**
     * ADMIN puede registrar cualquier rol interno o cliente.
     * ASESOR únicamente puede registrar clientes.
     * CLIENTE no puede registrar usuarios.
     */
    public boolean puedeRegistrar(RolUsuario rolObjetivo) {
        if (rolObjetivo == null) {
            return false;
        }
        return switch (this) {
            case ADMIN -> true;
            case ASESOR -> rolObjetivo == CLIENTE;
            case CLIENTE -> false;
        };
    }
}
