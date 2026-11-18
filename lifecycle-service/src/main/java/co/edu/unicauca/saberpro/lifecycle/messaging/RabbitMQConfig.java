package co.edu.unicauca.saberpro.lifecycle.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Se declara la misma topologia que en question-service para que no importe
 * cual servicio arranca primero (la declaracion en RabbitMQ es idempotente).
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "pregunta.exchange";
    public static final String QUEUE_REVISAR = "pregunta.revisar.queue";
    public static final String ROUTING_KEY_REVISAR = "pregunta.revisar";

    @Bean
    public DirectExchange preguntaExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue revisarQueue() {
        return new Queue(QUEUE_REVISAR, true);
    }

    @Bean
    public Binding revisarBinding(Queue revisarQueue, DirectExchange preguntaExchange) {
        return BindingBuilder.bind(revisarQueue).to(preguntaExchange).with(ROUTING_KEY_REVISAR);
    }

    /**
     * Spring Boot usa este convertidor automaticamente en los @RabbitListener.
     * Se fuerza a convertir al tipo del parametro del listener, porque el
     * header __TypeId__ trae el nombre de la clase de question-service.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setAlwaysConvertToInferredType(true);
        return converter;
    }
}
