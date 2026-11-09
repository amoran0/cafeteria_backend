package exceptions;

/** 409 - conflicto (p. ej. duplicado) */
public class ConflictoException extends RuntimeException {
    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
