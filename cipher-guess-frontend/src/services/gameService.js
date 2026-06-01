import authService from './authService'

// FIX: was falling back to empty string '', which breaks requests when env var is missing
const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

// Builds auth headers — always sends Bearer token if present.
// OAuth2 users rely on the HttpOnly cookie which the browser sends automatically
// via credentials: 'include', so no token header is needed for them.
function authHeaders() {
  const token = authService.getToken()
  const headers = {}
  if (token) headers['Authorization'] = `Bearer ${token}`
  return headers
}

const gameService = {

  async startGame(difficulty) {
    const res = await fetch(`${BASE_URL}/game/start?difficulty=${difficulty}`, {
      method: 'POST',
      headers: authHeaders(),
      credentials: 'include'  // sends HttpOnly cookie for OAuth2 users
    })
    return { success: res.ok, message: await res.text() }
  },

  async submitGuess(guess) {
    const res = await fetch(`${BASE_URL}/game/guess?guess=${guess}`, {
      method: 'POST',
      headers: authHeaders(),
      credentials: 'include'
    })
    return { success: res.ok, message: await res.text() }
  },

  async forfeit() {
    const res = await fetch(`${BASE_URL}/game/forfeit`, {
      method: 'POST',
      headers: authHeaders(),
      credentials: 'include'
    })
    return { success: res.ok, message: await res.text() }
  },

  async getStats() {
    const res = await fetch(`${BASE_URL}/game/stats`, {
      headers: authHeaders(),
      credentials: 'include'
    })
    if (!res.ok) return null
    return await res.json()
  }
}

export default gameService