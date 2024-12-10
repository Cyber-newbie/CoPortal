package co.portal.submission_service.repository;

import co.portal.submission_service.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

//    @Query("SELECT s FROM Submission s JOIN s.quiz q JOIN s.user u WHERE q.id = :quizId AND u.id = :userId ")
//    Submission findByQuizAndUserId(@Param("quizId") int quizId, @Param("userId") Long userId);

    @Query("SELECT s FROM Submission s where s.userId = :userId")
    List<Submission> findUserSubmissions(@Param("userId") Long userId);
//    void findAndUpdate(Submission submission);

}
