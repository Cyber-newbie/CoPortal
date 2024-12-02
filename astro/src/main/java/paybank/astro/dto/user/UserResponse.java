package paybank.astro.dto.user;

import lombok.Getter;
import lombok.Setter;
import paybank.astro.entity.User;

@Getter
@Setter
public class UserResponse {
    private String status;
    private String message;
    private User user;
    private String token;
}
