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
import wolfgang.daos.CompositionDAO;
import wolfgang.daos.UserDAO;
import wolfgang.models.Composition;
import wolfgang.models.User;

@WebServlet("/composition/list")
public class ListCompositionServlet extends HttpServlet {
    private UserDAO userDAO;
    private CompositionDAO compositionDAO;

    public void init() throws ServletException {
        DatabaseConfig.init(getServletContext());
        userDAO = new UserDAO();
        compositionDAO = new CompositionDAO();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        List<Composition> myCompositions;
        List<Composition> memberCompositions;
        List<Composition> publicCompositions;

        User user = (User) session.getAttribute("user");

        if(user != null) {
            myCompositions = compositionDAO.findByUser(user.getId());
            //MemberCompositions =
            req.setAttribute("myCompositions", myCompositions);
        }
        publicCompositions = compositionDAO.findPublic();
        req.setAttribute("publicCompositions", publicCompositions);

        getServletContext().getRequestDispatcher("/WEB-INF/list_compositions.jsp").forward(req,resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
