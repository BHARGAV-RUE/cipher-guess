import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import authService from '../services/authService'
import './index.css'

// FIX: removed duplicate API_URL — only one BASE_URL needed
const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'
const GOOGLE_AUTH_URL = `${BASE_URL}/oauth2/authorization/google`

export default function Index() {

  /* ── Navigation ── */
  const navigate = useNavigate()

  /* ── Modal State ── */
  const [modal, setModal] = useState(null)

  /* ── Login Form State ── */
  const [loginData, setLoginData] = useState({ username: '', password: '' })

  /* ── Signup Form State ── */
  const [signupData, setSignupData] = useState({ email: '', username: '', password: '' })

  /* ── Form Messages ── */
  const [loginError,   setLoginError]   = useState('')
  const [loginSuccess, setLoginSuccess] = useState('')
  const [signupError,   setSignupError]   = useState('')
  const [signupSuccess, setSignupSuccess] = useState('')

  /* ── Password Visibility ── */
  const [showLoginPass,  setShowLoginPass]  = useState(false)
  const [showSignupPass, setShowSignupPass] = useState(false)

  /* ── Loading State ── */
  const [loading, setLoading] = useState(false)

  /* ── Password Strength ── */
  const [strength, setStrength] = useState({
    score: 0, label: '', color: '', width: '0%'
  })

  /* ─────────────────────────────────────────────
     OAuth Redirect Handler
     FIX: token no longer comes in the URL — it is now an HttpOnly cookie
     set by the backend. We only read ?username= from the URL for display.
  ───────────────────────────────────────────── */
  useEffect(() => {
    const params   = new URLSearchParams(window.location.search)
    const username = params.get('username')

    if (username) {
      // Token is in HttpOnly cookie — browser sends it automatically on every request.
      // We only store username in localStorage for display purposes.
      localStorage.setItem('username', decodeURIComponent(username))
      window.history.replaceState({}, document.title, '/')
      navigate('/dashboard')
    }
  }, [navigate])

  /* ─────────────────────────────────────────────
     Password Strength Checker
  ───────────────────────────────────────────── */
  function checkStrength(password) {
    let score = 0
    if (password.length >= 8)       score++
    if (/[A-Z]/.test(password))     score++
    if (/[0-9]/.test(password))     score++
    if (/[^A-Za-z0-9]/.test(password)) score++

    const levels = {
      0: { label: '',       color: 'transparent', width: '0%'   },
      1: { label: 'WEAK',   color: '#ff4444',     width: '25%'  },
      2: { label: 'FAIR',   color: '#FFA500',     width: '50%'  },
      3: { label: 'GOOD',   color: '#FFD700',     width: '75%'  },
      4: { label: 'STRONG', color: '#00ff88',     width: '100%' }
    }
    setStrength({ score, ...levels[score] })
  }

  /* ─────────────────────────────────────────────
     Login Handler
  ───────────────────────────────────────────── */
  async function handleLogin(e) {
    e.preventDefault()
    setLoginError('')
    setLoginSuccess('')

    if (!loginData.username || !loginData.password) {
      setLoginError('Please fill in all fields')
      return
    }

    setLoading(true)
    setLoginSuccess('Logging in...')

    const result = await authService.login(loginData.username, loginData.password)
    setLoading(false)

    if (result.success) {
      setLoginSuccess('Login successful! Redirecting...')
      setTimeout(() => navigate('/dashboard'), 800)
    } else {
      setLoginSuccess('')
      setLoginError(result.message || 'Invalid username or password')
    }
  }

  /* ─────────────────────────────────────────────
     Register Handler
  ───────────────────────────────────────────── */
  async function handleRegister(e) {
    e.preventDefault()
    setSignupError('')
    setSignupSuccess('')

    if (!signupData.email || !signupData.username || !signupData.password) {
      setSignupError('Please fill in all fields')
      return
    }

    if (strength.score < 3) {
      setSignupError('Password too weak — use 8+ chars, uppercase and a number')
      return
    }

    setLoading(true)
    setSignupSuccess('Creating your account...')

    const result = await authService.register(
      signupData.username,
      signupData.email,
      signupData.password
    )

    if (result.success) {
      setSignupSuccess('Account created! Logging you in...')
      const loginResult = await authService.login(signupData.username, signupData.password)
      setLoading(false)

      if (loginResult.success) {
        setSignupSuccess('Welcome to CipherGuess! Redirecting...')
        setTimeout(() => navigate('/dashboard'), 800)
      } else {
        setSignupSuccess('Account created! Please login.')
        setTimeout(() => { setModal('login') }, 1500)
      }
    } else {
      setLoading(false)
      setSignupSuccess('')
      setSignupError(result.message || 'Registration failed. Try again.')
    }
  }

  /* ─────────────────────────────────────────────
     Close Modal
  ───────────────────────────────────────────── */
  function closeModal() {
    setModal(null)
    setLoginError('');  setLoginSuccess('')
    setSignupError(''); setSignupSuccess('')
    setShowLoginPass(false)
    setShowSignupPass(false)
    setLoading(false)
    setStrength({ score: 0, label: '', color: 'transparent', width: '0%' })
  }

  /* ─────────────────────────────────────────────
     JSX
  ───────────────────────────────────────────── */
  return (
    <div className="index-page">

      {/* ── Full Page Content ── */}
      <div className="page-content">

        {/* ── Navbar ── */}
        <nav className="nav-bar">
          <span className="text-heading">CiPhErGuEsS</span>
          <div className="nav-buttons">
            <button className="btn-outline" onClick={() => setModal('login')}>LOGIN</button>
            <button className="btn-primary" onClick={() => setModal('signup')}>SIGN UP</button>
          </div>
        </nav>

        {/* ── Main Content ── */}
        <main className="content">
          <div className="box-1">
            <h2 className="abt-game">About Game</h2>
            <p className="cont-1">
              Crack The Number is a number guessing game where
              you compete against a randomly generated secret number.
            </p>
          </div>
          <div className="box-2">
            <h2 className="rules">Rules</h2>
            <ul>
              <li>Choose a difficulty level.</li>
              <li>Guess the secret number.</li>
              <li>Hints will guide you.</li>
              <li>Win in fewer attempts for a better score.</li>
            </ul>
          </div>
        </main>

      </div>

      {/* ── Modal Section ── */}
      {modal && (
        <div
          className="modal-overlay active"
          onClick={e => e.target === e.currentTarget && closeModal()}
        >
          <div className="modal-box">

            <span className="modal-close" onClick={closeModal}>&times;</span>

            {/* ── LOGIN MODAL ── */}
            {modal === 'login' && (
              <>
                <div className="modal-title">LOGIN</div>

                {loginError   && <div className="form-msg error">{loginError}</div>}
                {loginSuccess && <div className="form-msg success">{loginSuccess}</div>}

                <div className="form-group">
                  <label className="form-label">USERNAME</label>
                  <input
                    className="form-input"
                    type="text"
                    placeholder="Enter username"
                    value={loginData.username}
                    onChange={e => setLoginData({ ...loginData, username: e.target.value })}
                    autoFocus
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">PASSWORD</label>
                  <div className="password-wrapper">
                    <input
                      className="form-input"
                      type={showLoginPass ? 'text' : 'password'}
                      placeholder="Enter password"
                      value={loginData.password}
                      onChange={e => setLoginData({ ...loginData, password: e.target.value })}
                      onKeyDown={e => e.key === 'Enter' && handleLogin(e)}
                    />
                    <span className="eye-toggle" onClick={() => setShowLoginPass(!showLoginPass)}>
                      {showLoginPass ? '🙈' : '👁️'}
                    </span>
                  </div>
                </div>

                <button
                  className="btn-primary modal-submit"
                  onClick={handleLogin}
                  disabled={loading}
                >
                  {loading ? 'LOGGING IN...' : 'LOGIN'}
                </button>

                <div className="divider"><span>OR</span></div>

                <a className="btn-google" href={GOOGLE_AUTH_URL}>
                  <i className="fab fa-google"></i> CONTINUE WITH GOOGLE
                </a>

                <div className="modal-switch">
                  No account?{' '}
                  <span onClick={() => { closeModal(); setModal('signup') }}>Sign up</span>
                </div>
              </>
            )}

            {/* ── SIGNUP MODAL ── */}
            {modal === 'signup' && (
              <>
                <div className="modal-title">SIGN UP</div>

                {signupError   && <div className="form-msg error">{signupError}</div>}
                {signupSuccess && <div className="form-msg success">{signupSuccess}</div>}

                <div className="form-group">
                  <label className="form-label">EMAIL</label>
                  <input
                    className="form-input"
                    type="email"
                    placeholder="Enter email"
                    value={signupData.email}
                    onChange={e => setSignupData({ ...signupData, email: e.target.value })}
                    autoFocus
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">USERNAME</label>
                  <input
                    className="form-input"
                    type="text"
                    placeholder="Choose a username"
                    value={signupData.username}
                    onChange={e => setSignupData({ ...signupData, username: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">PASSWORD</label>
                  <div className="password-wrapper">
                    <input
                      className="form-input"
                      type={showSignupPass ? 'text' : 'password'}
                      placeholder="Create a password"
                      value={signupData.password}
                      onChange={e => {
                        setSignupData({ ...signupData, password: e.target.value })
                        checkStrength(e.target.value)
                      }}
                    />
                    <span className="eye-toggle" onClick={() => setShowSignupPass(!showSignupPass)}>
                      {showSignupPass ? '🙈' : '👁️'}
                    </span>
                  </div>

                  {signupData.password && (
                    <div className="strength-bar-container">
                      <div className="strength-bar-track">
                        <div
                          className="strength-bar-fill"
                          style={{ width: strength.width, backgroundColor: strength.color }}
                        />
                      </div>
                      <span className="strength-label" style={{ color: strength.color }}>
                        {strength.label}
                      </span>
                    </div>
                  )}
                </div>

                <button
                  className="btn-primary modal-submit"
                  onClick={handleRegister}
                  disabled={loading}
                >
                  {loading ? 'CREATING...' : 'CREATE ACCOUNT'}
                </button>

                <div className="divider"><span>OR</span></div>

                <a className="btn-google" href={GOOGLE_AUTH_URL}>
                  <i className="fab fa-google"></i> CONTINUE WITH GOOGLE
                </a>

                <div className="modal-switch">
                  Already have an account?{' '}
                  <span onClick={() => { closeModal(); setModal('login') }}>Login</span>
                </div>
              </>
            )}

          </div>
        </div>
      )}

      {/* ── Footer ── */}
      <footer className="footer">
        <div className="footer-left">
          <h4>MORE GAMES & REQUESTS</h4>
          <p>We'd love to hear from you!</p>
          <a href="mailto:bhargavkallepally9@gmail.com">bhargavkallepally9@gmail.com</a>
        </div>
        <div className="footer-right">
          <h4>ABOUT DEVELOPER</h4>
          <p>Developed by <strong style={{ color: 'var(--primary)' }}>Bhargav</strong></p>
          <p>Passionate backend developer building fun and functional web apps.</p>
          <a href="https://bhargav-portfolio-smoky.vercel.app/" target="_blank" rel="noreferrer">
            <i className="fas fa-globe"></i> Portfolio
          </a>
          <a href="https://www.linkedin.com/in/bhargav-kallepally-816504241/" target="_blank" rel="noreferrer">
            <i className="fab fa-linkedin"></i> LinkedIn
          </a>
          <a href="https://github.com/BHARGAV-RUE" target="_blank" rel="noreferrer">
            <i className="fab fa-github"></i> GitHub
          </a>
        </div>
      </footer>

    </div>
  )
}