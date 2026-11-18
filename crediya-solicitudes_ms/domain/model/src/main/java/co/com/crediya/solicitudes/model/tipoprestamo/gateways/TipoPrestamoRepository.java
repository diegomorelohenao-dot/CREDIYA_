package co.com.crediya.solicitudes.model.tipoprestamo.gateways;

import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;

import java.util.Optional;

public interface TipoPrestamoRepository {
    Optional<TipoPrestamo> findActiveByCode(String code);
}