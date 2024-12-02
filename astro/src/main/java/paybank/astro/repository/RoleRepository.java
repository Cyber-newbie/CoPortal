package paybank.astro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import paybank.astro.entity.Roles;

public interface RoleRepository extends JpaRepository<Roles, Long> {
    Roles findByRole(String role);
}
