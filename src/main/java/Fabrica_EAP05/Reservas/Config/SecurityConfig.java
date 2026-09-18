package Fabrica_EAP05.Reservas.Config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Desactivar CSRF (necesario para APIs REST sin sesión)
            .csrf(csrf -> csrf.disable())

            // 2. Configurar permisos de rutas
            .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/login", "/api/auth/registro", "/api/usuario/registrar", "/api/usuario/login").permitAll()
            .anyRequest().authenticated()
)

            // 3. Desactivar el formulario HTML por defecto y la autenticación básica
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            // 4. Configurar manejo de sesión como Stateless (sin sesión HTTP, ideal para JWT)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
