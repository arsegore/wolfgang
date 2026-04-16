package wolfgang.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import wolfgang.config.DatabaseConfig;
import wolfgang.repositories.UserRepository;
import wolfgang.utils.FlashMessageUtils;
import wolfgang.utils.PasswordUtils;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        DatabaseConfig.init(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        // si déjà connecté
        if (session.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (username == null || username.isBlank()) {
            FlashMessageUtils.setFlash(req, "error", "Nom d'utilisateur invalide.");
            req.getRequestDispatcher("/WEB-INF/register.jsp").forward(req, resp);
            return;
        }
        if (email == null || email.isBlank()) {
            FlashMessageUtils.setFlash(req, "error", "Email invalide.");
            req.getRequestDispatcher("/WEB-INF/register.jsp").forward(req, resp);
            return;
        }
        if (password == null || password.isBlank()) {
            FlashMessageUtils.setFlash(req, "error", "Mot de passe invalide.");
            req.getRequestDispatcher("/WEB-INF/register.jsp").forward(req, resp);
            return;
        }

        if (UserRepository.createUser(
                username,
                email,
                PasswordUtils.hashPassword(password)
        )) {
            FlashMessageUtils.setFlash(req, "success", "Inscription réussie.");
            resp.sendRedirect(req.getContextPath() + "/login");
        } else {
            req.setAttribute("error", "Erreur d'inscription, veuillez réessayer.");
            req.getRequestDispatcher("/WEB-INF/register.jsp").forward(req, resp);
        }
    }
}
