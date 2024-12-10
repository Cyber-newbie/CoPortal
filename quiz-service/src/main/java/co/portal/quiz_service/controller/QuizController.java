package co.portal.quiz_service.controller;

import co.portal.quiz_service.dto.QuizRequest;
import co.portal.quiz_service.dto.QuizResponse;
import co.portal.quiz_service.entity.Quiz;
import co.portal.quiz_service.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    private QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    public ResponseEntity<QuizResponse> createQuiz(@Valid  @RequestBody QuizRequest quiz,
                                                   @RequestHeader("loggedInUsername") String username ) throws Exception {

        Quiz createdQuiz = this.quizService.createQuiz(quiz, username);
        QuizResponse quizResponse = new QuizResponse();

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

}
