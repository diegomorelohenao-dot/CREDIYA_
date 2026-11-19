package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.api.security.JwtTokenProviderAdapter;
import co.com.crediya.autenticacion.model.usuario.RolUsuario;
import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.model.usuario.exceptions.RolNoPermitidoException;
import co.com.crediya.autenticacion.usecase.login.LoginCommand;
import co.com.crediya.autenticacion.usecase.login.LoginResult;
import co.com.crediya.autenticacion.usecase.login.LoginUseCase;
import co.com.crediya.autenticacion.usecase.registrarusuario.RegistrarUsuarioCommand;
import co.com.crediya.autenticacion.usecase.registrarusuario.RegistrarUsuarioUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TestConfig.class, properties = "security.jwt.secret=AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=")
class ApiRestTest {
    private static final String REQUEST = """
            {"nombres":"Ana María","apellidos":"Gómez Pérez","documento":"1020304050",
             "fechaNacimiento":"1995-04-20","direccion":"Calle 10 # 20-30","telefono":"3001234567",
             "correoElectronico":"ana@example.com","salarioBase":2500000.00,"password":"ClaveSegura123"}
            """;
    @Autowired private WebApplicationContext context;
    @Autowired private RegistrarUsuarioUseCase registrarUsuarioUseCase;
    @Autowired private LoginUseCase loginUseCase;
    @Autowired private JwtTokenProviderAdapter tokenProvider;
    private RestTestClient client;

    @BeforeEach
    void setup() {
        reset(registrarUsuarioUseCase, loginUseCase);
        client = RestTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void publicRegistrationAlwaysAssignsClientRole() {
        when(registrarUsuarioUseCase.registrarUsuario(any(RegistrarUsuarioCommand.class)))
                .thenAnswer(call -> call.<RegistrarUsuarioCommand>getArgument(0).usuario().toBuilder()
                        .id(1L).rol(RolUsuario.CLIENTE).build());

        client.post().uri("/api/v1/usuarios").contentType(MediaType.APPLICATION_JSON).body(REQUEST)
                .exchange().expectStatus().isCreated().expectBody().jsonPath("$.rol").isEqualTo("CLIENTE");

        ArgumentCaptor<RegistrarUsuarioCommand> captor = ArgumentCaptor.forClass(RegistrarUsuarioCommand.class);
        verify(registrarUsuarioUseCase).registrarUsuario(captor.capture());
        assertEquals(RolUsuario.CLIENTE, captor.getValue().rol());
        assertNull(captor.getValue().rolSolicitante(), "El registro público no debe declarar solicitante.");
    }

    @Test
    void loginEndpointIsPublicAndReturnsTokenWithRole() {
        when(loginUseCase.login(any(LoginCommand.class)))
                .thenReturn(new LoginResult("jwt", 1L, "ana@example.com", RolUsuario.CLIENTE));
        client.post().uri("/api/v1/login").contentType(MediaType.APPLICATION_JSON)
                .body("{\"correoElectronico\":\"ana@example.com\",\"password\":\"ClaveSegura123\"}")
                .exchange().expectStatus().isOk().expectBody()
                .jsonPath("$.accessToken").isEqualTo("jwt")
                .jsonPath("$.usuario.id").isEqualTo(1)
                .jsonPath("$.usuario.rol").isEqualTo("CLIENTE");
    }

    @Test
    void registrationWithInternalRoleRequiresAdminOrAdvisor() {
        String bearer = "Bearer " + tokenProvider.generate(
                Usuario.builder().id(9L).correoElectronico("admin@example.com").rol(RolUsuario.ADMIN).build());
        when(registrarUsuarioUseCase.registrarUsuario(any(RegistrarUsuarioCommand.class)))
                .thenAnswer(call -> call.<RegistrarUsuarioCommand>getArgument(0).usuario().toBuilder()
                        .id(2L).rol(RolUsuario.ASESOR).build());

        client.post().uri("/api/v1/usuarios/internos?rol=ASESOR").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).body(REQUEST)
                .exchange().expectStatus().isCreated().expectBody().jsonPath("$.rol").isEqualTo("ASESOR");

        ArgumentCaptor<RegistrarUsuarioCommand> captor = ArgumentCaptor.forClass(RegistrarUsuarioCommand.class);
        verify(registrarUsuarioUseCase).registrarUsuario(captor.capture());
        assertEquals(RolUsuario.ADMIN, captor.getValue().rolSolicitante());
        assertEquals(RolUsuario.ASESOR, captor.getValue().rol());
    }

    @Test
    void clientCannotRegisterInternalUsers() {
        String bearer = "Bearer " + tokenProvider.generate(
                Usuario.builder().id(1L).correoElectronico("ana@example.com").rol(RolUsuario.CLIENTE).build());
        client.post().uri("/api/v1/usuarios/internos?rol=ASESOR").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).body(REQUEST).exchange().expectStatus().isForbidden();
    }

    @Test
    void roleDeniedByDomainUsesTheSameErrorFormat() {
        String bearer = "Bearer " + tokenProvider.generate(
                Usuario.builder().id(3L).correoElectronico("asesor@example.com").rol(RolUsuario.ASESOR).build());
        when(registrarUsuarioUseCase.registrarUsuario(any(RegistrarUsuarioCommand.class)))
                .thenThrow(new RolNoPermitidoException("El rol ASESOR no puede registrar usuarios con rol ADMIN."));

        client.post().uri("/api/v1/usuarios/internos?rol=ADMIN").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).body(REQUEST)
                .exchange().expectStatus().isForbidden().expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.message").exists()
                .jsonPath("$.timestamp").exists();
    }

    @Test
    void shouldRejectInvalidEmail() {
        String invalid = REQUEST.replace("ana@example.com", "correo-invalido");
        client.post().uri("/api/v1/usuarios").contentType(MediaType.APPLICATION_JSON).body(invalid)
                .exchange().expectStatus().isBadRequest().expectBody().jsonPath("$.details.correoElectronico").exists();
    }
}
