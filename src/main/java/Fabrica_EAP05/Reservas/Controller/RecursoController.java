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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/recursos")
@Tag(name = "Recursos", description = "Gestión de recursos (salas, equipos, etc.)")
public class RecursoController {

    @Autowired
    private RecursoService recursoService;

    @Operation(summary = "Registrar recurso",
            description = "Crea un nuevo recurso. Solo administradores.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Recurso registrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Sin autenticación"),
            @ApiResponse(responseCode = "403", description = "Sin permiso (requiere rol administrador)"),
            @ApiResponse(responseCode = "409", description = "Ya existe un recurso con ese nombre")
    })
    @PostMapping("/registrar")
    @PreAuthorize("hasRole('administrador')")
    public ResponseEntity<RegistroRecursoResponse> RegistrarRecurso(@Valid @RequestBody RegistrarRecursoRequest request) {
        RegistroRecursoResponse response = recursoService.registrarRecurso(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
