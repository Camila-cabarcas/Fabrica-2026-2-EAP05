package Fabrica_EAP05.Reservas.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Email(message = "El correo no es válido")
    @NotBlank(message = "El correo es obligatorio")
    @Column(unique = true)
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @JsonIgnore
    private String contrasena;
    private String direccion;
    private String telefono;
    @Enumerated(EnumType.STRING)
    private Rol rol = Rol.cliente; // Valor por defecto

    private Boolean activo = true;
}