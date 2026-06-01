import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],

  server: {
    proxy: {

      /*
      ─────────────────────────────────────
      LOCAL DEVELOPMENT
      Frontend  → localhost:5173
      Backend   → localhost:8080
      ─────────────────────────────────────
      */

      '/auth': 'http://localhost:8080',
      '/game': 'http://localhost:8080',

      /*
      ─────────────────────────────────────
      PRODUCTION BACKEND (Railway)
      Uncomment when needed
      ─────────────────────────────────────
      */

      // '/auth': 'https://cipher-guess-production-c391.up.railway.app',
      // '/game': 'https://cipher-guess-production-c391.up.railway.app'
    }
  }
})