package com.example.demo;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "solicitudes_constancia")
public class SolicitudConstancia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    private String matricula;
    private String correo;
    private String tipoConstancia;
    
    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;
    
    private String estado;
    
    @PrePersist
    public void prePersist() {
        fechaSolicitud = LocalDateTime.now();
        estado = "PENDIENTE";
    }
}
