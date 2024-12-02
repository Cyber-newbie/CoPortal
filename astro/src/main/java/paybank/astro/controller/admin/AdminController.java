package paybank.astro.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import paybank.astro.dto.user.UserRequest;
import paybank.astro.dto.user.UserResponse;
import paybank.astro.entity.User;
import paybank.astro.service.admin.AdminServiceImpl;
import paybank.astro.util.mapper.UserMapper;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private AdminServiceImpl adminService;
    private UserMapper userMapper;

    @Autowired
    public AdminController(AdminServiceImpl adminService, UserMapper userMapper) {
        this.adminService = adminService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid  @RequestBody UserRequest userRequest) throws Exception {

        User user = this.adminService.createUser(userRequest);
        UserResponse userResponse = this.userMapper.toDto(user, "201", "User created successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);

    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UserRequest request,
                                                   @PathVariable("userId") String userId){

        return null;
    }

}
