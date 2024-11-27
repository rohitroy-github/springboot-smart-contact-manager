package springdev.scm.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import springdev.scm.entities.User;
import springdev.scm.repositories.UserRepo;
import springdev.scm.services.UserService;

import org.slf4j.LoggerFactory;

@Component
public class OAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    Logger logger = LoggerFactory.getLogger(OAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // :> [testing]
        logger.info("onAuthenticationSuccess_executing");

        // :> fetching the oauth2 client ?
        var oAuth2AuthenticationToken = (OAuth2AuthenticationToken)authentication;
        String oAuthClientId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
        // :> [testing]
        logger.info(":> oauth2 client : {}", oAuthClientId);

        DefaultOAuth2User oAuthenticatedUser = (DefaultOAuth2User) authentication.getPrincipal();


        // oAuthenticatedUser.getAttributes().forEach((key, value) -> { 
        //     logger.info(key + " : " + value);
        // });
        
        // // :> extracting email attrubute from
        String email = oAuthenticatedUser.getAttribute("email").toString();

        // String name = oAuthenticatedUser.getAttribute("name").toString();
        // String profilePicture =
        // oAuthenticatedUser.getAttribute("picture").toString();

        // // [testing] checking the extracted data
        // logger.info("fetched data {} {} {} : ", email, name, profilePicture);

        // :> check if user already exists ?
        User userWithSameEmail = userRepo.findByEmail(email).orElse(null);
        if (userWithSameEmail == null) {

            // :> saving the newly authenticated user to db
            User savedUser = userService.saveOAuthenticatedUser(oAuthenticatedUser, oAuthClientId);

            logger.info("User successfully saved in DB");

        } else {
            logger.info("User already exists in DB.");

        }

        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");
    }

}
