// ── Modal Controls ──
function showLogin() {
    document.getElementById('login-modal').classList.add('active');
}

function showSignin() {
    document.getElementById('signin-modal').classList.add('active');
}

function closeModals() {
    document.getElementById('login-modal').classList.remove('active');
    document.getElementById('signin-modal').classList.remove('active');
    document.getElementById('login-error').innerText = '';
    document.getElementById('signin-error').innerText = '';
}

function switchToLogin() {
    document.getElementById('signin-modal').classList.remove('active');
    document.getElementById('login-modal').classList.add('active');
}

function switchToSignin() {
    document.getElementById('login-modal').classList.remove('active');
    document.getElementById('signin-modal').classList.add('active');
}

// close modal when clicking outside
document.querySelectorAll('.modal-overlay').forEach(overlay => {
    overlay.addEventListener('click', function (e) {
        if (e.target === this) closeModals();
    });
});

// ── Auth Actions ──
async function login() {
    const username = document.getElementById('login-username').value.trim();
    const password = document.getElementById('login-password').value.trim();
    const errorEl  = document.getElementById('login-error');

    if (!username || !password) {
        errorEl.innerText = 'Please fill in all fields';
        return;
    }

    const result = await authService.login(username, password);
    if (result.success) {
        window.location.href = '/pages/dashboard.html';
    } else {
        errorEl.innerText = result.message;
    }
}

async function register() {
    const email    = document.getElementById('signin-email').value.trim();
    const username = document.getElementById('signin-username').value.trim();
    const password = document.getElementById('signin-password').value.trim();
    const errorEl  = document.getElementById('signin-error');

    if (!email || !username || !password) {
        errorEl.innerText = 'Please fill in all fields';
        return;
    }

    const result = await authService.register(username, email, password);
    if (result.success) {
        errorEl.style.color = '#00ff88';
        errorEl.innerText = 'Account created! Please login.';
        setTimeout(switchToLogin, 1500);
    } else {
        errorEl.innerText = result.message;
    }
}