package co.com.crediya.autenticacion.config;

import co.com.crediya.autenticacion.model.usuario.RolUsuario;
import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.model.usuario.gateways.UsuarioRepository;
import co.com.crediya.autenticacion.usecase.registrarusuario.RegistrarUsuarioCommand;
import co.com.crediya.autenticacion.usecase.registrarusuario.RegistrarUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Crea el primer usuario ADMIN cuando el sistema arranca vacío.
 * Se habilita explícitamente con BOOTSTRAP_ADMIN_ENABLED=true y la contraseña
 * debe entregarse mediante BOOTSTRAP_ADMIN_PASSWORD (idealmente un secreto).
 * Es idempotente: si el correo o el documento ya existen, no hace nada.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InicializadorAdmin {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final UsuarioRepository usuarioRepository;

    @Value("${bootstrap.admin.enabled:false}")
    private boolean habilitado;

    @Value("${bootstrap.admin.correo:admin@crediya.com}")
    private String correo;

    @Value("${bootstrap.admin.password:}")
    private String password;

    @Value("${bootstrap.admin.documento:0000000001}")
    private String documento;

    @Value("${bootstrap.admin.nombres:Administrador}")
    private String nombres;

    @Value("${bootstrap.admin.apellidos:CreDiYa}")
    private String apellidos;

    @Value("${bootstrap.admin.fecha-nacimiento:1990-01-01}")
    private LocalDate fechaNacimiento;

    @Value("${bootstrap.admin.direccion:Sin dirección}")
    private String direccion;

    @Value("${bootstrap.admin.telefono:3000000000}")
    private String telefono;

    @Value("${bootstrap.admin.salario-base:1000000}")
    private BigDecimal salarioBase;

    @EventListener(ApplicationReadyEvent.class)
    public void crearAdminInicial() {
        if (!habilitado) {
            log.info("Bootstrap de ADMIN deshabilitado.");
            return;
        }
        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "BOOTSTRAP_ADMIN_PASSWORD es obligatorio cuando BOOTSTRAP_ADMIN_ENABLED=true.");
        }
        if (usuarioRepository.existsByCorreoElectronico(correo)
                || usuarioRepository.existsByDocumento(documento)) {
            log.info("Bootstrap de ADMIN omitido: el correo o documento ya están registrados.");
            return;
        }

        Usuario admin = Usuario.builder()
                .nombres(nombres)
                .apellidos(apellidos)
                .documento(documento)
                .fechaNacimiento(fechaNacimiento)
                .direccion(direccion)
                .telefono(telefono)
                .correoElectronico(correo)
                .salarioBase(salarioBase)
                .build();

        Usuario creado = registrarUsuarioUseCase.registrarUsuario(
                RegistrarUsuarioCommand.interno(admin, password, RolUsuario.ADMIN, RolUsuario.ADMIN));
        log.info("Bootstrap de ADMIN completado: id={} correo={}", creado.getId(), creado.getCorreoElectronico());
    }
}
