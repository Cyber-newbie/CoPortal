package co.portal.user_service.service.user;

import co.portal.user_service.dto.user.*;
import co.portal.user_service.entity.Roles;
import co.portal.user_service.entity.User;
import co.portal.user_service.exception.RoleNotFoundException;
import co.portal.user_service.exception.UserAlreadyExistsException;
import co.portal.user_service.exception.UserNotFoundException;
import co.portal.user_service.repository.RoleRepository;
import co.portal.user_service.repository.UserRepository;
import co.portal.user_service.utils.JwtUtils;
import co.portal.user_service.utils.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

     private UserRepository userRepository;
     private RoleRepository roleRepository;
     private EntityManager entityManager;
     private JwtUtils jwtUtils;
     private UserMapper userMapper;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, JwtUtils jwtUtils,
                           EntityManager entityManager, UserMapper userMapper,
                           RoleRepository roleRepository) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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
    public UserDTO getUserByName(String username) throws Exception {
        User user = this.userRepository.findByUsername(username);
        if(user == null) {
            throw new UserNotFoundException("User with username '" + username + "' not found");
        }

        List<RoleDTO> roleList = new ArrayList<>();

        for (Roles userRole : user.getRoles()){
                RoleDTO role = RoleDTO.builder()
                        .id(userRole.getId())
                        .role(userRole.getRole())
                        .build();
                roleList.add(role);
        }

        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roleList)
                .build();

        return userDTO;
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
    public User createAdmin() throws Exception {
        Roles createdRole = Optional.ofNullable(roleRepository.findByRole("ADMIN")).orElseGet(() -> {
            Roles role = Roles.builder().role("ADMIN").build();
            return roleRepository.save(role);
        });


        List<User> users = userRepository.findUsersByRoles(createdRole); // Expecting a list of users
        if(users.isEmpty()) {
            //create admin
            User user = new User();
            user.setUsername("portalAdmin");
            user.setPassword("admin");
            user.setFirstName("admin");
            user.setLastName("");
            user.setEmail("admin@coPortal.com");

            List<Roles> roles = new ArrayList<>();
            roles.add(createdRole);
            user.setRoles(roles);
            userRepository.save(user);
            System.out.println("Admin created");
        }
        return null;
    }

    @Override
    @Transactional
    public User assignRoleToUser(int userId, String[] roles) throws Exception {

        User user = Optional.ofNullable(this.userRepository.findById(userId)).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );

        List<Roles> assignRoles = Arrays.stream(roles)
                .map((role) -> Optional.ofNullable
                        (this.roleRepository.findByRole(role))
                        .orElseThrow(() -> new RoleNotFoundException(role + " role do not exists"))).collect(Collectors.toList());


        user.setRoles(assignRoles);

        return this.entityManager.merge(user);
    }


}
