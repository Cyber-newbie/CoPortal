package co.portal.user_service.controller.user;

import co.portal.user_service.dto.user.*;
import co.portal.user_service.entity.User;
import co.portal.user_service.service.user.UserService;
import co.portal.user_service.utils.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@Slf4j
public class UserController {

    private UserService userService;
    private UserMapper userMapper;


    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }


    @PostMapping("/create")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) throws Exception {

        User createdUser =  this.userService.createUser(request);
        UserResponse response = this.userMapper.toDto(createdUser, "201", "User created successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/admin/assign-role/{userId}")
    public ResponseEntity<UserResponse> assignRoleToUser(@PathVariable("userId") Integer userId,
                                            @Valid @RequestBody RoleRequest request) throws Exception {

        log.info("assigning role ");

        User user = this.userService.assignRoleToUser(userId, request.getRoles());
        UserResponse response = this.userMapper.toDto(user, "204", "User's roles updated");
        return ResponseEntity.status(204).body(response);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUser(@PathVariable String username) throws Exception {

        try {
            UserDTO user = this.userService.getUserByName(username);

            log.info("get user by username: {}", username);

            return ResponseEntity.ok(user);

        } catch (Exception e) {
             ErrorResponse errorResponse = new ErrorResponse("500", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/admin/{userId}")
    public ResponseEntity<UserResponse> delete(@PathVariable long userId) throws Exception {
            this.userService.deleteUserById(userId);
            UserResponse userResponse = new UserResponse();
            userResponse.setStatus("200");
            userResponse.setMessage("User deleted successfully");
            return ResponseEntity.ok(userResponse);
    }


    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request) throws Exception {
           UserResponse UserResponse = this.userService.login(request);
           return ResponseEntity.ok(UserResponse);
    }


}
