package Fabrica_EAP05.Reservas.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import Fabrica_EAP05.Reservas.Entities.DisponibilidadRecurso;

public interface DisponibilidadRecursoRepository extends JpaRepository<DisponibilidadRecurso, UUID> {

    // inicio <= horarioInicio AND fin >= horarioFin:
    // períodos de disponibilidad que cubren por completo el rango del horario.
    List<DisponibilidadRecurso> findByRecursoIdAndInicioLessThanEqualAndFinGreaterThanEqual(
            UUID recursoId, LocalDateTime horarioInicio, LocalDateTime horarioFin);
}
