package es.safareyes.cafeteria.validaciones;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación propia: el campo debe ser un DNI español válido (8 números y la letra que les corresponde).
 * La comprobación la hace DniValidator. Un valor null o vacío se da por válido: si el campo es
 * obligatorio, se añade además @NotBlank.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DniValidator.class)
public @interface DniValido {

    String message() default "El DNI no es válido: deben ser 8 números y la letra correcta (p. ej. 12345678Z)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
