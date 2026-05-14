package wolfgang.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import wolfgang.config.DatabaseConfig;
import wolfgang.daos.CompositionDAO;
import wolfgang.daos.UserDAO;
import wolfgang.models.Composition;
import wolfgang.models.User;
import wolfgang.utils.FlashMessageUtils;

import java.io.IOException;

@WebServlet("/composition/create")
public class CreateCompositionServlet extends HttpServlet {
    private UserDAO userDAO;
    private CompositionDAO compositionDAO;

    public void init() throws ServletException {
        DatabaseConfig.init(getServletContext());
        userDAO = new UserDAO();
        compositionDAO = new CompositionDAO();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession();

        if (session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/create_composition.jsp").forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        User user = (User) session.getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String nameComp = req.getParameter("nameComp");
        String tempoParam = req.getParameter("tempo");
        String accessType = req.getParameter("accessType");

        if (nameComp == null || nameComp.isBlank()) {
            FlashMessageUtils.setFlash(req, "error", "Veuillez entrer un nom de composition.");
            req.getRequestDispatcher("/WEB-INF/create_composition.jsp").forward(req, resp);
            return;
        }
        if (accessType == null || accessType.isBlank()) {
            FlashMessageUtils.setFlash(req, "error", "Veuillez choisir un type d'accès.");
            req.getRequestDispatcher("/WEB-INF/create_composition.jsp").forward(req, resp);
            return;
        }

        int tempo;
        try {
            tempo = Integer.parseInt(tempoParam);
        } catch (NumberFormatException e) {
            FlashMessageUtils.setFlash(req, "error", "Tempo invalide.");
            req.getRequestDispatcher("/WEB-INF/create_composition.jsp").forward(req, resp);
            return;
        }

        if (compositionDAO.create(new Composition(nameComp, tempo, accessType, user))) {
            FlashMessageUtils.setFlash(req, "success", "Composition créée avec succès.");
            resp.sendRedirect(req.getContextPath() + "/home");
        } else {
            FlashMessageUtils.setFlash(req, "error", "Erreur lors de la création, veuillez réessayer.");
            req.getRequestDispatcher("/WEB-INF/create_composition.jsp").forward(req, resp);
        }
    }


}
