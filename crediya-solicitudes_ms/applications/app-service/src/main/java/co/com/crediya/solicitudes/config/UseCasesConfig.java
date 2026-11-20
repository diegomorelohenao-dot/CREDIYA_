package co.com.crediya.solicitudes.config;

import co.com.crediya.solicitudes.usecase.registrarsolicitud.CalcularCuotaMensual;
import co.com.crediya.solicitudes.usecase.registrarsolicitud.ValidarPlazoSolicitud;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "co.com.crediya.solicitudes.usecase",
        includeFilters = {@ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")},
        useDefaultFilters = false)
public class UseCasesConfig {

    @Bean
    public CalcularCuotaMensual calcularCuotaMensual() {
        return new CalcularCuotaMensual();
    }

    @Bean
    public ValidarPlazoSolicitud validarPlazoSolicitud() {
        return new ValidarPlazoSolicitud();
    }
}
