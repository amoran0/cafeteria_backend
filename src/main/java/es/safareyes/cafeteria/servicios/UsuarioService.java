package es.safareyes.cafeteria.servicios;

import es.safareyes.cafeteria.conversores.UsuarioMapper;
import es.safareyes.cafeteria.dtos.LoginDTO;
import es.safareyes.cafeteria.dtos.LoginResponse;
import es.safareyes.cafeteria.dtos.UsuarioDTO;
import es.safareyes.cafeteria.excepciones.CredencialesIncorrectas;
import es.safareyes.cafeteria.excepciones.ReglaNegocioException;
import es.safareyes.cafeteria.modelos.RolUsuario;
import es.safareyes.cafeteria.modelos.Usuario;
import es.safareyes.cafeteria.repositorios.IUsuarioRepositorio;
import es.safareyes.cafeteria.security.JWTService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private IUsuarioRepositorio repository;
    private UsuarioMapper mapper;
    private JWTService jwtService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByMailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se ha encontrado el usuario"));
    }

    public void guardarUsuario(UsuarioDTO dto) {
        if (!dto.password().equals(dto.confirmPassword()))
            throw new ReglaNegocioException("Las contraseñas no coinciden");
        if (repository.existsByMailIgnoreCase(dto.email()))
            throw new ReglaNegocioException("Ya existe un usuario con ese email");

        Usuario usuarioGuardar = mapper.toEntity(dto);
        usuarioGuardar.setFechaAlta(LocalDateTime.now());
        usuarioGuardar.setRol(RolUsuario.CLIENTE);    // el rol lo pone el servidor; nadie puede registrarse como ADMIN
        usuarioGuardar.setActivo(true);
        usuarioGuardar.setContrasena(passwordEncoder.encode(dto.password()));

        repository.save(usuarioGuardar);
    }

    public LoginResponse login(LoginDTO dto) {
        // el mismo mensaje en los dos casos para no revelar qué emails existen
        Usuario usuario = repository.findByMailIgnoreCase(dto.email())
                .orElseThrow(() -> new CredencialesIncorrectas("Credenciales incorrectas"));

        if (!passwordEncoder.matches(dto.password(), usuario.getContrasena()))
            throw new CredencialesIncorrectas("Credenciales incorrectas");

        if (!usuario.isEnabled())
            throw new CredencialesIncorrectas("Usuario desactivado");

        return new LoginResponse(jwtService.generar(usuario));
    }
}
