package co.edu.unicauca.saberpro.question.domain;

import co.edu.unicauca.saberpro.question.domain.exception.ReglaNegocioException;
import co.edu.unicauca.saberpro.question.domain.model.Enunciado;
import co.edu.unicauca.saberpro.question.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.question.domain.model.Justificacion;
import co.edu.unicauca.saberpro.question.domain.model.OpcionRespuesta;
import co.edu.unicauca.saberpro.question.domain.model.Pregunta;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas del dominio puro: no levantan Spring, no usan base de datos.
 */
class PreguntaTest {

    private static final Enunciado ENUNCIADO = new Enunciado("Cual es la complejidad de la busqueda binaria?");
    private static final Justificacion JUSTIFICACION =
            new Justificacion("Divide el espacio de busqueda a la mitad en cada paso");

    private List<OpcionRespuesta> cuatroOpcionesUnaCorrecta() {
        return List.of(
                new OpcionRespuesta("O(n)", false),
                new OpcionRespuesta("O(log n)", true),
                new OpcionRespuesta("O(n log n)", false),
                new OpcionRespuesta("O(1)", false));
    }

    @Test
    void enunciadoVacioLanzaExcepcion() {
        assertThrows(ReglaNegocioException.class, () -> new Enunciado("   "));
        assertThrows(ReglaNegocioException.class, () -> new Enunciado(null));
    }

    @Test
    void justificacionCortaLanzaExcepcion() {
        assertThrows(ReglaNegocioException.class, () -> new Justificacion("muy corta"));
    }

    @Test
    void preguntaNuevaIniciaEnBorrador() {
        Pregunta p = Pregunta.crear(ENUNCIADO, JUSTIFICACION, cuatroOpcionesUnaCorrecta());
        assertEquals(EstadoPregunta.BORRADOR, p.getEstado());
    }

    @Test
    void enviarARevisionConDatosValidosCambiaEstado() {
        Pregunta p = Pregunta.crear(ENUNCIADO, JUSTIFICACION, cuatroOpcionesUnaCorrecta());
        p.enviarARevision();
        assertEquals(EstadoPregunta.PENDIENTE_REVISION, p.getEstado());
    }

    @Test
    void enviarARevisionConTresOpcionesFalla() {
        Pregunta p = Pregunta.crear(ENUNCIADO, JUSTIFICACION, cuatroOpcionesUnaCorrecta().subList(0, 3));
        assertThrows(ReglaNegocioException.class, p::enviarARevision);
        assertEquals(EstadoPregunta.BORRADOR, p.getEstado());
    }

    @Test
    void enviarARevisionConDosClavesFalla() {
        Pregunta p = Pregunta.crear(ENUNCIADO, JUSTIFICACION, List.of(
                new OpcionRespuesta("A", true),
                new OpcionRespuesta("B", true),
                new OpcionRespuesta("C", false),
                new OpcionRespuesta("D", false)));
        assertThrows(ReglaNegocioException.class, p::enviarARevision);
    }

    @Test
    void noSePuedeAprobarSiNoEstaPendiente() {
        Pregunta p = Pregunta.crear(ENUNCIADO, JUSTIFICACION, cuatroOpcionesUnaCorrecta());
        assertThrows(ReglaNegocioException.class, p::aprobar);
    }

    @Test
    void lasOpcionesNoSePuedenModificarDesdeAfuera() {
        Pregunta p = Pregunta.crear(ENUNCIADO, JUSTIFICACION, cuatroOpcionesUnaCorrecta());
        assertThrows(UnsupportedOperationException.class,
                () -> p.getOpciones().add(new OpcionRespuesta("E", false)));
    }
}
