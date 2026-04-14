package wolfgang.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class FlashMessageUtils {

    public static void setFlash(HttpServletRequest req, String type, String message) {
        req.getSession().setAttribute("flash_type", type);
        req.getSession().setAttribute("flash_message", message);
    }

    public static String getMessage(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;

        String message = (String) session.getAttribute("flash_message");
        session.removeAttribute("flash_message");
        session.removeAttribute("flash_type");
        return message;
    }

    public static String getType(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        return (String) session.getAttribute("flash_type");
    }
}
