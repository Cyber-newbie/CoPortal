package paybank.astro.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import paybank.astro.dto.user.ErrorResponse;
import paybank.astro.dto.user.LoginRequest;
import paybank.astro.dto.user.UserRequest;
import paybank.astro.dto.user.UserResponse;
import paybank.astro.entity.User;
import paybank.astro.service.user.UserService;
import paybank.astro.util.mapper.UserMapper;
import paybank.astro.util.types.RoleTypes;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;
    private UserMapper userMapper;


    @Autowired
    public  UserController(UserService userService, UserMapper userMapper) {
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
            UserResponse userResponse = new UserResponse();
            userResponse.setUser(user);
            userResponse.setStatus("200");
            userResponse.setMessage("User fetched successfully");
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
             ErrorResponse errorResponse = new ErrorResponse("500", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{userId}")
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
