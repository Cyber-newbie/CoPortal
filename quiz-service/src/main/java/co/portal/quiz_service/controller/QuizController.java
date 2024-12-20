package co.portal.quiz_service.controller;

import co.portal.quiz_service.dto.Question;
import co.portal.quiz_service.dto.QuestionResponse;
import co.portal.quiz_service.dto.QuizRequest;
import co.portal.quiz_service.dto.QuizResponse;
import co.portal.quiz_service.entity.Quiz;
import co.portal.quiz_service.service.QuizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/quiz")
@Slf4j
public class QuizController {

    private QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/admin/create")
    public ResponseEntity<QuizResponse<Quiz>> createQuiz(@Valid  @RequestBody QuizRequest quiz,
                                                   @RequestHeader("loggedInUsername") String username ) throws Exception {

        Quiz createdQuiz = this.quizService.createQuiz(quiz, username);
        QuizResponse<Quiz> quizResponse = new QuizResponse<>();

        quizResponse.setStatus("201");
        quizResponse.setMessage("Quiz created successfully");
        quizResponse.setQuiz(createdQuiz);
        return ResponseEntity.status(HttpStatus.CREATED).body(quizResponse);
    }

    @GetMapping
    public ResponseEntity<QuizResponse<List<Quiz>>> getAllQuiz() throws Exception {
        List<Quiz> quizList = this.quizService.getAllQuizes();
        QuizResponse<List<Quiz>> quizResponse = new QuizResponse<>();
        quizResponse.setStatus("200");
        quizResponse.setQuiz(quizList);
        quizResponse.setMessage("Quiz list retrieved successfully");
        return ResponseEntity.status(HttpStatus.OK).body(quizResponse);
    }

    @GetMapping("/user/{quizId}")
    public ResponseEntity<QuizResponse<Quiz>> getQuizById(@PathVariable("quizId") Integer quizId) throws Exception {

        log.info("GET QUIZ BY ID");

        Quiz quiz = quizService.getQuiz(quizId);

        QuizResponse<Quiz> response = new QuizResponse<>();
        response.setQuiz(quiz);
        response.setMessage("Quiz fetched by id: " + quizId);
        response.setStatus("200");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

//    @GetMapping("/{quizId}")
//    public ResponseEntity<QuestionResponse> getQuizQuestions(@PathVariable("quizId") Integer quizId) throws Exception {
//
//        List<Question> questions = this.quizService.getQuizQuestions(quizId);
//        QuestionResponse response =  new QuestionResponse();
//        response.setQuestions(questions);
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }

}
