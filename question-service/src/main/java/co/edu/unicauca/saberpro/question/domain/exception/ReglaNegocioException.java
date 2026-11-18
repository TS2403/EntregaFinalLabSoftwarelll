package co.edu.unicauca.saberpro.question.domain.exception;

/**
 * Excepcion de dominio que se lanza cuando se viola una regla de negocio
 * (invariante) del agregado Pregunta o de sus objetos de valor.
 * Es Java puro: no depende de Spring ni de ningun framework.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
