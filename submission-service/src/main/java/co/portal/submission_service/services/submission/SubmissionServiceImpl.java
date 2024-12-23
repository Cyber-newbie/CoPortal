package co.portal.submission_service.services.submission;

import co.portal.submission_service.dto.quiz.QuizDTO;
import co.portal.submission_service.dto.submission.SubmissionRequest;
import co.portal.submission_service.dto.user.UserDTO;
import co.portal.submission_service.entity.Submission;
import co.portal.submission_service.repository.SubmissionRepository;
import co.portal.submission_service.utils.Utils;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SubmissionServiceImpl  implements SubmissionService {

    private SubmissionRepository submissionRepository;
    private  EntityManager entityManager;
    private Utils utils;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${quiz.service.url}")
    private String quizServiceUrl;

    @Autowired
    public SubmissionServiceImpl(
                                 SubmissionRepository submissionRepository,
                                 EntityManager entityManager,
                                 Utils utils) {
        this.submissionRepository = submissionRepository;
        this.entityManager = entityManager;
        this.utils = utils;
    }

    public UserDTO getLoggedInUser(String username) {
        log.info("USER SERVICE URL: {}", userServiceUrl + "/" + username);
        try {
            HttpResponse<UserDTO> user = Unirest.get(userServiceUrl + "/" + username).asObject(UserDTO.class);
            log.info("LOGGED IN USER {}" ,  user);
            return user.getBody();
        } catch (RestClientException e) {
            throw new RuntimeException(e);
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public Submission createSubmission(SubmissionRequest request,
                                       QuizDTO quiz,
                                       Integer securedScore,
                                       String username) throws Exception {
        UserDTO user = getLoggedInUser(username);
        Integer quizId = quiz.getId();

        Optional<Submission> existingSubmission = checkUserSubmission(String.valueOf(quizId), user.getId());

        if (!utils.checkQuizDeadline(quiz.getDeadline(), request.getSubmissionTime())) {
            throw new Exception("You have exceeded the time limit");
        }

        Submission submission = existingSubmission.orElseGet(() -> {
            Submission newSubmission = new Submission();
            newSubmission.setUserId(user.getId());
            newSubmission.setQuizId(quiz.getId());

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
    public List<Submission> getUserSubmissions(String username) {
        UserDTO user = getLoggedInUser(username);
        return this.submissionRepository.findUserSubmissions(user.getId());
    }
//
    @Override
    @Transactional
    public Submission evaluateQuizSubmit(SubmissionRequest request, String quizId, String username) throws Exception {


        // Fetch the quiz using the quiz ID
        HttpResponse<QuizDTO> quiz = Unirest.get(quizServiceUrl + "/user/" + quizId).asObject(QuizDTO.class);

        // Calculate the total score secured by the user
        Integer totalSecuredNumber = questionService.checkQuestionAgainstUserAnswers(quiz, request.getUserAnswers());

        // Create or update the submission with the score
        return createSubmission(request, quiz, totalSecuredNumber, username);
    }

}
