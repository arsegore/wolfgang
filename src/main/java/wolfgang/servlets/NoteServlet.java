package wolfgang.servlets;

import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wolfgang.config.DatabaseConfig;
import wolfgang.daos.NoteDAO;
import wolfgang.models.Note;
import wolfgang.models.Track;

import java.io.IOException;

@WebServlet("/note")
public class NoteServlet extends HttpServlet {
    private NoteDAO noteDAO;

    @Override
    public void init() throws ServletException {
        DatabaseConfig.init(getServletContext());
        noteDAO = new NoteDAO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String action = req.getParameter("action");
        if ("add".equals(action)) {
            handleAdd(req, resp);
        } else if ("delete".equals(action)) {
            handleDelete(req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"success\":false,\"error\":\"Unknown action\"}");
        }
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String trackIdStr = req.getParameter("trackId");
        String pitchStr = req.getParameter("pitch");
        String startBeatStr = req.getParameter("startBeat");
        String durationStr = req.getParameter("duration");
        String velocityStr = req.getParameter("velocity");

        if (trackIdStr == null || pitchStr == null || startBeatStr == null || durationStr == null || velocityStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"success\":false,\"error\":\"Missing parameter\"}");
            return;
        }

        Track track = new Track();
        track.setId(Integer.parseInt(trackIdStr));

        Note note = new Note();
        note.setTrack(track);
        note.setPitch(Integer.parseInt(pitchStr));
        note.setStartBeat(Float.parseFloat(startBeatStr));
        note.setDuration(Float.parseFloat(durationStr));
        note.setVelocity(Integer.parseInt(velocityStr));

        int newId = noteDAO.create(note);

        if (newId < 0) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false,\"error\":\"DB error\"}");
            return;
        }

        JsonObject result = new JsonObject();
        result.addProperty("success", true);
        result.addProperty("id", newId);
        resp.getWriter().write(result.toString());
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String noteIdStr = req.getParameter("noteId");

        if (noteIdStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"success\":false,\"error\":\"Missing noteId\"}");
            return;
        }

        boolean ok = noteDAO.delete(Integer.parseInt(noteIdStr));
        JsonObject result = new JsonObject();
        result.addProperty("success", ok);
        resp.getWriter().write(result.toString());
    }
}
