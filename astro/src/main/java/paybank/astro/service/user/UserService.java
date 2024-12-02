package paybank.astro.service.user;

import paybank.astro.dto.user.LoginRequest;
import paybank.astro.dto.user.UserRequest;
import paybank.astro.dto.user.UserResponse;
import paybank.astro.entity.Roles;
import paybank.astro.entity.User;
import paybank.astro.util.types.RoleTypes;

import java.util.List;

public interface UserService {
    User createUser(UserRequest request) throws Exception;

    User getUserByName(String username) throws Exception;

    void deleteUserById(long id) throws Exception;

    UserResponse login(LoginRequest request) throws Exception;

    User createAdmin(Roles role) throws Exception;

}
