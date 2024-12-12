package co.portal.question_service.controller;

import co.portal.question_service.dto.QuestionRequest;
import co.portal.question_service.dto.Response;
import co.portal.question_service.entity.Question;
import co.portal.question_service.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Response> saveQuizQuestions(@RequestBody List<QuestionRequest> request, @PathVariable Integer quizId) throws Exception {
        Response response = this.questionService.createQuizQuestions(request, quizId);
        return ResponseEntity.ok(response);
    }
}
