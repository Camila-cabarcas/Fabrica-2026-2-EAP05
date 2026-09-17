package Fabrica_EAP05.Reservas.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Fabrica_EAP05.Reservas.Config.JwtUtil;
import Fabrica_EAP05.Reservas.DTO.LoginDTO;
import Fabrica_EAP05.Reservas.DTO.LoginResponseDTO;
import Fabrica_EAP05.Reservas.Entities.Usuario;
import Fabrica_EAP05.Reservas.Repository.UsuarioRepository;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private InactivityTrackingService inactivityTrackingService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    public LoginResponseDTO login(LoginDTO dto) {
        // 1. Buscar perfil local por email
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // 2. Verificar estado del usuario en tu base de datos
        if (Boolean.FALSE.equals(usuario.getActivo())) {
            throw new IllegalArgumentException("El usuario se encuentra inactivo");
        }

        // 3. Generar token y registrar actividad
        String token = jwtUtil.generarToken(usuario.getEmail(), usuario.getRol());
        inactivityTrackingService.registrarActividad(usuario.getEmail());

        return new LoginResponseDTO(token, usuario.getEmail(), usuario.getRol());
    }

    public void logout(String email, String token) {
        inactivityTrackingService.cerrarSesion(email);
        
        if (token != null && !token.isEmpty()) {
            tokenBlacklistService.agregarTokenALista(token);
        }
    }
}