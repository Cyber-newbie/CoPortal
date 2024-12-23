package co.portal.question_service.repository;

import co.portal.question_service.entity.Question;
import co.portal.question_service.entity.QuizQuestion;
import co.portal.question_service.entity.QuizQuestionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, QuizQuestionId> {

    @Query("SELECT q FROM QuizQuestion qq JOIN Question q on qq.questions_id = q.id where qq.quiz_id = :quizId")
    List<Question> getQuizQuestions(@Param("quizId") Integer quizId);

}
