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
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/composition/view")
public class CompositionServlet extends HttpServlet {
    private CompositionDAO compositionDAO;

    @Override
    public void init() throws ServletException {
        DatabaseConfig.init(getServletContext());
        compositionDAO = new CompositionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession();

        String idParam = req.getParameter("id");

        if (idParam == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        int id = Integer.parseInt(idParam);
        Composition comp = compositionDAO.findById(id);

        if (comp == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Vérification de l'utilisateur et du type d'accès
        switch (comp.getAccessType()) {
            case "private" -> {

                /*
                 * Ajouter le cas ou c'est un collaborateur : doit quand meme pouvoir acceder
                 * a la page
                 */

                User u = (User) session.getAttribute("user");
                if (!comp.getOwner().equals(u)) {
                    resp.sendRedirect(req.getContextPath() + "/composition/display");
                    return;
                }
            }
            case "public" -> {
                System.out.println("public");
            }
            case "link" -> {
                System.out.println("link");
            }
        }

        // Redirection
        req.setAttribute("composition", comp);
        req.getRequestDispatcher("/WEB-INF/composition.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        // Récupération de la composition
        int id = Integer.parseInt(req.getParameter("id"));
        Composition comp = compositionDAO.findById(id);

        if (comp == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Seul le propriétaire peut faire des modifications
        User u = (User) session.getAttribute("user");
        if (!comp.getOwner().equals(u)) {
            resp.sendRedirect(req.getContextPath()+"/composition/view?id="+id);
            return;
        }

        // Récupération et vérification des paramètres
        String action = req.getParameter("action");

        switch(action) {
            case "updateAccess" -> {
                String accessType = req.getParameter("accessType");
                if (!accessType.equals("public") && !accessType.equals("private") && !accessType.equals("link")) {
                    resp.sendRedirect(req.getContextPath()+"/composition/view?id="+id);
                    return;
                }
                comp.setAccessType(accessType);
            }
            case "updateDescription" -> {
                String description = req.getParameter("description");
                comp.setDescription(description);
            }
            case "updateTempo" -> {
                String paramTempo = req.getParameter("tempo");
                if (paramTempo == null) {
                    resp.sendRedirect(req.getContextPath()+"/composition/view?id="+id);
                    return;
                }
                try {
                    int tempo = Integer.parseInt(paramTempo);
                    if (tempo < 20) tempo = 20;
                    comp.setTempo(tempo);
                } catch (NumberFormatException e) {
                    resp.sendRedirect(req.getContextPath()+"/composition/view?id="+id);
                    return;
                }
            }
            default -> {
                resp.sendRedirect(req.getContextPath()+"/composition/view?id="+id);
                return;
            }
        }

        // Mise à jour de la composition
        compositionDAO.update(comp);

        // Redirection
        resp.sendRedirect(req.getContextPath()+"/composition/view?id="+id);
    }
}
