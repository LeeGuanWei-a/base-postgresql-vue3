#!/usr/bin/env node
/**
 * 同步根目录的 manifest.json / pages.json 到 src/ 下（保持双份一致）。
 * 适用 uniapp 3 alpha：CLI build 找 src/manifest.json，HBuilderX/dev 找根 manifest.json。
 * 用 Node 写，不依赖 PowerShell，跨平台通用。
 */
const fs = require('fs')
const path = require('path')

const root = __dirname
const files = ['manifest.json', 'pages.json']

for (const f of files) {
  const src = path.join(root, f)
  const dst = path.join(root, 'src', f)
  if (fs.existsSync(src)) {
    fs.copyFileSync(src, dst)
    console.log('synced: ' + f + ' -> src/' + f)
  } else {
    console.warn('skip (not found): ' + f)
  }
}
