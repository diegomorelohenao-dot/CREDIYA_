package co.com.crediya.autenticacion.usecase.registrarusuario;

import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.model.usuario.gateways.ReglaRegistroUsuario;
import co.com.crediya.autenticacion.model.usuario.gateways.UsuarioValidator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidarReglasRegistroUsuarioTest {

    private final UsuarioValidator validator = validator();

    @Test
    void shouldValidateWithoutChangingUserData() {
        Usuario usuario = validUser(" Ana@Example.com ");

        validator.validar(usuario);

        assertEquals(" Ana ", usuario.getNombres());
        assertEquals(" Ana@Example.com ", usuario.getCorreoElectronico());
    }

    @Test
    void shouldRejectUserYoungerThanEighteen() {
        Usuario usuario = validUser("ana@example.com");
        usuario.setFechaNacimiento(LocalDate.now().minusYears(18).plusDays(1));

        assertThrows(IllegalArgumentException.class, () -> validator.validar(usuario));
    }

    @Test
    void shouldRejectSalaryAboveConfiguredMaximum() {
        Usuario usuario = validUser("ana@example.com");
        usuario.setSalarioBase(new BigDecimal("15000000.01"));

        assertThrows(IllegalArgumentException.class, () -> validator.validar(usuario));
    }

    @Test
    void shouldRejectMissingRequiredData() {
        Usuario usuario = validUser("ana@example.com");
        usuario.setNombres(" ");

        assertThrows(IllegalArgumentException.class, () -> validator.validar(usuario));
    }

    private UsuarioValidator validator() {
        List<ReglaRegistroUsuario> reglas = List.of(
                new ValidarDatosObligatoriosUsuario(),
                new ValidarEdadMinima(18),
                new ValidarRangoSalario(new BigDecimal("15000000"))
        );
        return new ValidarReglasRegistroUsuario(reglas);
    }

    private Usuario validUser(String email) {
        return Usuario.builder()
                .nombres(" Ana ")
                .apellidos(" Gómez ")
                .documento(" 123456 ")
                .fechaNacimiento(LocalDate.now().minusYears(25))
                .direccion(" Calle 10 ")
                .telefono(" 3001234567 ")
                .correoElectronico(email)
                .salarioBase(new BigDecimal("2500000.00"))
                .build();
    }
}
