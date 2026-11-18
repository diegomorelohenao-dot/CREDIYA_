package co.com.crediya.solicitudes.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ApiError {
    private final Instant timestamp;
    private final int status;
    private final String message;
    private final Map<String, String> details;
}