package co.com.crediya.autenticacion.config;

import co.com.crediya.autenticacion.model.usuario.gateways.UsuarioValidator;
import co.com.crediya.autenticacion.model.usuario.gateways.NormalizadorUsuario;
import co.com.crediya.autenticacion.model.usuario.gateways.ReglaRegistroUsuario;
import co.com.crediya.autenticacion.usecase.registrarusuario.NormalizarDatosUsuario;
import co.com.crediya.autenticacion.usecase.registrarusuario.ValidarDatosObligatoriosUsuario;
import co.com.crediya.autenticacion.usecase.registrarusuario.ValidarEdadMinima;
import co.com.crediya.autenticacion.usecase.registrarusuario.ValidarRangoSalario;
import co.com.crediya.autenticacion.usecase.registrarusuario.ValidarReglasRegistroUsuario;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@ComponentScan(basePackages = "co.com.crediya.autenticacion.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {

    @Bean
    public ReglaRegistroUsuario validarDatosObligatoriosUsuario() {
        return new ValidarDatosObligatoriosUsuario();
    }

    @Bean
    public ReglaRegistroUsuario validarEdadMinimaUsuario() {
        return new ValidarEdadMinima(18);
    }

    @Bean
    public ReglaRegistroUsuario validarRangoSalarioUsuario() {
        return new ValidarRangoSalario(new BigDecimal("15000000"));
    }

    @Bean
    public UsuarioValidator usuarioValidator(List<ReglaRegistroUsuario> reglas) {
        return new ValidarReglasRegistroUsuario(reglas);
    }

    @Bean
    public NormalizadorUsuario normalizadorUsuario() {
        return new NormalizarDatosUsuario();
    }
}
