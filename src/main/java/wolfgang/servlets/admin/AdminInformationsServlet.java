package wolfgang.servlets.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wolfgang.config.DatabaseConfig;
import wolfgang.daos.InformationDAO;
import wolfgang.models.User;
import wolfgang.utils.FlashMessageUtils;

import java.io.IOException;

@WebServlet("/admin/informations")
public class AdminInformationsServlet extends HttpServlet {
    private InformationDAO informationDAO;

    @Override
    public void init() throws ServletException {
        DatabaseConfig.init(getServletContext());
        informationDAO = new InformationDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) {
            resp.sendRedirect(req.getContextPath()+"/");
            return;
        }

        req.setAttribute("informations", informationDAO.findAll());
        req.setAttribute("adminSection", "informations");
        req.getRequestDispatcher("/WEB-INF/admin/informations.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) {
            resp.sendRedirect(req.getContextPath()+"/");
            return;
        }

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            resp.sendRedirect(req.getContextPath()+"/admin/informations");
            return;
        }

        if (informationDAO.delete(Integer.parseInt(idParam))) {
            FlashMessageUtils.setFlash(req, "success", "Actualité supprimée avec succès.");
        } else {
            FlashMessageUtils.setFlash(req, "error", "Erreur lors de la suppression de l'actualité.");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/informations");
    }

    private boolean isAdmin(HttpServletRequest req) {
        User user = (User) req.getSession().getAttribute("user");
        return user != null && user.isAdmin();
    }
}
