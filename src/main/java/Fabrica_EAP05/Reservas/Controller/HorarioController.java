package Fabrica_EAP05.Reservas.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Fabrica_EAP05.Reservas.DTO.HorarioRequest;
import Fabrica_EAP05.Reservas.DTO.HorarioResponse;
import Fabrica_EAP05.Reservas.Entities.Horario;
import Fabrica_EAP05.Reservas.Service.HorarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    @Autowired
    private HorarioService horarioService;

    @PostMapping
    @PreAuthorize("hasAuthority('proveedor') or hasRole('proveedor')")
    public ResponseEntity<?> registrarHorario(@Valid @RequestBody HorarioRequest request) {
        try {
            Horario nuevoHorario = horarioService.registrarHorario(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(HorarioResponse.fromEntity(nuevoHorario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}