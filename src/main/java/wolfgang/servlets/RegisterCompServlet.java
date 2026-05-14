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

@WebServlet("/comp")
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

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        User user = (User) session.getAttribute("user");
        String nameComp = req.getParameter("nameComp");
        int tempo = Integer.parseInt(req.getParameter("tempo"));
        String accessType = req.getParameter("accessType");

        if(nameComp == null || nameComp.isBlank()) {
            FlashMessageUtils.setFlash(req, "error", "Veuillez entrer un nom de composition");
            req.getRequestDispatcher("/WEB-INF/registerComp.jsp").forward(req, resp);
            return;
        }

        if(CompositionDAO.create(new Composition(
                nameComp,
                tempo,
                accessType,
                user
        ))){
            FlashMessageUtils.setFlash(req, "success", "Inscription réussie.");
            resp.sendRedirect(req.getContextPath() + "/login");
        } else {
            req.setAttribute("error", "Erreur d'inscription, veuillez réessayer.");
            req.getRequestDispatcher("/WEB-INF/register.jsp").forward(req, resp);
        }

    }


}


