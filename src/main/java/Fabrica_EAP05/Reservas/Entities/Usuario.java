package Fabrica_EAP05.Reservas.Entities;

import java.util.UUID;
import java.time.OffsetDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter
@Setter
public class Usuario {

    @Id
    private UUID id; // Coincide directamente con auth.users.id (sin @GeneratedValue)

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Email(message = "El email no es válido")
    @NotBlank(message = "El email es obligatorio")
    @Column(name = "email", unique = true) // Mapeo explícito a la columna 'email'
    private String email;

    private String telefono;

    @Enumerated(EnumType.STRING)
    private Rol rol = Rol.cliente;

    private Boolean activo = true;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    private String direccion;
}