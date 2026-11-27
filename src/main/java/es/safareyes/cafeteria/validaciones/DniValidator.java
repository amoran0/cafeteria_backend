package es.safareyes.cafeteria.validaciones;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Comprueba un DNI: 8 números y una letra. La letra se calcula con el resto de dividir
 * el número entre 23, que indica su posición en LETRAS.
 */
public class DniValidator implements ConstraintValidator<DniValido, String> {

    private static final String LETRAS = "TRWAGMYFPDXBNJZSQVHLCKE";

    @Override
    public boolean isValid(String dni, ConstraintValidatorContext context) {
        if (dni == null || dni.isBlank()) {
            return true;   // campo opcional: la obligatoriedad la decide @NotBlank
        }
        String valor = dni.trim().toUpperCase();
        if (!valor.matches("\\d{8}[A-Z]")) {
            return false;
        }
        int numero = Integer.parseInt(valor.substring(0, 8));
        return valor.charAt(8) == LETRAS.charAt(numero % 23);
    }
}
