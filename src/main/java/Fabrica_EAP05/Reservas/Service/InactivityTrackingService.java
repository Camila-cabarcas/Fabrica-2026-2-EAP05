package Fabrica_EAP05.Reservas.Service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InactivityTrackingService {

    private static final long INACTIVITY_TIMEOUT_MS = 30 * 60 * 1000; // 30 minutos
    private final Map<String, Long> userLastActivity = new ConcurrentHashMap<>();

    public void registrarActividad(String correo) {
        userLastActivity.put(correo, System.currentTimeMillis());
    }

    public boolean validarActividad(String correo) {
        Long lastActivity = userLastActivity.get(correo);

        if (lastActivity == null) {
            return false; // Usuario no tiene registro de actividad
        }

        long tiempoTranscurrido = System.currentTimeMillis() - lastActivity;

        if (tiempoTranscurrido > INACTIVITY_TIMEOUT_MS) {
            userLastActivity.remove(correo);
            return false; // Sesión expirada por inactividad
        }

        return true; // Sesión válida
    }

    public void cerrarSesion(String correo) {
        userLastActivity.remove(correo);
    }
}
