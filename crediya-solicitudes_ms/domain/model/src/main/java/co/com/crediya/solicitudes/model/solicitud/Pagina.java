package co.com.crediya.solicitudes.model.solicitud;

import java.util.List;

public record Pagina<T>(List<T> contenido, int numeroPagina, int tamanoPagina,
                        long totalElementos, int totalPaginas) {
}
