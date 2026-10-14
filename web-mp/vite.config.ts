import { defineConfig, type PluginOption } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { spawnSync } from 'node:child_process'
import fs from 'node:fs'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)
const ROOT = __dirname

function mpFixupPlugin(): PluginOption {
  return {
    name: 'web-mp:fixup-mp-components',
    apply: 'build',
    closeBundle() {
      const script = path.join(ROOT, 'scripts', 'fixup-mp-components.cjs')
      if (fs.existsSync(script)) {
        const r = spawnSync(process.execPath, [script], { stdio: 'inherit', cwd: ROOT })
        if (r.status !== 0) {
          console.warn('[mp-fixup] fixup script returned', r.status)
        }
      }
    }
  }
}

export default defineConfig(({ command }) => {
  return {
    plugins: command === 'build' ? [uni(), mpFixupPlugin()] : [uni()],
    resolve: { alias: { '@': ROOT }, extensions: ['.js', '.ts', '.json', '.vue'] },
    transpileDependencies: ['uview-plus'],
    optimizeDeps: { include: ['uview-plus'] },
    server: { port: 8081 }
  }
})
