package springdev.scm.helper;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

public class Helper {

    public static String getEmailOfLoggedInUser(Authentication authentication) {

        // AuthenticationPrincipal principal = (AuthenticationPrincipal)
        // authentication.getPrincipal();

        if (authentication instanceof OAuth2AuthenticationToken) {
            // :> Oauth provider logins

            String fetchedUserEmail = "";

            String authClient = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
            DefaultOAuth2User oAuthenticatedUser = (DefaultOAuth2User) authentication.getPrincipal();

            if (authClient.equalsIgnoreCase("google")) {

                fetchedUserEmail = oAuthenticatedUser.getAttribute("email").toString();

            } else if (authClient.equalsIgnoreCase("github")) {

                fetchedUserEmail = oAuthenticatedUser.getAttribute("email").toString();

            }

            return fetchedUserEmail;

        } else {
            // :> SELF provider logins

            return authentication.getName();

        }

    }

}
