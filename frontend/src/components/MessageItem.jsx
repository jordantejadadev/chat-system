export default function MessageItem({
  message,
  user,
  replying,
  editing,
  actions,
  ui,
}) {
  const { replyingTo, setReplyingTo } = replying;
  const {
    editingMessageId,
    editingContent,
    setEditingContent,
    handleSvedEdit,
    handleEditClick,
  } = editing;
  const { handleDeleteMessage } = actions;
  const { getStatusIcon } = ui;
  
  return (
    <div>
      <strong>{message.sender}</strong>

      {message.deleted ? (
        <i>🗑️ Este mensaje fue eliminado</i>
      ) : (
        <>
          {editingMessageId === message.id ? (
            <>
              <input
                value={editingContent}
                onChange={(e) => setEditingContent(e.target.value)}
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
            <button onClick={() => handleDeleteMessage(message.id)}>
              Eliminar
            </button>
          )}

          {message.senderId === user.id &&
            !message.deleted &&
            editingMessageId === null && (
              <button onClick={() => handleEditClick(message)}>✏️</button>
            )}
        </>
      )}

      {message.senderId === user.id && (
        <small>{getStatusIcon(message.status)}</small>
      )}
    </div>
  );
}
