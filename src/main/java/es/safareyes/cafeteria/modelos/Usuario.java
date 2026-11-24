package es.safareyes.cafeteria.modelos;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Usuario que inicia sesión en la API. Implementa UserDetails para que Spring Security
 * sepa leer su nombre de usuario (el mail), su contraseña cifrada y su rol.
 */
@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_usuario")
    private UUID idUsuario;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "rol", nullable = false)
    private RolUsuario rol = RolUsuario.CLIENTE;

    @Column(name = "mail", nullable = false, unique = true)
    private String mail;

    /** Siempre cifrada con BCrypt ("{bcrypt}$2a$..."), nunca en claro */
    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // El prefijo ROLE_ es obligatorio para que funcione hasRole("ADMIN")
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    /** Se inicia sesión con el mail */
    @Override
    public String getUsername() {
        return mail;
    }

    @Override
    public String getPassword() {
        return contrasena;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    /** Un usuario desactivado no puede iniciar sesión ni usar su token */
    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(activo);
    }
}
