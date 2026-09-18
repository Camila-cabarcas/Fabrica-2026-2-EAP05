package Fabrica_EAP05.Reservas.Service;

import java.util.UUID;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Fabrica_EAP05.Reservas.DTO.UsuarioRegistroRequest;
import Fabrica_EAP05.Reservas.DTO.UsuarioRegistroResponse;
import Fabrica_EAP05.Reservas.Entities.Rol;
import Fabrica_EAP05.Reservas.Entities.Usuario;
import Fabrica_EAP05.Reservas.Exception.RecursoDuplicadoException;
import Fabrica_EAP05.Reservas.Repository.UsuarioRepository;

@Service
@Transactional
public class UsuarioServiceImpl implements IUsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public UsuarioRegistroResponse registrarUsuario(UsuarioRegistroRequest request, Rol rolForzado) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RecursoDuplicadoException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setTelefono(request.getTelefono());
        usuario.setDireccion(request.getDireccion());
        usuario.setRol(rolForzado);
        usuario.setActivo(true);

        Usuario guardado = usuarioRepository.saveAndFlush(usuario);

        entityManager.detach(guardado);

        Usuario recargado = usuarioRepository.findById(guardado.getId())
                .orElseThrow(() -> new IllegalStateException("No se pudo recuperar el usuario recién creado"));

        return new UsuarioRegistroResponse(
                recargado.getId(),
                recargado.getNombre(),
                recargado.getEmail(),
                recargado.getTelefono(),
                recargado.getDireccion(),
                recargado.getRol(),
                recargado.getCreatedAt()
        );
    }
}
