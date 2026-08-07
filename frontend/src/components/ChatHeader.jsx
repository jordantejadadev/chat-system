export default function ChatHeader({
  selectedUser,
  search,
  setSearch,
  typingUser,  
}) {
  return (
    <>    
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
    </>
  );
}