package co.com.crediya.solicitudes.jpa;

import co.com.crediya.solicitudes.jpa.entity.TipoPrestamoEntity;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.solicitudes.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TipoPrestamoRepositoryAdapter implements TipoPrestamoRepository {
    private final TipoPrestamoJPARepository repository;

    @Override
    public Optional<TipoPrestamo> findActiveByCode(String code) {
        return repository.findByCodigoIgnoreCaseAndActivoTrue(code).map(this::toDomain);
    }

    private TipoPrestamo toDomain(TipoPrestamoEntity entity) {
        return TipoPrestamo.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .nombre(entity.getNombre())
                .plazoMinimoMeses(entity.getPlazoMinimoMeses())
                .plazoMaximoMeses(entity.getPlazoMaximoMeses())
                .activo(entity.isActivo())
                .build();
    }
}