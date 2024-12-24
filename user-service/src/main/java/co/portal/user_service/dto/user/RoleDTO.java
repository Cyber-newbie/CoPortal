package co.portal.user_service.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleDTO {

    private long id;

    private String role;
}
