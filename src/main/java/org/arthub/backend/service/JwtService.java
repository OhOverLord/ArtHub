package org.arthub.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.arthub.backend.entity.User;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Service class for handling JSON Web Tokens (JWT).
 *
 * @since 1.0
 */
@Service
public class JwtService {
    /**
     * Secret key used to sign and verify JWT tokens.
     */
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    /**
     * Expiration time for the JWT token.
     */
    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    /**
     * Repository for retrieving user details from the database.
     */
    private final UserRepository userRepository;

    /**
     * Constructs an instance of {@link JwtService}.
     *
     * @param userRepository           the repository for accessing users in the database
     */
    public JwtService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * Retrieves the current user from the JWT token.
     *
     * @return the current user
     * @throws NotFound if the user is not found
     */
    public User getUserByToken() throws NotFound {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User user = userRepository.findByUsername(currentUsername).orElse(null);

        if (user == null) {
            throw new NotFound();
        }

        return user;
    }

    /**
     * Extracts the username from the JWT token.
     *
     * @param token the JWT token
     * @return the username extracted from the token
     */
    public String extractUsername(final String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the JWT token.
     *
     * @param <T> the type of the claim to extract
     * @param token the JWT token
     * @param claimsResolver a function to extract the claim
     * @return the claim extracted from the token
     */
    public <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a JWT token for the specified user.
     *
     * @param userDetails the user details for whom the token is generated
     * @return the generated JWT token
     */
    public String generateToken(final UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a JWT token with additional claims.
     *
     * @param extraClaims additional claims to include in the token
     * @param userDetails the user details for whom the token is generated
     * @return the generated JWT token
     */
    public String generateToken(
            final Map<String, Object> extraClaims,
            final UserDetails userDetails
        ) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Retrieves the expiration time for JWT tokens.
     *
     * @return the expiration time in milliseconds
     */
    public long getExpirationTime() {
        return jwtExpiration;
    }

    /**
     * Builds a JWT token with specified claims and expiration time.
     *
     * @param extraClaims additional claims to include in the token
     * @param userDetails the user details for whom the token is generated
     * @param expiration the expiration time for the token
     * @return the generated JWT token
     */
    private String buildToken(
            final Map<String, Object> extraClaims,
            final UserDetails userDetails,
            final long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates if the provided JWT token is still valid for the specified user.
     *
     * @param token the JWT token to validate
     * @param userDetails the user details to compare with the token
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(final String token, final UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Checks if the JWT token has expired.
     *
     * @param token the JWT token
     * @return true if the token has expired, false otherwise
     */
    private boolean isTokenExpired(final String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token the JWT token
     * @return the expiration date of the token
     */
    private Date extractExpiration(final String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token the JWT token
     * @return the claims contained in the token
     */
    private Claims extractAllClaims(final String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Retrieves the signing key for JWT token generation and validation.
     *
     * @return the key used to sign and verify JWT tokens
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
