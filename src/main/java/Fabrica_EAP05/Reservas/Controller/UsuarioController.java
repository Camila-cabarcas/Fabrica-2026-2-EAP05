package Fabrica_EAP05.Reservas.Controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import Fabrica_EAP05.Reservas.DTO.UsuarioRegistroRequest;
import Fabrica_EAP05.Reservas.DTO.UsuarioRegistroResponse;
import Fabrica_EAP05.Reservas.Entities.Rol;
import Fabrica_EAP05.Reservas.Service.IUsuarioService;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

    @PostMapping("/registrar")
    public ResponseEntity<UsuarioRegistroResponse> registrarCliente(@Valid @RequestBody UsuarioRegistroRequest request) {
        UsuarioRegistroResponse response = usuarioService.registrarUsuario(request, Rol.cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/registrar/proveedor")
    @PreAuthorize("hasRole('administrador')")
    public ResponseEntity<UsuarioRegistroResponse> registrarProveedor(@Valid @RequestBody UsuarioRegistroRequest request) {
        UsuarioRegistroResponse response = usuarioService.registrarUsuario(request, Rol.proveedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
