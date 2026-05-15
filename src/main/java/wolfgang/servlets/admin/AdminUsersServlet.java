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

import java.io.IOException;

@WebServlet("/admin/users")
public class AdminUsersServlet extends HttpServlet {
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

        req.setAttribute("users", userDAO.findAll());
        req.setAttribute("adminSection", "users");
        req.getRequestDispatcher("/WEB-INF/admin/users.jsp").forward(req, resp);
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
        User currentUser = (User) req.getSession().getAttribute("user");

        if (currentUser.getId() == id) {
            FlashMessageUtils.setFlash(req, "error", "Vous ne pouvez pas supprimer votre propre compte.");
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        }

        if (userDAO.delete(id)) {
            FlashMessageUtils.setFlash(req, "success", "Utilisateur supprimé avec succès.");
        } else {
            FlashMessageUtils.setFlash(req, "error", "Erreur lors de la suppression de l'utilisateur.");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }

    private boolean isAdmin(HttpServletRequest req) {
        User user = (User) req.getSession().getAttribute("user");
        return user != null && user.isAdmin();
    }
}
