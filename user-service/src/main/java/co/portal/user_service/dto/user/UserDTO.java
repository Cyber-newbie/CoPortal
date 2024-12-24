package co.portal.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UserDTO {

    private long id;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private List<RoleDTO> roles;
}