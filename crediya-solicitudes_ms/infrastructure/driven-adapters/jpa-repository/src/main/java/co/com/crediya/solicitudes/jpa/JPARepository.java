package co.com.crediya.solicitudes.jpa;

import co.com.crediya.solicitudes.jpa.entity.SolicitudEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JPARepository extends JpaRepository<SolicitudEntity, Long> {
}