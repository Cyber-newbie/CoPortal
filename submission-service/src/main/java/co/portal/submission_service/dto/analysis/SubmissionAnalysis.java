package co.portal.submission_service.dto.analysis;

import co.portal.submission_service.dto.SubmissionQuizDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class SubmissionAnalysis {

    @NotEmpty
    private List<SubmissionQuizDTO> submissionQuiz;

    @NotNull
    private int overallPointsScored;



}
