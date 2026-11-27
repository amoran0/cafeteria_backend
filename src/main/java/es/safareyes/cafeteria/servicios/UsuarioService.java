package es.safareyes.cafeteria.servicios;

import es.safareyes.cafeteria.conversores.UsuarioMapper;
import es.safareyes.cafeteria.dtos.request.LoginDto;
import es.safareyes.cafeteria.dtos.response.LoginResponse;
import es.safareyes.cafeteria.dtos.request.UsuarioDTO;
import es.safareyes.cafeteria.excepciones.CredencialesIncorrectas;
import es.safareyes.cafeteria.excepciones.ElementoDuplicado;
import es.safareyes.cafeteria.excepciones.ReglaNegocioException;
import es.safareyes.cafeteria.modelos.Cliente;
import es.safareyes.cafeteria.modelos.RolUsuario;
import es.safareyes.cafeteria.modelos.Usuario;
import es.safareyes.cafeteria.repositorios.IClienteRepositorio;
import es.safareyes.cafeteria.repositorios.IUsuarioRepositorio;
import es.safareyes.cafeteria.security.JWTService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class UsuarioService implements UserDetailsService {

    private PasswordEncoder passwordEncoder;
    private IUsuarioRepositorio repository;
    private IClienteRepositorio clienteRepositorio;
    private UsuarioMapper mapper;
    private JWTService jwtService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByMailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se ha encontrado el usuario"));
    }

    /** Alta de un usuario CLIENTE y de su ficha de cliente de la cafetería, en la misma transacción */
    @Transactional
    public void guardarUsuario(UsuarioDTO dto) {
        if (!dto.password().equals(dto.confirmPassword()))
            throw new ReglaNegocioException("Las contraseñas no coinciden");
        if (repository.existsByMailIgnoreCase(dto.email()))
            throw new ElementoDuplicado("Ya existe un usuario con ese email");   // duplicado → 409

        Usuario usuarioGuardar = mapper.toEntity(dto);
        usuarioGuardar.setFechaAlta(LocalDateTime.now());
        usuarioGuardar.setRol(RolUsuario.CLIENTE);    // el rol lo pone el servidor; nadie puede registrarse como ADMIN
        usuarioGuardar.setActivo(true);
        usuarioGuardar.setContrasena(passwordEncoder.encode(dto.password()));

        repository.save(usuarioGuardar);

        // al registrarse pasa a ser cliente de la cafetería (el DNI solo hace falta para FitZone)
        Cliente ficha = new Cliente();
        ficha.setUsuario(usuarioGuardar);
        ficha.setMail(usuarioGuardar.getMail());
        clienteRepositorio.save(ficha);
    }

    public LoginResponse login(LoginDto dto) {
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
