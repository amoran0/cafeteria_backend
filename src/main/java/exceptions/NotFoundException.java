package exceptions;

/** 404 - recurso no encontrado */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String mensaje) {
        super(mensaje);
    }
}
