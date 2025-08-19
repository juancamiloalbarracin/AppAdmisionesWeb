import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5174,
    strictPort: true,
    host: 'localhost',
    proxy: {
      // Proxy todas las llamadas a /api hacia el backend en 8080
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        // Reescribe la ruta si es necesario (aquí mantenemos /api)
        rewrite: (path) => path
      }
    }
  },
  base: './',
  build: {
    rollupOptions: {
      output: {
        manualChunks: undefined
      }
    }
  }
})
