package co.portal.gateway.utils;

import io.jsonwebtoken.*;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtUtils {
    private String SECRET_KEY = "secret";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

//    public String generateToken(UserDetails userDetails) {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("roles", userDetails.getAuthorities().stream()
//                .map(authority -> "ROLE_" + authority.getAuthority())
//                .collect(Collectors.toList()));
//        return createToken(claims, userDetails.getUsername());
//    }

    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public boolean validateToken(String token) {
        return (validateTokenSignature(token) && !isTokenExpired(token));
    }

    public boolean validateTokenSignature(String token) {
        try {
            // Debugging: log the token to check its format
            System.out.println("Received JWT Token: " + token);

            // Parsing the token
            Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token); // This will throw exception if token is invalid

            return true; // If no exception, the token is valid
        } catch (MalformedJwtException e) {
            // Debugging: log the specific exception for more insight
            System.err.println("Malformed JWT Exception: " + e.getMessage());
            return false;  // Token is malformed
        } catch (ExpiredJwtException e) {
            // Token has expired
            System.err.println("Expired JWT Exception: " + e.getMessage());
            return false;
        } catch (SignatureException e) {
            // Invalid signature
            System.err.println("Signature Exception: " + e.getMessage());
            return false;
        } catch (Exception e) {
            // Generic exception, could be something else
            System.err.println("JWT Validation failed: " + e.getMessage());
            return false;
        }
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class); // Extract roles claim
    }
}

