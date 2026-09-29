package Fabrica_EAP05.Reservas.Repository;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import Fabrica_EAP05.Reservas.Entities.Recurso;

public interface RecursoRepository extends JpaRepository<Recurso, UUID> {
    boolean existsByNombre(String nombre);
}
 