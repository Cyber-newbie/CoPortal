package co.portal.submission_service.dto.submission;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class SubmissionRequest {

    private final LocalDateTime submissionTime = LocalDateTime.now();

    @NotEmpty
    private List<UserAnswers> userAnswers;
}
