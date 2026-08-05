import { useEffect } from "react";
import { connect, disconnect } from "../services/websocketService";

export function useChatWebSocket({
  user,
  selectedUser,
  setMessages,
  setUsers,
  setTypingUser,
  loadUsers,
}) {
  const handleMessageReceived = (message) => {
    console.log("LLEGÓ MENSAJE");
    console.log(message);

    setMessages((previous) => {
      const exists = previous.some((m) => m.id === message.id);

      if (exists) return previous;

      return [...previous, message];
    });
  };

  const handleOnlineUsersChanged = (onlineUsers) => {
    console.log("Online:", onlineUsers);

    setUsers((previousUsers) =>
      previousUsers.map((u) => ({
        ...u,
        online: onlineUsers.includes(u.email),
      })),
    );
  };

  const handleStatusUpdated = (statusUpdate) => {
    console.log("Status recibido:", statusUpdate);

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
  };

  const handleTypingReceived = (typingNotification) => {
    if (typingNotification.sender === selectedUser?.email) {
      setTypingUser(
        typingNotification.typing ? typingNotification.sender : null,
      );
    }
  };

  const handleUnreadUpdated = (unreadUpdate) => {
    setUsers((previousUsers) =>
      previousUsers.map((u) =>
        u.id === unreadUpdate.senderId
          ? {
              ...u,
              unreadCount: unreadUpdate.unreadCount,
            }
          : u,
      ),
    );
  };

  const handleUsersUpdated = async () => {
    await loadUsers();
  };

  const handleMessageDeleted = (deleted) => {
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
  };

  const handleMessageEdited = (edited) => {
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
  };

  useEffect(() => {
    if (!user) return;

    connect(
      user.token,
      handleMessageReceived,
      handleOnlineUsersChanged,
      handleStatusUpdated,
      handleTypingReceived,
      handleUnreadUpdated,
      handleUsersUpdated,
      handleMessageDeleted,
      handleMessageEdited,
    );

    return () => disconnect();
  }, [user, selectedUser, setMessages, setUsers, setTypingUser]);
}
