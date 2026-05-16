package wolfgang.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wolfgang.config.DatabaseConfig;
import wolfgang.daos.InstrumentDAO;
import wolfgang.daos.TrackDAO;
import wolfgang.models.Composition;
import wolfgang.models.Instrument;
import wolfgang.models.Track;

import java.io.IOException;
import java.util.List;

@WebServlet("/track")
public class TrackServlet extends HttpServlet {
    private TrackDAO trackDAO;
    private InstrumentDAO instrumentDAO;

    @Override
    public void init() throws ServletException {
        DatabaseConfig.init(getServletContext());
        trackDAO = new TrackDAO();
        instrumentDAO = new InstrumentDAO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String action = req.getParameter("action");
        if ("create".equals(action)) {
            handleCreate(req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"success\":false,\"error\":\"Unknown action\"}");
        }
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String compositionIdStr = req.getParameter("compositionId");
        String instrumentIdStr = req.getParameter("instrumentId");
        String color = req.getParameter("color");

        if (name == null || compositionIdStr == null || instrumentIdStr == null || color == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"success\":false,\"error\":\"Missing parameter\"}");
            return;
        }

        int compositionId = Integer.parseInt(compositionIdStr);
        int instrumentId = Integer.parseInt(instrumentIdStr);

        List<Track> existing = trackDAO.findByComposition(compositionId);
        int position = existing.size();

        Composition comp = new Composition();
        comp.setId(compositionId);
        Instrument inst = instrumentDAO.findById(instrumentId);

        Track track = new Track(0, comp, name, inst, color, position);
        int newId = trackDAO.create(track);

        if (newId < 0) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false,\"error\":\"DB error\"}");
            return;
        }

        JsonObject trackJson = new JsonObject();
        trackJson.addProperty("id", newId);
        trackJson.addProperty("name", name);
        trackJson.addProperty("color", color);
        trackJson.addProperty("instrument", inst != null ? inst.getName() : "");
        trackJson.add("notes", new JsonArray());

        JsonObject result = new JsonObject();
        result.addProperty("success", true);
        result.add("track", trackJson);
        resp.getWriter().write(result.toString());
    }
}
