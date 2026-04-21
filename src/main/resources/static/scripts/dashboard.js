// redirect if not logged in
if (!authService.isLoggedIn()) {
    window.location.href = '../pages/index.html';
}

// set username display
const username = authService.getUsername();
document.getElementById('nav-username').innerText = username;
document.getElementById('welcome-username').innerText = username.toUpperCase();

let selectedDifficulty = null;

// ── Load Stats ──
async function loadStats() {
    const token = authService.getToken();
    const response = await fetch('/game/stats', {
        headers: { 'Authorization': 'Bearer ' + token }
    });

    if (!response.ok) return;

    const stats = await response.json();

    document.getElementById('total-games').innerText  = stats.totalGames;
    document.getElementById('total-wins').innerText   = stats.totalWins;
    document.getElementById('total-losses').innerText = stats.totalLosses;
    document.getElementById('best-score').innerText   = stats.bestScore === 0 ? '—' : stats.bestScore;
    document.getElementById('win-rate').innerText     = stats.winRate.toFixed(1) + '%';

    // animate win rate bar
    setTimeout(() => {
        document.getElementById('win-rate-bar').style.width = stats.winRate + '%';
    }, 500);
}

// ── Difficulty Selection ──
function selectDifficulty(diff) {
    selectedDifficulty = diff;

    // update button styles
    document.querySelectorAll('.diff-btn').forEach(btn => {
        btn.classList.remove('selected');
    });
    document.querySelector(`[data-diff="${diff}"]`).classList.add('selected');

    // enable play button
    document.getElementById('play-btn').classList.add('ready');
}

// ── Start Game ──
async function startGame() {
    if (!selectedDifficulty) return;

    const token = authService.getToken();
    const response = await fetch(`/game/start?difficulty=${selectedDifficulty}`, {
        method: 'POST',
        headers: { 'Authorization': 'Bearer ' + token }
    });

    if (response.ok) {
        // save difficulty for game page
        localStorage.setItem('difficulty', selectedDifficulty);
        window.location.href = '../pages/game.html';
    }
}

// ── Logout ──
async function logout() {
    await authService.logout();
}

// ── Init ──
loadStats();