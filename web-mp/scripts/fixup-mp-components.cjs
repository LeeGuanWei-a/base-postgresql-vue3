#!/usr/bin/env node
/**
 * mp-weixin build 后处理：
 * 把每个 page 的 index.json 注入 usingComponents（指向 uview-plus 组件）。
 *
 * 原因：uniapp 3 alpha + Vite 模式下，pages.json 的 easycom 配置不被识别，
 *       编译产物的 usingComponents 是空 {}。微信小程序要求 .json 声明
 *       自定义组件，否则 wxml 里的 <up-form> 标签会被当成 HTML 标签不渲染。
 *
 * 策略：
 *   1. 扫描 dist/.../mp-weixin/node-modules/uview-plus/components/ 下
 *      实际被 uniapp 编译出来的组件目录（有完整的 .js .json .wxml .wxss）
 *   2. 把这些组件的路径（u-xxx / up-xxx 双前缀）注入每个 page 的 usingComponents
 *   3. 用项目根绝对路径（/node-modules/...），避免 pages/ 嵌套层数差异
 */

const fs = require('fs')
const path = require('path')

function findMpWeixinOutput() {
  const dist = path.join(process.cwd(), 'dist')
  for (const sub of ['dev', 'build']) {
    const p = path.join(dist, sub, 'mp-weixin')
    if (fs.existsSync(p)) return p
  }
  return null
}

function findActualComponentDirs(mpRoot) {
  const distComp = path.join(mpRoot, 'node-modules', 'uview-plus', 'components')
  if (!fs.existsSync(distComp)) return []
  return fs
    .readdirSync(distComp, { withFileTypes: true })
    .filter((d) => {
      if (!d.isDirectory()) return false
      if (d.name.startsWith('u--')) return false
      const dir = path.join(distComp, d.name)
      return (
        fs.existsSync(path.join(dir, d.name + '.js')) &&
        fs.existsSync(path.join(dir, d.name + '.json')) &&
        fs.existsSync(path.join(dir, d.name + '.wxml')) &&
        fs.existsSync(path.join(dir, d.name + '.wxss'))
      )
    })
    .map((d) => d.name)
}

function injectUsingComponents(jsonPath, components) {
  const raw = fs.readFileSync(jsonPath, 'utf-8')
  let data
  try {
    data = JSON.parse(raw)
  } catch (e) {
    return false
  }

  // 用项目根绝对路径（以 / 开头），微信小程序会从项目根解析
  const usingComponents = {}
  components.forEach((cname) => {
    const realName = cname.replace(/^u-/, '')
    const cmpPath = `/node-modules/uview-plus/components/${cname}/${cname}`
    usingComponents[`u-${realName}`] = cmpPath
    usingComponents[`up-${realName}`] = cmpPath
  })

  data.usingComponents = usingComponents
  fs.writeFileSync(jsonPath, JSON.stringify(data, null, 2))
  return true
}

function walkJson(dir) {
  const results = []
  if (!fs.existsSync(dir)) return results
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name)
    if (entry.isDirectory()) {
      results.push(...walkJson(full))
    } else if (entry.name === 'index.json') {
      results.push(full)
    }
  }
  return results
}

function main() {
  // 支持 --watch 模式：每 1.5s 检查一次 dist 是否有新文件，有就跑 fixup
  if (process.argv.includes('--watch')) {
    console.log('[fixup-mp-components] watch mode: 1.5s polling...')
    let lastMtime = 0
    const mp = findMpWeixinOutput()
    if (!mp) {
      console.log('[fixup-mp-components] no mp-weixin output, exit watch')
      return
    }
    setInterval(() => {
      try {
        const stat = fs.statSync(mp)
        if (stat.mtimeMs > lastMtime) {
          lastMtime = stat.mtimeMs
          runOnce()
        }
      } catch (e) {
        // mp 目录被删了，等下次
      }
    }, 1500)
    return
  }

  runOnce()
}

function runOnce() {
  const mp = findMpWeixinOutput()
  if (!mp) {
    return
  }
  const components = findActualComponentDirs(mp)
  if (components.length === 0) {
    return
  }
  console.log(`[fixup-mp-components] injecting ${components.length} components`)

  const pagesDir = path.join(mp, 'pages')
  const pageJsons = walkJson(pagesDir)
  let count = 0
  pageJsons.forEach((j) => {
    injectUsingComponents(j, components)
    count++
  })

  const appJson = path.join(mp, 'app.json')
  if (fs.existsSync(appJson)) {
    injectUsingComponents(appJson, components)
  }
  console.log(`[fixup-mp-components] done (${count} pages + app.json)`)
}

main()
