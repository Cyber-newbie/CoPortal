package co.portal.quiz_service.dto;

import javax.validation.constraints.NotBlank;

public class Question {

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
