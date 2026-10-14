import { defineStore } from 'pinia'

interface AppState {
  sidebar: { opened: boolean }
  device: 'mobile' | 'desktop'
  size: 'default' | 'small' | 'large'
}

export const useAppStore = defineStore('app', {
  state: (): AppState => ({
    sidebar: { opened: false },
    device: 'mobile',
    size: 'default'
  }),
  actions: {
    toggleSidebar(): void {
      this.sidebar.opened = !this.sidebar.opened
    }
  }
})
