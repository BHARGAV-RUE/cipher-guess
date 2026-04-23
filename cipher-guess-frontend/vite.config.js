import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/auth': 'https://cipher-guess-production-c391.up.railway.app',
      '/game': 'https://cipher-guess-production-c391.up.railway.app'
    }
  }
})