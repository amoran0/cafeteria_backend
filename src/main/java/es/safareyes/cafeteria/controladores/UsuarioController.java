package es.safareyes.cafeteria.controladores;

import es.safareyes.cafeteria.dtos.LoginDto;
import es.safareyes.cafeteria.dtos.LoginResponse;
import es.safareyes.cafeteria.dtos.UsuarioDTO;
import es.safareyes.cafeteria.servicios.UsuarioService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class UsuarioController {

    private UsuarioService service;

    @PostMapping("/registro")
    public void registrarUsuario(@Valid @RequestBody UsuarioDTO dto) {
        service.guardarUsuario(dto);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginDto dto) {
        return service.login(dto);
    }
}
