package paybank.astro.service.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import paybank.astro.dto.user.UserRequest;
import paybank.astro.dto.user.UserResponse;
import paybank.astro.entity.User;
import paybank.astro.service.user.UserService;
import paybank.astro.service.user.UserServiceImpl;

@Service
public class AdminServiceImpl implements AdminService {

    private UserServiceImpl userService;

    @Autowired
    public AdminServiceImpl(UserServiceImpl userService) {
        this.userService = userService;
    }


    @Override
    @Transactional
    public User createUser(UserRequest request) throws Exception  {

         User user = this.userService.createUser(request);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(request.getPassword()));

        return user;
    }



}
