package es.safareyes.cafeteria.excepciones;

/** Login fallido: el gestor de errores la convierte en un 401 */
public class CredencialesIncorrectas extends RuntimeException {
    public CredencialesIncorrectas(String message) {
        super(message);
    }
}
