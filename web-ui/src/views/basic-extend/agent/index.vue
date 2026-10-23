<script setup name="AgentCapability">
/**
 * AgentScope 能力展厅。
 *
 * 左侧导航完全由后端能力注册表（/agentscope/capability/list）驱动，
 * 右侧按能力 id 渲染对应面板。新增后端能力后，刷新本页即可出现在导航里。
 */
import { computed, onMounted, ref } from 'vue'
import { listCapabilities } from '@/api/agentscope'
import { capabilityPanels } from './panels/index.js'
import GenericPanel from './panels/GenericPanel.vue'
import CapabilityDetailCard from './components/CapabilityDetailCard.vue'

const capabilities = ref([])
const loading = ref(false)
const activeId = ref('')
const errorMsg = ref('')

/** 按 category 分组（核心能力 / 外部依赖型） */
const groups = computed(() => {
  const map = new Map()
  capabilities.value.forEach((c) => {
    const key = c.categoryLabel || c.category || '其它'
    if (!map.has(key)) map.set(key, [])
    map.get(key).push(c)
  })
  return Array.from(map.entries()).map(([label, items]) => ({ label, items }))
})

const current = computed(() => capabilities.value.find((c) => c.id === activeId.value) || null)

const currentPanel = computed(() => capabilityPanels[activeId.value] || GenericPanel)

const enabledCount = computed(() => capabilities.value.filter((c) => c.enabled).length)

async function load() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await listCapabilities()
    const list = res.data || []
    capabilities.value = list
    if (!activeId.value && list.length) {
      activeId.value = list[0].id
    }
  } catch (e) {
    errorMsg.value = e?.message || '能力清单加载失败'
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="app-container agent-capability">
    <el-alert
      v-if="errorMsg"
      class="mb8"
      type="error"
      :closable="false"
      :title="errorMsg"
      show-icon
    />

    <el-row :gutter="12">
      <el-col :xs="24" :sm="24" :md="6" :lg="5" :xl="4">
        <el-card shadow="never" class="nav-card">
          <template #header>
            <div class="nav-header">
              <span>能力清单</span>
              <el-badge :value="enabledCount + '/' + capabilities.length" type="info" />
            </div>
          </template>

          <el-skeleton v-if="loading && !capabilities.length" :rows="6" animated />

          <el-empty v-else-if="!capabilities.length" description="未发现后端能力" :image-size="70" />

          <el-menu
            v-else
            :default-active="activeId"
            class="cap-menu"
            @select="(index) => (activeId = index)"
          >
            <el-menu-item-group v-for="group in groups" :key="group.label" :title="group.label">
              <el-menu-item v-for="item in group.items" :key="item.id" :index="item.id">
                <el-icon><component :is="item.icon || 'Menu'" /></el-icon>
                <span>{{ item.name }}</span>
                <el-tag v-if="!item.enabled" class="ml8" type="info" size="small">未就绪</el-tag>
              </el-menu-item>
            </el-menu-item-group>
          </el-menu>

          <el-button class="nav-refresh" link type="primary" :loading="loading" @click="load">
            重新加载能力清单
          </el-button>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :md="18" :lg="19" :xl="20">
        <el-card v-if="current" shadow="never" class="panel-card">
          <template #header>
            <div class="panel-header">
              <div class="panel-title">
                <el-icon class="panel-icon">
                  <component :is="current.icon || 'Menu'" />
                </el-icon>
                <span>{{ current.name }}</span>
                <el-tag size="small" :type="current.category === 'core' ? 'success' : 'warning'">
                  {{ current.categoryLabel }}
                </el-tag>
                <el-tag v-if="!current.enabled" size="small" type="info">依赖未就绪</el-tag>
              </div>
              <span class="panel-id">{{ current.id }}</span>
            </div>
          </template>

          <el-alert
            v-if="current.requires"
            class="mb8"
            type="warning"
            :closable="false"
            show-icon
            :title="'外部依赖：' + current.requires"
          />

          <p class="panel-summary">{{ current.summary }}</p>

          <component :is="currentPanel" :capability="current" />
        </el-card>

        <el-card v-else shadow="never">
          <el-empty description="请选择左侧的能力" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.agent-capability {
  height: 100%;
}
.nav-card {
  margin-bottom: 12px;
}
.nav-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.cap-menu {
  border-right: none;
  max-height: calc(100vh - 320px);
  overflow-y: auto;
}
.nav-refresh {
  margin-top: 8px;
  width: 100%;
}
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.panel-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}
.panel-icon {
  color: var(--el-color-primary);
}
.panel-id {
  color: var(--el-text-color-secondary);
  font-family: Consolas, Monaco, monospace;
  font-size: 12px;
}
.panel-summary {
  color: var(--el-text-color-regular);
  font-size: 13px;
  line-height: 1.7;
  margin: 0 0 12px;
}
.ml8 {
  margin-left: 8px;
}
</style>
