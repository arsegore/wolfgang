package wolfgang.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wolfgang.config.DatabaseConfig;
import wolfgang.daos.CompositionDAO;
import wolfgang.daos.InstrumentDAO;
import wolfgang.daos.NoteDAO;
import wolfgang.daos.TrackDAO;
import wolfgang.models.Composition;
import wolfgang.models.Instrument;
import wolfgang.models.Note;
import wolfgang.models.Track;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/composition/import")
public class ImportServlet extends HttpServlet {
    private TrackDAO trackDAO;
    private NoteDAO noteDAO;
    private InstrumentDAO instrumentDAO;
    private CompositionDAO compositionDAO;

    @Override
    public void init() throws ServletException {
        DatabaseConfig.init(getServletContext());
        trackDAO = new TrackDAO();
        noteDAO = new NoteDAO();
        instrumentDAO = new InstrumentDAO();
        compositionDAO = new CompositionDAO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        int compositionId = Integer.parseInt(req.getParameter("id"));
        List<Instrument> instruments = instrumentDAO.findAll();
        Map<String, Integer> trackNameToIdMap = new HashMap<>();

        InputStreamReader isr = new InputStreamReader(req.getInputStream(), StandardCharsets.UTF_8); //
        BufferedReader entree = new BufferedReader(isr);

        try {
            String ligne;
            boolean isHeader = true;

            while ((ligne = entree.readLine()) != null) {
                if (ligne.isBlank()) continue;

                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] tokens = ligne.split(";");
                if (tokens.length < 2) continue;

                String typeDonnee = tokens[0];

                if ("TEMPO".equals(typeDonnee)) {
                    int importedTempo = Integer.parseInt(tokens[1]);
                    Composition comp = compositionDAO.findById(compositionId);
                    if (comp != null) {
                        comp.setTempo(importedTempo);
                        compositionDAO.update(comp);
                    }
                }else if ("PISTE".equals(typeDonnee)) {
                    String nomPiste = tokens[1];
                    String nomInstrument = tokens[2];
                    Instrument instrument = instruments.stream()
                            .filter(i -> i.getName().equalsIgnoreCase(nomInstrument))
                            .findFirst()
                            .orElse(!instruments.isEmpty() ? instruments.get(0) : null);

                    int currentPosition = trackDAO.findByComposition(compositionId).size();

                    Composition comp = new Composition();
                    comp.setId(compositionId);

                    Track newTrack = new Track(0, comp, nomPiste, instrument, "#4a9eff", currentPosition);
                    int newTrackId = trackDAO.create(newTrack);

                    if (newTrackId > 0) {
                        trackNameToIdMap.put(nomPiste, newTrackId);
                    }

                } else if ("NOTE".equals(typeDonnee) && tokens.length >= 7) {
                    String nomPiste = tokens[1];
                    Integer targetTrackId = trackNameToIdMap.get(nomPiste);

                    if (targetTrackId != null) {
                        Track track = new Track();
                        track.setId(targetTrackId);

                        Note note = new Note();
                        note.setTrack(track);
                        note.setPitch(Integer.parseInt(tokens[3]));
                        note.setStartBeat(Float.parseFloat(tokens[4]));
                        note.setDuration(Float.parseFloat(tokens[5]));
                        note.setVelocity(Integer.parseInt(tokens[6]));

                        noteDAO.create(note);
                    }
                }
            }

            entree.close();
            isr.close();

            resp.getWriter().write("{\"success\":true}");

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}