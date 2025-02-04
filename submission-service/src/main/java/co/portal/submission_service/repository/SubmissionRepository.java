package co.portal.submission_service.repository;

import co.portal.submission_service.dto.SubmissionQuizDTO;
import co.portal.submission_service.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query("SELECT s FROM Submission s WHERE s.quizId = :quizId AND s.id = :userId ")
    Submission findByQuizAndUserId(@Param("quizId") int quizId, @Param("userId") Long userId);

    @Query("SELECT s FROM Submission s where s.userId = :userId")
    List<Submission> findUserSubmissions(@Param("userId") Long userId);

    @Query("SELECT COUNT(s) FROM Submission s WHERE s.userId = :userId AND s.quizId = :quizId")
    Integer countByUserId(@Param("userId") Long userId, @Param("quizId") int quizId);

    @Query("SELECT new co.portal.submission_service.dto.SubmissionQuizDTO(s, q) " +
            "FROM Submission s JOIN Quiz q ON q.id = s.quizId " +
            "WHERE s.userId = :userId")
    List<SubmissionQuizDTO> getUserSubmissionWithQuiz(@Param("userId") long userId);

}