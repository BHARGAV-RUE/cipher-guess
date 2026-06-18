package com.bhargav.crack_the_number.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── Helpers ────────────────────────────────────────────────────────────────

    private ResponseEntity<Map<String, Object>> build(HttpStatus status,
                                                      String error,
                                                      String message,
                                                      HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    status.value());
        body.put("error",     error);
        body.put("message",   message);
        body.put("path",      request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }

    // ── OAuth2 / Google Sign-In ────────────────────────────────────────────────

    /**
     * Catches NPE thrown when Google returns a null "name" attribute
     * (e.g. unverified accounts or restricted scopes) and any other
     * unexpected failure inside OAuth2SuccessHandler.
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, Object>> handleNullPointer(NullPointerException ex,
                                                                 HttpServletRequest request) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "A null value was encountered";
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Null Pointer Error",
                "Unexpected null value — " + msg,
                request);
    }

    // ── Request / Validation Errors ────────────────────────────────────────────

    /**
     * Missing @RequestParam (e.g. /game/start called without ?difficulty=).
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {

        return build(HttpStatus.BAD_REQUEST,
                "Missing Parameter",
                "Required parameter '" + ex.getParameterName() + "' is missing",
                request);
    }

    /**
     * Wrong type for a @RequestParam (e.g. /game/guess?guess=abc).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        String expected = ex.getRequiredType() != null
                ? ex.getRequiredType().getSimpleName()
                : "unknown";

        return build(HttpStatus.BAD_REQUEST,
                "Type Mismatch",
                "Parameter '" + ex.getName() + "' must be of type " + expected,
                request);
    }

    /**
     * Enum parse failure (e.g. /game/start?difficulty=IMPOSSIBLE).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        return build(HttpStatus.BAD_REQUEST,
                "Invalid Argument",
                ex.getMessage(),
                request);
    }

    // ── Auth Errors ────────────────────────────────────────────────────────────

    /**
     * Any RuntimeException that slips through (covers unexpected service errors).
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex,
                                                             HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred",
                request);
    }

    /**
     * Catch-all for anything not matched above — replaces Spring's Whitelabel page.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex,
                                                             HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected Error",
                ex.getMessage() != null ? ex.getMessage() : "Something went wrong",
                request);
    }
}