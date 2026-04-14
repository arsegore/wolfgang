package wolfgang.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import wolfgang.config.DatabaseConfig;
import wolfgang.entities.User;
import wolfgang.repositories.UserRepository;
import wolfgang.utils.FlashMessageUtils;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        DatabaseConfig.init(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || username.isBlank()) {
            req.setAttribute("error", "Veuillez entrer un nom d'utilisateur");
            req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
            return;
        }
        if (password == null || password.isBlank()) {
            req.setAttribute("error", "Veuillez entrer un mot de passe");
            req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
            return;
        }

        User user = UserRepository.authenticate(username, password);
        if (user != null) {
            session.setAttribute("user", user);
            resp.sendRedirect(req.getContextPath() + "/");
        } else {
            FlashMessageUtils.setFlash(req, "error", "Nom d'utilisateur ou mot de passe invalide.");
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }
}
