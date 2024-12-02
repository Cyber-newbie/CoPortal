package paybank.astro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import paybank.astro.entity.Question;
import paybank.astro.entity.Quiz;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {

    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.submissions")
    List<Quiz> findAllWithSubmissions();

    @Query("from Quiz q LEFT JOIN FETCH q.questions where q.id = :id")
    Quiz getQuizQuestions(@Param("id") int id);

}
