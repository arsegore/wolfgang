// On récupère le pseudo depuis la session Java via EL
const username = "${sessionScope.user.username}";
const socket = new WebSocket("ws://" + window.location.host + "${pageContext.request.contextPath}/chat/" + pseudo);

socket.onopen = function(event){
    consol.log("Connexion ouverte");
    ajouterMessage("Connexion ouverte avec le serveur");
};

socket.onmessage = function(event){
    console.log("message recu :", event.data);
    ajouterMessage(event.data);
}

socket.onclose = function(event){
    console.log("connexion fermée");
    ajouterMessage("connexion fermée");
}

socket.onerror = function(event){
    console.log("Erreur Websocket");
    ajouterMessage("erreur websocket");
}

function send() {
    const input = document.getElementById("messageInput");
    let message = input.value;
    socket.send(message);
    input.value = "";

    }

function ajouterMessage(message){
    let zone = document.getElementById("messages");
    zone.innerHTML+=message + "<br>";
    }
}