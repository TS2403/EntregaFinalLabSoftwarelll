package co.edu.unicauca.saberpro.question.application;

import co.edu.unicauca.saberpro.question.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.question.domain.model.Pregunta;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.CrearPreguntaUseCase.CrearPreguntaCommand;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.CrearPreguntaUseCase.OpcionCommand;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.NotificacionPort;
import co.edu.unicauca.saberpro.question.infrastructure.memory.InMemoryQuestionAdapter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Prueba de intercambiabilidad: el MISMO servicio de aplicacion funciona con el
 * adaptador en memoria y un notificador falso, sin Spring, sin RabbitMQ y sin BD.
 */
class IntercambiabilidadAdaptadoresTest {

    @Test
    void flujoCompletoConAdaptadorEnMemoria() {
        List<Long> notificadas = new ArrayList<>();
        NotificacionPort notificadorFalso = p -> notificadas.add(p.getId());

        QuestionApplicationService servicio = new QuestionApplicationService(
                new InMemoryQuestionAdapter(), notificadorFalso, p -> List.of());

        Pregunta creada = servicio.crear(new CrearPreguntaCommand(
                "Que patron separa el dominio de la infraestructura?",
                "La arquitectura hexagonal usa puertos y adaptadores",
                List.of(new OpcionCommand("Singleton", false),
                        new OpcionCommand("Puertos y Adaptadores", true),
                        new OpcionCommand("Observer", false),
                        new OpcionCommand("Facade", false))));

        assertNotNull(creada.getId());
        assertEquals(EstadoPregunta.BORRADOR, creada.getEstado());

        Pregunta enviada = servicio.enviarARevision(creada.getId());
        assertEquals(EstadoPregunta.PENDIENTE_REVISION, enviada.getEstado());
        assertEquals(List.of(creada.getId()), notificadas);
    }
}
