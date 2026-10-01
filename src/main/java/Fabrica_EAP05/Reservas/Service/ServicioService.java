package Fabrica_EAP05.Reservas.Service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Fabrica_EAP05.Reservas.DTO.RegistrarServicioRequest;
import Fabrica_EAP05.Reservas.DTO.RegistrarServicioResponse;
import Fabrica_EAP05.Reservas.Entities.Recurso;
import Fabrica_EAP05.Reservas.Entities.Servicio;
import Fabrica_EAP05.Reservas.Exception.RecursoDuplicadoException;
import Fabrica_EAP05.Reservas.Repository.RecursoRepository;
import Fabrica_EAP05.Reservas.Repository.ServicioRepository;
import jakarta.persistence.EntityManager;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private RecursoRepository recursoRepository;

    @Autowired
    private EntityManager entityManager;

    public boolean validarRecursoRequerido(UUID recursoId) {
        if (recursoId == null) {
            return true;
        }

        Recurso recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El recurso con ID " + recursoId + " no existe en el sistema"));

        if (!Boolean.TRUE.equals(recurso.getActivo())) {
            throw new IllegalArgumentException("El recurso con ID " + recursoId + " no está activo");
        }

        return true;
    }

    public RegistrarServicioResponse registrarServicio(RegistrarServicioRequest request) {
        if (servicioRepository.existsByNombre(request.getNombre())) {
            throw new RecursoDuplicadoException(
                    "El nombre del servicio '" + request.getNombre() + "' ya está registrado");
        }

        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del servicio es obligatorio");
        }

        Recurso recurso = null;
        if (request.getRecursoId() != null) {
            validarRecursoRequerido(request.getRecursoId());
            recurso = recursoRepository.findById(request.getRecursoId()).orElse(null);
        }

        Servicio servicio = new Servicio();
        servicio.setId(UUID.randomUUID());
        servicio.setNombre(request.getNombre());
        servicio.setDescripcion(request.getDescripcion());
        servicio.setDuracionMin(request.getDuracionMin());
        servicio.setRecurso(recurso);
        servicio.setActivo(true);

        Servicio guardado = servicioRepository.saveAndFlush(servicio);
        entityManager.detach(guardado);
        Servicio recargado = servicioRepository.findById(guardado.getId())
                .orElseThrow(() -> new IllegalStateException("No se pudo recuperar el servicio recién creado"));

        return new RegistrarServicioResponse(
                recargado.getId(),
                recargado.getNombre(),
                recargado.getDescripcion(),
                recargado.getDuracionMin(),
                recargado.getRecurso() != null ? recargado.getRecurso().getId() : null,
                recargado.getRecurso() != null ? recargado.getRecurso().getNombre() : null,
                recargado.getActivo(),
                recargado.getCreatedAt()
        );
    }
}
