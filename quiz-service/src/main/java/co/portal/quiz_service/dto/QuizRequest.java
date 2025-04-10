package co.portal.quiz_service.dto;

import lombok.Getter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
public class QuizRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String numberOfQuestions;

    @NotBlank
    private String maxMarks;

    private LocalDateTime deadline = LocalDateTime.now();

    private LocalTime timeLimit = LocalTime.now();

    private Boolean active = true;

    private int totalAttempts = 1;

    @NotNull
    private int categoryId;

    @NotEmpty(message = "Questions list cannot be empty")
    private List<Question> questions;



}
