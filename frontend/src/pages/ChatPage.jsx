import { useEffect, useRef, useState } from "react";
import { useAuth } from "../hooks/useAuth";
import {
  deleteMessage,
  editMessage,
  getConversation,
  markAsRead,
  searchConversation,
} from "../services/messageService";
import { getAllUsers } from "../services/userService";
import {
  connect,
  disconnect,
  sendMessage,
  sendTyping,
} from "../services/websocketService";

const ChatPage = () => {
  const { user } = useAuth();
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [messages, setMessages] = useState([]);
  const [content, setContent] = useState("");
  const [onlineUsers, setOnlineUsers] = useState([]);
  const [typingUser, setTypingUser] = useState(null);
  const [editingMessageId, setEditingMessageId] = useState(null);
  const [editingContent, setEditingContent] = useState("");
  const [replyingTo, setReplyingTo] = useState(null);
  const [search, setSearch] = useState("");
  const [searchResults, setSearchResults] = useState([]);

  // Ref para que el callback siempre vea el selectedUser actual
  const selectedUserRef = useRef(selectedUser);
  const typingTimeoutRef = useRef(null);

  useEffect(() => {
    selectedUserRef.current = selectedUser;
  }, [selectedUser]);

  const loadUsers = async () => {
    const allUsers = await getAllUsers();
    setUsers(allUsers);
  };

  // Carga la lista de contactos
  useEffect(() => {
    loadUsers();
  }, []);

  // Carga la conversación cuando cambia el usuario seleccionado
  useEffect(() => {
    if (!selectedUser) return;

    async function loadConversation() {
      try {
        const conversation = await getConversation(selectedUser.id);
        setMessages(conversation);
      } catch (error) {
        console.error(error);
      }
    }
    loadConversation();
  }, [selectedUser]);

  // Conecta el WebSocket UNA sola vez por sesión (no por cambio de chat)
  useEffect(() => {
    if (!user) return;

    connect(
      user.token,
      (newMessage) => {
        const currentSelected = selectedUserRef.current;

        if (
          currentSelected &&
          (newMessage.senderId === currentSelected.id ||
            newMessage.receiverId === currentSelected.id)
        ) {
          setMessages((previousMessages) => [...previousMessages, newMessage]);

          if (newMessage.status === "DELIVERED") {
            markAsRead(newMessage.senderId).catch((error) =>
              console.error("Error marcando mensajes como leídos: ", error),
            );
          }
        }
      },
      (emails) => {
        console.log("Online: ", emails);

        setUsers((previousUsers) =>
          previousUsers.map((user) => ({
            ...user,
            online: emails.includes(user.email),
          })),
        );
      },
      (statusUpdate) => {
        console.log("Status recibido: ", statusUpdate);

        setMessages((previous) =>
          previous.map((message) =>
            message.id === statusUpdate.messageId
              ? {
                  ...message,
                  status: statusUpdate.status,
                }
              : message,
          ),
        );
      },
      (typingNotification) => {
        if (typingNotification.typing) {
          setTypingUser(typingNotification.sender);
        } else {
          setTypingUser(null);
        }
      },
      (unreadUpdate) => {
        setUsers((previousUsers) =>
          previousUsers.map((user) =>
            user.id === unreadUpdate.senderId
              ? {
                  ...user,
                  unreadCount: unreadUpdate.unreadCount,
                }
              : user,
          ),
        );
      },
      async () => {
        await loadUsers();
      },
      (deleted) => {
        setMessages((previous) =>
          previous.map((message) =>
            message.id === deleted.messageId
              ? {
                  ...message,
                  deleted: true,
                }
              : message,
          ),
        );
      },
      (edited) => {
        setMessages((previous) =>
          previous.map((message) =>
            message.id === edited.messageId
              ? {
                  ...message,
                  content: edited.content,
                  edited: edited.edited,
                }
              : message,
          ),
        );
      },
    );

    return () => {
      disconnect();
    };
  }, [user]); // ← ya no depende de selectedUser

  useEffect(() => {
    if (!selectedUser) return;

    if (search.trim() === "") {
      setSearchResults([]);
      return;
    }

    const timeout = setTimeout(async () => {
      const results = await searchConversation(selectedUser.id, search);

      setSearchResults(results);
    }, 400);

    return () => clearTimeout(timeout);
  }, [search, selectedUser]);

  async function handleSendMessage() {
    if (content.trim() === "" || !selectedUser) return;

    console.log("replyingTo:", replyingTo);

    await sendMessage(selectedUser.id, content, replyingTo?.id ?? null);

    setContent("");
    setReplyingTo(null);
  }

  function getStatusIcon(status) {
    switch (status) {
      case "SENT":
        return <span>✓</span>;
      case "DELIVERED":
        return <span>✓✓</span>;
      case "READ":
        return <span style={{ color: "blue" }}>✓✓</span>;
      default:
        return "";
    }
  }

  const handleDeleteMessage = async (messageId) => {
    try {
      await deleteMessage(messageId);
    } catch (error) {
      console.error("Error eliminando mensaje: ", error);
    }
  };

  const handleEditClick = (message) => {
    setEditingMessageId(message.id);
    setEditingContent(message.content);
  };

  const handleSaveEdit = async () => {
    try {
      await editMessage(editingMessageId, editingContent);

      setEditingMessageId(null);
      setEditingContent("");
    } catch (error) {
      console.error(error);
    }
  };

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
            {u.online ? "🟢" : "⚪"} {u.username}
            {u.unreadCount > 0 && (
              <span
                style={{
                  marginLeft: "8px",
                  backgroundColor: "red",
                  color: "white",
                  borderRadius: "50%",
                  padding: "2px 8px",
                  fontSize: "12px",
                  fontWeight: "bold",
                }}
              >
                {u.unreadCount}
              </span>
            )}
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
              <input
                type="text"
                placeholder="Buscar mensajes..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
              {typingUser === selectedUser.email && (
                <small>{selectedUser.username} está escribiendo...</small>
              )}
              <div>
                {(search.trim() === "" ? messages : searchResults).map(
                  (message) => (
                    <div key={message.id}>
                      <strong>{message.sender}</strong>
                      {message.deleted ? (
                        <i>🗑️ Este mensaje fue eliminado</i>
                      ) : (
                        <>
                          {editingMessageId === message.id ? (
                            <>
                              <input
                                value={editingContent}
                                onChange={(e) =>
                                  setEditingContent(e.target.value)
                                }
                              />
                              <button onClick={handleSaveEdit}>Guardar</button>
                            </>
                          ) : (
                            <>
                              {message.replyTo && (
                                <div
                                  style={{
                                    borderLeft: "3px solid #4caf50",
                                    paddingLeft: "8px",
                                    marginBottom: "6px",
                                    background: "#f7f7f7",
                                    fontSize: "0.9rem",
                                  }}
                                >
                                  <strong>{message.replyTo.sender}</strong>
                                  <div>{message.replyTo.content}</div>
                                </div>
                              )}
                              <p>{message.content}</p>
                              {!message.deleted && (
                                <button onClick={() => setReplyingTo(message)}>
                                  Responder
                                </button>
                              )}
                              {message.edited && <small>(editado)</small>}
                            </>
                          )}
                          {message.senderId === user.id && (
                            <button
                              onClick={() => handleDeleteMessage(message.id)}
                            >
                              Eliminar
                            </button>
                          )}
                          {message.senderId === user.id &&
                            !message.deleted &&
                            editingMessageId === null && (
                              <button onClick={() => handleEditClick(message)}>
                                ✏️
                              </button>
                            )}
                        </>
                      )}
                      {message.senderId === user.id && (
                        <small>{getStatusIcon(message.status)}</small>
                      )}
                    </div>
                  ),
                )}
              </div>
            </main>

            <hr />

            <footer>
              {replyingTo && (
                <div
                  style={{
                    borderLeft: "4px solid green",
                    padding: "8px",
                    marginBottom: "8px",
                    background: "#f5f5f5",
                  }}
                >
                  <strong>Respondiendo a {replyingTo.sender}</strong>
                  <div>{replyingTo.content}</div>
                  <button onClick={() => setReplyingTo(null)}>X</button>
                </div>
              )}
              <input
                type="text"
                placeholder="Escribe un mensaje..."
                value={content}
                onChange={(e) => {
                  setContent(e.target.value);

                  if (!selectedUser) return;

                  // Avisar que está escribiendo
                  sendTyping(selectedUser.email, true);

                  // Cancelar el temporizador anterior
                  if (typingTimeoutRef.current) {
                    clearTimeout(typingTimeoutRef.current);
                  }

                  // Crear uno nuevo
                  typingTimeoutRef.current = setTimeout(() => {
                    sendTyping(selectedUser.email, false);
                  }, 1000);
                }}
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
