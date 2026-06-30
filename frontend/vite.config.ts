import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
      imports: ['vue', 'vue-router', 'pinia'],
      dts: 'src/auto-imports.d.ts',
    }),
    Components({
      resolvers: [ElementPlusResolver()],
      dts: 'src/components.d.ts',
    }),
  ],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
  server: {
    port: 3000,
    proxy: {
      // 统一代理：所有 API 调用皆为 POST，GET 请求是前端 SPA 路由
      // bypass GET → 返回 index.html，避免 Spring Boot 报 "No static resource"
      '/product': {
        target: 'http://localhost:8080',
        bypass(req) { if (req.method === 'GET') return '/index.html' },
      },
      '/equipment': {
        target: 'http://localhost:8080',
        bypass(req) { if (req.method === 'GET') return '/index.html' },
      },
      '/order': {
        target: 'http://localhost:8080',
        bypass(req) { if (req.method === 'GET') return '/index.html' },
      },
      '/plan': {
        target: 'http://localhost:8080',
        bypass(req) { if (req.method === 'GET') return '/index.html' },
      },
      '/schedule': {
        target: 'http://localhost:8080',
        bypass(req) { if (req.method === 'GET') return '/index.html' },
      },
      '/dailyWork': {
        target: 'http://localhost:8080',
        bypass(req) { if (req.method === 'GET') return '/index.html' },
      },
      '/user': {
        target: 'http://localhost:8080',
        bypass(req) { if (req.method === 'GET') return '/index.html' },
      },
      '/factory': {
        target: 'http://localhost:8080',
        bypass(req) { if (req.method === 'GET') return '/index.html' },
      },
      '/dashboard': {
        target: 'http://localhost:8080',
        bypass(req) { if (req.method === 'GET') return '/index.html' },
      },
    },
  },
})
