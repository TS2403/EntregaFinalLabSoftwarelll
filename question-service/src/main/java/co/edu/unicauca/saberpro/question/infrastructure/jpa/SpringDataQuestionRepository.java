package co.edu.unicauca.saberpro.question.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de Spring Data. Solo lo conoce JpaQuestionAdapter.
 */
public interface SpringDataQuestionRepository extends JpaRepository<QuestionRecord, Long> {
}
