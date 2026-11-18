package co.edu.unicauca.saberpro.question.infrastructure.jpa;

import co.edu.unicauca.saberpro.question.domain.model.Pregunta;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.PreguntaRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador secundario de persistencia basado en Spring Data JPA (H2 en el contenedor).
 * Implementa el mismo puerto que InMemoryQuestionAdapter.
 */
public class JpaQuestionAdapter implements PreguntaRepositoryPort {

    private final SpringDataQuestionRepository repository;

    public JpaQuestionAdapter(SpringDataQuestionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Pregunta guardar(Pregunta pregunta) {
        QuestionRecord guardado = repository.save(QuestionDomainMapper.toRecord(pregunta));
        if (pregunta.getId() == null && guardado.getId() != null) {
            pregunta.asignarId(guardado.getId());
        }
        return QuestionDomainMapper.toDomain(guardado);
    }

    @Override
    public Optional<Pregunta> buscarPorId(Long id) {
        return repository.findById(id).map(QuestionDomainMapper::toDomain);
    }

    @Override
    public List<Pregunta> listarTodas() {
        return repository.findAll().stream()
                .map(QuestionDomainMapper::toDomain)
                .toList();
    }
}
