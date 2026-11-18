package co.edu.unicauca.saberpro.question.infrastructure.config;

import co.edu.unicauca.saberpro.question.application.QuestionApplicationService;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.NotificacionPort;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.PreguntaRepositoryPort;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.ValidacionContenidoPort;
import co.edu.unicauca.saberpro.question.infrastructure.jpa.JpaQuestionAdapter;
import co.edu.unicauca.saberpro.question.infrastructure.jpa.SpringDataQuestionRepository;
import co.edu.unicauca.saberpro.question.infrastructure.memory.InMemoryQuestionAdapter;
import co.edu.unicauca.saberpro.question.infrastructure.rest.RestValidacionAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Clase de configuracion donde se "conectan" los puertos con los adaptadores.
 *
 * PRUEBA DE INTERCAMBIABILIDAD:
 *   La propiedad saberpro.persistencia decide que adaptador se inyecta en el
 *   puerto PreguntaRepositoryPort:
 *     - jpa      -> JpaQuestionAdapter (H2 / Spring Data JPA)  [por defecto]
 *     - memoria  -> InMemoryQuestionAdapter (HashMap)
 *   En docker-compose se cambia con la variable SABERPRO_PERSISTENCIA.
 *   El dominio y el servicio de aplicacion NO cambian.
 */
@Configuration
public class BeanConfiguration {

    private static final Logger log = LoggerFactory.getLogger(BeanConfiguration.class);

    @Bean
    @ConditionalOnProperty(name = "saberpro.persistencia", havingValue = "jpa", matchIfMissing = true)
    public PreguntaRepositoryPort jpaQuestionAdapter(SpringDataQuestionRepository repository) {
        log.info("=== Adaptador de persistencia ACTIVO: JpaQuestionAdapter (JPA + H2) ===");
        return new JpaQuestionAdapter(repository);
    }

    @Bean
    @ConditionalOnProperty(name = "saberpro.persistencia", havingValue = "memoria")
    public PreguntaRepositoryPort inMemoryQuestionAdapter() {
        log.info("=== Adaptador de persistencia ACTIVO: InMemoryQuestionAdapter (HashMap) ===");
        return new InMemoryQuestionAdapter();
    }

    @Bean
    public ValidacionContenidoPort validacionContenidoPort(
            @Value("${saberpro.validacion.habilitada:true}") boolean habilitada,
            @Value("${saberpro.validacion.url:http://localhost:8082}") String url) {
        if (!habilitada) {
            log.warn("=== Validacion externa DESHABILITADA: se acepta todo el contenido ===");
            return pregunta -> List.of();
        }
        log.info("=== Validacion externa via REST en {} ===", url);
        return new RestValidacionAdapter(url);
    }

    @Bean
    public QuestionApplicationService questionApplicationService(PreguntaRepositoryPort repositorio,
                                                                 NotificacionPort notificador,
                                                                 ValidacionContenidoPort validador) {
        return new QuestionApplicationService(repositorio, notificador, validador);
    }
}
