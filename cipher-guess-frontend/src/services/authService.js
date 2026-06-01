const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

const authService = {

  async register(username, email, password) {
    try {
      const res = await fetch(`${BASE_URL}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, email, password })
      })

      const message = await res.text()

      return {
        success: res.ok && message.includes('Successfully'),
        message
      }
    } catch (err) {
      return { success: false, message: 'Server error. Try again.' }
    }
  },

  async login(username, password) {
    try {
      const res = await fetch(`${BASE_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
      })

      const token = await res.text()

      if (res.ok && token && !token.includes(' ')) {
        localStorage.setItem('token', token)
        localStorage.setItem('username', username)
        return { success: true }
      }

      return { success: false, message: token }
    } catch (err) {
      return { success: false, message: 'Server error. Try again.' }
    }
  },

  async logout() {
    const token = localStorage.getItem('token')

    try {
      if (token) {
        await fetch(`${BASE_URL}/auth/logout`, {
          method: 'POST',
          headers: { Authorization: `Bearer ${token}` },
          credentials: 'include'   // send cookie too so OAuth2 sessions are cleared
        })
      }
    } catch (err) {
      console.log('Logout request failed')
    }

    localStorage.removeItem('token')
    localStorage.removeItem('username')
  },

  getToken() {
    return localStorage.getItem('token')
  },

  getUsername() {
    return localStorage.getItem('username')
  },

  // FIX: OAuth2 users have no token in localStorage (token is in HttpOnly cookie)
  // so check for username as the fallback signal that the user is logged in
  isLoggedIn() {
    return !!localStorage.getItem('token') || !!localStorage.getItem('username')
  }
}

export default authService