package co.portal.quiz_service.controller;

import co.portal.quiz_service.dto.QuizRequest;
import co.portal.quiz_service.dto.Response;
import co.portal.quiz_service.entity.Quiz;
import co.portal.quiz_service.service.QuizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Response<Quiz>> createQuiz(@Valid  @RequestBody QuizRequest quiz,
                                                     @RequestHeader("loggedInUsername") String username ) throws Exception {

        Quiz createdQuiz = this.quizService.createQuiz(quiz, username);
        Response<Quiz> quizResponse = new Response<>();

        quizResponse.setStatus("201");
        quizResponse.setMessage("Quiz created successfully");
        quizResponse.setData(createdQuiz);
        return ResponseEntity.status(HttpStatus.CREATED).body(quizResponse);
    }

    @GetMapping
    public ResponseEntity<Response<List<Quiz>>> getAllQuiz() throws Exception {
        List<Quiz> quizList = this.quizService.getAllQuizes();
        Response<List<Quiz>> quizResponse = new Response<>();
        quizResponse.setStatus("200");
        quizResponse.setData(quizList);
        quizResponse.setMessage("Quiz list retrieved successfully");
        return ResponseEntity.status(HttpStatus.OK).body(quizResponse);
    }

    @GetMapping("/user/{quizId}")
    public ResponseEntity<Response<Quiz>> getQuizById(@PathVariable("quizId") Integer quizId) throws Exception {

        log.info("GET QUIZ BY ID");

        Quiz quiz = quizService.getQuiz(quizId);

        Response<Quiz> response = new Response<>();
        response.setData(quiz);
        response.setMessage("Quiz fetched by id: " + quizId);
        response.setStatus("200");

        log.info("get quiz response {}", response.getData());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
