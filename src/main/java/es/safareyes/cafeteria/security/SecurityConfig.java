package es.safareyes.cafeteria.security;

import es.safareyes.cafeteria.servicios.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity     // permite usar @PreAuthorize("hasRole('ADMIN')") en los métodos
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();   // BCrypt
    }

    @Bean
    SecurityFilterChain cadenaApi(HttpSecurity http,
                                  UsuarioService service,
                                  JWTService jwtService) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // sin esto, un 403 se reenvía a /error como anónimo y acaba saliendo como 401
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/v1/usuarios/**").permitAll()
                        // la carta es pública
                        .requestMatchers(HttpMethod.GET, "/api/v1/categorias/**", "/api/v1/productos/**").permitAll()
                        // personal: agotar/reponer y hacer avanzar los pedidos
                        .requestMatchers(HttpMethod.PATCH,
                                "/api/v1/categorias/*/disponibilidad",
                                "/api/v1/productos/*/disponibilidad",
                                "/api/v1/pedidos/*/estado").hasAnyRole("ADMIN", "BARISTA")
                        // crear, modificar y dar de baja productos
                        .requestMatchers("/api/v1/categorias/**", "/api/v1/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/cupones/validar").authenticated()
                        // crear cupones e informes de ventas
                        .requestMatchers("/api/v1/cupones/**", "/api/v1/informes/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/pedidos/**").authenticated()
                        .anyRequest().authenticated())
                // 401 cuando no hay token o no es válido (por defecto daría 403)
                .exceptionHandling(e -> e.authenticationEntryPoint(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .addFilterBefore(new JWTFilter(jwtService, service),
                                 UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
