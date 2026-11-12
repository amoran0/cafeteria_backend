package es.safareyes.cafeteria.excepciones;

public class ElementoDuplicado extends RuntimeException {
    public ElementoDuplicado(String message) {
        super(message);
    }
}
