package co.portal.submission_service.services.calculation;

import co.portal.submission_service.dto.SubmissionQuizDTO;
import co.portal.submission_service.dto.user.UserDTO;
import co.portal.submission_service.entity.Analytics;
import co.portal.submission_service.repository.SubmissionRepository;
import co.portal.submission_service.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CalculationServiceImpl implements CalculationService{

    @Value("${points.quizMarks}")
    private int quizPoints;

    @Value("${points.attempts}")
    private int attemptPoints;

    @Value("${points.time}")
    private int timePoints;

    private final Utils utils;

    private final SubmissionRepository submissionRepository;

    @Autowired
    CalculationServiceImpl(Utils ut, SubmissionRepository repo){
        utils = ut;
        submissionRepository = repo;
    }



    @Override
    public Analytics calculate(SubmissionQuizDTO submission ,long userId) {

        int quizId = submission.getQuiz().getId();

        int obtainedMarks = submission.getSubmission().getScore();
        int totalMarks = Integer.parseInt(submission.getQuiz().getMaxMarks());
        int userSeconds = utils.extractTimeInSeconds(submission.getSubmission().getTimeTaken());
        int quizSeconds = utils.extractTimeInSeconds(submission.getQuiz().getTimeLimit());
        int userAttempts = submissionRepository.countByUserId(userId, quizId);
        int quizAttempts = submission.getQuiz().getTotalAttempts();

        log.info("time factor {} {} {} ", quizSeconds, userSeconds, timePoints);
        log.info("attempts factor {} {} {} ", quizAttempts, userAttempts, attemptPoints);

        int marksFactor = (int) (((double) obtainedMarks/totalMarks) * quizPoints);
        int timeFactor = (int) (timePoints * (((double) quizSeconds - userSeconds + 1.00) / quizSeconds));
        int attemptsFactor = (int) (attemptPoints * (((double) quizAttempts - userAttempts + 1.00) / quizAttempts));

        int pointsScored = marksFactor + timeFactor + attemptsFactor;

        return Analytics.builder()
                .attemptFactor(attemptsFactor)
                .marksFactor(marksFactor)
                .timeFactor(timeFactor)
                .pointsScored(pointsScored)
                .build();

    }
}
