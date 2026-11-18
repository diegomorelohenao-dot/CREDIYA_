package co.com.crediya.solicitudes.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class SolicitudRequest {
    @NotBlank(message = "El documento del cliente es obligatorio.")
    @Pattern(regexp = "^[A-Za-z0-9.-]{6,30}$",
            message = "El documento debe tener entre 6 y 30 caracteres alfanuméricos, puntos o guiones.")
    private String documentoCliente;

    @NotNull(message = "El monto es obligatorio.")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero.")
    private BigDecimal monto;

    @NotNull(message = "El plazo en meses es obligatorio.")
    @Positive(message = "El plazo debe ser mayor que cero.")
    private Integer plazoMeses;

    @NotBlank(message = "El código del tipo de préstamo es obligatorio.")
    private String codigoTipoPrestamo;
}