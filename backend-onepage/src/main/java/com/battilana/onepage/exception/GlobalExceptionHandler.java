package com.battilana.onepage.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FormatoArchivoNoValidoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleFormato(FormatoArchivoNoValidoException e){
        return Map.of("error", e.getMessage());
    }

    // Registro duplicado → mensaje claro en vez de 500
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleIntegridad(DataIntegrityViolationException e) {
        String causa = e.getMostSpecificCause().getMessage();
        if (causa != null && causa.contains("Duplicate entry")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)            // 409
                    .body(Map.of("error", "Los datos que intenta registrar ya están ingresados en el sistema."));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)            // 400
                .body(Map.of("error", "No se pudo guardar el registro: " + causa));
    }
}
