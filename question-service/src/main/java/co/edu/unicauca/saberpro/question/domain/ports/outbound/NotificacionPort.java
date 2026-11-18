package co.edu.unicauca.saberpro.question.domain.ports.outbound;

import co.edu.unicauca.saberpro.question.domain.model.Pregunta;

/**
 * Puerto de SALIDA de notificaciones / eventos.
 * Implementacion: RabbitMQPublisher (infrastructure/messaging).
 */
public interface NotificacionPort {

    void notificarEnvioARevision(Pregunta pregunta);
}
