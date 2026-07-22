import api from "../api/axios";

export async function getConversation(receiverId) {
  const response = await api.get(`/messages/conversation/${receiverId}`);

  return response.data;
}

export async function sendMessage(request) {
  const response = await api.post("/messages", request);

  return response.data;
}

export async function markAsRead(senderId) {
  await api.post(`/messages/read/${senderId}`);
}
