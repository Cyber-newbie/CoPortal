package co.portal.user_service.controller.user;

import co.portal.user_service.dto.user.ErrorResponse;
import co.portal.user_service.dto.user.LoginRequest;
import co.portal.user_service.dto.user.UserRequest;
import co.portal.user_service.dto.user.UserResponse;
import co.portal.user_service.entity.User;
import co.portal.user_service.service.user.UserService;
import co.portal.user_service.utils.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
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

    @GetMapping("/{username}")
    public ResponseEntity<?> getUser(@PathVariable String username) throws Exception {

        try {
            User user = this.userService.getUserByName(username);
//            UserResponse userResponse = new UserResponse();
//            userResponse.setUser(user);
//            userResponse.setStatus("200");
//            userResponse.setMessage("User fetched successfully");
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
