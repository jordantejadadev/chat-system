import { useEffect, useRef, useState } from "react";
import { useAuth } from "../hooks/useAuth";
import { getConversation } from "../services/messageService";
import { getAllUsers } from "../services/userService";
import { connect, disconnect, sendMessage } from "../services/websocketService";

const ChatPage = () => {
  const { user } = useAuth();
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [messages, setMessages] = useState([]);
  const [content, setContent] = useState("");

  // Ref para que el callback siempre vea el selectedUser actual
  const selectedUserRef = useRef(selectedUser);

  useEffect(() => {
    selectedUserRef.current = selectedUser;
  }, [selectedUser]);

  // Carga la lista de contactos
  useEffect(() => {
    async function loadUsers() {
      const allUsers = await getAllUsers();
      setUsers(allUsers);
    }
    loadUsers();
  }, []);

  // Carga la conversación cuando cambia el usuario seleccionado
  useEffect(() => {
    if (!selectedUser) return;

    async function loadConversation() {
      const conversation = await getConversation(selectedUser.id);
      setMessages(conversation);
    }
    loadConversation();
  }, [selectedUser]);

  // Conecta el WebSocket UNA sola vez por sesión (no por cambio de chat)
  useEffect(() => {
    if (!user) return;

    connect(user.token, (newMessage) => {
      const currentSelected = selectedUserRef.current;

      setMessages((previousMessages) => {
        if (
          currentSelected &&
          (newMessage.sender === currentSelected.email ||
            newMessage.receiver === currentSelected.email)
        ) {
          return [...previousMessages, newMessage];
        }
        return previousMessages;
      });
    });

    return () => {
      disconnect();
    };
  }, [user]); // ← ya no depende de selectedUser

  async function handleSendMessage() {
    if (content.trim() === "" || !selectedUser) return;
    sendMessage(selectedUser.id, content);
    setContent("");
  }

  return (
    <div style={{ display: "flex" }}>
      <aside style={{ width: "200px", borderRight: "1px solid #ccc" }}>
        <h3>Contactos</h3>
        {users.map((u) => (
          <div
            key={u.id}
            onClick={() => setSelectedUser(u)}
            style={{
              cursor: "pointer",
              fontWeight: selectedUser?.id === u.id ? "bold" : "normal",
              padding: "4px 0",
            }}
          >
            {u.username}
          </div>
        ))}
      </aside>

      <div style={{ flex: 1, padding: "0 16px" }}>
        <header>
          <h2>{user.username}</h2>
          <p>{user.email}</p>
        </header>

        <hr />

        {selectedUser ? (
          <>
            <main>
              <h4>Chat con {selectedUser.username}</h4>
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
          </>
        ) : (
          <p>Selecciona un contacto para empezar a chatear</p>
        )}
      </div>
    </div>
  );
};

export default ChatPage;