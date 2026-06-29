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

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthenticationSuccessHandler.class);

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        LOGGER.info("OAuth2 authentication success handler invoked");

        // :> fetching the oauth2 client ?
        var oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String oAuthClientId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();

        LOGGER.info("OAuth2 client registration id={}", oAuthClientId);

        DefaultOAuth2User oAuthenticatedUser = (DefaultOAuth2User) authentication.getPrincipal();

        LOGGER.debug("OAuth2 principal attribute keys={}", oAuthenticatedUser.getAttributes().keySet());

        // // :> extracting email attrubute from [GOOGLE]
        String email = oAuthenticatedUser.getAttribute("email").toString(); // :> "email" attribute is common for both
                                                                            // the providers
        // String name = oAuthenticatedUser.getAttribute("name").toString();
        // String profilePicture =
        // oAuthenticatedUser.getAttribute("picture").toString();

        // // [testing] checking the extracted data
        // logger.info("fetched data {} {} {} : ", email, name, profilePicture);

        // :> check if user already exists ?
        User userWithSameEmail = userRepo.findByEmail(email).orElse(null);
        if (userWithSameEmail == null) {

            LOGGER.info("New OAuth2 user detected for email={}", email);

            // :> saving user to DB
            userService.saveOAuthenticatedUser(oAuthenticatedUser, oAuthClientId);

            LOGGER.info("OAuth2 user persisted successfully for email={}", email);

        } else {
            LOGGER.info("OAuth2 user already exists for email={}", email);

        }

        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");
    }

}
