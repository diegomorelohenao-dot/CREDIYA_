package co.com.crediya.solicitudes.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "documento_cliente", nullable = false, length = 30)
    private String documentoCliente;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(name = "plazo_meses", nullable = false)
    private Integer plazoMeses;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_prestamo_id", nullable = false)
    private TipoPrestamoEntity tipoPrestamo;

    @Column(nullable = false, length = 40)
    private String estado;
}