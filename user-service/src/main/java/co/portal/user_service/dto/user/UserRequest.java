package co.portal.user_service.dto.user;

import co.portal.user_service.utils.types.RoleTypes;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class UserRequest {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String username;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, message = "password should be of 6 characters minimum")
    private String password;

    private String phone;

    private RoleTypes roles = RoleTypes.USER;
}
