package wolfgang.servlets.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wolfgang.config.DatabaseConfig;
import wolfgang.daos.CompositionDAO;
import wolfgang.models.Composition;
import wolfgang.models.User;
import wolfgang.utils.FlashMessageUtils;

import java.io.IOException;

@WebServlet("/admin/compositions/edit")
public class AdminCompositionsEditServlet extends HttpServlet {
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

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/admin/compositions");
            return;
        }

        Composition composition = compositionDAO.findById(Integer.parseInt(idParam));
        if (composition == null) {
            FlashMessageUtils.setFlash(req, "error", "Composition introuvable.");
            resp.sendRedirect(req.getContextPath() + "/admin/compositions");
            return;
        }

        req.setAttribute("composition", composition);
        req.setAttribute("adminSection", "compositions");
        req.getRequestDispatcher("/WEB-INF/admin/edit_composition.jsp").forward(req, resp);
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

        int id = Integer.parseInt(idParam);
        Composition composition = compositionDAO.findById(id);

        if (composition == null) {
            FlashMessageUtils.setFlash(req, "error", "Composition introuvable.");
            resp.sendRedirect(req.getContextPath() + "/admin/compositions");
            return;
        }

        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String tempoParam = req.getParameter("tempo");
        String accessType = req.getParameter("accessType");

        if (title == null || title.isBlank()) {
            FlashMessageUtils.setFlash(req, "error", "Le titre est obligatoire.");
            resp.sendRedirect(req.getContextPath() + "/admin/compositions/edit?id=" + id);
            return;
        }

        int tempo;
        try {
            tempo = Integer.parseInt(tempoParam);
        } catch (NumberFormatException e) {
            FlashMessageUtils.setFlash(req, "error", "Le tempo doit être un entier.");
            resp.sendRedirect(req.getContextPath() + "/admin/compositions/edit?id=" + id);
            return;
        }

        composition.setTitle(title);
        composition.setDescription(description);
        composition.setTempo(tempo);
        composition.setAccessType(accessType);

        if (compositionDAO.update(composition)) {
            FlashMessageUtils.setFlash(req, "success", "Composition mise à jour avec succès.");
        } else {
            FlashMessageUtils.setFlash(req, "error", "Erreur lors de la mise à jour de la composition.");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/compositions");
    }

    private boolean isAdmin(HttpServletRequest req) {
        User user = (User) req.getSession().getAttribute("user");
        return user != null && user.isAdmin();
    }
}
