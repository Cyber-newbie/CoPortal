package co.portal.user_service.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ErrorResponse  {
    private String status;
    private String message;
    private List<String> errors;
    public ErrorResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

}
