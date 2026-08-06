package app.mendnook.materials.web;

import app.mendnook.materials.shared.MaterialMissingException;
import app.mendnook.materials.shared.MaterialRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class MaterialsExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(MaterialsExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> fields = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> fields.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return response(HttpStatus.BAD_REQUEST, "Request validation failed", fields);
    }

    @ExceptionHandler(MaterialMissingException.class)
    ResponseEntity<ApiError> handleMissing(MaterialMissingException exception) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(MaterialRuleException.class)
    ResponseEntity<ApiError> handleRule(MaterialRuleException exception) {
        return response(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    ResponseEntity<ApiError> handleConcurrentChange(OptimisticLockingFailureException exception) {
        log.warn("Concurrent material stock update rejected");
        return response(HttpStatus.CONFLICT,
                "Material stock changed concurrently; reload and try again", Map.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException exception) {
        log.warn("Invalid API argument: {}", exception.getMessage());
        return response(HttpStatus.BAD_REQUEST, "A supplied value has an invalid format", Map.of());
    }

    private ResponseEntity<ApiError> response(HttpStatus status, String message, Map<String, String> fields) {
        return ResponseEntity.status(status)
                .body(new ApiError(Instant.now(), status.value(), message, fields));
    }
}
