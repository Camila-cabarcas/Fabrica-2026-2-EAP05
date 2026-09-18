package Fabrica_EAP05.Reservas.DTO;

import Fabrica_EAP05.Reservas.Entities.Rol;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class UsuarioRegistroResponse {
    private UUID id;
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;
    private Rol rol;
    private OffsetDateTime createdAt;

    public UsuarioRegistroResponse(UUID id, String nombre, String email, String telefono,
                                    String direccion, Rol rol, OffsetDateTime createdAt) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.rol = rol;
        this.createdAt = createdAt;
    }
}
