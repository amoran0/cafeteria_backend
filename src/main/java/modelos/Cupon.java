package modelos;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "cupon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cupon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_cupon")
    private UUID idCupon;

    @Column(name = "codigo", nullable = false, unique = true)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "tipo", nullable = false)
    private TipoCupon tipo;

    @Column(name = "valor")
    private BigDecimal valor;

    @Column(name = "importe_min")
    private BigDecimal importeMin;

    @Column(name = "max_usos")
    private Integer maxUsos;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;
}