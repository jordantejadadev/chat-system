import { useEffect, useLayoutEffect, useRef, useState } from "react";
import { getConversation } from "../services/messageService";

export function useConversation(selectedUser) {
  const [messages, setMessages] = useState([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);

  const messagesContainerRef = useRef(null);
  const previousScrollHeightRef = useRef(0);
  const restoringScrollRef = useRef(false);

  async function loadMoreMessages() {
    if (!selectedUser) return;

    if (!hasMore) return;

    if (loadingMore) return;

    console.log("Guardando altura");

    setLoadingMore(true);

    const container = messagesContainerRef.current;

    previousScrollHeightRef.current = container.scrollHeight;

    restoringScrollRef.current = true;

    const response = await getConversation(selectedUser.id, page, 20);

    setMessages((previous) => [...response.content.reverse(), ...previous]);

    setPage((previous) => previous + 1);

    setHasMore(!response.last);

    setLoadingMore(false);
  }

  useEffect(() => {
      const container = messagesContainerRef.current;
  
      if (!container) return;
  
      function handleScroll() {
        if (container.scrollTop === 0) {
          loadMoreMessages();
        }
      }
  
      container.addEventListener("scroll", handleScroll);
  
      return () => container.removeEventListener("scroll", handleScroll);
    }, [page, hasMore, loadingMore, selectedUser]);

  useLayoutEffect(() => {
    if (!restoringScrollRef.current) return;

    const container = messagesContainerRef.current;

    if (!container) return;

    const newHeight = container.scrollHeight;

    container.scrollTop += newHeight - previousScrollHeightRef.current;

    restoringScrollRef.current = false;
  }, [messages]);

  useEffect(() => {
    if (!selectedUser) return;

    async function loadConversation() {
      try {
        const conversation = await getConversation(selectedUser.id, 0, 20);

        setMessages(conversation.content.reverse());
        setPage(1);
        setHasMore(!conversation.last);
      } catch (error) {
        console.error(error);
      }
    }

    loadConversation();
  }, [selectedUser]);

  return {
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
  };
}
