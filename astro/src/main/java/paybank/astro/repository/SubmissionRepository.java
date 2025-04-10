package paybank.astro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import paybank.astro.entity.Submission;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Integer> {

    @Query("SELECT s FROM Submission s JOIN s.quiz q JOIN s.user u WHERE q.id = :quizId AND u.id = :userId ")
    Submission findByQuizAndUserId(@Param("quizId") int quizId, @Param("userId") Long userId);

    @Query("SELECT s FROM Submission s JOIN s.user u where u.id = :userId")
    List<Submission> findUserSubmissions(@Param("userId") Long userId);
//    void findAndUpdate(Submission submission);

}
