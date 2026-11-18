package co.edu.unicauca.saberpro.question.domain.model;

/**
 * Estados del ciclo de vida de una pregunta.
 * (En el Corte II esto se manejaba con el patron State; aqui queda como
 * parte del lenguaje ubicuo del dominio).
 */
public enum EstadoPregunta {
    BORRADOR,
    PENDIENTE_REVISION,
    APROBADA,
    RECHAZADA
}
