package paybank.astro;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import paybank.astro.entity.Roles;
import paybank.astro.entity.User;
import paybank.astro.repository.RoleRepository;
import paybank.astro.repository.UserRepository;
import paybank.astro.service.role.RoleServiceImpl;
import paybank.astro.service.user.UserServiceImpl;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class AstroApplication {

	public static void main(String[] args) {
		SpringApplication.run(AstroApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(RoleServiceImpl roleServiceImpl, UserServiceImpl userServiceImpl) {
		return args -> {

//			Roles role = roleServiceImpl.getRole("ADMIN");
//
//			if (role != null) {
//				 userServiceImpl.createAdmin(role);
//			} else {
//				System.out.println("Role not found: ADMIN");
//				roleServiceImpl.createRole("ADMIN");
//			}
		};
	}

}
