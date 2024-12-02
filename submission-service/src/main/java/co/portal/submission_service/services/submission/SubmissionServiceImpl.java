package co.portal.submission_service.services.submission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import paybank.astro.dto.submission.SubmissionRequest;
import paybank.astro.entity.Quiz;
import paybank.astro.entity.Submission;
import paybank.astro.entity.User;
import paybank.astro.repository.SubmissionRepository;
import paybank.astro.repository.UserRepository;
import paybank.astro.service.BaseService;
import paybank.astro.service.question.QuestionServiceImpl;
import paybank.astro.service.quiz.QuizServiceImpl;
import paybank.astro.util.quiz.QuizUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Service
public class SubmissionServiceImpl extends BaseService  implements SubmissionService {

    private UserRepository userRepository;
    private SubmissionRepository submissionRepository;

    private  EntityManager entityManager;
    private QuizServiceImpl quizService;
    private QuestionServiceImpl questionService;
    private QuizUtils quizUtils;

    @Autowired
    public SubmissionServiceImpl(UserRepository userRepository,
                                 SubmissionRepository submissionRepository,
                                 EntityManager entityManager,
                                 QuizServiceImpl quizService,
                                 QuizUtils quizUtils,
                                 QuestionServiceImpl questionService) {
        super(userRepository);
        this.submissionRepository = submissionRepository;
        this.entityManager = entityManager;
        this.quizService = quizService;
        this.quizUtils = quizUtils;
        this.questionService = questionService;
    }

    @Override
    @Transactional
    public Submission createSubmission(SubmissionRequest request, Quiz quiz, Integer securedScore) throws Exception {
        User user = getLoggedInUser();
        Integer quizId = quiz.getId();

        Optional<Submission> existingSubmission = checkUserSubmission(String.valueOf(quizId), user.getId());

        if (!quizUtils.checkQuizDeadline(quiz.getDeadline(), request.getSubmissionTime())) {
            throw new Exception("You have exceeded the time limit");
        }

        Submission submission = existingSubmission.orElseGet(() -> {
            Submission newSubmission = new Submission();
            newSubmission.setUser(user);
            newSubmission.setQuiz(quiz);
            return newSubmission;
        });

        // Update the submission details
        submission.setScore(securedScore);
        submission.setSubmissionTime(request.getSubmissionTime());

        // Save the submission
        submissionRepository.save(submission);
        return submission;
    }


    @Override
    public Optional<Submission> checkUserSubmission(String quizId, Long userId) {
        Submission submission = submissionRepository.findByQuizAndUserId(Integer.parseInt(quizId), userId);
        return Optional.ofNullable(submission);
    }



    @Override
    public List<Submission> getUserSubmissions() {
        User user = getLoggedInUser();
        return this.submissionRepository.findUserSubmissions(user.getId());
    }

    @Override
    @Transactional
    public Submission evaluateQuizSubmit(SubmissionRequest request, String quizId) throws Exception {
        // Fetch the quiz using the quiz ID
        Quiz quiz = quizService.getQuiz(Integer.parseInt(quizId));

        // Calculate the total score secured by the user
        Integer totalSecuredNumber = questionService.checkQuestionAgainstUserAnswers(quiz, request.getUserAnswers());

        // Create or update the submission with the score
        return createSubmission(request, quiz, totalSecuredNumber);
    }

}
