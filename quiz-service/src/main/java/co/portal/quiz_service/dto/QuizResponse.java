package co.portal.quiz_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizResponse <T>{

    private String status;

    private String message;

    private T quiz;
}
