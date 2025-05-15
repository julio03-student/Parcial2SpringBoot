package com.example.demo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SolicitudConstanciaDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "La matrícula es obligatoria")
    @Pattern(regexp = "^[A-Z0-9]{8,10}$", message = "La matrícula debe tener formato válido")
    private String matricula;
    
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe proporcionar un correo electrónico válido")
    private String correo;
    
    @NotBlank(message = "El tipo de constancia es obligatorio")
    private String tipoConstancia;
}
