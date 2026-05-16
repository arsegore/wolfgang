package wolfgang.servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import wolfgang.config.DatabaseConfig;
import wolfgang.daos.FriendsDAO;
import wolfgang.daos.UserDAO;
import wolfgang.models.User;

@WebServlet("/friends/add")
public class AddFriendServlet extends HttpServlet {
    private FriendsDAO friendsDAO;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        DatabaseConfig.init(getServletContext());
        friendsDAO = new FriendsDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User connectedUser = (User) session.getAttribute("user");

        // Vérifie que l'utilisateur est connecté
        if (connectedUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String idParam = req.getParameter("id");

        // Vérifie qu'un id est fourni
        if (idParam == null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        int friendId;

        try {
            friendId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        // Empêche de s'ajouter soi-même
        if (connectedUser.getId() == friendId) {
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        User friend = userDAO.findById(friendId);

        // Vérifie que l'utilisateur existe
        if (friend == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Création de la demande d'ami
        boolean success = friendsDAO.demanderAmitie(connectedUser, friend);

        if (success) {
            session.setAttribute("success", "Demande d'ami envoyée à " + friend.getUsername() + ".");
        } else {
            session.setAttribute("error", "Impossible d'envoyer la demande d'ami.");
        }

        resp.sendRedirect(req.getContextPath() + "/profile?id=" + friend.getId());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        doGet(req, resp);
    }
}