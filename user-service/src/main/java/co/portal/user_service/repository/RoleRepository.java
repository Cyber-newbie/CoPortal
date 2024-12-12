package co.portal.user_service.repository;

import co.portal.user_service.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Roles, Integer> {

    Roles findByRole(String role);

}