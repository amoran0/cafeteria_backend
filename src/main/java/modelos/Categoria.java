package modelos;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "categoria")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString



public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "")
    private int id;

    @Column (name = "nombre")
    private String nombre;

    @Column (name = "categoria")
    private String Boolean;

}
