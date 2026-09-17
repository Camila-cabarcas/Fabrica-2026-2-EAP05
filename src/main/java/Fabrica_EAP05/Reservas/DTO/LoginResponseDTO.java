package Fabrica_EAP05.Reservas.DTO;

import Fabrica_EAP05.Reservas.Entities.Rol; 
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
    private String token;
    private String mensaje;
    private String usuario;
    private Rol rol;

    public LoginResponseDTO(String token, String usuario, Rol rol) {
        this.token = token;
        this.usuario = usuario;
        this.mensaje = "Inicio de sesión exitoso";
        this.rol = rol;
    }
}
