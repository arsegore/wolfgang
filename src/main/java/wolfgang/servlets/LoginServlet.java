package wolfgang.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import wolfgang.config.DatabaseConfig;
import wolfgang.models.User;
import wolfgang.daos.UserDAO;
import wolfgang.utils.FlashMessageUtils;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        DatabaseConfig.init(getServletContext());
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        // si déjà connecté
        if (session.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || username.isBlank()) {
            FlashMessageUtils.setFlash(req, "error", "Veuillez entrer un nom d'utilisateur");
            req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
            return;
        }
        if (password == null || password.isBlank()) {
            FlashMessageUtils.setFlash(req, "error", "Veuillez entrer un mot de passe");
            req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
            return;
        }

        User user = userDAO.authenticate(username, password);
        if (user != null) {
            session.setAttribute("user", user);
            resp.sendRedirect(req.getContextPath() + "/home");
        } else {
            FlashMessageUtils.setFlash(req, "error", "Nom d'utilisateur ou mot de passe invalide.");
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }
}
