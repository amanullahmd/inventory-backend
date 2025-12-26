package management.backend.inventory.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Structured error response returned by the API.
 * Includes timestamp, status, error type, message, and optional field-level details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Structured error response")
public class ErrorResponse {

    @Schema(description = "Timestamp when the error occurred", example = "2025-12-24T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error type/code", example = "VALIDATION_ERROR")
    private String error;

    @Schema(description = "Human-readable error message", example = "Validation failed")
    private String message;

    @Schema(description = "Request path that caused the error", example = "/api/auth/register")
    private String path;

    @Schema(description = "Field-level error details (for validation errors)")
    private Map<String, String> details;

    /**
     * Create an error response with all fields.
     */
    public static ErrorResponse of(int status, String error, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .build();
    }

    /**
     * Create an error response with field-level details.
     */
    public static ErrorResponse of(int status, String error, String message, String path, Map<String, String> details) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .details(details)
                .build();
    }
}
