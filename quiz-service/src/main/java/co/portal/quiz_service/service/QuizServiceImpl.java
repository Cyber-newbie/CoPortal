package co.portal.quiz_service.service;

import co.portal.quiz_service.dto.QuizRequest;
import co.portal.quiz_service.dto.user.UserDTO;
import co.portal.quiz_service.entity.Category;
import co.portal.quiz_service.entity.Quiz;
import co.portal.quiz_service.repository.CategoryRepository;
import co.portal.quiz_service.repository.QuizRepository;
import co.portal.quiz_service.service.QuizService;
import co.portal.quiz_service.utils.QuizUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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

    @Value("${user.service.url")
    private String userServiceUrl;


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
    @Transactional
    public Quiz createQuiz(QuizRequest quiz, String username) throws Exception {

        Quiz newQuiz = new Quiz();
        UserDTO user = this.getLoggedInUser(username);

        newQuiz.setTitle(quiz.getTitle());
        newQuiz.setDescription(quiz.getDescription());
        newQuiz.setMaxMarks(quiz.getMaxMarks());
        newQuiz.setNumberOfQuestions(quiz.getNumberOfQuestions());
        newQuiz.setActive(quiz.getActive());
        newQuiz.setUserId(user.getId());

        //get and set category
        Optional<Category> category = this.categoryRepository.findById(quiz.getCategoryId());
        category.ifPresent(newQuiz::setCategory);
        //create questions
        for(QuestionRequest ques : quiz.getQuestions()){
            Question newQuestion = new Question();

            newQuestion.setQuiz(newQuiz);
            newQuestion.setQuestion(ques.getQuestion());
            newQuestion.setAnswer(ques.getAnswer());

            newQuestion.setOption1(ques.getOption1());
            newQuestion.setOption2(ques.getOption2());
            newQuestion.setOption3(ques.getOption3());
            newQuestion.setOption4(ques.getOption4());

            questionsList.add(newQuestion);
        }

        //add questionList to quiz
        newQuiz.setQuestions(questionsList);

        quizRepository.save(newQuiz);

        return newQuiz;
    }

    @Override
    public Quiz getQuiz(int id) throws Exception {
        return this.quizRepository.findById(id).orElseThrow(() -> new Exception("Quiz not found"));
    }

    @Override
    public List<Quiz> getAllQuizes() throws Exception {
        return this.quizRepository.findAll();
    }


    @Override
    public Quiz updateQuiz(int quizId, QuizRequest request) throws Exception {

        List<Question> questionsList = new ArrayList<>();

        Quiz quiz = this.getQuiz(quizId);
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setMaxMarks(request.getMaxMarks());
        quiz.setNumberOfQuestions(request.getNumberOfQuestions());
        quiz.setActive(request.getActive());

        //create questions
        for(QuestionRequest ques : request.getQuestions()){
            Question newQuestion = new Question();

            newQuestion.setQuiz(quiz);
            newQuestion.setQuestion(ques.getQuestion());
            newQuestion.setAnswer(ques.getAnswer());

            newQuestion.setOption1(ques.getOption1());
            newQuestion.setOption2(ques.getOption2());
            newQuestion.setOption3(ques.getOption3());
            newQuestion.setOption4(ques.getOption4());

            questionsList.add(newQuestion);
        }

        quiz.setQuestions(questionsList);



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
