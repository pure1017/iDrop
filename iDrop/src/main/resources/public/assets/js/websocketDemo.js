// small helper function for selecting element by id
let id = id => document.getElementById(id);
const userName = sessionStorage.getItem("userName");
const userPicture = sessionStorage.getItem("userPicture");
const isHost = sessionStorage.getItem("isHost");


console.log("websocket userpic:"+userPicture)

//Establish the WebSocket connection and set up event handlers
let ws = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat?userName="+userName+"&picture="+userPicture+"&isHost="+isHost);

ws.onmessage = msg => updateChat(msg);
ws.onclose = () => alert("WebSocket connection closed");

// Add event listeners to button and input field
id("send").addEventListener("click", () => sendAndClear(id("message").value));
id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) { // Send message if enter is pressed in input field
        sendAndClear(e.target.value);
    }
});

function sendAndClear(message) {
    if (message !== "") {
        ws.send(message);
        id("message").value = "";
    }
}

function updateChat(msg) { // Update chat-panel and list of connected users
    let data = JSON.parse(msg.data);
    id("chat").insertAdjacentHTML("beforeend", data.userMessage);
    id("userlist").innerHTML = data.userlist.map(user => "<li id="+ user +">" + user + "</li>").join("");
}