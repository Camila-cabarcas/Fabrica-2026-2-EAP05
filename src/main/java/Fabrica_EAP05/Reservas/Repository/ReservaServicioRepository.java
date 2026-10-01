package Fabrica_EAP05.Reservas.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Fabrica_EAP05.Reservas.Entities.ReservaServicio;

public interface ReservaServicioRepository extends JpaRepository<ReservaServicio, UUID> {
    long countByServicioId(UUID servicioId);
    List<ReservaServicio> findByServicioId(UUID servicioId);

    // Reservas confirmadas que ya ocupan el recurso en un horario que se cruza con [inicio, fin)
    @Query("SELECT COUNT(r) FROM ReservaServicio r, Horario h "
            + "WHERE r.horarioId = h.id "
            + "AND r.recursoId = :recursoId "
            + "AND r.estado = Fabrica_EAP05.Reservas.Enums.EstadoReserva.confirmada "
            + "AND h.inicio < :fin AND h.fin > :inicio")
    long contarReservasDeRecursoEnRango(@Param("recursoId") UUID recursoId,
                                        @Param("inicio") LocalDateTime inicio,
                                        @Param("fin") LocalDateTime fin);
}
