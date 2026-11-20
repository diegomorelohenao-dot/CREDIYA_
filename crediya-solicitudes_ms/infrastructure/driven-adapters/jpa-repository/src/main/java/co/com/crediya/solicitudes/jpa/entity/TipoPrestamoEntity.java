package co.com.crediya.solicitudes.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Entity
@Table(name = "tipos_prestamo")
@Getter
@Setter
@NoArgsConstructor
public class TipoPrestamoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "plazo_minimo_meses", nullable = false)
    private Integer plazoMinimoMeses;

    @Column(name = "plazo_maximo_meses", nullable = false)
    private Integer plazoMaximoMeses;

    @ColumnDefault("0")
    @Column(name = "tasa_interes", nullable = false, precision = 9, scale = 6)
    private BigDecimal tasaInteres;

    @Column(nullable = false)
    private boolean activo;
}
