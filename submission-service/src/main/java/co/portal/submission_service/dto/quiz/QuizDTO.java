package co.portal.submission_service.dto.quiz;

import co.portal.submission_service.dto.question.QuestionDTO;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class QuizDTO {

    private int id;

    @NotNull
    private String title;

    private String description;

    private String maxMarks;

    private LocalDateTime deadline;

    private LocalTime timeLimit;

    @NotNull
    private String numberOfQuestions;

    private Boolean active = false;

    private CategoryDTO category;

    private long userId;

    private List<QuestionDTO> questions = new ArrayList<>();

}