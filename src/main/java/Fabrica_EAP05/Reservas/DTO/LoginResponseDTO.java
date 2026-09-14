package Fabrica_EAP05.Reservas.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
    private String token;
    private String mensaje;
    private String usuario;

    public LoginResponseDTO(String token, String usuario) {
        this.token = token;
        this.usuario = usuario;
        this.mensaje = "Inicio de sesión exitoso";
    }
}
