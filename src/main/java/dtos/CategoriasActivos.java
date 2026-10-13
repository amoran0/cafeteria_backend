package dtos;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriasActivos {
    private UUID idCategoria;
    private String nombre;
    private Long cantidadProductosActivos;
}