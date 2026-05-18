package wolfgang.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import wolfgang.config.DatabaseConfig;
import wolfgang.daos.CompositionDAO;
import wolfgang.daos.InstrumentDAO;
import wolfgang.daos.NoteDAO;
import wolfgang.daos.TrackDAO;
import wolfgang.daos.UserDAO;
import wolfgang.models.Composition;
import wolfgang.models.Instrument;
import wolfgang.models.Note;
import wolfgang.models.Track;
import wolfgang.models.User;
import wolfgang.utils.FlashMessageUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/composition/view")
public class CompositionServlet extends HttpServlet {
    private CompositionDAO compositionDAO;
    private TrackDAO trackDAO;
    private NoteDAO noteDAO;
    private InstrumentDAO instrumentDAO;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        DatabaseConfig.init(getServletContext());
        compositionDAO = new CompositionDAO();
        trackDAO       = new TrackDAO();
        noteDAO        = new NoteDAO();
        instrumentDAO  = new InstrumentDAO();
        userDAO        = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession();
        User u = (User) session.getAttribute("user");

        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        int id = Integer.parseInt(idParam);
        Composition comp = compositionDAO.findById(id);

        if (comp == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // chargement des membres de la compo
        Map<User, String> members = compositionDAO.findMembers(comp.getId());
        members.forEach((member, role) -> comp.addMember(member, role));

        // rôle de l'utilisateur courant
        String userRole = resolveRole(u, comp, members);

        // vérification des perms
        switch (comp.getAccessType()) {
            case "private" -> {
                if (!"owner".equals(userRole) && !"editor".equals(userRole) && !"viewer".equals(userRole)) {
                    resp.sendRedirect(req.getContextPath() + "/composition/list");
                    return;
                }
            }
            case "public", "link" -> {}
        }

        boolean canEdit = "owner".equals(userRole)
                || "editor".equals(userRole)
                || (comp.isPublicEditable() && "public".equals(comp.getAccessType()));

        List<Track> tracks = trackDAO.findByComposition(id);

        req.setAttribute("composition",    comp);
        req.setAttribute("userRole",       userRole);
        req.setAttribute("canEdit",        canEdit);
        req.setAttribute("tracksJson",     buildTracksJson(tracks));
        req.setAttribute("instrumentsJson", buildInstrumentsJson(instrumentDAO.findAll()));
        req.getRequestDispatcher("/WEB-INF/composition.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User u = (User) session.getAttribute("user");

        int id = Integer.parseInt(req.getParameter("id"));
        Composition comp = compositionDAO.findById(id);

        if (comp == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Seul le propriétaire peut poster des modifications
        if (u == null || comp.getOwner().getId() != u.getId()) {
            resp.sendRedirect(req.getContextPath() + "/composition/view?id=" + id);
            return;
        }

        String action = req.getParameter("action");

        switch (action) {
            case "updateAccess" -> {
                String accessType = req.getParameter("accessType");
                if (!accessType.equals("public") && !accessType.equals("private") && !accessType.equals("link")) {
                    resp.sendRedirect(req.getContextPath() + "/composition/view?id=" + id);
                    return;
                }
                comp.setAccessType(accessType);
            }
            case "updateDescription" -> comp.setDescription(req.getParameter("description"));
            case "updateTempo" -> {
                String paramTempo = req.getParameter("tempo");
                if (paramTempo == null) {
                    resp.sendRedirect(req.getContextPath() + "/composition/view?id=" + id);
                    return;
                }
                try {
                    int tempo = Integer.parseInt(paramTempo);
                    if (tempo < 20) tempo = 20;
                    comp.setTempo(tempo);
                } catch (NumberFormatException e) {
                    resp.sendRedirect(req.getContextPath() + "/composition/view?id=" + id);
                    return;
                }
            }
            case "updatePublicEditable" -> {
                comp.setPublicEditable("1".equals(req.getParameter("publicEditable")));
            }
            case "addMember" -> {
                String username = req.getParameter("username");
                String role     = req.getParameter("role");

                if (username == null || username.isBlank()) {
                    FlashMessageUtils.setFlash(req, "error", "Pseudo manquant.");
                    resp.sendRedirect(req.getContextPath() + "/composition/view?id=" + id);
                    return;
                }
                if (!"editor".equals(role) && !"viewer".equals(role)) role = "viewer";

                User target = userDAO.findByUsername(username.trim());
                if (target == null) {
                    FlashMessageUtils.setFlash(req, "error", "Utilisateur « " + username.trim() + " » introuvable.");
                    resp.sendRedirect(req.getContextPath() + "/composition/view?id=" + id);
                    return;
                }
                if (target.getId() == comp.getOwner().getId()) {
                    FlashMessageUtils.setFlash(req, "error", "Le propriétaire est déjà présent.");
                    resp.sendRedirect(req.getContextPath() + "/composition/view?id=" + id);
                    return;
                }

                compositionDAO.addMember(id, target.getId(), role);
                resp.sendRedirect(req.getContextPath() + "/composition/view?id=" + id);
                return;
            }
            case "removeMember" -> {
                String userIdParam = req.getParameter("userId");
                if (userIdParam != null) {
                    compositionDAO.removeMember(id, Integer.parseInt(userIdParam));
                }
                resp.sendRedirect(req.getContextPath() + "/composition/view?id=" + id);
                return;
            }
            default -> {
                resp.sendRedirect(req.getContextPath() + "/composition/view?id=" + id);
                return;
            }
        }

        compositionDAO.update(comp);
        resp.sendRedirect(req.getContextPath() + "/composition/view?id=" + id);
    }

    private String resolveRole(User u, Composition comp, Map<User, String> members) {
        if (u == null) return "none";
        if (comp.getOwner().getId() == u.getId()) return "owner";
        for (Map.Entry<User, String> entry : members.entrySet()) {
            if (entry.getKey().getId() == u.getId()) return entry.getValue();
        }
        return "none";
    }

    private String buildTracksJson(List<Track> tracks) {
        JsonArray tracksArray = new JsonArray();
        for (Track t : tracks) {
            JsonObject trackObj = new JsonObject();
            trackObj.addProperty("id",         t.getId());
            trackObj.addProperty("name",       t.getName());
            trackObj.addProperty("color",      t.getColor() != null ? t.getColor() : "#4a9eff");
            trackObj.addProperty("instrument", t.getInstrument() != null ? t.getInstrument().getName() : "");
            trackObj.addProperty("waveType",   t.getInstrument() != null ? t.getInstrument().getWaveType() : "sine");

            JsonArray notesArray = new JsonArray();
            for (Note n : noteDAO.findByTrack(t.getId())) {
                JsonObject noteObj = new JsonObject();
                noteObj.addProperty("id",        n.getId());
                noteObj.addProperty("pitch",     n.getPitch());
                noteObj.addProperty("startBeat", n.getStartBeat());
                noteObj.addProperty("duration",  n.getDuration());
                noteObj.addProperty("velocity",  n.getVelocity());
                notesArray.add(noteObj);
            }
            trackObj.add("notes", notesArray);
            tracksArray.add(trackObj);
        }
        return tracksArray.toString();
    }

    private String buildInstrumentsJson(List<Instrument> instruments) {
        JsonArray arr = new JsonArray();
        for (Instrument i : instruments) {
            JsonObject obj = new JsonObject();
            obj.addProperty("id",       i.getId());
            obj.addProperty("name",     i.getName());
            obj.addProperty("waveType", i.getWaveType());
            arr.add(obj);
        }
        return arr.toString();
    }
}
