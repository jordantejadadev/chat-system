import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

let stompClient = null;

export function connect(onMessageReceived) {

    stompClient = new Client({

        webSocketFactory: () => new SockJS("http://localhost:8080/ws"),

        reconnectDelay: 5000,

        onConnect: () => {
            console.log("Conectado al WebSocket");

            stompClient.subscribe(
                "/user/queue/messages",
                (message) => {
                    const body = JSON.parse(message.body);
                    onMessageReceived(body);                    
                }
            )
        }
    });

    stompClient.activate();
}

export function disconnect() {
    if (stompClient) {
        stompClient.deactivate();
    }
}