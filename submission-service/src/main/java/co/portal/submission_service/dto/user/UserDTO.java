package co.portal.submission_service.dto.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@ToString
public class UserDTO {

    private long id;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private List<RoleDTO> roles;
}
