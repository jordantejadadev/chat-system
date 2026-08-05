export default function ContactsList({
  users,
  selectedUser,
  setSelectedUser,
  onlineUsers,
}) {
  return (
    <aside>
      <h3>Usuarios</h3>

      <ul>
        {users.map((user) => (
          <li
            key={user.id}
            style={{
              cursor: "pointer",
              fontWeight:
                selectedUser?.id === user.id ? "bold" : "normal",
            }}
            onClick={() => setSelectedUser(user)}
          >
            {user.username}

            {onlineUsers.includes(user.email) && (
              <span style={{ color: "green" }}> ●</span>
            )}

            {user.unreadCount > 0 && (
              <strong style={{ color: "red" }}>
                {" "}({user.unreadCount})
              </strong>
            )}
          </li>
        ))}
      </ul>
    </aside>
  );
}