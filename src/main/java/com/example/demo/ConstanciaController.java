package com.example.demo;

import com.example.demo.SolicitudConstanciaDTO;
import com.example.demo.SolicitudConstancia;
import com.example.demo.ConstanciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/constancias")
@RequiredArgsConstructor
public class ConstanciaController {

    private final ConstanciaService constanciaService;

    @PostMapping
    public ResponseEntity<SolicitudConstancia> registrarSolicitud(
            @Valid @RequestBody SolicitudConstanciaDTO solicitudDTO) {
        return ResponseEntity.ok(constanciaService.registrarSolicitud(solicitudDTO));
    }

    @GetMapping
    public ResponseEntity<List<SolicitudConstancia>> listarSolicitudes() {
        return ResponseEntity.ok(constanciaService.obtenerTodasLasSolicitudes());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}