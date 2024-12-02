package paybank.astro.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import paybank.astro.dto.user.LoginRequest;
import paybank.astro.dto.user.UserRequest;
import paybank.astro.dto.user.UserResponse;
import paybank.astro.entity.Roles;
import paybank.astro.entity.User;
import paybank.astro.exception.UserAlreadyExistsException;
import paybank.astro.exception.UserNotFoundException;
import paybank.astro.repository.UserRepository;
import paybank.astro.util.JwtUtils;
import paybank.astro.util.mapper.UserMapper;
import paybank.astro.util.types.RoleTypes;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

     private UserRepository userRepository;
     private EntityManager entityManager;
     private JwtUtils jwtUtils;
     private UserMapper userMapper;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, JwtUtils jwtUtils,
                           EntityManager entityManager, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.entityManager = entityManager;
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
    }

    @Override
    public User createUser(UserRequest request) throws Exception {
        User isUser = userRepository.findByUsername(request.getUsername());
        if(isUser != null) {
            throw new UserAlreadyExistsException("User with username '" + request.getUsername() + "' already exists");
        }
        User user = userMapper.toEntity(request);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(request.getPassword()));

        userRepository.save(user);

        return user;
    }

    @Override
    public User getUserByName(String username) throws Exception {
        User user = this.userRepository.findByUsername(username);
        if(user == null) {
            throw new UserNotFoundException("User with username '" + username + "' not found");
        }
        return user;
    }

    @Override
    public void deleteUserById(long id) throws Exception {
        User user = this.userRepository.findById(id);
        if(user == null) {
            throw new UserNotFoundException("User not found");
        }
        this.userRepository.delete(user);
    }

    @Override
    public UserResponse login(LoginRequest request) throws Exception {

        User user = this.userRepository.findByUsername(request.getUsername());
        System.out.println(user);
        if(user == null) {
            throw new UserNotFoundException("User not found");
        }

       String token = jwtUtils.generateToken(user);
        UserResponse userResponse = new UserResponse();

        userResponse.setUser(user);
        userResponse.setToken(token);
        userResponse.setStatus("200");
        userResponse.setMessage("Login successful");
        return userResponse;
    }

    @Override
    @Transactional
    public User createAdmin(Roles role) throws Exception {
        List<User> users = userRepository.findUsersByRoles(role); // Expecting a list of users
        Roles managedRole = entityManager.merge(role);
        if(users.isEmpty()) {
            //create admin
            User user = new User();
            user.setUsername("admin");
            user.setPassword("admin");
            user.setFirstName("admin");
            user.setLastName("");
            user.setEmail("admin@coPortal.com");

            List<Roles> roles = new java.util.ArrayList<>();
            roles.add(managedRole);
            user.setRoles(roles);
            userRepository.save(user);
            System.out.println("Admin created");
        }
        System.out.println("Admin already exists");
        return null;
    }


}
