import { createWebHistory, createRouter } from 'vue-router'
/* Layout */
import Layout from '@/layout'

/**
 * Note: 路由配置项
 *
 * hidden: true                     // 当设置 true 的时候该路由不会再侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
 * alwaysShow: true                 // 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
 *                                  // 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
 *                                  // 若你想不管路由下面的 children 声明的个数都显示你的根路由
 *                                  // 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
 * redirect: noRedirect             // 当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
 * name:'router-name'               // 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
 * query: '{"id": 1, "name": "ry"}' // 访问路由的默认传递参数
 * roles: ['admin', 'common']       // 访问路由的角色权限
 * permissions: ['a:a:a', 'b:b:b']  // 访问路由的菜单权限
 * meta : {
    noCache: true                   // 如果设置为true，则不会被 <keep-alive> 缓存(默认 false)
    title: 'title'                  // 设置该路由在侧边栏和面包屑中展示的名字
    icon: 'svg-name'                // 设置该路由的图标，对应路径src/assets/icons/svg
    breadcrumb: false               // 如果设置为false，则不会在breadcrumb面包屑中显示
    activeMenu: '/system/user'      // 当路由设置了该属性，则会高亮相对应的侧边栏。
  }
 */

// 公共路由
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index.vue')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login'),
    hidden: true
  },
  {
    path: '/register',
    component: () => import('@/views/register'),
    hidden: true
  },
  {
    path: "/:pathMatch(.*)*",
    component: () => import('@/views/error/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error/401'),
    hidden: true
  },
  {
    path: '',
    component: Layout,
    redirect: '/index',
    children: [
      {
        path: '/index',
        component: () => import('@/views/index'),
        name: 'Index',
        meta: { title: '首页', icon: 'dashboard', affix: true }
      }
    ]
  },
  {
    path: '/lock',
    component: () => import('@/views/lock'),
    hidden: true,
    meta: { title: '锁定屏幕' }
  },
  {
    path: '/user',
    component: Layout,
    hidden: true,
    redirect: 'noredirect',
    children: [
      {
        path: 'profile/:activeTab?',
        component: () => import('@/views/system/user/profile/index'),
        name: 'Profile',
        meta: { title: '个人中心', icon: 'user' }
      }
    ]
  },
  // ==================== 工作流 hidden 路由（操作型页面，不在侧边栏显示）====================
  {
    path: '/workflow/category/dataForm',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '',
        component: () => import('@/views/workflow/category/dataForm'),
        name: 'WfCategoryForm',
        meta: { title: '流程分类表单', activeMenu: '/workflow/category' }
      }
    ]
  },
  {
    path: '/workflow/model/design',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '',
        component: () => import('@/views/workflow/model/design'),
        name: 'WfModelDesign',
        meta: { title: '流程设计器', activeMenu: '/workflow/model' }
      }
    ]
  },
  {
    path: '/workflow/process/start',
    component: Layout,
    // 兼容两种访问方式：
    // 1) 从「流程模型」列表点「发起」按钮进入（隐藏路由，activeMenu 指向 /workflow/model）
    // 2) 直接点左侧菜单「测试启动流程」进入（独立菜单项，由后端菜单数据驱动）
    // activeMenu 留空，让前端按实际路由路径自动匹配高亮
    hidden: true,
    children: [
      {
        path: '',
        component: () => import('@/views/workflow/process/start'),
        name: 'WfProcessStart',
        meta: { title: '测试启动流程', activeMenu: '' }
      }
    ]
  },
  {
    // 流程迁移：菜单靠 sys_menu 驱动（parent_id=1500 流程引擎下）。
    // router 这条只作路由占位 + activeMenu 高亮跟随流程引擎，不在左侧菜单独立显示。
    path: '/workflow/migrate',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '',
        component: () => import('@/views/workflow/migrate/index'),
        name: 'WfProcessMigrate',
        meta: { title: '流程迁移', activeMenu: '/workflow' }
      }
    ]
  },
  {
    path: '/workflow/process/detail',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '',
        component: () => import('@/views/workflow/process/detail'),
        name: 'WfProcessDetail',
        meta: { title: '流程详情', activeMenu: '/workflow/task/todo' }
      }
    ]
  },
  {
    path: '/workflow/form/design',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '',
        component: () => import('@/views/workflow/form/design'),
        name: 'WfFormDesign',
        meta: { title: '表单设计器', activeMenu: '/workflow/form' }
      }
    ]
  }
]

// 动态路由，基于用户权限动态去加载
export const dynamicRoutes = [
  {
    path: '/system/user-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:user:edit'],
    children: [
      {
        path: 'role/:userId(\\d+)',
        component: () => import('@/views/system/user/authRole'),
        name: 'AuthRole',
        meta: { title: '分配角色', activeMenu: '/system/user' }
      }
    ]
  },
  {
    path: '/system/role-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:role:edit'],
    children: [
      {
        path: 'user/:roleId(\\d+)',
        component: () => import('@/views/system/role/authUser'),
        name: 'AuthUser',
        meta: { title: '分配用户', activeMenu: '/system/role' }
      }
    ]
  },
  {
    path: '/system/dict-data',
    component: Layout,
    hidden: true,
    permissions: ['system:dict:list'],
    children: [
      {
        path: 'index/:dictId(\\d+)',
        component: () => import('@/views/system/dict/data'),
        name: 'Data',
        meta: { title: '字典数据', activeMenu: '/system/dict' }
      }
    ]
  },
  {
    path: '/monitor/job-log',
    component: Layout,
    hidden: true,
    permissions: ['monitor:job:list'],
    children: [
      {
        path: 'index/:jobId(\\d+)',
        component: () => import('@/views/monitor/job/log'),
        name: 'JobLog',
        meta: { title: '调度日志', activeMenu: '/monitor/job' }
      }
    ]
  },
  {
    path: '/tool/gen-edit',
    component: Layout,
    hidden: true,
    permissions: ['tool:gen:edit'],
    children: [
      {
        path: 'index/:tableId(\\d+)',
        component: () => import('@/views/tool/gen/editTable'),
        name: 'GenEdit',
        meta: { title: '修改生成配置', activeMenu: '/tool/gen' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    }
    return { top: 0 }
  },
})

export default router
