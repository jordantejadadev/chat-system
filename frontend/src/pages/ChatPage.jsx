import { useEffect, useLayoutEffect, useRef, useState } from "react";
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
import ContactsList from "../components/ContactsList";
import ChatHeader from "../components/ChatHeader";
import MessageItem from "../components/MessageItem";
import MessageList from "../components/MessageList";
import MessageInput from "../components/MessageInput";
import { useConversation } from "../hooks/useConversation";
import { useChatWebSocket } from "../hooks/useChatWebSocket";
import { useMessageSearch } from "../hooks/useMessageSearch";

const ChatPage = () => {
  const { user } = useAuth();
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [content, setContent] = useState("");
  const [onlineUsers, setOnlineUsers] = useState([]);
  const [typingUser, setTypingUser] = useState(null);
  const [editingMessageId, setEditingMessageId] = useState(null);
  const [editingContent, setEditingContent] = useState("");
  const [replyingTo, setReplyingTo] = useState(null);
  const [search, setSearch] = useState("");
  const {
    messages,
    setMessages,
    page,
    setPage,
    hasMore,
    setHasMore,
    loadingMore,
    setLoadingMore,
    loadMoreMessages,
    messagesContainerRef,
  } = useConversation(selectedUser);
  const { searchResults } = useMessageSearch(selectedUser, search);

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

  useChatWebSocket({
    user,
    selectedUser,
    setMessages,
    setUsers,
    setTypingUser,
    loadUsers,
  });

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

  const editing = {
    editingMessageId,
    editingContent,
    setEditingContent,
    handleSaveEdit,
    handleEditClick,
  };

  const replying = {
    replyingTo,
    setReplyingTo,
  };

  const actions = {
    handleDeleteMessage,
  };

  const ui = {
    getStatusIcon,
  };

  return (
    <div style={{ display: "flex" }}>
      <ContactsList
        users={users}
        selectedUser={selectedUser}
        setSelectedUser={setSelectedUser}
      />

      <div style={{ flex: 1, padding: "0 16px" }}>
        <header>
          <h2>{user.username}</h2>
          <p>{user.email}</p>
        </header>

        <hr />

        {selectedUser ? (
          <>
            <main>
              <ChatHeader
                selectedUser={selectedUser}
                search={search}
                setSearch={setSearch}
                typingUser={typingUser}
              />
              <MessageList
                messages={messages}
                search={search}
                searchResults={searchResults}
                messagesContainerRef={messagesContainerRef}
                user={user}
                editing={editing}
                replying={replying}
                actions={actions}
                ui={ui}
              />
            </main>

            <hr />

            <MessageInput
              selectedUser={selectedUser}
              replyingTo={replyingTo}
              setReplyingTo={setReplyingTo}
              content={content}
              setContent={setContent}
              handleSendMessage={handleSendMessage}
              typingTimeoutRef={typingTimeoutRef}
            />
          </>
        ) : (
          <p>Selecciona un contacto para empezar a chatear</p>
        )}
      </div>
    </div>
  );
};

export default ChatPage;
