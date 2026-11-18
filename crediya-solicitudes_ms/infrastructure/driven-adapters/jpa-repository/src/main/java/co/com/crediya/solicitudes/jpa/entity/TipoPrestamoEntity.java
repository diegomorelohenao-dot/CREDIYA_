package co.com.crediya.solicitudes.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(nullable = false)
    private boolean activo;
}