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

import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    UserDAO userDAO;
    CompositionDAO compositionDAO;

    @Override
    public void init() throws ServletException {
        DatabaseConfig.init(getServletContext());
        userDAO = new UserDAO();
        compositionDAO = new CompositionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        List<Composition> myLastCompo;
        List<Composition> friendsLastCompo;
        List<Composition> publicLastCompo;

        // Dernières compositions publiques
        publicLastCompo = compositionDAO.findPublic(6);
        req.setAttribute("publicLastCompo", publicLastCompo);

        User user = (User) session.getAttribute("user");

        if (user == null) {
            req.getRequestDispatcher("/WEB-INF/home.jsp").forward(req, resp);
            return;
        }

        // Dernière compo de l'utilisateur
        myLastCompo = compositionDAO.findByUser(user.getId(), 1);
        req.setAttribute("myLastCompo", myLastCompo.getFirst());

        // Dernières compos des amis de l'utilisateur
        if (user.getFriends() != null && !user.getFriends().isEmpty()) {
            friendsLastCompo = compositionDAO.findFriendsComposition(user.getFriends(), 2);
            req.setAttribute("friendsLastCompo", friendsLastCompo);
        }

        // Redirection
        req.getRequestDispatcher("/WEB-INF/home.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
