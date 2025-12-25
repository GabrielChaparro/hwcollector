package com.hwcollectors.app.controller;

import com.hwcollectors.app.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException ex) {
        // Aquí usamos la "reason" como code. Puedes mapear a mensajes más humanos.
        String code = ex.getReason() == null ? "ERROR" : ex.getReason();
        String message = humanize(code);

        var body = new ApiError(code, message, Instant.now());
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        var body = new ApiError("INTERNAL_ERROR", "Ha ocurrido un error inesperado", Instant.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private String humanize(String code) {
        return switch (code) {
            case "HOTWHEEL_NOT_FOUND" -> "No existe ese código de HotWheel";
            case "WISHLIST_ITEM_EXISTS" -> "Ese coche ya está en tu wishlist";
            case "COLLECTION_ITEM_NOT_FOUND" -> "No se encontró el item de tu colección";
            case "PUBLIC_VISIBILITY_REQUIRED" -> "Para vender o mostrar disponibilidad, el item debe ser público";
            case "INVALID_AVAILABILITY" -> "Disponibilidad inválida";
            case "INVALID_VISIBILITY" -> "Visibilidad inválida";
            case "INVALID_PRICE" -> "Precio inválido";
            case "NOT_YOURS" -> "No tienes permisos sobre ese recurso";
            default -> code; // fallback
        };
    }
}

