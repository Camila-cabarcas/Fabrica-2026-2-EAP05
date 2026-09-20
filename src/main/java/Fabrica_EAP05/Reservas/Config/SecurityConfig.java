package Fabrica_EAP05.Reservas.Config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Sin esto, un frontend en otro origen (ej. localhost:5173) nunca llega
    // al filtro de Spring Security: el navegador corta la petición en el
    // preflight y algunas herramientas de dev lo reportan como 403.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Desactivar CSRF (necesario para APIs REST sin sesión)
            .csrf(csrf -> csrf.disable())

            // 2. Habilitar CORS con la configuración de arriba
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // 3. Configurar permisos de rutas
            .authorizeHttpRequests(auth -> auth
                // El preflight CORS (OPTIONS) no matchea contra requestMatchers(String...)
                // porque Spring MVC no expone OPTIONS como handler real de la ruta;
                // sin esta línea, cualquier request con Content-Type: application/json
                // (que dispara preflight) cae en anyRequest().authenticated() -> 403.
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuario/registrar").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
                .anyRequest().authenticated()
            )

            // 4. Desactivar el formulario HTML por defecto y la autenticación básica
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            // 5. Configurar manejo de sesión como Stateless (sin sesión HTTP, ideal para JWT)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            // TEMPORAL: quitar una vez identificada la causa del 403 en Render.
            // Corre después de JwtFilter para mostrar exactamente lo que va a
            // evaluar el AuthorizationFilter (authorizeHttpRequests) al final de la cadena.
            .addFilterAfter(new RequestLoggingFilter(), JwtFilter.class);
        return http.build();
    }

    // TEMPORAL: filtro de diagnóstico. No loguea el valor de Authorization
    // (solo si está presente) para no exponer JWTs en los logs de Render.
    private static class RequestLoggingFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                         HttpServletResponse response,
                                         FilterChain filterChain) throws ServletException, IOException {
            Authentication authAntes = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("[SECURITY-DEBUG] >>> " + request.getMethod() + " " + request.getRequestURI()
                    + " | Content-Length=" + request.getContentLength()
                    + " | Content-Type=" + request.getContentType()
                    + " | Authorization=" + (request.getHeader("Authorization") != null ? "presente" : "ausente")
                    + " | authAntesDeAuthorizationFilter=" + (authAntes != null ? authAntes.getClass().getSimpleName() + "/" + authAntes.isAuthenticated() : "null"));

            filterChain.doFilter(request, response);

            System.out.println("[SECURITY-DEBUG] <<< " + request.getMethod() + " " + request.getRequestURI()
                    + " -> status=" + response.getStatus());
        }
    }
}
