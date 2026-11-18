package co.edu.unicauca.saberpro.question.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declaracion de la topologia de RabbitMQ (exchange, cola y binding)
 * y del convertidor JSON para los mensajes.
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

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
