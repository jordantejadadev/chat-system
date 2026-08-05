import { sendTyping } from "../services/websocketService";

export default function MessageInput({
  selectedUser,
  replyingTo,
  setReplyingTo,
  content,
  setContent,
  handleSendMessage,
  typingTimeoutRef,
}) {
  return (
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
          <strong>
            Respondiendo a {replyingTo.sender}
          </strong>

          <div>{replyingTo.content}</div>

          <button
            onClick={() => setReplyingTo(null)}
          >
            X
          </button>
        </div>
      )}

      <input
        type="text"
        placeholder="Escribe un mensaje..."
        value={content}
        onChange={(e) => {
          setContent(e.target.value);

          if (!selectedUser) return;

          sendTyping(selectedUser.email, true);

          if (typingTimeoutRef.current) {
            clearTimeout(
              typingTimeoutRef.current
            );
          }

          typingTimeoutRef.current =
            setTimeout(() => {
              sendTyping(
                selectedUser.email,
                false
              );
            }, 1000);
        }}
      />

      <button onClick={handleSendMessage}>
        Enviar
      </button>
    </footer>
  );
}