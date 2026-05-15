package wolfgang.servlets.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wolfgang.config.DatabaseConfig;
import wolfgang.daos.UserDAO;
import wolfgang.models.User;
import wolfgang.utils.FlashMessageUtils;
import wolfgang.utils.PasswordUtils;

import java.io.IOException;

@WebServlet("/admin/users/edit")
public class AdminUsersEditServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        DatabaseConfig.init(getServletContext());
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        }

        User user = userDAO.findById(Integer.parseInt(idParam));
        if (user == null) {
            FlashMessageUtils.setFlash(req, "error", "Utilisateur introuvable.");
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        }

        req.setAttribute("editedUser", user);
        req.setAttribute("adminSection", "users");
        req.getRequestDispatcher("/WEB-INF/admin/edit_user.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        }

        int id = Integer.parseInt(idParam);
        User user = userDAO.findById(id);

        if (user == null) {
            FlashMessageUtils.setFlash(req, "error", "Utilisateur introuvable.");
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        }

        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String newPassword = req.getParameter("password");
        boolean isAdmin = "on".equals(req.getParameter("isAdmin"));

        if (username == null || username.isBlank() || email == null || email.isBlank()) {
            FlashMessageUtils.setFlash(req, "error", "Le nom d'utilisateur et l'email sont obligatoires.");
            resp.sendRedirect(req.getContextPath() + "/admin/users/edit?id=" + id);
            return;
        }

        user.setUsername(username);
        user.setEmail(email);
        user.setAdmin(isAdmin);

        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(PasswordUtils.hashPassword(newPassword));
        }

        if (userDAO.update(user)) {
            FlashMessageUtils.setFlash(req, "success", "Utilisateur mis à jour avec succès.");
        } else {
            FlashMessageUtils.setFlash(req, "error", "Erreur lors de la mise à jour de l'utilisateur.");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }

    private boolean isAdmin(HttpServletRequest req) {
        User user = (User) req.getSession().getAttribute("user");
        return user != null && user.isAdmin();
    }
}
