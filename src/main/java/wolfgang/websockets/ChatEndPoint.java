package wolfgang.websockets;

import jakarta.websocket.Session;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.util.Hashtable;

@ServerEndpoint(value="chat/{pseudo}")
public class ChatEndPoint {
    private static ChatEndPoint instance = new ChatEndPoint();
    private Hashtable<String, Session> sessions = new Hashtable<>();

    public void ChatEndPoint(){};

    public ChatEndPoint getInstance(){ return instance;}

    @OnOpen
    public void onOpen(Session session, @PathParam("pseudo") String pseudo){
        Session.getUserProperties().put("pseudo", pseudo);
        Session.put(pseudo, session);
        sendMessage("Admin : connexion etablie par " + pseudo);
    }

    @OnClose
    public void onClose(Session session){
        String pseudo = (String) session.getUserProperties().get("pseudo");
        sessions.remove(pseudo);
        sendMessage("Admin : connexion perdu " + pseudo);
    }

    @OnError
    public void OnError(Throwable error){
        System.err.println("Erreur websocket : " + error);
        error.printStackTrace();
    }

    @OnMessage
    public void handleMessage(Session session, String message){
        String monMessage = "pseudo : " + message;
        sendMessage(monMessage);
    }

    public void sendMessage(String monMessage) {
        System.out.println(monMessage);
        for(Session session : sessions.values()){
            try{
                session.getBasicRemote().sendText(monMessage);
            }catch (Exception exception){
                system.err.println("Erreur d'envoie : " + exception.getMessage());
            }
        }
    }
}
