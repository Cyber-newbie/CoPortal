package paybank.astro.dto.quiz;

import lombok.Getter;
import lombok.Setter;
import paybank.astro.entity.Quiz;

@Getter
@Setter
public class QuizResponse <T>{

    private String status;

    private String message;

    private T quiz;
}
