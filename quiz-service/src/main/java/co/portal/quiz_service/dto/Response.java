package co.portal.quiz_service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class Response<T>{

    private String status;

    private String message;

    private T data;
}
