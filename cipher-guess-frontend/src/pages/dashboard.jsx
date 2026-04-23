import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import authService from '../services/authService'
import gameService from '../services/gameService'
import './dashboard.css'

const DIFFICULTIES = [
  { key: 'EASY',   range: '1 — 10',   desc: 'Warm up',        god: false },
  { key: 'MEDIUM', range: '1 — 50',   desc: 'Getting hot',    god: false },
  { key: 'HARD',   range: '1 — 100',  desc: 'Real challenge', god: false },
  { key: 'GOD',    range: '1 — 1000', desc: 'Are you serious?', god: true },
]

export default function Dashboard() {
  const navigate = useNavigate()
  const [stats, setStats]           = useState(null)
  const [selected, setSelected]     = useState(null)
  const username = authService.getUsername()

  useEffect(() => {
    gameService.getStats().then(setStats)
  }, [])

  async function startGame() {
    if (!selected) return
    const result = await gameService.startGame(selected)
    if (result.success) {
      localStorage.setItem('difficulty', selected)
      navigate('/game')
    }
  }

  async function logout() {
    await authService.logout()
    navigate('/')
  }

  return (
    <div className="dashboard-page">

      {/* ── Navbar ── */}
      <nav className="nav-bar">
        <span className="text-heading">CiPhErGuEsS</span>
        <div className="nav-right">
          <span className="nav-username">👾 {username}</span>
          <button className="btn-outline" onClick={logout}>LOGOUT</button>
        </div>
      </nav>

      <main className="dashboard-main">

        {/* Welcome */}
        <div className="welcome-section">
          <p className="welcome-label">WELCOME BACK</p>
          <h1 className="welcome-name">{username?.toUpperCase()}</h1>
          <p className="welcome-sub">Ready to crack some numbers?</p>
        </div>

        {/* Stats */}
        <div className="stats-grid">
          <div className="stat-card">
            <span className="stat-icon">🎮</span>
            <p className="stat-value">{stats?.totalGames ?? 0}</p>
            <p className="stat-label">Total Games</p>
          </div>
          <div className="stat-card stat-card--gold">
            <span className="stat-icon">🏆</span>
            <p className="stat-value">{stats?.totalWins ?? 0}</p>
            <p className="stat-label">Total Wins</p>
          </div>
          <div className="stat-card">
            <span className="stat-icon">💀</span>
            <p className="stat-value">{stats?.totalLosses ?? 0}</p>
            <p className="stat-label">Total Losses</p>
          </div>
          <div className="stat-card">
            <span className="stat-icon">⚡</span>
            <p className="stat-value">{stats?.bestScore === 0 ? '—' : (stats?.bestScore ?? '—')}</p>
            <p className="stat-label">Best Score</p>
          </div>
          <div className="stat-card stat-card--wide">
            <span className="stat-icon">📈</span>
            <div>
              <p className="stat-value">{stats ? stats.winRate.toFixed(1) + '%' : '0%'}</p>
              <p className="stat-label">Win Rate</p>
            </div>
            <div className="win-rate-bar">
              <div className="win-rate-fill"
                style={{ width: stats ? stats.winRate + '%' : '0%' }} />
            </div>
          </div>
        </div>

        {/* Difficulty Select */}
        <div className="game-select-section">
          <p className="section-label">SELECT DIFFICULTY</p>
          <div className="difficulty-grid">
            {DIFFICULTIES.map(d => (
              <button
                key={d.key}
                className={`diff-btn ${d.god ? 'diff-btn--god' : ''} ${selected === d.key ? 'selected' : ''}`}
                onClick={() => setSelected(d.key)}
              >
                <span className="diff-range">{d.range}</span>
                <span className="diff-name">{d.key}</span>
                <span className="diff-desc">{d.desc}</span>
              </button>
            ))}
          </div>
          <button
            className={`btn-primary play-btn ${selected ? 'ready' : ''}`}
            onClick={startGame}
            disabled={!selected}
          >
            CRACK IT
          </button>
        </div>

      </main>
    </div>
  )
}