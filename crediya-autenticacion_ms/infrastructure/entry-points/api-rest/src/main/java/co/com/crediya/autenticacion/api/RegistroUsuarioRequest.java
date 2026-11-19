package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.model.usuario.RolUsuario;
import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.usecase.registrarusuario.RegistrarUsuarioCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Datos necesarios para registrar un usuario")
public record RegistroUsuarioRequest(
        @Schema(example = "Ana María")
        @NotBlank(message = "Los nombres son obligatorios.") String nombres,
        @Schema(example = "Gómez Pérez")
        @NotBlank(message = "Los apellidos son obligatorios.") String apellidos,
        @Schema(example = "1020304050")
        @NotBlank(message = "El documento es obligatorio.") String documento,
        @Schema(example = "1995-04-20")
        @NotNull(message = "La fecha de nacimiento es obligatoria.") LocalDate fechaNacimiento,
        @Schema(example = "Calle 10 # 20-30")
        @NotBlank(message = "La dirección es obligatoria.") String direccion,
        @Schema(example = "3001234567")
        @NotBlank(message = "El teléfono es obligatorio.") String telefono,
        @Schema(example = "ana@example.com")
        @NotBlank(message = "El correo electrónico es obligatorio.")
        @Email(message = "El correo electrónico no tiene un formato válido.") String correoElectronico,
        @Schema(example = "2500000.00")
        @NotNull(message = "El salario base es obligatorio.") BigDecimal salarioBase,
        @Schema(example = "ClaveSegura123", accessMode = Schema.AccessMode.WRITE_ONLY)
        @NotBlank(message = "La contraseña es obligatoria.")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres.") String password
) {
    public Usuario toDomain() {
        return Usuario.builder()
                .nombres(nombres)
                .apellidos(apellidos)
                .documento(documento)
                .fechaNacimiento(fechaNacimiento)
                .direccion(direccion)
                .telefono(telefono)
                .correoElectronico(correoElectronico)
                .salarioBase(salarioBase)
                .build();
    }

    public RegistrarUsuarioCommand toCommand(RolUsuario rol, RolUsuario rolSolicitante) {
        return new RegistrarUsuarioCommand(toDomain(), password, rol, rolSolicitante);
    }
}