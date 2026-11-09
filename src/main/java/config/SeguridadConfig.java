package config;

import modelos.RolUsuario;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import repositorios.IUsuarioRepositorio;

/** Autenticación HTTP Basic con los usuarios de la tabla usuario (mail + contrasena). */
@Configuration
public class SeguridadConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        // Productos y categorías
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos", "/api/v1/productos/*", "/api/v1/categorias").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/productos/*/disponibilidad",
                                "/api/v1/categorias/*/disponibilidad").hasAnyRole("ADMIN", "BARISTA")
                        .requestMatchers("/api/v1/productos/**").hasRole("ADMIN")
                        // Pedidos
                        .requestMatchers(HttpMethod.POST, "/api/v1/pedidos").hasAnyRole("BARISTA", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/**").hasAnyRole("ADMIN", "BARISTA", "CLIENTE")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/pedidos/*/estado").hasAnyRole("ADMIN", "BARISTA")
                        // Cupones
                        .requestMatchers(HttpMethod.POST, "/api/v1/cupones/validar").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/cupones").hasRole("ADMIN")
                        // Informes
                        .requestMatchers("/api/v1/informes/**").hasRole("ADMIN")
                        .anyRequest().denyAll());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(IUsuarioRepositorio usuarioRepositorio) {
        return mail -> usuarioRepositorio.findByMail(mail)
                .map(u -> User.withUsername(u.getMail())
                        .password(u.getContrasena())
                        .roles((u.getRol() == null ? RolUsuario.CLIENTE : u.getRol()).name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + mail));
    }

    /** Admite contraseñas con prefijo ({bcrypt}...) y, si no lo tienen, en texto plano (datos de prueba). */
    @Bean
    @SuppressWarnings("deprecation")
    public PasswordEncoder passwordEncoder() {
        DelegatingPasswordEncoder encoder =
                (DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();
        encoder.setDefaultPasswordEncoderForMatches(NoOpPasswordEncoder.getInstance());
        return encoder;
    }
}
