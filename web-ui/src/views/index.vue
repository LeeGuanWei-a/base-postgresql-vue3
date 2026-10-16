<template>
  <div class="app-container home">
    <el-row :gutter="20">
      <el-col :sm="24" :lg="24" style="padding-left: 20px">
        <h2>基础平台</h2>
        <p>
          面向企业内部业务场景的统一管理平台，提供用户、角色、权限、部门、岗位、字典、参数等基础能力，以及系统监控、定时任务、代码生成等运维工具。
        </p>
        <p>
          <b>当前版本:</b> <span>v{{ version }}</span>
        </p>
        <p>
          <el-tag type="success">内部使用</el-tag>
        </p>
      </el-col>

      <el-col :sm="24" :lg="12" style="padding-left: 50px">
        <el-row>
          <el-col :span="12">
            <h2>技术选型</h2>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <h4>后端技术</h4>
            <ul>
              <li>Spring Boot</li>
              <li>Spring Security</li>
              <li>JWT</li>
              <li>MyBatis Plus</li>
              <li>PostgreSQL</li>
              <li>Redis</li>
              <li>Druid</li>
            </ul>
          </el-col>
          <el-col :span="8">
            <h4>前端技术</h4>
            <ul>
              <li>Vue 3</li>
              <li>Vite</li>
              <li>Element Plus</li>
              <li>Pinia</li>
              <li>Vue Router</li>
              <li>Axios</li>
              <li>Sass</li>
            </ul>
          </el-col>
          <el-col :span="8">
            <h4>基础设施</h4>
            <ul>
              <li>Springdoc OpenAPI</li>
              <li>Nginx</li>
              <li>MinIO</li>
              <li>Quartz</li>
              <li>Yauaa（User-Agent 解析）</li>
            </ul>
          </el-col>
        </el-row>
      </el-col>
    </el-row>

    <el-divider />

    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card class="update-log">
          <template #header>
            <div class="clearfix">
              <span>快捷入口</span>
            </div>
          </template>
          <div class="quick-links">
            <el-button type="primary" plain icon="User" @click="goRoute('/system/user')">用户管理</el-button>
            <el-button type="primary" plain icon="UserFilled" @click="goRoute('/system/role')">角色管理</el-button>
            <el-button type="primary" plain icon="Menu" @click="goRoute('/system/menu')">菜单管理</el-button>
            <el-button type="primary" plain icon="OfficeBuilding" @click="goRoute('/system/dept')">部门管理</el-button>
            <el-button type="primary" plain icon="Notebook" @click="goRoute('/system/dict')">字典管理</el-button>
            <el-button type="primary" plain icon="Operation" @click="goRoute('/monitor/operlog')">操作日志</el-button>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card class="update-log">
          <template #header>
            <div class="clearfix">
              <span>系统说明</span>
            </div>
          </template>
          <ul class="notice-list">
            <li>所有接口调用均需在请求头携带 <code>Authorization: Bearer {token}</code>。</li>
            <li>登录密码默认 <code>admin123</code>，首次登录后请尽快修改。</li>
            <li>用户、角色、菜单变更后需重新登录方可生效。</li>
            <li>如遇问题请联系平台运维团队。</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts" name="Index">
import { useRouter } from 'vue-router'

const version = ref<string>('4.0.0')
const router = useRouter()

function goRoute(path: string): void {
  router.push(path)
}
</script>

<style scoped lang="scss">
.home {
  padding: 20px;
}
</style>
