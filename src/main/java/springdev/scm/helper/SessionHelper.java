package springdev.scm.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;
import springdev.scm.controllers.ContactController;

@Component
public class SessionHelper {

    private Logger logger = LoggerFactory.getLogger(ContactController.class);

    // Removing message attribute from session storage
    public void removeMessage() {
        try {
            logger.info(":> removing_session_message");
            HttpSession session = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getRequest().getSession();
            session.removeAttribute("message");
        } catch (Exception e) {
            logger.info(":> error_SessionHelper : {}", e.getMessage());

            e.printStackTrace();
        }
    }
}
