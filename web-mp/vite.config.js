import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'
import path from 'path'
import { fileURLToPath } from 'url'
import { spawnSync } from 'child_process'
import fs from 'fs'

/**
 * uniapp 3 + Vite 模式构建配置。
 *
 * 工程布局：
 *   web-mp/                ← vite 根
 *   ├── index.html
 *   ├── vite.config.js
 *   ├── package.json
 *   ├── config.js
 *   └── src/               ← @dcloudio/uni-cli-shared 硬编码找这里
 *       ├── main.js
 *       ├── App.vue
 *       ├── manifest.json
 *       ├── pages.json
 *       ├── uni.scss
 *       └── ...
 */
const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)
const ROOT = __dirname

/**
 * 注入微信小程序 usingComponents 的 fixup 插件
 *
 * 监听 build 钩子，每次编译后跑 scripts/fixup-mp-components.cjs
 * 解决 uniapp 3 alpha 不支持 pages.json easycom 的问题
 */
function mpFixupPlugin() {
  return {
    name: 'web-mp:fixup-mp-components',
    apply: 'build',
    closeBundle() {
      // dev 模式由 watch re-run 处理；build 模式只跑一次
      const script = path.join(ROOT, 'scripts', 'fixup-mp-components.cjs')
      if (fs.existsSync(script)) {
        const r = spawnSync(process.execPath, [script], {
          stdio: 'inherit',
          cwd: ROOT
        })
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
    resolve: {
      alias: {
        '@': ROOT
      },
      extensions: ['.js', '.ts', '.json', '.vue']
    },
    // uview-plus 3 要求：把它加入 transpileDependencies 才能被 vite 正确处理
    transpileDependencies: ['uview-plus'],
    // 让 vite 在 dev 启动时预编译 uview-plus 内部依赖
    optimizeDeps: {
      include: ['uview-plus']
    },
    // dev server 配置
    server: {
      port: 8081
      // 不再配 /dev-api 代理 —— src/config.js 统一指向真实 IP/域名
    }
  }
})
