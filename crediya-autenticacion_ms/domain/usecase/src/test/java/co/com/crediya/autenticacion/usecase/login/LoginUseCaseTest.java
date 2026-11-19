package co.com.crediya.autenticacion.usecase.login;

import co.com.crediya.autenticacion.model.usuario.RolUsuario;
import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.model.usuario.exceptions.CredencialesInvalidasException;
import co.com.crediya.autenticacion.model.usuario.gateways.PasswordHasher;
import co.com.crediya.autenticacion.model.usuario.gateways.TokenGenerator;
import co.com.crediya.autenticacion.model.usuario.gateways.UsuarioRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoginUseCaseTest {
    private final UsuarioRepository repository = mock(UsuarioRepository.class);
    private final PasswordHasher passwordHasher = mock(PasswordHasher.class);
    private final TokenGenerator tokenGenerator = mock(TokenGenerator.class);
    private final LoginUseCase useCase = new LoginUseCase(repository, passwordHasher, tokenGenerator);

    @Test
    void returnsTokenAndUserRoleForValidCredentials() {
        Usuario user = Usuario.builder().id(4L).correoElectronico("ana@example.com")
                .passwordHash("hash").rol(RolUsuario.CLIENTE).build();
        when(repository.findByCorreoElectronico("ana@example.com")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("Clave1234", "hash")).thenReturn(true);
        when(tokenGenerator.generate(user)).thenReturn("jwt");

        LoginResult result = useCase.login(new LoginCommand("ana@example.com", "Clave1234"));

        assertEquals("jwt", result.accessToken());
        assertEquals(4L, result.usuarioId());
        assertEquals(RolUsuario.CLIENTE, result.rol());
    }

    @Test
    void rejectsInvalidCredentialsWithoutGeneratingToken() {
        Usuario user = Usuario.builder().passwordHash("hash").build();
        when(repository.findByCorreoElectronico("ana@example.com")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("incorrecta", "hash")).thenReturn(false);

        assertThrows(CredencialesInvalidasException.class,
                () -> useCase.login(new LoginCommand("ana@example.com", "incorrecta")));
    }

    @Test
    void rejectsLegacyUserWithoutPasswordHash() {
        when(repository.findByCorreoElectronico("legacy@example.com"))
                .thenReturn(Optional.of(Usuario.builder().build()));
        assertThrows(CredencialesInvalidasException.class,
                () -> useCase.login(new LoginCommand("legacy@example.com", "Clave1234")));
    }
}