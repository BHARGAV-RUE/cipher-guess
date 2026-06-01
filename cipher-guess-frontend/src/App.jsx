import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Index from './pages/index.jsx'
import Dashboard from './pages/dashboard.jsx'
import Game from './pages/game.jsx'
import authService from './services/authService'

function PrivateRoute({ children }) {
  // FIX: was only checking localStorage('token'), which meant OAuth2 users
  // (who have no token in localStorage — only username + an HttpOnly cookie)
  // were always redirected back to '/'.
  // Now uses authService.isLoggedIn() which checks for either token OR username.
  return authService.isLoggedIn() ? children : <Navigate to="/" replace />
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Index />} />
        <Route path="/dashboard" element={
          <PrivateRoute><Dashboard /></PrivateRoute>
        } />
        <Route path="/game" element={
          <PrivateRoute><Game /></PrivateRoute>
        } />
      </Routes>
    </BrowserRouter>
  )
}