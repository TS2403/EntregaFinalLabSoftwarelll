package co.edu.unicauca.saberpro.validation.filtros;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Tuberia (pipe) que ejecuta en orden todos los filtros de validacion
 * y acumula los errores.
 */
@Component
public class PipelineValidacion {

    private static final Set<String> PALABRAS_PROHIBIDAS = Set.of("tonto", "estupido", "idiota");
    private static final int LONGITUD_MINIMA_ENUNCIADO = 10;

    private final List<FiltroValidacion> filtros = List.of(
            // Filtro 1: el enunciado debe tener una longitud minima razonable
            s -> s.enunciado() == null || s.enunciado().trim().length() < LONGITUD_MINIMA_ENUNCIADO
                    ? List.of("El enunciado debe tener minimo " + LONGITUD_MINIMA_ENUNCIADO + " caracteres")
                    : List.of(),

            // Filtro 2: no se permite lenguaje ofensivo en el enunciado
            s -> {
                String texto = s.enunciado() == null ? "" : s.enunciado().toLowerCase(Locale.ROOT);
                return PALABRAS_PROHIBIDAS.stream()
                        .filter(texto::contains)
                        .map(p -> "El enunciado contiene una palabra no permitida: " + p)
                        .toList();
            },

            // Filtro 3: no puede haber opciones con el mismo texto
            s -> {
                if (s.opciones() == null) return List.of();
                long distintas = s.opciones().stream()
                        .map(o -> o.texto() == null ? "" : o.texto().trim().toLowerCase(Locale.ROOT))
                        .distinct().count();
                return distintas < s.opciones().size()
                        ? List.of("Hay opciones de respuesta repetidas")
                        : List.of();
            }
    );

    public List<String> ejecutar(SolicitudValidacion solicitud) {
        List<String> errores = new ArrayList<>();
        for (FiltroValidacion filtro : filtros) {
            errores.addAll(filtro.aplicar(solicitud));
        }
        return errores;
    }
}
