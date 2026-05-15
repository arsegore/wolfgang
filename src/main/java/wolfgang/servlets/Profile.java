package wolfgang.servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import wolfgang.config.DatabaseConfig;
import wolfgang.daos.UserDAO;
import wolfgang.models.User;

@WebServlet("/profile")
public class Profile extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        DatabaseConfig.init(getServletContext());
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User connectedUser = (User) session.getAttribute("user");

        if (connectedUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String idParam = req.getParameter("id");
        User profileUser;

        // Aucun id = notre propre profil
        if (idParam == null) {
            profileUser = connectedUser;
        } else {
            int profileId = Integer.parseInt(idParam);
            profileUser = userDAO.findById(profileId);
            if (profileUser == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        // Vérifie si c'est son propre profil
        boolean isOwnProfile = (connectedUser.getId() == profileUser.getId());

        req.setAttribute("user", profileUser);
        req.setAttribute("isOwnProfile", isOwnProfile);

        req.getRequestDispatcher("/WEB-INF/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}