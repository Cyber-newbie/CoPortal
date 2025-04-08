package co.portal.submission_service.controller.submission;

import co.portal.submission_service.dto.SubmissionQuizDTO;
import co.portal.submission_service.dto.analysis.SubmissionAnalysis;
import co.portal.submission_service.dto.submission.SubmissionRequest;
import co.portal.submission_service.dto.submission.SubmissionResponse;
import co.portal.submission_service.entity.Submission;
import co.portal.submission_service.services.submission.SubmissionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<SubmissionResponse<Submission>> submit(@Valid @RequestBody SubmissionRequest request,
                                                                 @RequestHeader("loggedInUsername") String username )
            throws Exception {

        String quizId = String.valueOf(request.getQuizId());

        Submission submission = submissionService.evaluateQuizSubmit(request, quizId, username);

        SubmissionResponse<Submission> response = new SubmissionResponse<>();
        response.setStatus("201");
        response.setMessage("You have submitted successfully");
        response.setData(submission);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user")
    public ResponseEntity<SubmissionResponse<List<Submission>>>
    getUserSubmissions(@RequestHeader("loggedInUsername") String username ){

        List<Submission> userSubmissions = submissionService.getUserSubmissions(username);

        SubmissionResponse<List<Submission>> response = new SubmissionResponse<>();
        response.setStatus("201");
        response.setMessage("Submissions fetched successfully");
        response.setData(userSubmissions);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @GetMapping("/user/details")
    public ResponseEntity<List<SubmissionQuizDTO>> getSubmissionsWithQuiz(@RequestHeader("loggedInUsername") String username) throws Exception {
        try {

        List<SubmissionQuizDTO> userSubmissions = submissionService.getSubmissionsWithQuiz(username);
        return ResponseEntity.status(HttpStatus.OK).body(userSubmissions);

        } catch(Exception e) {
            throw new Exception("User submissions with quiz fetch failed ");

        }
    }

    @GetMapping("/user/analytics")
    public ResponseEntity<SubmissionResponse<Object>> getUserSubmissionAnalytics() throws Exception {

        //aspect will inject the user object
        List<SubmissionQuizDTO> submissions = submissionService.submissionAnalysis(null);
        int totalPointScored = submissionService.getTotalPointsScored(submissions);

        SubmissionAnalysis data = SubmissionAnalysis.builder()
                .submissionQuiz(submissions)
                .overallPointsScored(totalPointScored)
                .build();
        ;

        SubmissionResponse<Object> response = SubmissionResponse.builder()
                .status("200")
                .message("User's submission analytics")
                .data(data)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);


    }

}
