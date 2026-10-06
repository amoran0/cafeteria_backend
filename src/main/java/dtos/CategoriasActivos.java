package dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import modelos.Categoria;

@Data
@AllArgsConstructor
public class CategoriasActivos {

    private Boolean categoria;
    private String cantidadProductosActivos;
}
