package co.edu.unicauca.saberpro.question.infrastructure.jpa;

import co.edu.unicauca.saberpro.question.domain.model.Enunciado;
import co.edu.unicauca.saberpro.question.domain.model.Justificacion;
import co.edu.unicauca.saberpro.question.domain.model.OpcionRespuesta;
import co.edu.unicauca.saberpro.question.domain.model.Pregunta;

import java.util.List;

/**
 * Traduce entre el agregado de dominio (Pregunta) y la entidad de persistencia (QuestionRecord).
 * Asi el dominio nunca ve JPA y JPA nunca ve las reglas de negocio.
 */
public final class QuestionDomainMapper {

    private QuestionDomainMapper() {
    }

    public static QuestionRecord toRecord(Pregunta pregunta) {
        List<AnswerOptionRecord> opciones = pregunta.getOpciones().stream()
                .map(o -> new AnswerOptionRecord(o.getTexto(), o.isCorrecta()))
                .toList();
        return new QuestionRecord(
                pregunta.getId(),
                pregunta.getEnunciado().getTexto(),
                pregunta.getJustificacion().getTexto(),
                pregunta.getEstado(),
                opciones);
    }

    public static Pregunta toDomain(QuestionRecord record) {
        List<OpcionRespuesta> opciones = record.getOpciones().stream()
                .map(o -> new OpcionRespuesta(o.getTexto(), o.isCorrecta()))
                .toList();
        return Pregunta.reconstruir(
                record.getId(),
                new Enunciado(record.getEnunciado()),
                new Justificacion(record.getJustificacion()),
                opciones,
                record.getEstado());
    }
}
