package co.portal.question_service.controller;

import co.portal.question_service.dto.*;
import co.portal.question_service.entity.Question;
import co.portal.question_service.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {

    private QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService){
        this.questionService = questionService;
    }

    @GetMapping("/{quizId}")
    public List<Question> getQuestionsByQuizId(@PathVariable("quizId") int quizId){
        return this.questionService.getQuizQuestions(quizId);
    }

    @PostMapping("/save/{quizId}")
    public ResponseEntity<Response<List<Question>>> saveQuizQuestions(@RequestBody List<QuestionRequest> request, @PathVariable Integer quizId) throws Exception {

        List<Question> questions = this.questionService.createQuizQuestions(request, quizId);
        Response<List<Question>> response = new Response<>("201", "Questions added to quiz", questions);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("check/{quizId}")
    public ResponseEntity<Response<Integer>> checkUserAnswers(@Valid  @RequestBody List<UserAnswers> userAnswers,
                                                     @PathVariable("quizId") Integer quizId,
                                                              @RequestHeader("Authorization") String token) throws Exception {

        int totalSecuredNumber = questionService.checkQuestionAgainstUserAnswers(quizId, userAnswers, token);
        Response<Integer> response = new Response<>("200", "total number secured", totalSecuredNumber);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
