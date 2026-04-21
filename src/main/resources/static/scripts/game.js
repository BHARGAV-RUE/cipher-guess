// redirect if not logged in
if (!authService.isLoggedIn()) {
    window.location.href = '../pages/index.html';
}

// set username
document.getElementById('nav-username').innerText = authService.getUsername();

// get difficulty from localStorage
const difficulty = localStorage.getItem('difficulty') || 'EASY';
const ranges = { EASY: '1 — 10', MEDIUM: '1 — 50', HARD: '1 — 100', GOD: '1 — 1000' };

document.getElementById('game-difficulty').innerText = difficulty;
document.getElementById('game-range').innerText = ranges[difficulty];

let guessCount = 0;
let gameOver = false;

// ── Submit Guess ──
async function submitGuess() {
    if (gameOver) return;

    const input = document.getElementById('guess-input');
    const guess = parseInt(input.value);

    if (!guess && guess !== 0) {
        shakeInput();
        return;
    }

    const result = await gameService.submitGuess(guess);
    const message = result.message.toUpperCase();

    guessCount++;
    document.getElementById('guess-count').innerText = guessCount;

    // add to history
    addGuessTag(guess, message);

    const resultDisplay = document.getElementById('result-display');
    const resultText = document.getElementById('result-text');

    // clear classes
    resultDisplay.classList.remove('too-high', 'too-low', 'correct');

    if (message.includes('TOO HIGH')) {
        resultDisplay.classList.add('too-high');
        resultText.innerText = 'TOO HIGH ↓';
    } else if (message.includes('TOO LOW')) {
        resultDisplay.classList.add('too-low');
        resultText.innerText = 'TOO LOW ↑';
    } else if (message.includes('CORRECT')) {
        resultDisplay.classList.add('correct');
        resultText.innerText = 'CRACKED!';
        gameOver = true;
        showWinModal(guess, guessCount);
    }

    input.value = '';
    input.focus();
}

// ── Guess History ──
function addGuessTag(guess, message) {
    const history = document.getElementById('guess-history');
    const tag = document.createElement('span');
    tag.classList.add('guess-tag');
    tag.innerText = guess;

    if (message.includes('TOO HIGH')) tag.classList.add('high');
    else if (message.includes('TOO LOW')) tag.classList.add('low');

    history.appendChild(tag);
}

// ── Win Modal ──
function showWinModal(number, guesses) {
    document.getElementById('win-number').innerText   = number;
    document.getElementById('win-guesses').innerText  = guesses;
    document.getElementById('win-modal').classList.add('active');
}

// ── Forfeit ──
async function forfeit() {
    if (gameOver) return;
    const result = await gameService.forfeit();

    // extract number from message like "The number was 42."
    const match = result.message.match(/\d+/);
    const number = match ? match[0] : '?';

    document.getElementById('forfeit-number').innerText = number;
    document.getElementById('forfeit-modal').classList.add('active');
    gameOver = true;
}

// ── Play Again ──
function playAgain() {
    window.location.href = '../pages/dashboard.html';
}

// ── Dashboard ──
function goToDashboard() {
    window.location.href = '../pages/dashboard.html';
}

// ── Enter key support ──
function handleKeyDown(event) {
    if (event.key === 'Enter') submitGuess();
}

// ── Shake animation ──
function shakeInput() {
    const input = document.getElementById('guess-input');
    input.classList.add('shake');
    setTimeout(() => input.classList.remove('shake'), 400);
}

// ── Focus input on load ──
document.getElementById('guess-input').focus();