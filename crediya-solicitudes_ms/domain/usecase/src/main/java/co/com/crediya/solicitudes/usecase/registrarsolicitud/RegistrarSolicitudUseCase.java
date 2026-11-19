package co.com.crediya.solicitudes.usecase.registrarsolicitud;

import co.com.crediya.solicitudes.model.exceptions.TipoPrestamoNoEncontradoException;
import co.com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.solicitudes.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegistrarSolicitudUseCase {
    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final ValidarPlazoSolicitud validarPlazoSolicitud;
    private final CalcularCuotaMensual calcularCuotaMensual;

    public Solicitud execute(Solicitud solicitud) {
        TipoPrestamo tipoPrestamo = tipoPrestamoRepository
                .findActiveByCode(solicitud.getTipoPrestamo().getCodigo())
                .orElseThrow(() -> new TipoPrestamoNoEncontradoException(solicitud.getTipoPrestamo().getCodigo()));
        validarPlazoSolicitud.validar(solicitud.getPlazoMeses(), tipoPrestamo);

        Solicitud solicitudPendiente = solicitud.toBuilder()
                .tipoPrestamo(tipoPrestamo)
                .tasaInteres(tipoPrestamo.getTasaInteres())
                .cuotaMensual(calcularCuotaMensual.calcular(
                        solicitud.getMonto(), tipoPrestamo.getTasaInteres(), solicitud.getPlazoMeses()))
                .estado(EstadoSolicitud.PENDIENTE_REVISION)
                .build();
        return solicitudRepository.save(solicitudPendiente);
    }
}
