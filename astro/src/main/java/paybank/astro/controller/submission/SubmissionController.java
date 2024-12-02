package paybank.astro.controller.submission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import paybank.astro.dto.submission.SubmissionRequest;
import paybank.astro.dto.submission.SubmissionResponse;
import paybank.astro.entity.Submission;
import paybank.astro.service.submission.SubmissionServiceImpl;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("submission")
public class SubmissionController {

    private SubmissionServiceImpl submissionService;

    @Autowired
    public SubmissionController(SubmissionServiceImpl submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping("/{quizId}")
    public ResponseEntity<SubmissionResponse<Submission>> submit(@PathVariable("quizId") String quizId,
                                                                 @Valid @RequestBody SubmissionRequest request)
            throws Exception {


        Submission submission = submissionService.evaluateQuizSubmit(request, quizId);

        SubmissionResponse<Submission> response = new SubmissionResponse<>();
        response.setStatus("201");
        response.setMessage("You have submitted successfully");
        response.setData(submission);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user")
    public ResponseEntity<SubmissionResponse<List<Submission>>> getUserSubmissions(){

        List<Submission> userSubmissions = submissionService.getUserSubmissions();

        SubmissionResponse<List<Submission>> response = new SubmissionResponse<>();
        response.setStatus("201");
        response.setMessage("You have submitted successfully");
        response.setData(userSubmissions);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }
}
