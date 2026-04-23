import { useState, useEffect, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import authService from '../services/authService'
import gameService from '../services/gameService'
import './game.css'

const RANGES = { EASY: '1 — 10', MEDIUM: '1 — 50', HARD: '1 — 100', GOD: '1 — 1000' }

export default function Game() {
  const navigate  = useNavigate()
  const inputRef  = useRef(null)
  const difficulty = localStorage.getItem('difficulty') || 'EASY'

  const [guess, setGuess]         = useState('')
  const [result, setResult]       = useState({ text: 'ENTER YOUR GUESS', type: '' })
  const [guessCount, setGuessCount] = useState(0)
  const [history, setHistory]     = useState([])
  const [gameOver, setGameOver]   = useState(false)
  const [winModal, setWinModal]   = useState(null)   // { number, guesses }
  const [forfeitModal, setForfeitModal] = useState(null) // { number }
  const [shake, setShake]         = useState(false)

  useEffect(() => { inputRef.current?.focus() }, [])

  async function submitGuess() {
    if (gameOver) return
    const num = parseInt(guess)
    if (!num && num !== 0) { triggerShake(); return }

    const res = await gameService.submitGuess(num)
    const msg = res.message.toUpperCase()
    const newCount = guessCount + 1
    setGuessCount(newCount)

    if (msg.includes('TOO HIGH')) {
      setResult({ text: 'TOO HIGH ↓', type: 'too-high' })
      setHistory(h => [...h, { value: num, type: 'high' }])
    } else if (msg.includes('TOO LOW')) {
      setResult({ text: 'TOO LOW ↑', type: 'too-low' })
      setHistory(h => [...h, { value: num, type: 'low' }])
    } else if (msg.includes('CORRECT')) {
      setResult({ text: 'CRACKED!', type: 'correct' })
      setGameOver(true)
      setWinModal({ number: num, guesses: newCount })
    }

    setGuess('')
    inputRef.current?.focus()
  }

  async function handleForfeit() {
    if (gameOver) return
    const res = await gameService.forfeit()
    const match = res.message.match(/\d+/)
    setForfeitModal({ number: match ? match[0] : '?' })
    setGameOver(true)
  }

  function triggerShake() {
    setShake(true)
    setTimeout(() => setShake(false), 400)
  }

  function handleKeyDown(e) {
    if (e.key === 'Enter') submitGuess()
  }

  return (
    <div className="game-page">

      {/* ── Navbar ── */}
      <nav className="nav-bar">
        <span className="text-heading">CiPhErGuEsS</span>
        <div className="nav-right">
          <span className="nav-username">👾 {authService.getUsername()}</span>
          <button className="btn-outline" onClick={() => navigate('/dashboard')}>DASHBOARD</button>
        </div>
      </nav>

      <main className="game-main">

        {/* Header */}
        <div className="game-header">
          <div className="game-info">
            <span className="game-difficulty">{difficulty}</span>
            <span className="game-range">{RANGES[difficulty]}</span>
          </div>
          <div className="guess-counter">
            <span className="guess-label">GUESSES</span>
            <span className="guess-count">{guessCount}</span>
          </div>
        </div>

        {/* Result */}
        <div className={`result-display ${result.type}`}>
          <p className="result-text">{result.text}</p>
        </div>

        {/* History */}
        <div className="guess-history">
          {history.map((h, i) => (
            <span key={i} className={`guess-tag ${h.type}`}>{h.value}</span>
          ))}
        </div>

        {/* Input */}
        <div className="input-area">
          <input
            ref={inputRef}
            className={`guess-input ${shake ? 'shake' : ''}`}
            type="number"
            placeholder="Type your number..."
            value={guess}
            onChange={e => setGuess(e.target.value)}
            onKeyDown={handleKeyDown}
            disabled={gameOver}
          />
          <button className="btn-primary guess-btn" onClick={submitGuess} disabled={gameOver}>
            GUESS
          </button>
        </div>

        {/* Forfeit */}
        {!gameOver && (
          <button className="forfeit-btn" onClick={handleForfeit}>FORFEIT</button>
        )}

      </main>

      {/* ── Win Modal ── */}
      {winModal && (
        <div className="modal-overlay active">
          <div className="modal-box win-modal-box">
            <h2 className="modal-title win-title">CRACKED IT!</h2>
            <p className="win-number">{winModal.number}</p>
            <p className="win-message">You got it in <strong>{winModal.guesses}</strong> guess(es)!</p>
            <div className="win-buttons">
              <button className="btn-primary" onClick={() => navigate('/dashboard')}>PLAY AGAIN</button>
              <button className="btn-outline" onClick={() => navigate('/dashboard')}>DASHBOARD</button>
            </div>
          </div>
        </div>
      )}

      {/* ── Forfeit Modal ── */}
      {forfeitModal && (
        <div className="modal-overlay active">
          <div className="modal-box">
            <h2 className="modal-title">FORFEITED</h2>
            <p className="forfeit-message">
              The number was <strong className="gold-text">{forfeitModal.number}</strong>
            </p>
            <div className="win-buttons">
              <button className="btn-primary" onClick={() => navigate('/dashboard')}>TRY AGAIN</button>
              <button className="btn-outline" onClick={() => navigate('/dashboard')}>DASHBOARD</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}