package es.safareyes.cafeteria;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import repositorios.IAlergenoRepositorio;
import repositorios.ICategoriaRepositorio;
import repositorios.IClienteRepositorio;
import repositorios.ICuponRepositorio;
import repositorios.IPedidoRepositorio;
import repositorios.IProductoRepositorio;
import repositorios.IUsuarioRepositorio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CafeteriaApplicationTests {

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
    @DisplayName("Conecta con PostgreSQL y accede al schema configurado")
    void conectaConPostgreSql() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            assertTrue(connection.isValid(2), "La conexión JDBC debe estar activa");
            assertThat(connection.getMetaData().getDatabaseProductName())
                    .isEqualTo("PostgreSQL");

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT current_schema()")) {
                assertTrue(resultSet.next(), "PostgreSQL debe devolver el schema actual");
                assertThat(resultSet.getString(1)).isNotBlank();
            }
        }
    }

    @Test
    @DisplayName("Los repositorios pueden consultar sus tablas")
    void repositoriosConsultanSusTablas() {
        assertAll(
                () -> assertDoesNotThrow(() -> alergenoRepositorio.findAll()),
                () -> assertDoesNotThrow(() -> categoriaRepositorio.findAll()),
                () -> assertDoesNotThrow(() -> clienteRepositorio.findAll()),
                () -> assertDoesNotThrow(() -> cuponRepositorio.findAll()),
                () -> assertDoesNotThrow(() -> pedidoRepositorio.findAll()),
                () -> assertDoesNotThrow(() -> pedidoRepositorio.obtenerTodosPedidos()),
                () -> assertDoesNotThrow(() -> productoRepositorio.findAll()),
                () -> assertDoesNotThrow(() -> usuarioRepositorio.findAll())
        );
    }
}
