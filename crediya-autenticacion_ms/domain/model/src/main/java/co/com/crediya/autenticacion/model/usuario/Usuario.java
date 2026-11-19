package co.com.crediya.autenticacion.model.usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Usuario {
    private Long id;
    private String nombres;
    private String apellidos;
    private String documento;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String telefono;
    private String correoElectronico;
    private BigDecimal salarioBase;
    private String passwordHash;
    private RolUsuario rol;

    public String getNombreCompleto() {
        return ((nombres == null ? "" : nombres.trim()) + " " +
                (apellidos == null ? "" : apellidos.trim())).trim();
    }
}
