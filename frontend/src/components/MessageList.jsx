import MessageItem from "./MessageItem";

export default function MessageList({
  messages,
  search,
  searchResults,
  messagesContainerRef,
  user,
  replying,
  editing,
  actions,
  ui,
}) {
  const messagesToShow = search.trim() === "" ? messages : searchResults;

  return (
    <div
      ref={messagesContainerRef}
      style={{
        height: "500px",
        overflow: "auto",
      }}
    >
      {messagesToShow.map((message) => (
        <MessageItem
          key={message.id}
          message={message}
          user={user}
          replying={replying}
          editing={editing}
          actions={actions}
          ui={ui}
        />
      ))}
    </div>
  );
}
