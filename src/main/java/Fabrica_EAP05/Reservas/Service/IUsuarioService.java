package Fabrica_EAP05.Reservas.Service;

import Fabrica_EAP05.Reservas.DTO.UsuarioRegistroRequest;
import Fabrica_EAP05.Reservas.DTO.UsuarioRegistroResponse;
import Fabrica_EAP05.Reservas.Entities.Rol;

public interface IUsuarioService {
    UsuarioRegistroResponse registrarUsuario(UsuarioRegistroRequest request, Rol rolForzado);
}
