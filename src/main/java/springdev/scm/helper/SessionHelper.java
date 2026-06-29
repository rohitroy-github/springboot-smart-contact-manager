package springdev.scm.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Component
public class SessionHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionHelper.class);

    // Removing message attribute from session storage
    public void removeMessage() {
        try {
            LOGGER.debug("Removing session message attribute");
            HttpSession session = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getRequest().getSession();
            session.removeAttribute("message");
        } catch (Exception e) {
            LOGGER.error("Failed to remove session message attribute", e);
        }
    }
}
