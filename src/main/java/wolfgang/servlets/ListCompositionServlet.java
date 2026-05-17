package wolfgang.servlets;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import wolfgang.config.DatabaseConfig;
import wolfgang.daos.CompositionDAO;
import wolfgang.models.Composition;
import wolfgang.models.User;

@WebServlet("/composition/list")
public class ListCompositionServlet extends HttpServlet {
    private CompositionDAO compositionDAO;

    public void init() throws ServletException {
        DatabaseConfig.init(getServletContext());
        compositionDAO = new CompositionDAO();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        List<Composition> publicCompositions = compositionDAO.findPublic();

        if (user != null) {
            List<Composition> myCompositions          = compositionDAO.findOwned(user.getId());
            Map<Composition, String> memberCompositions = compositionDAO.findMemberships(user.getId());

            // exclure des compositions publiques celles que l'utilisateur possède ou rejoint déjà
            Set<Integer> knownIds = new HashSet<>();
            myCompositions.forEach(c -> knownIds.add(c.getId()));
            memberCompositions.keySet().forEach(c -> knownIds.add(c.getId()));
            publicCompositions.removeIf(c -> knownIds.contains(c.getId()));

            req.setAttribute("myCompositions",     myCompositions);
            req.setAttribute("memberCompositions", memberCompositions);
        }

        req.setAttribute("publicCompositions", publicCompositions);
        getServletContext().getRequestDispatcher("/WEB-INF/list_compositions.jsp").forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
