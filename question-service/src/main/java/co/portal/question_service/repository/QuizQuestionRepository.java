package co.portal.question_service.repository;

import co.portal.question_service.entity.QuizQuestion;
import co.portal.question_service.entity.QuizQuestionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, QuizQuestionId> {
}
