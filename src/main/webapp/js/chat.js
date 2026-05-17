const configElement = document.getElementById("chat-config");
const pseudo = configElement ? configElement.getAttribute("data-pseudo") : "";
const contextPath = configElement ? configElement.getAttribute("data-context") : "";

if (pseudo && pseudo.trim() !== "") {
    const socket = new WebSocket("ws://" + window.location.host + contextPath + "/chat/" + pseudo);

    socket.onopen = function(event){
        console.log("Connexion ouverte");
        ajouterMessage("Connexion ouverte avec le serveur");
    };

    socket.onmessage = function(event){
        console.log("message recu :", event.data);
        ajouterMessage(event.data);
    };

    socket.onclose = function(event){
        console.log("connexion fermée");
        ajouterMessage("connexion fermée");
    };

    socket.onerror = function(event){
        console.log("Erreur Websocket");
        ajouterMessage("erreur websocket");
    };

    window.send = function() {
        const input = document.getElementById("messageInput");
        let message = input.value;
        if (message.trim() !== "") {
            socket.send(message);
            input.value = "";
        }
    };
} else {
    console.warn("Chat désactivé : Aucun utilisateur connecté en session.");
    let zone = document.getElementById("messages");
    if (zone) {
        zone.innerHTML = "<i>Connectez-vous pour participer à la discussion.</i>";
    }
}

function ajouterMessage(message){
    let zone = document.getElementById("messages");
    if (zone) {
        zone.innerHTML += message + "<br>";
    }
}