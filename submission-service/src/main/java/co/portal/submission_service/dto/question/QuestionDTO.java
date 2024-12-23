package co.portal.submission_service.dto.question;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;


@Data
@Builder
public class QuestionDTO {

    private int id;

    @NotNull
    private String question;

    @NotNull
    private String answer;

    private String image;

    private String option1;

    private String option2;

    private String option3;

    private String option4;

}
