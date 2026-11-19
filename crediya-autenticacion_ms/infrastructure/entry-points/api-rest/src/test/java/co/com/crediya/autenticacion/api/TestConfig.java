package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.api.security.BCryptPasswordHasher;
import co.com.crediya.autenticacion.api.security.JwtAuthenticationFilter;
import co.com.crediya.autenticacion.api.security.JwtTokenProviderAdapter;
import co.com.crediya.autenticacion.api.security.SecurityConfig;
import co.com.crediya.autenticacion.api.security.SecurityErrorHandlers;
import co.com.crediya.autenticacion.usecase.login.LoginUseCase;
import co.com.crediya.autenticacion.usecase.registrarusuario.RegistrarUsuarioUseCase;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.Mockito.mock;

@Configuration
@EnableAutoConfiguration
@EnableWebMvc
@Import({ApiRest.class, LoginApi.class, RegistroUsuarioInternoApi.class, ApiExceptionHandler.class,
        SecurityConfig.class, JwtAuthenticationFilter.class, JwtTokenProviderAdapter.class,
        SecurityErrorHandlers.class, BCryptPasswordHasher.class})
public class TestConfig {
    @Bean RegistrarUsuarioUseCase registrarUsuarioUseCase() { return mock(RegistrarUsuarioUseCase.class); }
    @Bean LoginUseCase loginUseCase() { return mock(LoginUseCase.class); }
}