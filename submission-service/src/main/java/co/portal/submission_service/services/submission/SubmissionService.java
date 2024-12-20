package co.portal.submission_service.services.submission;

import co.portal.submission_service.dto.submission.SubmissionRequest;
import co.portal.submission_service.dto.user.UserDTO;
import co.portal.submission_service.entity.Submission;

import java.util.List;
import java.util.Optional;

public interface SubmissionService {
    public UserDTO getLoggedInUser(String username);

    Submission createSubmission(SubmissionRequest request, int quiz, Integer securedScore) throws Exception;

    Optional<Submission> checkUserSubmission(String quizId, Long userId);

    List<Submission> getUserSubmissions(String username);

    Submission evaluateQuizSubmit(SubmissionRequest request, String quizId, String username) throws Exception;

}
