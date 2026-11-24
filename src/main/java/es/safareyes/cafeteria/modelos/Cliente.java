package es.safareyes.cafeteria.modelos;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Ficha de cliente de la cafetería. Se crea al registrarse; nombre y DNI son opcionales
 * (el DNI solo hace falta para el descuento de socio de FitZone).
 */
@Entity
@Table(name = "cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_cliente")
    private UUID idCliente;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "mail", nullable = false)
    private String mail;

    @Column(name = "dni", unique = true)
    private String dni;
}
