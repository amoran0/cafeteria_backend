package es.safareyes.cafeteria.security;

import es.safareyes.cafeteria.servicios.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

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

                        // ===== PÚBLICAS (sin token) =====
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/registro", "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/categorias").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos", "/api/v1/productos/*").permitAll()

                        // ===== ADMIN o BARISTA: agotar/reponer y hacer avanzar los pedidos =====
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/categorias/*/disponibilidad").hasAnyRole("ADMIN", "BARISTA")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/productos/*/disponibilidad").hasAnyRole("ADMIN", "BARISTA")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/pedidos/*/estado").hasAnyRole("ADMIN", "BARISTA")

                        // ===== SOLO ADMIN =====
                        .requestMatchers(HttpMethod.POST, "/api/v1/productos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/productos/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/productos/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/cupones").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/informes/ventas-dia").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/informes/productos-top").hasRole("ADMIN")

                        // ===== BARISTA o CLIENTE: crear pedidos (en barra o desde la app) =====
                        .requestMatchers(HttpMethod.POST, "/api/v1/pedidos").hasAnyRole("BARISTA", "CLIENTE")

                        // ===== CUALQUIER USUARIO LOGUEADO =====
                        .requestMatchers(HttpMethod.POST, "/api/v1/cupones/validar").authenticated()
                        // un cliente solo ve sus pedidos: lo controla PedidoService
                        .requestMatchers(HttpMethod.GET, "/api/v1/pedidos", "/api/v1/pedidos/*").authenticated()

                        // rutas que no existen: con token llegan al controlador de errores (404)
                        .anyRequest().authenticated())
                // 401 sin token o con token no válido, 403 sin el rol necesario; ambos con formato ProblemDetail
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((peticion, respuesta, ex) -> responder(peticion, respuesta,
                                HttpStatus.UNAUTHORIZED, "No autenticado",
                                "Falta el token o no es válido (sin token, firma incorrecta o caducado)"))
                        .accessDeniedHandler((peticion, respuesta, ex) -> responder(peticion, respuesta,
                                HttpStatus.FORBIDDEN, "Acceso denegado",
                                "Tu rol no tiene permiso para esta operación")))
                .addFilterBefore(new JWTFilter(jwtService, service),
                                 UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Los errores de seguridad se producen en los filtros, antes de llegar a los controladores, así que no pasan por
     * ErrorGlobalController: se escribe aquí el mismo formato ProblemDetail (type, title, status, detail, instance, timestamp).
     */
    private static void responder(HttpServletRequest peticion, HttpServletResponse respuesta, HttpStatus estado,
                                  String titulo, String detalle) throws IOException {
        respuesta.setStatus(estado.value());
        respuesta.setCharacterEncoding(StandardCharsets.UTF_8.name());
        respuesta.setContentType("application/problem+json");
        String instancia = peticion.getRequestURI().replace("\\", "\\\\").replace("\"", "\\\"");
        respuesta.getWriter().write("""
                {"type":"about:blank","title":"%s","status":%d,"detail":"%s","instance":"%s","timestamp":"%s"}"""
                .formatted(titulo, estado.value(), detalle, instancia, LocalDateTime.now()));
    }
}
