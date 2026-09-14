package Fabrica_EAP05.Reservas.Service;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public void agregarTokenALista(String token) {
        blacklistedTokens.add(token);
    }

    public boolean estaEnLista(String token) {
        return blacklistedTokens.contains(token);
    }

    public void limpiarToken(String token) {
        blacklistedTokens.remove(token);
    }
}
