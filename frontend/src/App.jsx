import React from "react";
import LoginPage from "./pages/LoginPage";
import { useAuth } from "./hooks/useAuth";
import ChatPage from "./pages/ChatPage";
import { Toaster } from "react-hot-toast";

const App = () => {
  const { user } = useAuth();
  return (
    <>
      {user ? <ChatPage /> : <LoginPage />}
      <Toaster position="top-right" />
    </>
  );
};

export default App;
