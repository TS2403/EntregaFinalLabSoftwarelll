package co.edu.unicauca.saberpro.question.domain.ports.inbound;

import co.edu.unicauca.saberpro.question.domain.model.Pregunta;

import java.util.List;

/**
 * Puerto de ENTRADA (caso de uso): crear una pregunta nueva en estado BORRADOR.
 * Lo implementa la capa de aplicacion y lo invoca el adaptador primario REST.
 */
public interface CrearPreguntaUseCase {

    Pregunta crear(CrearPreguntaCommand comando);

    /** Datos de entrada del caso de uso (independiente del JSON del controlador). */
    record CrearPreguntaCommand(String enunciado, String justificacion, List<OpcionCommand> opciones) {
    }

    record OpcionCommand(String texto, boolean correcta) {
    }
}
