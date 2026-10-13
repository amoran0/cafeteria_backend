package modelos;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "descuento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Descuento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_descuento")
    private UUID idDescuento;

    @Column(name = "origen")
    private Short origen;

    @Column(name = "importe")
    private BigDecimal importe;
}