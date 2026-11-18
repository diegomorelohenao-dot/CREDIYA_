package co.com.crediya.solicitudes.jpa;

import co.com.crediya.solicitudes.jpa.entity.TipoPrestamoEntity;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipoPrestamoRepositoryAdapterTest {
    @Mock
    private TipoPrestamoJPARepository repository;

    @Test
    void returnsActiveLoanTypeFromPersistence() {
        TipoPrestamoEntity entity = new TipoPrestamoEntity();
        entity.setId(1L);
        entity.setCodigo("LIBRE_INVERSION");
        entity.setNombre("Crédito de libre inversión");
        entity.setPlazoMinimoMeses(6);
        entity.setPlazoMaximoMeses(60);
        entity.setActivo(true);
        when(repository.findByCodigoIgnoreCaseAndActivoTrue("LIBRE_INVERSION"))
                .thenReturn(Optional.of(entity));

        TipoPrestamo result = new TipoPrestamoRepositoryAdapter(repository)
                .findActiveByCode("LIBRE_INVERSION")
                .orElseThrow();

        assertEquals("LIBRE_INVERSION", result.getCodigo());
        assertEquals(6, result.getPlazoMinimoMeses());
        assertEquals(60, result.getPlazoMaximoMeses());
    }
}