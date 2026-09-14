package Fabrica_EAP05.Reservas.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponseDTO login(LoginDTO dto) {
        Usuario usuario = usuarioRepository.findByCorreo(dto.getCorreo())
                .orElseThrow(() -> new IllegalArgumentException("Correo o contraseña incorrectos"));

        if (!passwordEncoder.matches(dto.getContrasena(), usuario.getContrasena())) {
            throw new IllegalArgumentException("Correo o contraseña incorrectos");
        }

        String token = jwtUtil.generarToken(usuario.getCorreo());
        inactivityTrackingService.registrarActividad(usuario.getCorreo());

        return new LoginResponseDTO(token, usuario.getCorreo());
    }

    public void logout(String correo, String token) {
        inactivityTrackingService.cerrarSesion(correo);
        
        if (token != null && !token.isEmpty()) {
            tokenBlacklistService.agregarTokenALista(token);
        }
    }
}