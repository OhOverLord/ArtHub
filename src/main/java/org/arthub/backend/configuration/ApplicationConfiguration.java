/**
 * This package contains configuration classes for the Arthub backend application.
    */
package org.arthub.backend.configuration;

import lombok.RequiredArgsConstructor;
import org.arthub.backend.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuration class for managing authentication and user details services.
 * <p>
 * This class is designed to be extended by subclasses for further customization of the
 * security configuration. When subclassing, ensure that the {@link UserDetailsService},
 * {@link AuthenticationManager}, and {@link AuthenticationProvider} beans are properly
 * configured to avoid security risks.
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {
    /**
     * Repository for user data used for authentication and authorization.
     */
    private final UserRepository userRepository;

    /**
     * Returns a {@link UserDetailsService} bean that retrieves user details from the {@link UserRepository}.
     * <p>
     * This service is used to load user-specific data, typically for authentication purposes.
     * </p>
     *
     * @return the {@link UserDetailsService}
     * @throws UsernameNotFoundException if the user is not found in the repository
     */
    @Bean
    UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Returns a {@link BCryptPasswordEncoder} bean for password encoding.
     * <p>
     * This encoder will be used to securely hash and verify user passwords.
     * </p>
     *
     * @return the {@link BCryptPasswordEncoder}
     */
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Returns the {@link AuthenticationManager} using the provided {@link AuthenticationConfiguration}.
     * <p>
     * The authentication manager is responsible for processing authentication requests.
     * </p>
     *
     * @param config the {@link AuthenticationConfiguration} to be used
     * @return the {@link AuthenticationManager}
     * @throws Exception if an error occurs during authentication setup
     */
    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Returns an {@link AuthenticationProvider} bean configured with {@link DaoAuthenticationProvider}.
     * <p>
     * This provider will use the {@link UserDetailsService} and {@link BCryptPasswordEncoder}
     * to authenticate users.
     * </p>
     *
     * @return the {@link AuthenticationProvider}
     */
    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
}
