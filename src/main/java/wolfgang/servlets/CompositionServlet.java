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
import wolfgang.models.Composition;
import wolfgang.models.Instrument;
import wolfgang.models.Note;
import wolfgang.models.Track;
import wolfgang.models.User;

import java.io.IOException;
import java.util.List;

@WebServlet("/composition")
public class CompositionServlet extends HttpServlet {
    private CompositionDAO compositionDAO;
    private TrackDAO trackDAO;
    private NoteDAO noteDAO;
    private InstrumentDAO instrumentDAO;

    @Override
    public void init() throws ServletException {
        DatabaseConfig.init(getServletContext());
        compositionDAO = new CompositionDAO();
        trackDAO = new TrackDAO();
        noteDAO = new NoteDAO();
        instrumentDAO = new InstrumentDAO();
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
        Composition comp = compositionDAO.findById(id);

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
            case "public" -> {}
            case "link" -> {}
        }

        List<Track> tracks = trackDAO.findByComposition(id);

        req.setAttribute("composition", comp);
        req.setAttribute("tracksJson", buildTracksJson(tracks));
        req.setAttribute("instrumentsJson", buildInstrumentsJson(instrumentDAO.findAll()));
        req.getRequestDispatcher("/WEB-INF/composition.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private String buildTracksJson(List<Track> tracks) {
        JsonArray tracksArray = new JsonArray();

        for (Track t : tracks) {
            JsonObject trackObj = new JsonObject();
            trackObj.addProperty("id", t.getId());
            trackObj.addProperty("name", t.getName());
            trackObj.addProperty("color", t.getColor() != null ? t.getColor() : "#4a9eff");
            trackObj.addProperty("instrument", t.getInstrument() != null ? t.getInstrument().getName() : "");

            JsonArray notesArray = new JsonArray();
            for (Note n : noteDAO.findByTrack(t.getId())) {
                JsonObject noteObj = new JsonObject();
                noteObj.addProperty("id", n.getId());
                noteObj.addProperty("pitch", n.getPitch());
                noteObj.addProperty("startBeat", n.getStartBeat());
                noteObj.addProperty("duration", n.getDuration());
                noteObj.addProperty("velocity", n.getVelocity());
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
            obj.addProperty("id", i.getId());
            obj.addProperty("name", i.getName());
            arr.add(obj);
        }
        return arr.toString();
    }
}
