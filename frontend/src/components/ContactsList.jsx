export default function ContactsList({
  users,
  selectedUser,
  setSelectedUser,
}) {
  return (
    <aside
      style={{
        width: "200px",
        borderRight: "1px solid #ccc",
      }}
    >
      <h3>Contactos</h3>

      {users.map((u) => (
        <div
          key={u.id}
          onClick={() => setSelectedUser(u)}
          style={{
            cursor: "pointer",
            fontWeight:
              selectedUser?.id === u.id ? "bold" : "normal",
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
  );
}