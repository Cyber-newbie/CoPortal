package co.portal.question_service.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class QuestionRequest {

    private int questionId;

    @NotBlank
    private String question;

    @NotBlank
    private String answer;

    private String image;

    private String option1;

    private String option2;

    private String option3;

    private String option4;

}
