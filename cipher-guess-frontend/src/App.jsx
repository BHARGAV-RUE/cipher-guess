import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Index from './pages/index.jsx'
import Dashboard from './pages/dashboard.jsx'
import Game from './pages/game.jsx'

function PrivateRoute({ children }) {
  const token = localStorage.getItem('token')
  return token ? children : <Navigate to="/" replace />
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