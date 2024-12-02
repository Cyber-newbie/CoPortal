package paybank.astro.config;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import paybank.astro.service.UserDetailsServiceImpl;
import paybank.astro.util.JwtUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            final String token = authorizationHeader.substring(7);

            try {
                // Extract the username from the token
                String username = jwtUtils.extractUsername(token);

                // Check if the username exists and user is found
                if (username != null && userDetailsService.loadUserByUsername(username) != null) {
                    final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // Validate the token
                    if (this.jwtUtils.validateToken(token, userDetails)) {
                        // If the token is valid, set the authentication in the security context
                        List<GrantedAuthority> authorities = jwtUtils.extractRoles(token).stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Token expired or invalid");
                        return; // Stop further processing if token is invalid or expired
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token, username not found");
                    return; // Stop further processing if the user is not found
                }
            } catch (ExpiredJwtException e) {
                // Handle expired token exception
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("JWT token is expired");
                return; // Stop further processing if token is expired
            } catch (Exception e) {
                // Handle other exceptions (invalid token, parsing errors, etc.)
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: " + e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}
