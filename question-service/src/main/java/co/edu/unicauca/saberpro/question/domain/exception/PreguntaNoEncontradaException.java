package co.edu.unicauca.saberpro.question.domain.exception;

/**
 * Se lanza cuando se busca una pregunta por id y no existe en el repositorio.
 */
public class PreguntaNoEncontradaException extends RuntimeException {

    public PreguntaNoEncontradaException(Long id) {
        super("No existe una pregunta con id " + id);
    }
}
