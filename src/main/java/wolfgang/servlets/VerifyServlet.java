package wolfgang.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wolfgang.daos.UserDAO;
import wolfgang.utils.FlashMessageUtils;

import java.io.IOException;

@WebServlet("/verify")
public class VerifyServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getParameter("token");

        if (token == null || token.isBlank()) {
            FlashMessageUtils.setFlash(req, "error", "Jeton de vérification manquant.");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (userDAO.verifyUser(token)) {
            FlashMessageUtils.setFlash(req, "success", "Votre compte a été vérifié avec succès ! Vous pouvez maintenant vous connecter.");
        } else {
            FlashMessageUtils.setFlash(req, "error", "Lien de vérification invalide ou expiré.");
        }

        resp.sendRedirect(req.getContextPath() + "/login");
    }
}