package wolfgang.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import wolfgang.config.DatabaseConfig;
import wolfgang.daos.UserDAO;

import java.io.IOException;

@WebServlet("./registerComp")
public class RegisterCompServlet extends HttpServlet {
    private UserDAO userDAO;

    public void init() throws ServletException {
        DatabaseConfig.init(getServletContext());
        userDAO = new UserDAO();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession();

        // si utilisateur pas connecté
        if(session.getAttribute("user") == null){
            resp.sendRedirect("LoginServlet");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/registerComp.jsp").forward(req, resp);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response){

    }


}
