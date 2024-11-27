// :> creating user and login using java code with in memory service

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

@Configuration
public class SecurityConfig {

    // private InMemoryUserDetailsManager inMemoryUserDetailsManager;

    // public UserDetailsService userDetailService() {

    // // :> creating a admin new user
    // UserDetails admin_user =
    // User.withDefaultPasswordEncoder().username("admin").password("password")
    // .roles("ADMIN", "USER").build();
    // // :> creating a developer new user
    // UserDetails dev_user =
    // User.withDefaultPasswordEncoder().username("rhtry").password("password")
    // .roles("ADMIN", "DEV").build();

    // var inMemoryUserDetailsManager = new InMemoryUserDetailsManager(admin_user,
    // dev_user);

    // return inMemoryUserDetailsManager;

    // }

    @Autowired
    private SecurityCustomUserDetailService securityCustomUserDetailService;

    @Autowired
    private OAuthenticationSuccessHandler oAuthenticationSuccessHandler;

    // :> configuraiton of authentication providerfor spring security

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        // user detail service ka object:
        daoAuthenticationProvider.setUserDetailsService(securityCustomUserDetailService);
        // password encoder ka object
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    // :> creating object for [daoAuthenticationProvider.setPasswordEncoder]
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();

    }

    // :> creating filterchain for spring-security for configuring routes and login
    // configurations
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        // :> configuration: url protection
        httpSecurity.authorizeHttpRequests(authorize -> {
            // :> making [/home], [/register], [/services] unprotected
            // authorize.requestMatchers("/home", "/register", "/services").permitAll();
            // :> making [/users/[any]] protected
            authorize.requestMatchers("/user/**").authenticated();
            // :> making all routes unprotected
            authorize.anyRequest().permitAll();

        });

        // :> allowing the default login [spring-security] functionality
        // httpSecurity.formLogin(Customizer.withDefaults());

        // :> customizing the default login page / functionality
        httpSecurity.formLogin(formLogin -> {
            formLogin.loginPage("/login");
            formLogin.loginProcessingUrl("/login-user");
            formLogin.successForwardUrl("/user/profile");
            formLogin.failureForwardUrl("/login?error=true");
            formLogin.defaultSuccessUrl("/user/profile");
            formLogin.usernameParameter("email");
            formLogin.passwordParameter("password");
        });

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        httpSecurity.logout(logoutForm -> { 
            logoutForm.logoutUrl("/logout");
            logoutForm.logoutSuccessUrl("/login?logout=true");
        });

        // :> oauth2 configuration
        
        // :> making oauth2 login as the default login method
        // httpSecurity.oauth2Login(Customizer.withDefaults());

        // :> setting up oauth2 login methods
        httpSecurity.oauth2Login(oauth -> { 
            oauth.loginPage("/login");
            oauth.successHandler(oAuthenticationSuccessHandler);
        });


        return httpSecurity.build();
    }

}
