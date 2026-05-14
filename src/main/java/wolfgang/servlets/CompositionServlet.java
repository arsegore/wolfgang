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

@WebServlet("/composition")
public class CompositionServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        DatabaseConfig.init(getServletContext());
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
        Composition comp = CompositionDAO.findById(id);

        if (comp == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        switch (comp.getAccessType()) {
            case "private" -> {
                User u = (User) session.getAttribute("user");
                if (!comp.getOwner().equals(u)) {
                    resp.sendRedirect(req.getContextPath() + "/home");
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

        req.setAttribute("composition", comp);
        req.getRequestDispatcher("/WEB-INF/composition.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
