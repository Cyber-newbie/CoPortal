package co.portal.quiz_service.repository;

import co.portal.quiz_service.dto.Question;
import co.portal.quiz_service.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {


//    @Query("SELECT q FROM Question q JOIN QuizQuestion qq ON q.id = qq.question.id WHERE qq.quiz.id = :quizId")
//    public List<Question> getQuizQuestions(@Param("quizId") int quizId);

    @Modifying
    @Query("UPDATE Quiz q SET q.active = FALSE WHERE q.deadline < :currentDate")
    public int findOutdatedQuizes(@Param("currentDate") LocalDateTime currentDate);

}
