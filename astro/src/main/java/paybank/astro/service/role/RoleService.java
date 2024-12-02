package paybank.astro.service.role;

import paybank.astro.entity.Roles;

public interface RoleService {
    Roles getRole(String role);
    void createRole(String role);
}
