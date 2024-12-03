//package co.portal.submission_service.services.submission;
//
//import co.portal.submission_service.dto.submission.SubmissionRequest;
//import co.portal.submission_service.entity.Submission;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface SubmissionService {
//    Submission createSubmission(SubmissionRequest request, Quiz quiz, Integer securedScore) throws Exception;
//
//    Optional<Submission> checkUserSubmission(String quizId, Long userId);
//
//    List<Submission> getUserSubmissions();
//
//    Submission evaluateQuizSubmit(SubmissionRequest request, String quizId) throws Exception;
//
//}
