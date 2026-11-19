package co.com.crediya.solicitudes.jpa;

import co.com.crediya.solicitudes.jpa.entity.SolicitudEntity;
import co.com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface JPARepository extends JpaRepository<SolicitudEntity, Long> {

    @Query("""
            SELECT s FROM SolicitudEntity s
            WHERE (:estado IS NULL OR s.estado = :estado)
              AND (:codigoTipoPrestamo IS NULL OR UPPER(s.tipoPrestamo.codigo) = UPPER(:codigoTipoPrestamo))
              AND (:documentoCliente IS NULL OR s.documentoCliente = :documentoCliente)
            """)
    Page<SolicitudEntity> findAllFiltered(
            @Param("estado") EstadoSolicitud estado,
            @Param("codigoTipoPrestamo") String codigoTipoPrestamo,
            @Param("documentoCliente") String documentoCliente,
            Pageable pageable);

    @Query("""
            SELECT SUM(s.cuotaMensual) FROM SolicitudEntity s
            WHERE s.solicitanteId = :solicitanteId AND s.estado = :estado
            """)
    BigDecimal sumarCuotasAprobadas(@Param("solicitanteId") Long solicitanteId,
                                    @Param("estado") EstadoSolicitud estado);
}
