package co.portal.quiz_service.service;

import co.portal.quiz_service.dto.Question;
import co.portal.quiz_service.dto.QuestionResponse;
import co.portal.quiz_service.dto.QuizRequest;
import co.portal.quiz_service.dto.user.UserDTO;
import co.portal.quiz_service.entity.Category;
import co.portal.quiz_service.entity.Quiz;
import co.portal.quiz_service.exception.NotFoundException;
import co.portal.quiz_service.exception.QuizNotFoundException;
import co.portal.quiz_service.repository.CategoryRepository;
import co.portal.quiz_service.repository.QuizRepository;
import co.portal.quiz_service.service.QuizService;
import co.portal.quiz_service.utils.QuizUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class QuizServiceImpl implements QuizService {

    private RestTemplate restTemplate;
    private QuizRepository quizRepository;
    private CategoryRepository categoryRepository;
    private QuizUtils quizUtils;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${question.service.url}")
    private String questionServiceUrl;

    @Autowired
    public QuizServiceImpl(QuizRepository quizRepository,
                           CategoryRepository categoryRepository,
                           QuizUtils quizUtils,
                            RestTemplate restTemplate) {

        this.quizRepository = quizRepository;
        this.categoryRepository = categoryRepository;
        this.quizUtils = quizUtils;
        this.restTemplate = restTemplate;

    }

    @Override
    public Quiz createQuiz(QuizRequest quiz, String username) throws Exception {

        UserDTO user = this.getLoggedInUser(username);

        //get and set category
        Category category = this.categoryRepository.findById(quiz.getCategoryId())
                .orElseThrow(() ->
                        new NotFoundException("Category not found"));

        Quiz newQuiz = Quiz.builder()
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .maxMarks(quiz.getMaxMarks())
                .timeLimit(quiz.getTimeLimit())
                .category(category)
                .deadline(quiz.getDeadline())
                .userId(user.getId())
                .active(quiz.getActive())
                .build();

        try {
            //create quiz
            Quiz createdQuiz = quizRepository.save(newQuiz);

            //create questions
            Optional<QuestionResponse> response = Optional.ofNullable(restTemplate.postForObject(questionServiceUrl + "/save/" + createdQuiz.getId(),
                    quiz.getQuestions(), QuestionResponse.class));

            response.ifPresent(questionResponse -> newQuiz.setQuestions(questionResponse.getQuestions()));

            return newQuiz;
        } catch (RestClientException e) {
            throw new RuntimeException("failed to create quiz with questions: " + e.getMessage());
        }
    }

    @Override
    public Quiz getQuiz(int id) throws Exception {
        return this.quizRepository.findById(id).orElseThrow(() -> new Exception("Quiz not found"));
    }

//    @Override
//    public List<Question> getQuizQuestions(int quizId) throws Exception {
//
//        Quiz quiz = this.quizRepository.findById(quizId).orElseThrow(() ->
//                new QuizNotFoundException("Quiz not found"));
//
//        return this.quizRepository.getQuizQuestions(quizId);

//    }
    @Override
    public List<Quiz> getAllQuizes() throws Exception {
        return this.quizRepository.findAll();
    }


    @Override
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deactivateOutdatedQuizes() {

        log.info("SCHEDULER RUNNING TO DEACTIVATE OUTDATED QUIZ");

        LocalDateTime currentDate = LocalDateTime.now();
        int quizes = quizRepository.findOutdatedQuizes(currentDate);
        log.info("Quizes: {}", quizes);
    }

    @Override
    public Quiz updateQuiz(int quizId, QuizRequest request) throws Exception {

        Quiz quiz = this.getQuiz(quizId);
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setMaxMarks(request.getMaxMarks());
        quiz.setNumberOfQuestions(request.getNumberOfQuestions());
        quiz.setActive(request.getActive());

        //create questions


        return null;
    }

    @Override
    public void deleteQuiz(int id) throws Exception {

    }

    public UserDTO getLoggedInUser(String username) {
        log.info("USER SERVICE URL: {}", userServiceUrl + "/" + username);
        try {
            UserDTO user = restTemplate.getForObject(userServiceUrl + "/" + username , UserDTO.class);
            log.info("LOGGED IN USER {}" ,  user);
            return user;
        } catch (RestClientException e) {
            throw new RuntimeException(e);
        }
    }



}
