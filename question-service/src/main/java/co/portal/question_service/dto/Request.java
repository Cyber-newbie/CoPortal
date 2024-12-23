package co.portal.question_service.dto;

import lombok.Data;

@Data
public class Request<T>{

    private T data;

}
