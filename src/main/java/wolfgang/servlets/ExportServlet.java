package wolfgang.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wolfgang.daos.CompositionDAO;
import wolfgang.daos.NoteDAO;
import wolfgang.daos.TrackDAO;
import wolfgang.models.Composition;
import wolfgang.models.Note;
import wolfgang.models.Track;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/composition/export")
public class ExportServlet extends HttpServlet {
    private final CompositionDAO compositionDAO = new CompositionDAO();
    private final TrackDAO trackDAO = new TrackDAO();
    private final NoteDAO noteDAO = new NoteDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identifiant de composition manquant.");
            return;
        }

        int compositionId = Integer.parseInt(idStr);
        Composition comp = compositionDAO.findById(compositionId);
        if (comp == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Composition introuvable.");
            return;
        }

        List<Track> tracks = trackDAO.findByComposition(compositionId);

        String sanitizedTitle = comp.getTitle().replaceAll("[^a-zA-Z0-9\\-_]", "_");
        resp.setContentType("text/plain;charset=UTF-8"); // Fichier texte brut standard
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + sanitizedTitle + "_export.txt\"");

        try (PrintWriter out = resp.getWriter()) {
            // pour nous
            out.println("TYPE_DONNEE;NOM_PISTE;INSTRUMENT;HAUTEUR_NOTE;DEBUT_TEMPS;DUREE;VELOCITE");

            for (Track track : tracks) {
                String instName = (track.getInstrument() != null) ? track.getInstrument().getName() : "Inconnu";

                // Ligne de définition de la piste
                out.println("PISTE;" + track.getName() + ";" + instName + ";;;;");

                // Extraction des notes de cette piste
                List<Note> notes = noteDAO.findByTrack(track.getId());
                for (Note note : notes) {
                    out.println("NOTE;" + track.getName() + ";" + instName + ";"
                            + note.getPitch() + ";"
                            + note.getStartBeat() + ";"
                            + note.getDuration() + ";"
                            + note.getVelocity());
                }
            }
            out.flush();
        }
    }
}