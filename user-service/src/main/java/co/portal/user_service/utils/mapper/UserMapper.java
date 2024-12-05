package co.portal.user_service.utils.mapper;

import co.portal.user_service.dto.user.UserRequest;
import co.portal.user_service.dto.user.UserResponse;
import co.portal.user_service.entity.Roles;
import co.portal.user_service.entity.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserMapper implements GenericMapper<User, UserRequest, UserResponse>{

    @Override
    public User toEntity(UserRequest dto) {
         User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());

        //assign role
        Roles role = new Roles(dto.getRoles().name());
        List<Roles> userRoles = new ArrayList<>();
        userRoles.add(role);
        user.setRoles(userRoles);
        return user;
    }

    @Override
    public UserResponse toDto(User user, String status, String message, String token) {

        UserResponse userResponse = new UserResponse();

        userResponse.setUser(user);
        userResponse.setStatus(status);
        userResponse.setMessage(message);
        userResponse.setToken(token);

        return userResponse;
    }
}
