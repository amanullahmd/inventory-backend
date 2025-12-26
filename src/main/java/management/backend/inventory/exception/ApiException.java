package management.backend.inventory.exception;

/**
 * Base exception class for all API-related exceptions.
 * Provides consistent error handling across the application.
 */
public abstract class ApiException extends RuntimeException {
    private final String errorCode;
    private final int httpStatus;

    public ApiException(String message, String errorCode, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public ApiException(String message, Throwable cause, String errorCode, int httpStatus) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
