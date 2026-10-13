package modelos;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "alergeno")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Alergeno {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_alergeno")
    private UUID idAlergeno;

    @Column(name = "nombre", nullable = false)
    private String nombre;
}