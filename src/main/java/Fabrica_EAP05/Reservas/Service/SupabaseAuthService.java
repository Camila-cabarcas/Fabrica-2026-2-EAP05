package Fabrica_EAP05.Reservas.Service;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import Fabrica_EAP05.Reservas.Exception.RecursoDuplicadoException;

// Usa el Admin API de Supabase (requiere la service_role key, nunca la anon
// key) para crear/eliminar filas en auth.users. public.usuario tiene un FK
// hacia auth.users, así que cualquier id insertado ahí debe existir primero
// en Supabase Auth.
@Service
public class SupabaseAuthService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-role-key}")
    private String serviceRoleKey;

    public UUID crearUsuarioAuth(String email) {
        HttpHeaders headers = headersAdmin();

        Map<String, Object> body = Map.of(
                "email", email,
                "password", UUID.randomUUID().toString(),
                "email_confirm", true
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    supabaseUrl + "/auth/v1/admin/users",
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            String id = (String) response.getBody().get("id");
            return UUID.fromString(id);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 422 || e.getStatusCode().value() == 400) {
                throw new RecursoDuplicadoException("El email ya está registrado en Supabase Auth");
            }
            throw new IllegalStateException("No se pudo crear el usuario en Supabase Auth: " + e.getStatusCode(), e);
        }
    }

    // Compensación best-effort: si falla el insert local después de crear el
    // usuario en Supabase Auth, se intenta borrar para no dejar huérfanos.
    // Un fallo aquí no debe ocultar la excepción original.
    public void eliminarUsuarioAuth(UUID id) {
        HttpHeaders headers = headersAdmin();
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(
                    supabaseUrl + "/auth/v1/admin/users/" + id,
                    HttpMethod.DELETE,
                    request,
                    Void.class
            );
        } catch (Exception e) {
            System.err.println("[SupabaseAuthService] No se pudo limpiar auth.users id=" + id + ": " + e.getMessage());
        }
    }

    private HttpHeaders headersAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", serviceRoleKey);
        headers.setBearerAuth(serviceRoleKey);
        return headers;
    }
}
