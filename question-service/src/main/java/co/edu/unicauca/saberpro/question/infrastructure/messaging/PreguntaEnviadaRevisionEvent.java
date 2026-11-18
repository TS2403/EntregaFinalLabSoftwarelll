package co.edu.unicauca.saberpro.question.infrastructure.messaging;

/**
 * Evento de integracion que viaja por RabbitMQ hacia lifecycle-service.
 */
public record PreguntaEnviadaRevisionEvent(Long preguntaId, String enunciado, String estado, String fecha) {
}
