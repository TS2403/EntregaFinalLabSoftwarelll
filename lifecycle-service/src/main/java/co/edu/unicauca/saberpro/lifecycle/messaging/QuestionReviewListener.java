package co.edu.unicauca.saberpro.lifecycle.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Consumidor asincrono: escucha la cola pregunta.revisar.queue y registra
 * las preguntas que entran a revision.
 */
@Component
public class QuestionReviewListener {

    private static final Logger log = LoggerFactory.getLogger(QuestionReviewListener.class);

    private final List<PreguntaEnviadaRevisionEvent> recibidos = Collections.synchronizedList(new ArrayList<>());

    @RabbitListener(queues = RabbitMQConfig.QUEUE_REVISAR)
    public void recibir(PreguntaEnviadaRevisionEvent evento) {
        recibidos.add(evento);
        log.info("<<< [AMQP] Mensaje recibido de pregunta.revisar.queue -> pregunta id={} estado={} enunciado=\"{}\"",
                evento.preguntaId(), evento.estado(), evento.enunciado());
        log.info("<<< Pregunta {} asignada a revision por el comite academico", evento.preguntaId());
    }

    public List<PreguntaEnviadaRevisionEvent> getRecibidos() {
        synchronized (recibidos) {
            return List.copyOf(recibidos);
        }
    }
}
