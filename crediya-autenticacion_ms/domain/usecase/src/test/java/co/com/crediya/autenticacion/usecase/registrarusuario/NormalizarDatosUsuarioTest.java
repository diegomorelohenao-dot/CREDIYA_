package co.com.crediya.autenticacion.usecase.registrarusuario;

import co.com.crediya.autenticacion.model.usuario.Usuario;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class NormalizarDatosUsuarioTest {

    @Test
    void shouldNormalizeIntoANewUserInstance() {
        Usuario original = Usuario.builder()
                .nombres(" Ana ")
                .apellidos(" Gómez ")
                .documento(" 123456 ")
                .fechaNacimiento(LocalDate.now().minusYears(25))
                .direccion(" Calle 10 ")
                .telefono(" 3001234567 ")
                .correoElectronico(" Ana@Example.com ")
                .salarioBase(new BigDecimal("2500000.00"))
                .build();

        Usuario normalizado = new NormalizarDatosUsuario().normalizar(original);

        assertNotSame(original, normalizado);
        assertEquals("Ana", normalizado.getNombres());
        assertEquals("ana@example.com", normalizado.getCorreoElectronico());
        assertEquals(" Ana ", original.getNombres());
        assertEquals(" Ana@Example.com ", original.getCorreoElectronico());
    }
}
