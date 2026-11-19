package co.com.crediya.solicitudes.api;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Documento del cliente", example = "1020304050")
    @NotBlank(message = "El documento del cliente es obligatorio.")
    @Pattern(regexp = "^[A-Za-z0-9.-]{6,30}$",
            message = "El documento debe tener entre 6 y 30 caracteres alfanuméricos, puntos o guiones.")
    private String documentoCliente;

    @Schema(description = "Nombre completo del cliente", example = "Ana María Gómez")
    @NotBlank(message = "El nombre del cliente es obligatorio.")
    private String nombreCliente;

    @Schema(description = "Salario base del cliente", example = "2500000.00")
    @NotNull(message = "El salario base es obligatorio.")
    @DecimalMin(value = "0.01", message = "El salario base debe ser mayor que cero.")
    private BigDecimal salarioBase;

    @Schema(description = "Monto solicitado", example = "5000000.00")
    @NotNull(message = "El monto es obligatorio.")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero.")
    private BigDecimal monto;

    @Schema(description = "Plazo en meses", example = "24")
    @NotNull(message = "El plazo en meses es obligatorio.")
    @Positive(message = "El plazo debe ser mayor que cero.")
    private Integer plazoMeses;

    @Schema(description = "Código del tipo de préstamo", example = "LIBRE_INVERSION")
    @NotBlank(message = "El código del tipo de préstamo es obligatorio.")
    private String codigoTipoPrestamo;
}
