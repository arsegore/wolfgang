package wolfgang.websockets;

import jakarta.websocket.Session;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.util.Hashtable;

@ServerEndpoint(value="/chat/{compositionId}/{pseudo}")
public class ChatEndPoint {
    private static ChatEndPoint instance = new ChatEndPoint();

    private static Hashtable<Integer, Hashtable<String, Session>> rooms = new Hashtable<>();

    public void ChatEndPoint(){};

    public ChatEndPoint getInstance(){ return instance;}

    @OnOpen
    public void onOpen(Session session, @PathParam("compositionId") int compositionId, @PathParam("pseudo") String pseudo){
        session.getUserProperties().put("compositionId", compositionId);
        session.getUserProperties().put("pseudo", pseudo);

        if (!rooms.containsKey(compositionId)) {
            rooms.put(compositionId, new Hashtable<>());
        }

        rooms.get(compositionId).put(pseudo, session);
        sendMessage(compositionId, "Admin : connexion etablie par " + pseudo);
    }

    @OnClose
    public void onClose(Session session){
        Integer compositionId = (Integer) session.getUserProperties().get("compositionId");
        String pseudo = (String) session.getUserProperties().get("pseudo");

        if (compositionId != null && rooms.containsKey(compositionId)) {
            rooms.get(compositionId).remove(pseudo);

            if (rooms.get(compositionId).isEmpty()) {
                rooms.remove(compositionId);
            }
        }
        sendMessage(compositionId, "Admin : connexion perdu " + pseudo);
    }

    @OnError
    public void OnError(Throwable error){
        System.err.println("Erreur websocket chat : " + error);
        error.printStackTrace();
    }

    @OnMessage
    public void handleMessage(Session session, String message){
        int compositionId = (int) session.getUserProperties().get("compositionId");
        String pseudo = (String) session.getUserProperties().get("pseudo");
        String monMessage = pseudo + " : " + message;

        sendMessage(compositionId, monMessage);
    }

    public void sendMessage(int compositionId, String monMessage) {
        System.out.println("Compo #" + compositionId + " - " + monMessage);

        Hashtable<String, Session> roomSessions = rooms.get(compositionId);
        if (roomSessions != null) {
            for(Session session : roomSessions.values()){
                try{
                    session.getBasicRemote().sendText(monMessage);
                }catch (Exception exception){
                    System.err.println("Erreur d'envoie chat : " + exception.getMessage());
                }
            }
        }
    }
}