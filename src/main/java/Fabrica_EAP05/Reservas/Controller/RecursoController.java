package Fabrica_EAP05.Reservas.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import Fabrica_EAP05.Reservas.DTO.RegistrarRecursoRequest;
import Fabrica_EAP05.Reservas.DTO.RegistroRecursoResponse;



import Fabrica_EAP05.Reservas.Service.RecursoService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/recursos")
public class RecursoController {
    
    @Autowired
    private RecursoService recursoService;

    @PostMapping("/registrar")
    @PreAuthorize("hasRole('administrador')")
    public ResponseEntity<RegistroRecursoResponse> RegistrarRecurso(@Valid @RequestBody RegistrarRecursoRequest request) {
        RegistroRecursoResponse response = recursoService.registrarRecurso(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
