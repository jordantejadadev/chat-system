import { Client } from "@stomp/stompjs";

let stompClient = null;

export function connect(
  token,
  onMessageReceived,
  onOnlineUsersChanged,
  onStatusUpdated,
  onTypingReceived,
  onUnreadCountUpdated
) {
  stompClient = new Client({
    brokerURL: "ws://localhost:8080/ws",

    connectHeaders: {
      Authorization: `Bearer ${token}`,
    },

    reconnectDelay: 5000,

    onConnect: () => {
      console.log("Conectado al WebSocket");

      stompClient.subscribe("/user/queue/messages", (message) => {
        const body = JSON.parse(message.body);
        onMessageReceived(body);
      });

      stompClient.subscribe("/topic/online-users", (message) => {
        const onlineUsers = JSON.parse(message.body);
        onOnlineUsersChanged(onlineUsers);
      });

      stompClient.subscribe("/user/queue/message-status", (message) => {
        const body = JSON.parse(message.body);
        onStatusUpdated(body);
      });

      stompClient.subscribe("/user/queue/typing", (message) => {
        const body = JSON.parse(message.body);
        onTypingReceived(body);
      });

      stompClient.subscribe("/user/queue/unread-count", (message) => {
        const body = JSON.parse(message.body);
        console.log("UnreadCount: ", body);
        
        onUnreadCountUpdated(body);
      });
    },

    onStompError: (frame) => {
      console.error("STOMP Error:", frame);
    },

    onWebSocketClose: (event) => {
      console.log("WebSocket cerrado", event);
    },

    onWebSocketError: (event) => {
      console.error("WebSocket error", event);
    },
  });

  stompClient.activate();
}

export function disconnect() {
  if (stompClient) {
    stompClient.deactivate();
  }
}

export function sendMessage(receiverId, content) {
  if (!stompClient.connected) {
    console.log("Aún no conectado");
    return;
  }

  stompClient.publish({
    destination: "/app/chat",
    body: JSON.stringify({
      receiverId,
      content,
    }),
  });
}

export function sendTyping(receiver, typing) {
  if (!stompClient || !stompClient.connected) return;

  stompClient.publish({
    destination: "/app/chat/typing",
    body: JSON.stringify({
      receiver,
      typing,
    }),
  });
}
