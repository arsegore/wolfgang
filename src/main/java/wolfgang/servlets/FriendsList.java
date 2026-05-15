package wolfgang.servlets;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import wolfgang.config.DatabaseConfig;
import wolfgang.daos.FriendsDAO;
import wolfgang.daos.UserDAO;
import wolfgang.models.Friendship;
import wolfgang.models.User;

@WebServlet("/friends")
public class FriendsList extends HttpServlet {
    private UserDAO userDAO;
    private FriendsDAO friendsDAO;

    @Override
    public void init() throws ServletException {
        DatabaseConfig.init(getServletContext());
        userDAO = new UserDAO();
        friendsDAO = new FriendsDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        List<Friendship> myFriends;
        User user = (User) session.getAttribute("user");

        if(user != null) {
            myFriends = friendsDAO.findFriends(user);
            req.setAttribute("myFriends", myFriends);
        }

        req.getRequestDispatcher("/WEB-INF/friends_list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}