package co.com.crediya.solicitudes.api;

import java.util.List;

public record PaginaResponse<T>(List<T> content, int page, int size,
                                long totalElements, int totalPages) {
}
