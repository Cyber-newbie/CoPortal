package co.portal.user_service.repository;

import co.portal.user_service.dto.user.UserDTO;
import co.portal.user_service.entity.Roles;
import co.portal.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    User findById(long id);

    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.id = :id")
    User findByIdWithRoles(@Param("id") long id);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :roles")
    List<User> findUsersByRoles(@Param("roles") Roles roles);
}
