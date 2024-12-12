package co.portal.quiz_service.repository;

import co.portal.quiz_service.dto.Question;
import co.portal.quiz_service.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {


//    @Query("SELECT q FROM Question q JOIN QuizQuestion qq ON q.id = qq.question.id WHERE qq.quiz.id = :quizId")
//    public List<Question> getQuizQuestions(@Param("quizId") int quizId);

}
