package paybank.astro.service.admin;

import paybank.astro.dto.user.UserRequest;
import paybank.astro.entity.User;

public interface AdminService {
    User createUser(UserRequest request) throws Exception;
}
