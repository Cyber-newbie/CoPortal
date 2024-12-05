package co.portal.user_service.dto.user;

import co.portal.user_service.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
    private String status;
    private String message;
    private User user;
    private String token;
}
