package Fabrica_EAP05.Reservas.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import Fabrica_EAP05.Reservas.Entities.ReservaServicio;

public interface ReservaServicioRepository extends JpaRepository<ReservaServicio, UUID> {
    long countByServicioId(UUID servicioId);
    List<ReservaServicio> findByServicioId(UUID servicioId);
}
