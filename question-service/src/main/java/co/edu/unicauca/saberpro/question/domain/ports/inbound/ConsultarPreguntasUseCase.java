package co.edu.unicauca.saberpro.question.domain.ports.inbound;

import co.edu.unicauca.saberpro.question.domain.model.Pregunta;

import java.util.List;

/**
 * Puerto de ENTRADA: consultas de solo lectura sobre las preguntas.
 */
public interface ConsultarPreguntasUseCase {

    List<Pregunta> listar();

    Pregunta buscarPorId(Long id);
}
