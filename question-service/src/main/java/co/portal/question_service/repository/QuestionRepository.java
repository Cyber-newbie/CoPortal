package co.portal.question_service.repository;

import co.portal.question_service.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

    List<Question> findByQuizId(int quizId);

}
