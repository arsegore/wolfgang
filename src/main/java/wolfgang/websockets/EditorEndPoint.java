package wolfgang.websockets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnError;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import wolfgang.daos.InstrumentDAO;
import wolfgang.daos.NoteDAO;
import wolfgang.daos.TrackDAO;
import wolfgang.models.Composition;
import wolfgang.models.Instrument;
import wolfgang.models.Note;
import wolfgang.models.Track;

import java.util.Hashtable;


@ServerEndpoint(value = "/editeur/{compositionId}")
public class EditorEndPoint {

    private static Hashtable<Integer, Hashtable<String, Session>> rooms = new Hashtable<>();

    private final NoteDAO noteDAO = new NoteDAO();
    private final TrackDAO trackDAO = new TrackDAO();
    private final InstrumentDAO instrumentDAO = new InstrumentDAO();
    private final Gson gson = new Gson();

    @OnOpen
    public void onOpen(Session session, @PathParam("compositionId") int compositionId) {
        session.getUserProperties().put("compositionId", compositionId);

        if (!rooms.containsKey(compositionId)) {
            rooms.put(compositionId, new Hashtable<>());
        }
        rooms.get(compositionId).put(session.getId(), session);
        System.out.println("Éditeur : connexion etablie par session " + session.getId() + " sur composition " + compositionId);
    }

    @OnClose
    public void onClose(Session session) {
        int compositionId = (int) session.getUserProperties().get("compositionId");

        if (rooms.containsKey(compositionId)) {
            rooms.get(compositionId).remove(session.getId());
            if (rooms.get(compositionId).isEmpty()) {
                rooms.remove(compositionId);
            }
        }
        System.out.println("Éditeur : connexion perdu pour la session " + session.getId());
    }

    @OnError
    public void OnError(Throwable error) {
        System.err.println("Erreur websocket éditeur : " + error);
        error.printStackTrace();
    }

    @OnMessage
    public void handleMessage(Session session, String message) {
        int compositionId = (int) session.getUserProperties().get("compositionId");

        try {
            JsonObject json = gson.fromJson(message, JsonObject.class);
            String action = json.get("action").getAsString();
            JsonObject data = json.getAsJsonObject("data");

            JsonObject response = new JsonObject();

            if ("ADD_NOTE".equals(action)) {
                handleNoteAdd(data, response);
            } else if ("DELETE_NOTE".equals(action)) {
                handleNoteDelete(data, response);
            } else if ("RESIZE_NOTE".equals(action)) {
                handleNoteResize(data, response);
            } else if ("CREATE_TRACK".equals(action)) {
                handleTrackCreate(data, compositionId, response);
            } else {
                response.addProperty("success", false);
                response.addProperty("error", "Unknown action");
            }

            if (response.has("success") && response.get("success").getAsBoolean()) {
                broadcast(compositionId, response.toString());
            } else {
                session.getBasicRemote().sendText(response.toString());
            }

        } catch (Exception e) {
            System.err.println("Erreur traitement message MIDI : " + e.getMessage());
        }
    }

    /**
     * Envoie le message à toutes les sessions connectées à la même composition
     */
    public void broadcast(int compositionId, String monMessage) {
        Hashtable<String, Session> roomSessions = rooms.get(compositionId);
        if (roomSessions != null) {
            for (Session session : roomSessions.values()) {
                try {
                    session.getBasicRemote().sendText(monMessage);
                } catch (Exception exception) {
                    System.err.println("Erreur d'envoie éditeur : " + exception.getMessage());
                }
            }
        }
    }

    private void handleNoteAdd(JsonObject data, JsonObject response) {
        Track track = new Track();
        track.setId(data.get("trackId").getAsInt());

        Note note = new Note();
        note.setTrack(track);
        note.setPitch(data.get("pitch").getAsInt());
        note.setStartBeat(data.get("startBeat").getAsFloat());
        note.setDuration(data.get("duration").getAsFloat());
        note.setVelocity(data.get("velocity").getAsInt());

        int newId = noteDAO.create(note);
        if (newId > 0) {
            response.addProperty("action", "NOTE_ADDED");
            response.addProperty("success", true);
            response.addProperty("id", newId);
            response.addProperty("trackId", track.getId());
            response.addProperty("pitch", note.getPitch());
            response.addProperty("startBeat", note.getStartBeat());
            response.addProperty("duration", note.getDuration());
            response.addProperty("velocity", note.getVelocity());
        } else {
            response.addProperty("success", false);
        }
    }

    private void handleNoteDelete(JsonObject data, JsonObject response) {
        int noteId = data.get("noteId").getAsInt();
        boolean ok = noteDAO.delete(noteId);

        response.addProperty("action", "NOTE_DELETED");
        response.addProperty("success", ok);
        response.addProperty("noteId", noteId);
    }

    private void handleNoteResize(JsonObject data, JsonObject response) {
        int noteId = data.get("noteId").getAsInt();
        float duration = Math.max(1f, data.get("duration").getAsFloat());
        boolean ok = noteDAO.updateDuration(noteId, duration);

        response.addProperty("action", "NOTE_RESIZED");
        response.addProperty("success", ok);
        response.addProperty("noteId", noteId);
        response.addProperty("duration", duration);
    }

    private void handleTrackCreate(JsonObject data, int compositionId, JsonObject response) {
        String name = data.get("name").getAsString();
        int instrumentId = data.get("instrumentId").getAsInt();
        String color = data.get("color").getAsString();

        int position = trackDAO.findByComposition(compositionId).size();

        Composition comp = new Composition();
        comp.setId(compositionId);
        Instrument inst = instrumentDAO.findById(instrumentId);

        Track track = new Track(0, comp, name, inst, color, position);
        int newId = trackDAO.create(track);

        if (newId > 0) {
            response.addProperty("action", "TRACK_CREATED");
            response.addProperty("success", true);

            JsonObject trackJson = new JsonObject();
            trackJson.addProperty("id",       newId);
            trackJson.addProperty("name",     name);
            trackJson.addProperty("color",    color);
            trackJson.addProperty("instrument", inst != null ? inst.getName() : "");
            trackJson.addProperty("waveType", inst != null ? inst.getWaveType() : "sine");
            trackJson.add("notes", new JsonArray());

            response.add("track", trackJson);
        } else {
            response.addProperty("success", false);
        }
    }
}
