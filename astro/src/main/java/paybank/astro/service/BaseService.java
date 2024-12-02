package paybank.astro.service;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import paybank.astro.entity.User;
import paybank.astro.exception.UserNotFoundException;
import paybank.astro.repository.UserRepository;

@Service
public abstract class BaseService {

    protected final UserRepository userRepository;

    @Autowired
    public BaseService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    protected User getLoggedInUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        if(user == null) {
            throw new UserNotFoundException("User not found");
        }
        return user;
    }
}
