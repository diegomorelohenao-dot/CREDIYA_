package co.com.crediya.solicitudes.jpa;

import co.com.crediya.solicitudes.jpa.entity.TipoPrestamoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoPrestamoJPARepository extends JpaRepository<TipoPrestamoEntity, Long> {
    Optional<TipoPrestamoEntity> findByCodigoIgnoreCaseAndActivoTrue(String codigo);
}