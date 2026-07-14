import { useEffect, useState } from "react";
import { getCurrentUser } from "../services/authService";
import { useAuth } from "../hooks/useAuth";
import { getConversation, sendMessage } from "../services/messageService";
import { connect, disconnect } from "../services/websocketService";

const ChatPage = () => {
  //   useEffect(() => {
  //     async function loadUser() {
  //       const user = await getCurrentUser();
  //       console.log(user);
  //     }

  //     loadUser();
  //   }, []);

  const { user } = useAuth();
  const [messages, setMessages] = useState([]);
  const [content, setContent] = useState("");

  // Carga la conversación
  useEffect(() => {
    async function loadConversation() {
      const conversation = await getConversation(4);

      setMessages(conversation);
    }

    loadConversation();
  }, []);

  // Conecta el WebSocket
  useEffect(() => {
    connect((newMessage) => {
      setMessages((previousMessages) => [...previousMessages, newMessage]);
    });

    return () => {
      // Se ejecuta cuando ChatPage se desmonta
      disconnect();
    }
  }, []);

  async function handleSendMessage() {
    if (content.trim() === "") {
      return;
    }

    const request = {
      receiverId: 4,
      content: content,
    };

    const message = await sendMessage(request);

    setMessages((previousMessages) => [...previousMessages, message]);

    setContent("");
  }

  return (
    <div>
      <header>
        <h2>{user.username}</h2>
        <p>{user.email}</p>
      </header>

      <hr />

      <main>
        <div>
          {messages.map((message) => (
            <div key={message.id}>
              <strong>{message.sender}</strong>
              <p>{message.content}</p>
            </div>
          ))}
        </div>
      </main>

      <hr />

      <footer>
        <input
          type="text"
          placeholder="Escribe un mensaje..."
          value={content}
          onChange={(e) => setContent(e.target.value)}
        />
        <button onClick={handleSendMessage}>Enviar</button>
      </footer>
    </div>
  );
};

export default ChatPage;
