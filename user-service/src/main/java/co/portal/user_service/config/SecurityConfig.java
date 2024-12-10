package co.portal.user_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Disable basic authentication
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/auth/**").permitAll() // Permit authentication endpoint
//                .anyRequest().authenticated()  // All other requests need to be authenticated
                .and()
                .httpBasic().disable() // Disabling basic authentication
                .formLogin().disable() // Disabling form login if needed
                .headers().frameOptions().disable(); // Disable frame options if using H2 console
    }
}
