package es.safareyes.cafeteria;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import es.safareyes.cafeteria.dtos.CategoriaNombrePrecioDisponibilidad;
import es.safareyes.cafeteria.dtos.CategoriasActivos;
import es.safareyes.cafeteria.dtos.ProductoPorAlergeno;

import es.safareyes.cafeteria.modelos.Cupon;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import es.safareyes.cafeteria.repositorios.IAlergenoRepositorio;
import es.safareyes.cafeteria.repositorios.ICategoriaRepositorio;
import es.safareyes.cafeteria.repositorios.IClienteRepositorio;
import es.safareyes.cafeteria.repositorios.ICuponRepositorio;
import es.safareyes.cafeteria.repositorios.IPedidoRepositorio;
import es.safareyes.cafeteria.repositorios.IProductoRepositorio;
import es.safareyes.cafeteria.repositorios.IUsuarioRepositorio;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CafeteriaApplicationTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private IAlergenoRepositorio alergenoRepositorio;

    @Autowired
    private ICategoriaRepositorio categoriaRepositorio;

    @Autowired
    private IClienteRepositorio clienteRepositorio;

    @Autowired
    private ICuponRepositorio cuponRepositorio;

    @Autowired
    private IPedidoRepositorio pedidoRepositorio;

    @Autowired
    private IProductoRepositorio productoRepositorio;

    @Autowired
    private IUsuarioRepositorio usuarioRepositorio;

    @Test
    void testConexionBaseDeDatos() throws SQLException {
        assertThat(dataSource).isNotNull();
        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection.isValid(2)).isTrue();
            assertThat(connection.getMetaData().getDatabaseProductName()).isEqualTo("PostgreSQL");
        }
    }

    @Test
    void testFindAllProductos() {
        List<CategoriaNombrePrecioDisponibilidad> productos = productoRepositorio.CategoriaNombrePrecioDisponibilidad();
        assertThat(productos).isNotNull();
    }

    @Test
    void testFindNombreYAlergenos() {
        // ✅ Corregido a productoRepositorio
        List<ProductoPorAlergeno> todos = productoRepositorio.findNombreYNombreAlergenosPorId();
        assertThat(todos).isNotNull();
    }

    @Test
    void testProductosActivos() {
        List<CategoriasActivos> categorias = categoriaRepositorio.buscarCategoriasProductosActivos();
        assertThat(categorias).isNotNull();
    }
    @Test
    void testFindAllCupones() {
        List<Cupon> cupones = cuponRepositorio.findAll();
        assertThat(cupones).isNotNull();
    }
}