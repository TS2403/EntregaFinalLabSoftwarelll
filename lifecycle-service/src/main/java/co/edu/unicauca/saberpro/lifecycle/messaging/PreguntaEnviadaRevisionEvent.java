package co.edu.unicauca.saberpro.lifecycle.messaging;

/**
 * Copia local del evento. Cada bounded context tiene su propio modelo:
 * no se comparten clases entre microservicios, solo el formato JSON.
 */
public record PreguntaEnviadaRevisionEvent(Long preguntaId, String enunciado, String estado, String fecha) {
}
