package co.com.crediya.autenticacion.usecase.registrarusuario;

import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.model.usuario.exceptions.UsuarioYaRegistradoException;
import co.com.crediya.autenticacion.model.usuario.gateways.ReglaRegistroUsuario;
import co.com.crediya.autenticacion.model.usuario.gateways.UsuarioValidator;
import co.com.crediya.autenticacion.model.usuario.gateways.NormalizadorUsuario;
import co.com.crediya.autenticacion.model.usuario.gateways.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegistrarUsuarioUseCaseTest {

    private FakeUsuarioRepository repository;
    private RegistrarUsuarioUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new FakeUsuarioRepository();
        List<ReglaRegistroUsuario> reglas = List.of(
                new ValidarDatosObligatoriosUsuario(),
                new ValidarEdadMinima(18),
                new ValidarRangoSalario(new BigDecimal("15000000"))
        );
        UsuarioValidator validator = new ValidarReglasRegistroUsuario(reglas);
        NormalizadorUsuario normalizador = new NormalizarDatosUsuario();
        useCase = new RegistrarUsuarioUseCase(repository, validator, normalizador);
    }

    @Test
    void shouldRegisterAdultAndNormalizeEmail() {
        Usuario registered = useCase.registrarUsuario(validUser(" Ana@Example.com ", "123456"));

        assertEquals("ana@example.com", registered.getCorreoElectronico());
        assertEquals(1, repository.savedUsers.size());
    }

    @Test
    void shouldRejectDuplicateEmailOrDocument() {
        repository.savedUsers.add(validUser("ana@example.com", "123456"));

        assertThrows(UsuarioYaRegistradoException.class,
                () -> useCase.registrarUsuario(validUser("ana@example.com", "654321")));
    }

    private Usuario validUser(String email, String document) {
        return Usuario.builder()
                .nombres("Ana")
                .apellidos("Gómez")
                .documento(document)
                .fechaNacimiento(LocalDate.now().minusYears(25))
                .direccion("Calle 10")
                .telefono("3001234567")
                .correoElectronico(email)
                .salarioBase(new BigDecimal("2500000.00"))
                .build();
    }

    private static class FakeUsuarioRepository implements UsuarioRepository {
        private final List<Usuario> savedUsers = new ArrayList<>();

        @Override
        public Usuario save(Usuario usuario) {
            savedUsers.add(usuario);
            return usuario;
        }

        @Override
        public boolean existsByCorreoElectronico(String correoElectronico) {
            return savedUsers.stream().anyMatch(user -> user.getCorreoElectronico().equalsIgnoreCase(correoElectronico));
        }

        @Override
        public boolean existsByDocumento(String documento) {
            return savedUsers.stream().anyMatch(user -> user.getDocumento().equals(documento));
        }
    }
}
