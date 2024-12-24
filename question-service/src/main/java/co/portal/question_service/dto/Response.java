package co.portal.question_service.dto;

import co.portal.question_service.entity.Question;
import lombok.*;

import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Response<T> {

    private String status;

    private String message;

    private T data;

}
