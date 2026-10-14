import { createPinia } from 'pinia'

const pinia = createPinia()

export default pinia

export { useUserStore } from './modules/user'
export { usePermissionStore } from './modules/permission'
export { useAppStore } from './modules/app'
