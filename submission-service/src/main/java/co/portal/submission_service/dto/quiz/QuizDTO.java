package co.portal.submission_service.dto.quiz;

import co.portal.submission_service.dto.question.QuestionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @Null
    private Object[] questions;

}