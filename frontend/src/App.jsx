import React from 'react'
import LoginPage from './pages/LoginPage'
import { useAuth } from './hooks/useAuth'
import ChatPage from './pages/ChatPage';

const App = () => {
  const { user } = useAuth();  
  return user ? <ChatPage /> : <LoginPage />
}

export default App