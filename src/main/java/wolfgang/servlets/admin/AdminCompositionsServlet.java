package wolfgang.servlets.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wolfgang.config.DatabaseConfig;
import wolfgang.daos.CompositionDAO;
import wolfgang.models.User;
import wolfgang.utils.FlashMessageUtils;

import java.io.IOException;

@WebServlet("/admin/compositions")
public class AdminCompositionsServlet extends HttpServlet {
    private CompositionDAO compositionDAO;

    @Override
    public void init() throws ServletException {
        DatabaseConfig.init(getServletContext());
        compositionDAO = new CompositionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        req.setAttribute("compositions", compositionDAO.findAll());
        req.setAttribute("adminSection", "compositions");
        req.getRequestDispatcher("/WEB-INF/admin/compositions.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/admin/compositions");
            return;
        }

        if (compositionDAO.delete(Integer.parseInt(idParam))) {
            FlashMessageUtils.setFlash(req, "success", "Composition supprimée avec succès.");
        } else {
            FlashMessageUtils.setFlash(req, "error", "Erreur lors de la suppression de la composition.");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/compositions");
    }

    private boolean isAdmin(HttpServletRequest req) {
        User user = (User) req.getSession().getAttribute("user");
        return user != null && user.isAdmin();
    }
}
