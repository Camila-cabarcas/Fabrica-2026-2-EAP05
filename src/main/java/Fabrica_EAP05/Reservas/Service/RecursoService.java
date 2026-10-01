package Fabrica_EAP05.Reservas.Service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Fabrica_EAP05.Reservas.DTO.RegistrarRecursoRequest;
import Fabrica_EAP05.Reservas.DTO.RegistroRecursoResponse;



import Fabrica_EAP05.Reservas.Entities.Recurso;
import Fabrica_EAP05.Reservas.Exception.RecursoDuplicadoException;
import Fabrica_EAP05.Reservas.Repository.RecursoRepository;
import jakarta.persistence.EntityManager;


@Service 
public class RecursoService{

    @Autowired 
    RecursoRepository recursoRepository;

    @Autowired
    private EntityManager entityManager;


    public RegistroRecursoResponse registrarRecurso(RegistrarRecursoRequest request) {
        if (recursoRepository.existsByNombre(request.getNombre())) {
            throw new RecursoDuplicadoException("El nombre de este recurso ya está registrado");
        }
        
        Recurso recurso = new Recurso();
        recurso.setId(UUID.randomUUID());
        recurso.setNombre(request.getNombre());
        recurso.setDescripcion(request.getDescripcion());
        recurso.setActivo(true);
        Recurso guardado = recursoRepository.saveAndFlush(recurso);
        entityManager.detach(guardado);
        Recurso recargado = recursoRepository.findById(guardado.getId())
            .orElseThrow(() -> new IllegalStateException("No se pudo recuperar el recurso recién creado"));
        return new RegistroRecursoResponse(
                recargado.getId(),
                recargado.getNombre(),
                recargado.getDescripcion(),
                recargado.getActivo(),
                recargado.getCreatedAt()
        );
    }
}
