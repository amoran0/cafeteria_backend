package es.safareyes.cafeteria.controladores;

import es.safareyes.cafeteria.excepciones.ElementoDuplicado;
import es.safareyes.cafeteria.excepciones.ElementoNoEncontrado;
import es.safareyes.cafeteria.excepciones.ReglaNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorGlobalController {

    @ExceptionHandler(ReglaNegocioException.class)
    public ProblemDetail reglaNegocio(ReglaNegocioException e) {
        return construir(HttpStatusCode.valueOf(422), "Regla de negocio no cumplida", e.getMessage());
    }

    @ExceptionHandler(ElementoNoEncontrado.class)
    public ProblemDetail elementoNoEncontrado(ElementoNoEncontrado e) {
        return construir(HttpStatus.NOT_FOUND, "Elemento no encontrado", e.getMessage());
    }

    @ExceptionHandler(ElementoDuplicado.class)
    public ProblemDetail elementoDuplicado(ElementoDuplicado e) {
        return construir(HttpStatus.CONFLICT, "Elemento duplicado", e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail parametroIncorrecto(IllegalArgumentException e) {
        return construir(HttpStatus.BAD_REQUEST, "Parámetro no válido", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail erroresDTO(MethodArgumentNotValidException e) {
        Map<String, String> errores = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));

        ProblemDetail problema = construir(HttpStatus.BAD_REQUEST, "Datos no válidos", "Revisa los campos indicados");
        problema.setProperty("errores", errores);
        return problema;
    }

    private ProblemDetail construir(HttpStatusCode estado, String titulo, String detalle) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(estado, detalle);
        problema.setTitle(titulo);
        problema.setProperty("timestamp", LocalDateTime.now());
        return problema;
    }
}
