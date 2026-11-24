package es.safareyes.cafeteria.security;

import es.safareyes.cafeteria.servicios.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Se ejecuta una vez por petición. Si llega "Authorization: Bearer <token>" y el token es válido,
 * mete al usuario en el SecurityContext. No rechaza nada: quien decide si se puede pasar es SecurityConfig.
 * No lleva @Component para que Spring no lo registre también como filtro normal (se ejecutaría dos veces).
 */
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private static final String PREFIJO = "Bearer ";
    private final JWTService jwtService;
    private final UsuarioService usuarioDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest peticion, HttpServletResponse respuesta,
                                    FilterChain filterChain) throws ServletException, IOException {
        String cabecera = peticion.getHeader(HttpHeaders.AUTHORIZATION);
        if (cabecera == null || !cabecera.startsWith(PREFIJO)) {
            filterChain.doFilter(peticion, respuesta);   // sin token: sigue como anónimo
            return;
        }
        try {
            Jwt token = jwtService.validar(cabecera.substring(PREFIJO.length()));
            UserDetails detalles = usuarioDetailsService.loadUserByUsername(token.getSubject());
            if (detalles.isEnabled()) {
                var autenticacion = UsernamePasswordAuthenticationToken.authenticated(
                        detalles, null, detalles.getAuthorities());
                autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(peticion));
                SecurityContext contexto = SecurityContextHolder.createEmptyContext();
                contexto.setAuthentication(autenticacion);
                SecurityContextHolder.setContext(contexto);
            }
        } catch (JwtException | UsernameNotFoundException e) {
            // token falso, caducado o de un usuario que ya no existe: sigue como anónimo
            SecurityContextHolder.clearContext();
            logger.debug("Token rechazado: " + e.getMessage());
        }
        filterChain.doFilter(peticion, respuesta);
    }
}
