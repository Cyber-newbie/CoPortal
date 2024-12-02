package paybank.astro.controller.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import paybank.astro.dto.quiz.QuizRequest;
import paybank.astro.dto.quiz.QuizResponse;
import paybank.astro.entity.Question;
import paybank.astro.entity.Quiz;
import paybank.astro.service.quiz.QuizService;

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
    public ResponseEntity<QuizResponse> createQuiz(@Valid  @RequestBody QuizRequest quiz) throws Exception {
        Quiz createdQuiz = this.quizService.createQuiz(quiz);
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


    @GetMapping("/user/{quizId}")
    public Quiz getQuizQuestions(@PathVariable("quizId") String quizId )
            throws Exception {
        return this.quizService.getQuizQuestions(quizId);
    }
}
