package co.portal.submission_service.services.submission;

import co.portal.submission_service.aspect.annotation.GetUserObject;
import co.portal.submission_service.dto.Response;
import co.portal.submission_service.dto.SubmissionQuizDTO;
import co.portal.submission_service.dto.analysis.SubmissionAnalysis;
import co.portal.submission_service.dto.quiz.QuizDTO;
import co.portal.submission_service.dto.submission.SubmissionRequest;
import co.portal.submission_service.dto.user.UserDTO;
import co.portal.submission_service.entity.Analytics;
import co.portal.submission_service.entity.Submission;
import co.portal.submission_service.exception.TimeLimitExceedException;
import co.portal.submission_service.exception.TotalAttempExceedException;
import co.portal.submission_service.repository.SubmissionRepository;
import co.portal.submission_service.services.calculation.CalculationService;
import co.portal.submission_service.services.calculation.CalculationServiceImpl;
import co.portal.submission_service.utils.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.validation.constraints.Null;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SubmissionServiceImpl  implements SubmissionService {

    private SubmissionRepository submissionRepository;
    private CalculationServiceImpl calculationService;
    private  EntityManager entityManager;
    private ObjectMapper objectMapper;
    private Utils utils;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${quiz.service.url}")
    private String quizServiceUrl;

    @Value("${question.service.url}")
    private String questionServiceUrl;

    @Value("${points.quizMarks}")
    private Integer quizPoints;

    @Value("${points.attempts}")
    private Integer attemptPoints;

    @Value("${points.time}")
    private Integer timePoints;


    @Autowired
    public SubmissionServiceImpl(
                                 SubmissionRepository submissionRepository,
                                 CalculationServiceImpl cal,
                                 EntityManager entityManager,
                                 Utils utils,
                                 ObjectMapper om) {
        this.submissionRepository = submissionRepository;
        this.calculationService = cal;
        this.entityManager = entityManager;
        this.utils = utils;
        this.objectMapper = om;
    }

    public UserDTO getLoggedInUser(String username) {
        log.info("USER SERVICE URL: {}", userServiceUrl + "/" + username);
        try {
            HttpResponse<UserDTO> user = Unirest.get(userServiceUrl + "/" + username).asObject(UserDTO.class);
            log.info("LOGGED IN USER {}" ,  user.getBody());
            return user.getBody();
        } catch (RestClientException | UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public Submission createSubmission(SubmissionRequest request,
                                       QuizDTO quiz,
                                       int securedScore,
                                       String username) throws Exception {

        log.info("CREATING SUBMISSION ");

        UserDTO user = getLoggedInUser(username);
        Integer quizId = quiz.getId();
        Integer userTotalSubmissions = getUserSubmissionCount(user.getId(), quizId);

        if(userTotalSubmissions >= quiz.getTotalAttempts()){
            log.error("Quiz total attempts already utilized by user!");
            throw new TotalAttempExceedException("You have already utilized total attempts");
        }

        if (!utils.checkQuizDeadline(quiz.getDeadline(), request.getSubmissionTime())) {
            throw new TimeLimitExceedException("You have exceeded the time limit");
        }

        Submission submission = new Submission();
        submission.setUserId(user.getId());
        submission.setQuizId(quiz.getId());
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
    public Integer getUserSubmissionCount(long userId, int quizId) {
        return this.submissionRepository.countByUserId(userId, quizId);
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

        String token = utils.getAuthorizationHeader();
        TypeReference<Response<Integer>> intRef = new TypeReference<Response<Integer>>() {};
        TypeReference<Response<QuizDTO>> quizRef = new TypeReference<Response<QuizDTO>>() {};

        // Fetch the quiz using the quiz ID
        HttpResponse<String> quizResponse = Unirest.get(quizServiceUrl + "/user/" + quizId)
                .getHttpRequest()
                .header("Authorization", token)
                .asString();


        log.info("GET QUIZ REQUEST: status [{}] with body [{}]", quizResponse.getStatus(), quizResponse.getBody() );

        if (quizResponse.getBody() == null || quizResponse.getBody().trim().isEmpty()) {
            throw new Exception("Quiz service failed to provide data " + quizId);
        }

        //get total secured marks
        HttpResponse<String> response = Unirest.post(questionServiceUrl + "/check/" + quizId)
                .body(request.getUserAnswers())
                .getHttpRequest()
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .asString();



        log.info("POST ANSWER REQUEST: status [{}] with body [{}]", response.getStatus(), response.getBody() );

        // Check if response body is empty
        if (response.getBody() == null || response.getBody().trim().isEmpty()) {
            throw new Exception("Question service failed to provide data " + quizId);
        }

        ;
        Response<Integer> totalSecuredNumber = objectMapper.readValue(response.getBody(), intRef);
        Response<QuizDTO>  quiz = objectMapper.readValue(quizResponse.getBody(), quizRef);

        // Create or update the submission with the score
        return createSubmission(request, quiz.getData(), totalSecuredNumber.getData(), username);
    }

    public List<SubmissionQuizDTO> getSubmissionsWithQuiz(String username) throws Exception {
        try {
            UserDTO user = getLoggedInUser(username);
            return submissionRepository.getUserSubmissionWithQuiz(user.getId());
        } catch (Exception e) {
            log.error("error occurred ", e);
            throw new Exception("sdasd");
        }
    }

    @GetUserObject
    public List<SubmissionQuizDTO> submissionAnalysis(@Nullable UserDTO user) throws Exception {

        log.info("user object received  {}", user);

        if(user == null){
            throw new IllegalArgumentException("User object cannot be null");
        }

        long userId = user.getId();

        List<SubmissionQuizDTO> submissions = getSubmissionsWithQuiz(user.getUsername());

        submissions.forEach(submission -> {

            Analytics analytics = calculationService.calculate(submission, userId);
            submission.setAnalytics(analytics);
        });

        return submissions;
    }

    public int getTotalPointsScored(List<SubmissionQuizDTO> submissions){
       return submissions.stream().
               mapToInt(item -> Math.toIntExact(item.getAnalytics().getPointsScored()))
               .sum();
    }

}
