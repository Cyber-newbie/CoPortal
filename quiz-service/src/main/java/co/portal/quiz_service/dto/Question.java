package co.portal.quiz_service.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class Question {

    private int id;

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
