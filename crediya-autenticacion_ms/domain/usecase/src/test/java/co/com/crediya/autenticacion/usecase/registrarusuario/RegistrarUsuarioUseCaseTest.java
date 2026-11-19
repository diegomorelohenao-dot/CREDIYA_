package co.com.crediya.autenticacion.usecase.registrarusuario;

import co.com.crediya.autenticacion.model.usuario.RolUsuario;
import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.model.usuario.exceptions.RolNoPermitidoException;
import co.com.crediya.autenticacion.model.usuario.exceptions.UsuarioYaRegistradoException;
import co.com.crediya.autenticacion.model.usuario.gateways.NormalizadorUsuario;
import co.com.crediya.autenticacion.model.usuario.gateways.PasswordHasher;
import co.com.crediya.autenticacion.model.usuario.gateways.ReglaRegistroUsuario;
import co.com.crediya.autenticacion.model.usuario.gateways.UsuarioRepository;
import co.com.crediya.autenticacion.model.usuario.gateways.UsuarioValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegistrarUsuarioUseCaseTest {
    private FakeUsuarioRepository repository;
    private RegistrarUsuarioUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new FakeUsuarioRepository();
        List<ReglaRegistroUsuario> rules = List.of(
                new ValidarDatosObligatoriosUsuario(), new ValidarEdadMinima(18),
                new ValidarRangoSalario(new BigDecimal("15000000")));
        UsuarioValidator validator = new ValidarReglasRegistroUsuario(rules);
        NormalizadorUsuario normalizer = new NormalizarDatosUsuario();
        PasswordHasher hasher = new PasswordHasher() {
            @Override public String hash(String password) { return "hashed:" + password; }
            @Override public boolean matches(String password, String passwordHash) {
                return passwordHash.equals(hash(password));
            }
        };
        useCase = new RegistrarUsuarioUseCase(repository, validator, normalizer, hasher,
                new ValidarPermisoRegistroUsuario());
    }

    @Test
    void registersPublicUserAsClientAndHashesPassword() {
        Usuario usuario = validUser(" Ana@Example.com ", "123456");
        Usuario registered = useCase.registrarUsuario(RegistrarUsuarioCommand.publico(usuario, "Clave1234"));

        assertEquals("ana@example.com", registered.getCorreoElectronico());
        assertEquals(RolUsuario.CLIENTE, registered.getRol());
        assertEquals("hashed:Clave1234", registered.getPasswordHash());
        assertEquals(1, repository.savedUsers.size());
    }

    @Test
    void adminCanRegisterAdvisor() {
        Usuario registered = useCase.registrarUsuario(command("asesor@example.com", "123456",
                RolUsuario.ASESOR, RolUsuario.ADMIN));

        assertEquals(RolUsuario.ASESOR, registered.getRol());
    }

    @Test
    void adminCanRegisterAnotherAdmin() {
        Usuario registered = useCase.registrarUsuario(command("otro-admin@example.com", "123457",
                RolUsuario.ADMIN, RolUsuario.ADMIN));

        assertEquals(RolUsuario.ADMIN, registered.getRol());
    }

    @Test
    void advisorCanOnlyRegisterClients() {
        Usuario client = useCase.registrarUsuario(command("cliente@example.com", "123458",
                RolUsuario.CLIENTE, RolUsuario.ASESOR));
        assertEquals(RolUsuario.CLIENTE, client.getRol());

        assertThrows(RolNoPermitidoException.class, () -> useCase.registrarUsuario(
                command("asesor2@example.com", "123459", RolUsuario.ASESOR, RolUsuario.ASESOR)));
    }

    @Test
    void clientCannotRegisterAnyone() {
        assertThrows(RolNoPermitidoException.class, () -> useCase.registrarUsuario(
                command("otro@example.com", "123460", RolUsuario.CLIENTE, RolUsuario.CLIENTE)));
        assertEquals(0, repository.savedUsers.size());
    }

    @Test
    void shouldRejectDuplicateEmailOrDocument() {
        repository.savedUsers.add(validUser("ana@example.com", "123456"));
        assertThrows(UsuarioYaRegistradoException.class,
                () -> useCase.registrarUsuario(RegistrarUsuarioCommand.publico(
                        validUser("ana@example.com", "654321"), "Clave1234")));
    }

    private RegistrarUsuarioCommand command(String email, String document, RolUsuario rol, RolUsuario solicitante) {
        return RegistrarUsuarioCommand.interno(validUser(email, document), "Clave1234", rol, solicitante);
    }

    private Usuario validUser(String email, String document) {
        return Usuario.builder().nombres("Ana").apellidos("Gómez").documento(document)
                .fechaNacimiento(LocalDate.now().minusYears(25)).direccion("Calle 10")
                .telefono("3001234567").correoElectronico(email)
                .salarioBase(new BigDecimal("2500000.00")).build();
    }

    private static class FakeUsuarioRepository implements UsuarioRepository {
        private final List<Usuario> savedUsers = new ArrayList<>();

        @Override public Usuario save(Usuario usuario) { savedUsers.add(usuario); return usuario; }

        @Override public boolean existsByCorreoElectronico(String email) {
            return savedUsers.stream().anyMatch(user -> user.getCorreoElectronico().equalsIgnoreCase(email));
        }

        @Override public boolean existsByDocumento(String document) {
            return savedUsers.stream().anyMatch(user -> user.getDocumento().equals(document));
        }

        @Override public Optional<Usuario> findByCorreoElectronico(String email) {
            return savedUsers.stream().filter(user -> user.getCorreoElectronico().equalsIgnoreCase(email)).findFirst();
        }
    }
}
