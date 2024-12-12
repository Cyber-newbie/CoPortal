package co.portal.user_service.service.user;


import co.portal.user_service.dto.user.LoginRequest;
import co.portal.user_service.dto.user.UserRequest;
import co.portal.user_service.dto.user.UserResponse;
import co.portal.user_service.entity.Roles;
import co.portal.user_service.entity.User;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public interface UserService {
    User createUser(UserRequest request) throws Exception;

    User getUserByName(String username) throws Exception;

    void deleteUserById(long id) throws Exception;

    UserResponse login(LoginRequest request) throws Exception;

    User createAdmin(Roles role) throws Exception;

    User assignRoleToUser(int userId, String[] Roles) throws Exception;

}
