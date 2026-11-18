package co.edu.unicauca.saberpro.question.infrastructure.messaging;

import co.edu.unicauca.saberpro.question.domain.model.Pregunta;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.NotificacionPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Adaptador secundario de mensajeria: publica el evento de envio a revision
 * en el exchange de RabbitMQ (comunicacion ASINCRONA con lifecycle-service).
 */
@Component
public class RabbitMQPublisher implements NotificacionPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void notificarEnvioARevision(Pregunta pregunta) {
        PreguntaEnviadaRevisionEvent evento = new PreguntaEnviadaRevisionEvent(
                pregunta.getId(),
                pregunta.getEnunciado().getTexto(),
                pregunta.getEstado().name(),
                LocalDateTime.now().toString());

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY_REVISAR, evento);
        log.info(">>> [AMQP] Evento publicado en {} (routing key {}): pregunta id={}",
                RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY_REVISAR, pregunta.getId());
    }
}
