package co.edu.unicauca.saberpro.question.api;

import co.edu.unicauca.saberpro.question.domain.model.Pregunta;

import java.util.List;

/**
 * DTO de salida del API REST. Evita exponer el agregado de dominio directamente.
 */
public record PreguntaResponse(Long id, String enunciado, String justificacion,
                               List<OpcionResponse> opciones, String estado) {

    public record OpcionResponse(String texto, boolean correcta) {
    }

    public static PreguntaResponse desde(Pregunta p) {
        return new PreguntaResponse(
                p.getId(),
                p.getEnunciado().getTexto(),
                p.getJustificacion().getTexto(),
                p.getOpciones().stream().map(o -> new OpcionResponse(o.getTexto(), o.isCorrecta())).toList(),
                p.getEstado().name());
    }
}
