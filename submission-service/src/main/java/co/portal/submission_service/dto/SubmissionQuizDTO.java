package co.portal.submission_service.dto;

import co.portal.submission_service.entity.Analytics;
import co.portal.submission_service.entity.Quiz;
import co.portal.submission_service.entity.Submission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionQuizDTO {

    private Submission submission;

    private Quiz quiz;

    @Transient
    private Analytics analytics;

    @Transient
    private long totalPointsSecured;

    public SubmissionQuizDTO(Submission submission, Quiz quiz) {
        this.quiz = quiz;
        this.submission = submission;
    }

}
