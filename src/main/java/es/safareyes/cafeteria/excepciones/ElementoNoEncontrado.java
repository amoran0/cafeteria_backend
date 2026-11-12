package es.safareyes.cafeteria.excepciones;

public class ElementoNoEncontrado extends RuntimeException {
    public ElementoNoEncontrado(String message) {
        super(message);
    }
}
