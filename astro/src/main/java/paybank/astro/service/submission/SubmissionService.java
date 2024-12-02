package paybank.astro.service.submission;

import paybank.astro.dto.submission.SubmissionRequest;
import paybank.astro.dto.submission.SubmissionResponse;
import paybank.astro.entity.Quiz;
import paybank.astro.entity.Submission;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface SubmissionService {
    Submission createSubmission(SubmissionRequest request, Quiz quiz, Integer securedScore) throws Exception;

    Optional<Submission> checkUserSubmission(String quizId, Long userId);

    List<Submission> getUserSubmissions();

    Submission evaluateQuizSubmit(SubmissionRequest request, String quizId) throws Exception;

}
