package co.portal.question_service.dto;

import co.portal.question_service.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Response<T> {

    private T data;

}
