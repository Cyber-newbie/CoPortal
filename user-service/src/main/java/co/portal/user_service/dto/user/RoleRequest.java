package co.portal.user_service.dto.user;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@Getter
public class RoleRequest {

    @NotEmpty
    private String[] roles;

}
