/**
 * RSA 加密工具 —— 跨 H5 / 微信小程序 / App 兼容
 *
 * 公钥与 web-ui 保持一致（同一对密钥），后端 RsaUtils 用公钥解密。
 *
 * 平台差异说明：
 *   - H5：import ESM 版本，编译时静态打包
 *   - 微信小程序：require UMD 版本（node_modules/jsencrypt/bin/jsencrypt.min.js），
 *                但微信开发者工具 **不会**自动把 node_modules 里的 npm 包打进 miniprogram_npm/，
 *                所以**必须**在「微信开发者工具 → 工具 → 构建 npm」先构建一次。
 *
 * 用法：
 *   import { encrypt } from '@/utils/jsencrypt'
 *   const cipher = encrypt(plainText)
 */

import JSEncryptCtor from 'jsencrypt'

// 与 web-ui 一致的密钥对
const publicKey =
  'MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKoR8mX0rGKLqzcWmOzbfj64K8ZIgOdH\n' +
  'nzkXSOVOZbFu/TJhZ7rFAN+eaGkl3C4buccQd/EjEsj9ir7ijT7h96MCAwEAAQ=='

const privateKey =
  'MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAqhHyZfSsYourNxaY\n' +
  '7Nt+PrgrxkiA50efORdI5U5lsW79MmFnusUA355oaSXcLhu5xxB38SMSyP2KvuKN\n' +
  'PuH3owIDAQABAkAfoiLyL+Z4lf4Myxk6xUDgLaWGximj20CUf+5BKKnlrK+Ed8gA\n' +
  'kM0HqoTt2UZwA5E2MzS4EI2gjfQhz5X28uqxAiEA3wNFxfrCZlSZHb0gn2zDpWow\n' +
  'cSxQAgiCstxGUoOqlW8CIQDDOerGKH5OmCJ4Z21v+F25WaHYPxCFMvwxpcw99Ecv\n' +
  'DQIgIdhDTIqD2jfYjPTY8Jj3EDGPbH2HHuffvflECt3Ek60CIQCFRlCkHpi7hthh\n' +
  'YhovyloRYsM+IS9h/0BzlEAuO0ktMQIgSPT3aFAgJYwKpqRYKlLDVcflZFCKY7u3\n' +
  'UP8iWi1Qw0Y='

/** jsencrypt 实例的最小类型（避开 jsencrypt 自身的 d.ts 不全） */
interface JSEncryptLike {
  setPublicKey(key: string): boolean
  setPrivateKey(key: string): boolean
  encrypt(text: string): string
  decrypt(text: string): string
}

type JSEncryptCtor = new () => JSEncryptLike

/** 加密：明文 → base64 密文（PKCS#1 v1.5） */
export function encrypt(txt: string): string {
  try {
    const e = new (JSEncryptCtor as unknown as JSEncryptCtor)()
    e.setPublicKey(publicKey)
    return e.encrypt(txt) || ''
  } catch (err) {
    console.error('[jsencrypt] encrypt error', err)
    return ''
  }
}

/** 解密（前端通常用不到） */
export function decrypt(txt: string): string {
  try {
    const e = new (JSEncryptCtor as unknown as JSEncryptCtor)()
    e.setPrivateKey(privateKey)
    return e.decrypt(txt) || ''
  } catch (err) {
    console.error('[jsencrypt] decrypt error', err)
    return ''
  }
}
