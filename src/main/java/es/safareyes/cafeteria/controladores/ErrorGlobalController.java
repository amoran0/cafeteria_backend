package es.safareyes.cafeteria.controladores;

import es.safareyes.cafeteria.excepciones.ElementoDuplicado;
import es.safareyes.cafeteria.excepciones.ElementoNoEncontrado;
import es.safareyes.cafeteria.excepciones.ReglaNegocioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Todas las respuestas de error salen como ProblemDetail y sin detalles internos
 * (trazas, nombres de clases Java, consultas JPQL...).
 */
@RestControllerAdvice
public class ErrorGlobalController {

    private static final Logger log = LoggerFactory.getLogger(ErrorGlobalController.class);

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

    /** Lo lanzan los servicios con mensajes propios (p. ej. límites de los informes) */
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

    /** Parámetro de la URL con un tipo incorrecto: UUID, número, fecha o enum mal escrito */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail tipoParametro(MethodArgumentTypeMismatchException e) {
        return construir(HttpStatus.BAD_REQUEST, "Parámetro no válido",
                "El parámetro '" + e.getName() + "' tiene un valor no válido ('" + e.getValue() + "')"
                        + formatoEsperado(e.getRequiredType()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail parametroObligatorio(MissingServletRequestParameterException e) {
        return construir(HttpStatus.BAD_REQUEST, "Parámetro obligatorio",
                "Falta el parámetro obligatorio '" + e.getParameterName() + "'");
    }

    /** Cuerpo ausente, JSON mal formado o un campo con un tipo/valor imposible de convertir */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail cuerpoIlegible(HttpMessageNotReadableException e) {
        String detalle;
        if (e.getCause() instanceof InvalidFormatException formato) {
            detalle = "El campo '" + campo(formato) + "' tiene un valor no válido ('" + formato.getValue() + "')"
                    + formatoEsperado(formato.getTargetType());
        } else if (e.getCause() instanceof JacksonException jackson && !jackson.getPath().isEmpty()) {
            detalle = "El campo '" + campo(jackson) + "' tiene un tipo de dato no válido";
        } else if (e.getCause() == null) {
            detalle = "Falta el cuerpo de la petición";
        } else {
            detalle = "El cuerpo de la petición no es un JSON válido";
        }
        return construir(HttpStatus.BAD_REQUEST, "Cuerpo de la petición no válido", detalle);
    }

    /** Ordenación (?sort=) por un campo que no existe */
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ProblemDetail ordenacionIncorrecta(InvalidDataAccessApiUsageException e) {
        return construir(HttpStatus.BAD_REQUEST, "Parámetro no válido",
                "El parámetro 'sort' hace referencia a un campo que no existe");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail metodoNoPermitido(HttpRequestMethodNotSupportedException e) {
        return construir(HttpStatus.METHOD_NOT_ALLOWED, "Método no permitido",
                "El método " + e.getMethod() + " no está permitido en esta ruta. Métodos admitidos: "
                        + e.getSupportedHttpMethods());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ProblemDetail tipoContenido(HttpMediaTypeNotSupportedException e) {
        return construir(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Tipo de contenido no soportado",
                "El cuerpo debe enviarse como application/json");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail rutaNoEncontrada(NoResourceFoundException e) {
        return construir(HttpStatus.NOT_FOUND, "Ruta no encontrada",
                "No existe la ruta /" + e.getResourcePath());
    }

    /** Cualquier otro error: se registra en el log y al cliente no se le muestran detalles internos */
    @ExceptionHandler(Exception.class)
    public ProblemDetail errorInterno(Exception e) {
        log.error("Error no controlado", e);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno",
                "Se ha producido un error inesperado en el servidor");
    }

    private static String campo(JacksonException e) {
        return e.getPath().stream()
                .map(ref -> ref.getPropertyName() != null ? ref.getPropertyName() : "[" + ref.getIndex() + "]")
                .collect(Collectors.joining("."))
                .replace(".[", "[");
    }

    private static String formatoEsperado(Class<?> tipo) {
        if (tipo == null) {
            return "";
        }
        if (tipo.isEnum()) {
            return ". Valores admitidos: " + Arrays.toString(tipo.getEnumConstants());
        }
        return switch (tipo.getSimpleName()) {
            case "UUID" -> ". Se espera un UUID";
            case "LocalDate" -> ". Se espera una fecha con formato yyyy-MM-dd";
            case "Boolean", "boolean" -> ". Se espera true o false";
            case "Integer", "int", "Long", "long" -> ". Se espera un número entero";
            case "BigDecimal", "Double", "double" -> ". Se espera un número";
            default -> "";
        };
    }

    private ProblemDetail construir(HttpStatusCode estado, String titulo, String detalle) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(estado, detalle);
        problema.setTitle(titulo);
        problema.setProperty("timestamp", LocalDateTime.now());
        return problema;
    }
}
