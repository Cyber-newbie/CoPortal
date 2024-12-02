package paybank.astro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import paybank.astro.entity.Roles;
import paybank.astro.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findById(long id);

    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.id = :id")
    User findByIdWithRoles(@Param("id") long id);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :roles")
    List<User> findUsersByRoles(@Param("roles") Roles roles);
}
