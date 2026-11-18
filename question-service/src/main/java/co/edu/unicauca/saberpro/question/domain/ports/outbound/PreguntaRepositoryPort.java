package co.edu.unicauca.saberpro.question.domain.ports.outbound;

import co.edu.unicauca.saberpro.question.domain.model.Pregunta;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de SALIDA de persistencia.
 * El dominio solo conoce este contrato; no sabe si detras hay JPA, un HashMap
 * o cualquier otra tecnologia. Implementaciones:
 *  - JpaQuestionAdapter      (infrastructure/jpa)
 *  - InMemoryQuestionAdapter (infrastructure/memory)
 */
public interface PreguntaRepositoryPort {

    Pregunta guardar(Pregunta pregunta);

    Optional<Pregunta> buscarPorId(Long id);

    List<Pregunta> listarTodas();
}
