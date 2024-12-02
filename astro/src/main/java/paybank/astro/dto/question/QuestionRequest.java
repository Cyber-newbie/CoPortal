package paybank.astro.dto.question;

import lombok.Getter;
import paybank.astro.entity.Quiz;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

@Getter
public class QuestionRequest {

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
