package co.edu.unicauca.saberpro.question.domain.ports.inbound;

import co.edu.unicauca.saberpro.question.domain.model.Pregunta;

/**
 * Puerto de ENTRADA: casos de uso del flujo de revision de una pregunta.
 */
public interface EvaluarPreguntaUseCase {

    /** Valida las invariantes, cambia a PENDIENTE_REVISION y publica el evento asincrono. */
    Pregunta enviarARevision(Long id);

    Pregunta aprobar(Long id);

    Pregunta rechazar(Long id);
}
