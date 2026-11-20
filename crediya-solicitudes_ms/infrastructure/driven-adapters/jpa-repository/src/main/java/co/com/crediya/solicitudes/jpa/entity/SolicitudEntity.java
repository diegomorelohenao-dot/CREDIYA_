package co.com.crediya.solicitudes.jpa.entity;

import co.com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Entity
@Table(name = "solicitudes")
@Getter
@Setter
@NoArgsConstructor
public class SolicitudEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ColumnDefault("0")
    @Column(name = "solicitante_id", nullable = false)
    private Long solicitanteId;

    @Column(name = "documento_cliente", nullable = false, length = 30)
    private String documentoCliente;

    @ColumnDefault("'No disponible'")
    @Column(name = "nombre_cliente", nullable = false, length = 150)
    private String nombreCliente;

    @ColumnDefault("'No disponible'")
    @Column(name = "correo_electronico", nullable = false, length = 150)
    private String correoElectronico;

    @ColumnDefault("0")
    @Column(name = "salario_base", nullable = false, precision = 15, scale = 2)
    private BigDecimal salarioBase;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(name = "plazo_meses", nullable = false)
    private Integer plazoMeses;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_prestamo_id", nullable = false)
    private TipoPrestamoEntity tipoPrestamo;

    @ColumnDefault("0")
    @Column(name = "tasa_interes", nullable = false, precision = 9, scale = 6)
    private BigDecimal tasaInteres;

    @ColumnDefault("0")
    @Column(name = "cuota_mensual", nullable = false, precision = 15, scale = 2)
    private BigDecimal cuotaMensual;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoSolicitud estado;
}
