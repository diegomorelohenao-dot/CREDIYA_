package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.usecase.listarsolicitudes.ListarSolicitudesParaRevisionUseCase;
import co.com.crediya.solicitudes.usecase.registrarsolicitud.RegistrarSolicitudUseCase;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableAutoConfiguration
@EnableWebMvc
@Import({ApiRest.class, RevisionSolicitudesApi.class, ApiExceptionHandler.class,
        co.com.crediya.solicitudes.api.security.SecurityConfig.class,
        co.com.crediya.solicitudes.api.security.JwtAuthenticationFilter.class,
        co.com.crediya.solicitudes.api.security.JwtTokenVerifier.class,
        co.com.crediya.solicitudes.api.security.SecurityErrorHandlers.class})
public class TestConfig {
    @Bean
    RegistrarSolicitudUseCase registrarSolicitudUseCase() {
        return Mockito.mock(RegistrarSolicitudUseCase.class);
    }

    @Bean
    ListarSolicitudesParaRevisionUseCase listarSolicitudesParaRevisionUseCase() {
        return Mockito.mock(ListarSolicitudesParaRevisionUseCase.class);
    }
}
