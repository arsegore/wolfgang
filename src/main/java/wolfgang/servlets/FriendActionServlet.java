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

@WebServlet("/friends/action")
public class FriendActionServlet extends HttpServlet {
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
        String action = req.getParameter("action");

        // Vérifie qu'un id et une action sont fournis
        if (idParam == null || action == null) {
            resp.sendRedirect(req.getContextPath() + "/friends");
            return;
        }

        int friendId;

        try {
            friendId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/friends");
            return;
        }

        // Empêche d'agir sur soi-même
        if (connectedUser.getId() == friendId) {
            resp.sendRedirect(req.getContextPath() + "/friends");
            return;
        }

        User friend = userDAO.findById(friendId);

        // Vérifie que l'utilisateur existe
        if (friend == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        boolean success = false;

        switch (action) {

            case "add":
                success = friendsDAO.demanderAmitie(connectedUser, friend);
                if (success) {
                    session.setAttribute("success", "Demande d'ami envoyée à " + friend.getUsername() + ".");
                } else {
                    session.setAttribute("error", "Impossible d'envoyer la demande d'ami.");
                }
                break;

            case "cancel":
                success = friendsDAO.supprimerDemande(connectedUser, friend);
                if (success) {
                    session.setAttribute("success", "Demande d'ami supprimée avec " + friend.getUsername() + ".");
                } else {
                    session.setAttribute("error", "Impossible de supprimer la demande d'ami.");
                }
                break;

            case "accept":
                success = friendsDAO.ajouterAmitie(friend, connectedUser);
                if (success) {
                    session.setAttribute("success", "Vous êtes maintenant ami avec " + friend.getUsername() + ".");
                } else {
                    session.setAttribute("error", "Impossible d'accepter la demande.");
                }
                break;

            case "refuse":
                success = friendsDAO.supprimerDemande(friend, connectedUser);
                if (success) {
                    session.setAttribute("success", "Vous avez refusé le demande d'ami de " + friend.getUsername() + ".");
                } else {
                    session.setAttribute("error", "Impossible de refuser la demande.");
                }
                break;

            case "delete":
                success = friendsDAO.supprimerAmitie(connectedUser, friend);
                if (success) {
                    session.setAttribute("success", "Vous n'êtes maintenant plus ami avec " + friend.getUsername() + ".");
                } else {
                    session.setAttribute("error", "Impossible de supprimer la relation.");
                }
                break;

            default:
                session.setAttribute("error", "Action inconnue.");
                break;
        }

        // si sur profil, on reste sur profil / si sur liste d'amis, on reste sur liste d'amis
        String referer = req.getHeader("Referer");
        if (referer != null) {
            resp.sendRedirect(referer);
        } else {
            resp.sendRedirect(req.getContextPath() + "/friends");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}