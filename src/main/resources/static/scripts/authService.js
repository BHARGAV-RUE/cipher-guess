const authService = {

    async register(username, email, password) {
        try {
            const response = await fetch('/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, email, password })
            });
            const message = await response.text();
            if (message === 'Registered Successfully!') {
                return { success: true, message };
            }
            return { success: false, message };
        } catch (err) {
            return { success: false, message: 'Server error. Try again.' };
        }
    },

    async login(username, password) {
        try {
            const response = await fetch('/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });
            const token = await response.text();
            if (response.ok && token && !token.includes(' ')) {
                localStorage.setItem('token', token);
                localStorage.setItem('username', username);
                return { success: true };
            }
            return { success: false, message: token };
        } catch (err) {
            return { success: false, message: 'Server error. Try again.' };
        }
    },

    async logout() {
        const token = localStorage.getItem('token');
        if (token) {
            await fetch('/auth/logout', {
                method: 'POST',
                headers: { 'Authorization': 'Bearer ' + token }
            });
        }
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        window.location.href = '/pages/index.html';
    },

    getToken() {
        return localStorage.getItem('token');
    },

    getUsername() {
        return localStorage.getItem('username');
    },

    isLoggedIn() {
        return !!localStorage.getItem('token');
    }
};