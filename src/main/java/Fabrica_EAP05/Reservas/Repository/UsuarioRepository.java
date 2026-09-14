package Fabrica_EAP05.Reservas.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import Fabrica_EAP05.Reservas.Entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByCorreo(String correo);
    java.util.Optional<Usuario> findByCorreo(String correo);
}

