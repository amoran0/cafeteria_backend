package es.safareyes.cafeteria;

import es.safareyes.cafeteria.validaciones.DniValidator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Validador propio @DniValido: 8 números y la letra que corresponde (resto de dividir entre 23) */
class DniValidatorTest {

    private final DniValidator validador = new DniValidator();

    @Test
    void dniCorrectos() {
        assertThat(validador.isValid("12345678Z", null)).isTrue();
        assertThat(validador.isValid("12345678z", null)).isTrue();   // la letra en minúscula también vale
        assertThat(validador.isValid("00000000T", null)).isTrue();
    }

    @Test
    void dniIncorrectos() {
        assertThat(validador.isValid("12345678A", null)).isFalse();  // letra que no corresponde
        assertThat(validador.isValid("1234567Z", null)).isFalse();   // 7 números
        assertThat(validador.isValid("12345678", null)).isFalse();   // sin letra
        assertThat(validador.isValid("ABCDEFGHZ", null)).isFalse();
    }

    @Test
    void campoOpcional() {
        assertThat(validador.isValid(null, null)).isTrue();
        assertThat(validador.isValid("  ", null)).isTrue();
    }
}
