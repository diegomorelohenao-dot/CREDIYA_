package co.com.crediya.autenticacion.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot 4 auto-configura un ObjectMapper de Jackson 3 (tools.jackson.*),
 * mientras que los filtros de seguridad usan Jackson 2 (com.fasterxml.jackson.*).
 * Se expone explícitamente el mapper de Jackson 2 para poder inyectarlo.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper jackson2ObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
