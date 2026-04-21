const gameService = {

    async startGame(difficulty) {
        const token = authService.getToken();
        const response = await fetch(`/game/start?difficulty=${difficulty}`, {
            method: 'POST',
            headers: { 'Authorization': 'Bearer ' + token }
        });
        const message = await response.text();
        return { success: response.ok, message };
    },

    async submitGuess(guess) {
        const token = authService.getToken();
        const response = await fetch(`/game/guess?guess=${guess}`, {
            method: 'POST',
            headers: { 'Authorization': 'Bearer ' + token }
        });
        const message = await response.text();
        return { success: response.ok, message };
    },

    async forfeit() {
        const token = authService.getToken();
        const response = await fetch('/game/forfeit', {
            method: 'POST',
            headers: { 'Authorization': 'Bearer ' + token }
        });
        const message = await response.text();
        return { success: response.ok, message };
    },

    async getStats() {
        const token = authService.getToken();
        const response = await fetch('/game/stats', {
            headers: { 'Authorization': 'Bearer ' + token }
        });
        if (!response.ok) return null;
        return await response.json();
    }
};