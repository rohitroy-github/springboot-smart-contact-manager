package springdev.scm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import springdev.scm.implementations.SecurityCustomUserDetailService;

/**
 * Spring Security configuration class that sets up authentication, authorization,
 * password encoding, and login/logout handling for the application.
 *
 * Features:
 * - DAO-based authentication with custom user details service
 * - OAuth2 login integration with custom success handler
 * - Form-based login with custom login page
 * - Role-based URL protection (e.g., /user/** requires authentication)
 * - BCrypt password encoding for secure password storage
 * - CSRF protection disabled (for API-style endpoints)
 */
@Configuration
public class SecurityConfig {

    @Autowired
    private SecurityCustomUserDetailService securityCustomUserDetailService;

    @Autowired
    private OAuthenticationSuccessHandler oAuthenticationSuccessHandler;

    /**
     * Configures and provides a DAO-based authentication provider.
     * Uses the custom user details service to load user credentials from the database
     * and BCrypt password encoder for password verification.
     *
     * @return a configured {@link DaoAuthenticationProvider}
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(securityCustomUserDetailService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    /**
     * Creates a BCrypt password encoder bean for secure password hashing and verification.
     *
     * @return a {@link BCryptPasswordEncoder} instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain for HTTP request authorization and authentication.
     *
     * Authorization rules:
     * - /user/** endpoints require authentication
     * - All other endpoints are publicly accessible
     *
     * Form login configuration:
     * - Custom login page at /login
     * - Login processing at /login-user
     * - Email used as username parameter (not standard 'username')
     * - Redirects to /user/dashboard on success
     *
     * OAuth2 login:
     * - Enabled with custom success handler
     * - Uses the same /login page
     *
     * Logout configuration:
     * - Logout URL: /logout
     * - Redirect to /login?logout=true after logout
     *
     * @param httpSecurity the HttpSecurity object to configure
     * @return the built {@link SecurityFilterChain}
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        // Configure URL authorization: /user/** is protected, all others are public
        httpSecurity.authorizeHttpRequests(authorize -> {
            authorize.requestMatchers("/user/**").authenticated();
            authorize.anyRequest().permitAll();
        });

        // Configure custom form-based login
        httpSecurity.formLogin(formLogin -> {
            formLogin.loginPage("/login");
            formLogin.loginProcessingUrl("/login-user");
            formLogin.successForwardUrl("/user/dashboard");
            formLogin.failureForwardUrl("/login?error=true");
            formLogin.defaultSuccessUrl("/user/dashboard");
            formLogin.usernameParameter("email");
            formLogin.passwordParameter("password");
        });

        // Configure OAuth2 login
        httpSecurity.oauth2Login(oauth -> {
            oauth.loginPage("/login");
            oauth.successHandler(oAuthenticationSuccessHandler);
        });

        // Disable CSRF for API endpoints (adjust if needed for production)
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        // Configure logout
        httpSecurity.logout(logoutForm -> {
            logoutForm.logoutUrl("/logout");
            logoutForm.logoutSuccessUrl("/login?logout=true");
        });

        return httpSecurity.build();
    }

}
