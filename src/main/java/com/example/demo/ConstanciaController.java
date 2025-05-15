package com.example.demo;

import com.example.demo.SolicitudConstanciaDTO;
import com.example.demo.SolicitudConstancia;
import com.example.demo.ConstanciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}