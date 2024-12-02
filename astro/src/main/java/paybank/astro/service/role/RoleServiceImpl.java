package paybank.astro.service.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import paybank.astro.entity.Roles;
import paybank.astro.repository.RoleRepository;

@Service
public class RoleServiceImpl implements  RoleService {

    private RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Roles getRole(String role) {
        return roleRepository.findByRole("ADMIN");
    }

    @Override
    @Transactional
    public void createRole(String role) {
        System.out.println("Creating role");
        Roles newRole = new Roles("ADMIN");
        roleRepository.save(newRole);
        System.out.println("Role created: " + newRole);
    }
}
