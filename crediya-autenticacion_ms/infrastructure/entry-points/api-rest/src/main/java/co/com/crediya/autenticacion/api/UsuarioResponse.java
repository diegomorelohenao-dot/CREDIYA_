package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.model.usuario.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Usuario registrado")
public record UsuarioResponse(
        Long id,
        String nombres,
        String apellidos,
        String documento,
        LocalDate fechaNacimiento,
        String direccion,
        String telefono,
        String correoElectronico,
        BigDecimal salarioBase
) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getDocumento(),
                usuario.getFechaNacimiento(),
                usuario.getDireccion(),
                usuario.getTelefono(),
                usuario.getCorreoElectronico(),
                usuario.getSalarioBase()
        );
    }
}