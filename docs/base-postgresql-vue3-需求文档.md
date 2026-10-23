# 基础平台(base-postgresql-vue3) 需求文档(PRD)

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 `master` 分支(全栈:后端 + web-ui + web-mp)
> 前置依赖:《架构方案》v0.1

---

## 1. 文档信息与版本

- 当前版本:**v0.1** — 初稿,基于 README + 代码扫描抽取
- 变更历史

| 版本 | 日期 | 变更 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:覆盖系统管理、定时任务、代码生成、PC 端、移动端、锁屏解锁 |

---

## 2. 背景与目标

### 2.1 为什么做

本仓库在 RuoYi-Vue 3.8.2 基础上,以「**后端零升级 + 数据库迁 PostgreSQL + 增加移动端**」为最小改动原则,提供一套对内/对外可复用的后台管理基础平台。背景包括:

- 公司多个内部系统都需要 RBAC + 字典 + 定时任务 + 代码生成,重复造轮子成本高
- 移动办公场景越来越普及,仅有 PC 端无法满足一线运营
- 旧平台基于 MySQL,统一改 PostgreSQL 后,可与其它业务线共享中间件

### 2.2 目标清单

| 目标 | 成功标准 |
|---|---|
| G1 提供完整的 RBAC 后台管理能力 | 角色/菜单/部门/岗位/用户/字典/参数 全部可在线管理 |
| G2 提供定时任务在线编排 | 通过 Web 配置 Cron,执行可观测(`sys_job_log`) |
| G3 提供数据表反向代码生成 | 选择表 → 一键生成 Controller/Service/Mapper/Vue 页面 |
| G4 提供 PC + 移动双端 | web-ui 满足桌面运营;web-mp 满足移动办公 |
| G5 保证接口兼容 RuoYi 3.8.2 | `/login` `/logout` `/captchaImage` `/getInfo` `/getRouters` 命名/行为与原版一致 |
| G6 数据库统一在 PostgreSQL `basicdb` schema | 一份 `pg_dump` 脚本可恢复整套 34 张表 |

### 2.3 非目标(本分支不做)

- 业务流程建模 / 审批(走 `master-extend-flowable` 分支)
- 多租户 / SaaS 化
- SSO 联邦(走 keycloak 集成项目)
- Spring Boot 2 → 3/4 升级(走 `springboot3` / `springboot4` 分支)
- 微服务拆分 / K8s 化 / 灰度发布
- 国际化完整词条(仅引入 vue-i18n,词条待补)

---

## 3. 术语表

| 术语 | 含义 |
|---|---|
| RBAC | Role-Based Access Control,基于角色的访问控制 |
| 数据权限 | 在功能权限之上,基于「本人 / 本部门 / 本部门及子 / 全部 / 自定义」的数据范围过滤 |
| 菜单权限 | 决定是否能看到菜单项(对应路由元数据 `meta.roles` / `perms`) |
| 按钮权限 | 决定页面上按钮是否显示(`v-hasPermi` / `@PreAuthorize`) |
| 锁屏 | 离开座位时手动锁定前端,需重新输入密码解锁;不销毁 Token |
| Kaptcha | 后端图形验证码,基于 `kaptcha 2.3.2` |
| `basicdb` | PostgreSQL schema,本项目所有表所在 |
| `qrtz_*` | Quartz 表,共 11 张 |
| `gen_*` | 代码生成元数据表,共 2 张 |
| uview-plus | uView 的 Vue3 版本,组件前缀 `up-` |

---

## 4. 用户角色与使用场景

### 4.1 角色

| 角色 | 关键动作 | 频率 |
|---|---|---|
| **超级管理员(`admin`)** | 全部功能,含角色/菜单/用户/部门维护、代码生成、监控查看 | 高 |
| **业务管理员** | 受限于「数据权限」的 CRUD,通常负责本部门数据 | 中 |
| **审计员** | 仅看 `sys_oper_log` / `sys_logininfor`,无写操作 | 低 |
| **业务运营(PC)** | 通知/字典/参数查询,通知公告发布 | 高 |
| **业务运营(移动)** | 登录、查看通知、个人中心、移动端特有页面 | 中 |
| **开发者** | 用代码生成器建表/生成模块,导入字典 | 中 |

### 4.2 关键场景走查

#### 场景 S1:运营首次登录并查看通知(PC)

1. 打开 `http://<host>:8080/` → 跳转 `/login`
2. 输入账号 → 调 `/captchaImage` 拿验证码
3. 输入密码 → 前端用 `jsencrypt` RSA 加密 → 调 `/login`
4. 后端返回 `token` → 前端存 `Basic-Token` → 调 `/getInfo` 拿用户/角色/权限
5. 调 `/getRouters` 拿动态路由 → 生成左侧菜单
6. 进入首页 → 头部铃铛展示未读公告数

#### 场景 S2:管理员新增角色并赋菜单

1. 系统管理 → 角色管理 → 新增
2. 填写 角色名/权限字符/显示顺序/数据范围
3. 分配菜单(树形勾选)→ 分配部门
4. 保存 → 角色绑定到用户 → 用户下次登录生效(角色变更缓存失效)

#### 场景 S3:开发者用代码生成器建模块

1. 工具 → 代码生成 → 导入表 `sys_demo`
2. 同步字段、编辑生成配置(模块名/业务名/生成路径)
3. 下载 zip → 解压到后端对应包,前端 `src/views/<module>/` 目录
4. 重启服务 → 业务菜单已就绪

#### 场景 S4:移动端登录(H5)

1. `web-mp` H5 启动 → `/pages/login/index`
2. 调 `/captchaImage` → 调 `/login`(同样 RSA + BCrypt)
3. 存 `Basic-Token` → 进入工作台 TabBar
4. 调 `/getInfo` 拿用户信息 → 调 `/getRouters`(本分支暂仅预留,菜单用本地 `pages.json`)

#### 场景 S5:锁屏与解锁(PC)

1. 头部下拉 → 锁定 → `lock-screen=true` 隐藏应用,展示锁屏页
2. 输入密码 → 调 `SysLoginService.login`(同登录入口,Token 复用)
3. 密码正确 → 隐藏锁屏,恢复应用
4. 失败 → 提示并保留锁屏

#### 场景 S6:定时任务 Cron 在线配置

1. 监控 → 定时任务 → 新增
2. 填 任务名称 / 调用目标字符串(`ryTask.ryNoParams`) / Cron 表达式 / 执行策略
3. 保存 → 立即生效,可在列表「执行一次」
4. 任务失败/成功 → `sys_job_log` 记录,可查日志

---

## 5. 总体需求

### 5.1 范围图

```
                    ┌────────────────────────────────┐
                    │   平台能力(本仓库)            │
                    │  ┌──────────┐  ┌───────────┐  │
                    │  │ PC Web   │  │ 移动 H5/  │  │
                    │  │ (Vue3)   │  │ 小程序/App │  │
                    │  └────┬─────┘  └─────┬─────┘  │
                    │       │  HTTP/JSON   │        │
                    │  ┌────┴───────────────┴─────┐  │
                    │  │   Spring Boot API       │  │
                    │  │  (basic-admin-api)      │  │
                    │  └────┬────────────────────┘  │
                    │       │  MyBatis-Plus         │
                    │  ┌────┴────────────────────┐  │
                    │  │  PostgreSQL basicdb     │  │
                    │  │  (34 表 + 5 函数)       │  │
                    │  └─────────────────────────┘  │
                    └────────────────────────────────┘
```

### 5.2 核心规则总表

| 模块 | 关键字段 | L1 校验(字段级) | L2 校验(跨模块) |
|---|---|---|---|
| 用户 | `userName` / `password` / `phonenumber` | 必填、长度、邮箱格式、手机号正则 | 用户名全局唯一;同一部门下不能重名 |
| 角色 | `roleName` / `roleKey` / `dataScope` | 必填、`roleKey` 英文+数字、显示顺序 0-999 | `roleKey` 全局唯一;`admin` 角色不可删 |
| 菜单 | `menuName` / `menuType(M/C/F)` / `perms` | 必填、类型枚举、组件路径合法 | 上级菜单 + 路由组件 + 权限字符(若 type=F) |
| 部门 | `deptName` / `parentId` | 必填、parentId 不能等于自己 | 父级链路不能形成环 |
| 岗位 | `postCode` / `postName` | 必填、长度 | 全局唯一 `postCode` |
| 字典 | `dictType` / `dictLabel` | 必填 | 字典类型存在性校验;`dictValue` 类型内唯一 |
| 通知 | `noticeTitle` / `noticeType` / `status` | 必填、长度 | 发布前必须已选 `status=1` 才对外可见 |
| 定时任务 | `invokeTarget` / `cronExpression` | 必填、Cron 合法 | `invokeTarget` 必须对应 `@Component("ryTask")` 内方法 |
| 代码生成 | `tableName` | 必填、英文+下划线 | 表必须已存在 `basicdb` schema |

---

## 6. 功能需求

### 6.1 文件上传与下载

| 项 | 规则 |
|---|---|
| 单文件大小 | ≤ 10MB(`spring.servlet.multipart.max-file-size`) |
| 单次请求 | ≤ 20MB(`max-request-size`) |
| 存储位置 | `D:/ruoyi/uploadPath`(可通过 `BasicConfig.profile` 修改) |
| 访问路径 | `/profile/**`(走 `ResourcesConfig`) |
| 允许扩展名 | 白名单控制(`MimeTypeUtils`) |
| 失败处理 | 上传/下载失败抛出 `FileException` / `InvalidExtensionException`,前端弹窗提示 |

### 6.2 自动校验(L1)

后端入口通过 `@Validated` + `javax.validation` 注解完成字段级校验,失败由 `GlobalExceptionHandler` 包装为 `R.fail(HttpStatus.BAD_REQUEST, msg)`。

### 6.3 错误明细与重跑

- 登录失败:`sys_logininfor` 记录原因(账号/密码/验证码),前端提示。
- 业务校验失败:返回 `R.fail(code, msg)`,前端 toast。
- 定时任务失败:`sys_job_log` 记录异常堆栈,可在「日志」页查看。

### 6.4 业务模块需求(按模块细化)

#### 6.4.1 用户管理

| 字段 | 类型 | 必填 | 校验 |
|---|---|---|---|
| `userId` | bigint | 自增 | 主键 |
| `userName` | varchar(30) | 是 | 唯一,长度 2-30,字母数字下划线 |
| `nickName` | varchar(30) | 是 | 长度 1-30 |
| `email` | varchar(50) | 否 | 邮箱格式 |
| `phonenumber` | varchar(11) | 否 | 国内手机号正则 |
| `sex` | char(1) | 否 | 0=男 1=女 2=未知 |
| `avatar` | varchar(100) | 否 | URL |
| `password` | varchar(100) | 是 | BCrypt 散列,数据库存密文 |
| `status` | char(1) | 是 | 0=正常 1=停用 |
| `deptId` | bigint | 是 | 存在性校验 |
| `postIds` | bigint[] | 否 | 存在性校验,落 `sys_user_post` |
| `roleIds` | bigint[] | 否 | 存在性校验,落 `sys_user_role` |
| `remark` | varchar(500) | 否 | — |

#### 6.4.2 角色管理

| 字段 | 类型 | 必填 | 校验 |
|---|---|---|---|
| `roleId` | bigint | 自增 | 主键 |
| `roleName` | varchar(30) | 是 | 长度 1-30 |
| `roleKey` | varchar(100) | 是 | 全局唯一,英文/数字/下划线,首字母小写 |
| `roleSort` | int | 是 | 0-999 |
| `dataScope` | char(1) | 是 | 1=全部 2=本部门 3=本部门及下 4=本人 5=自定义 |
| `status` | char(1) | 是 | 0=正常 1=停用 |
| `menuIds` | bigint[] | 否 | 落 `sys_role_menu` |
| `deptIds` | bigint[] | 否 | 落 `sys_role_dept`(`dataScope=5` 时必填) |

#### 6.4.3 菜单管理

| 字段 | 类型 | 必填 | 校验 |
|---|---|---|---|
| `menuId` | bigint | 自增 | 主键 |
| `menuName` | varchar(50) | 是 | 长度 1-50 |
| `parentId` | bigint | 是 | 0=顶级 |
| `menuType` | char(1) | 是 | M=目录 C=菜单 F=按钮 |
| `path` | varchar(200) | 否 | `M`/`C` 必填,前端路由路径 |
| `component` | varchar(255) | 否 | 前端组件路径 |
| `perms` | varchar(100) | 否 | `F` 必填,权限字符 |
| `icon` | varchar(100) | 否 | Element Plus icon |
| `isFrame` | char(1) | 否 | 1=外链 0=内链 |
| `visible` | char(1) | 否 | 0=显示 1=隐藏 |
| `status` | char(1) | 是 | 0=正常 1=停用 |

> `sys_menu` 还有扩展列 `is_resource` / `show_tags` / `menu_name_en` / `menu_name_zh_hant`,用于资源标识/标签显示/国际化。

#### 6.4.4 部门管理

| 字段 | 类型 | 必填 | 校验 |
|---|---|---|---|
| `deptId` | bigint | 自增 | 主键 |
| `parentId` | bigint | 是 | 0=顶级 |
| `deptName` | varchar(30) | 是 | 同级唯一 |
| `ancestors` | varchar(500) | 否 | 系统生成,`0,1,2` 链路 |
| `orderNum` | int | 是 | 0-999 |
| `leader` | varchar(20) | 否 | 关联 `sys_user.user_id` |
| `phone` / `email` | varchar | 否 | 格式校验 |
| `status` | char(1) | 是 | 0=正常 1=停用 |

#### 6.4.5 字典管理

- `sys_dict_type`:字典类型(全局),字段 `dictId` / `dictName` / `dictType` / `status` / `remark`
- `sys_dict_data`:字典值,字段 `dictCode` / `dictSort` / `dictLabel` / `dictValue` / `dictType` / `cssClass` / `listClass` / `isDefault` / `status`
- 缓存:Redis 缓存,key=`sys_dict::<dictType>`,失效/主动清除由 `DictUtils` 管理

#### 6.4.6 通知公告

| 字段 | 类型 | 必填 | 校验 |
|---|---|---|---|
| `noticeId` | int | 自增 | 主键 |
| `noticeTitle` | varchar(50) | 是 | 长度 1-50 |
| `noticeType` | char(1) | 是 | 1=通知 2=公告 |
| `noticeContent` | text | 是 | — |
| `status` | char(1) | 是 | 0=正常 1=关闭 |
| `createBy` / `createTime` | — | — | 系统填充 |
| 已读 | `sys_notice_read(user_id, notice_id)` 唯一 | | |

#### 6.4.7 定时任务

| 字段 | 类型 | 必填 | 校验 |
|---|---|---|---|
| `jobId` | bigint | 自增 | 主键 |
| `jobName` | varchar(64) | 是 | 长度 1-64 |
| `jobGroup` | varchar(64) | 是 | DEFAULT / SYSTEM |
| `invokeTarget` | varchar(500) | 是 | `ryTask.ryNoParams` / `ryTask.ryParams('a','b')` |
| `cronExpression` | varchar(255) | 是 | Cron 合法 |
| `misfirePolicy` | varchar(20) | 是 | 0=默认 1=立即触发 2=触发一次 3=不触发 |
| `concurrent` | char(1) | 是 | 0=允许 1=禁止 |
| `status` | char(1) | 是 | 0=运行 1=暂停 |

#### 6.4.8 代码生成

- 列表:`/tool/gen/list` 返回所有 `gen_table` + 字段
- 导入表:`/tool/gen/importTable` 传入表名 → 自动反查 `information_schema.columns` 落 `gen_table_column`
- 同步:`/tool/gen/synchDb` 用物理表结构更新元数据
- 生成:前端下载 zip,内含 `Controller/Service/Mapper/Mapper.xml/Vue`

#### 6.4.9 文件/字典/通知国际化

- `sys_i18n`:平台级别国际化表,key/value 形式
- `sys_files`:文件元数据表(URL/原始名/大小/上传人/上传时间)

### 6.5 跨业务比对(L2)

- 用户名/角色 key 全局唯一:在 `Service` 显式 `checkUnique*` 调用
- 字典值类型内唯一:`dictType` 维度下 `dictValue` 唯一
- 通知已读:`(user_id, notice_id)` 唯一索引

### 6.6 审批复核(本分支不涉及)

> 走 `master-extend-flowable` 分支实现,见该分支 PRD。

### 6.7 统计报表

- 操作日志:按时间段/模块/操作人筛选,导出 Excel(`@Excel` 注解)
- 登录日志:同上
- 服务器监控:`ServerController` 实时返回 CPU/内存/磁盘/JVM

### 6.8 审计

- 写操作:`@Log(title, businessType = BusinessType.UPDATE)` → 切面 → `sys_oper_log`
- 登录:`AsyncManager` 异步 → `sys_logininfor`(成功/失败)
- 异常:全局 `GlobalExceptionHandler` 落日志,可选落库(默认仅控制台)

---

## 7. 非功能需求

| 属性 | 目标 |
|---|---|
| 一致性 | 单服务事务边界在 Service 层,异步日志/统计不污染主链路 |
| 可靠性 | 操作日志失败不影响主业务;定时任务异常隔离(每个 Job 独立 try/catch) |
| 性能 | 列表分页 1000 条数据 < 1s;监控实时接口 < 200ms;登录链路 < 500ms |
| 审计合规 | 90 天操作日志可查询、可导出 |
| 安全 | RSA 加密登录密码;BCrypt 散列存储;JWT 30min 过期;XSS 过滤;重复提交拦截;限流;显式 CORS 白名单 |
| 可用性 | 单实例 99.9%(运维保障) |
| 部署 | 单一可执行 jar,`tomcat.max-threads=800` |

---

## 8. 权限与角色

### 8.1 RBAC 权限点矩阵(摘录)

| 模块 | 权限字符 | 操作 |
|---|---|---|
| 系统管理/用户管理 | `system:user:list` / `query` / `add` / `edit` / `remove` / `resetPwd` / `export` / `import` | 列表/查询/新增/编辑/删除/重置密码/导出/导入 |
| 系统管理/角色管理 | `system:role:list` / `add` / `edit` / `remove` / `export` / `query` | 同上 |
| 系统管理/菜单管理 | `system:menu:list` / `add` / `edit` / `remove` | 增删改查 |
| 系统管理/部门管理 | `system:dept:list` / `add` / `edit` / `remove` | 增删改查 |
| 系统管理/岗位管理 | `system:post:list` / `add` / `edit` / `remove` | 增删改查 |
| 系统管理/字典管理 | `system:dict:list` / `add` / `edit` / `remove` / `export` | 增删改查+导出 |
| 系统管理/参数设置 | `system:config:list` / `add` / `edit` / `remove` / `export` | 增删改查+导出 |
| 系统管理/通知公告 | `system:notice:list` / `add` / `edit` / `remove` | 增删改查 |
| 监控/操作日志 | `monitor:operlog:list` / `export` / `remove` / `clean` | 查/导出/删/清空 |
| 监控/登录日志 | `monitor:logininfor:list` / `export` / `remove` / `clean` / `unlock` | 查/导出/删/清空/解锁 |
| 监控/在线用户 | `monitor:online:list` / `forceLogout` | 强退 |
| 监控/定时任务 | `monitor:job:list` / `add` / `edit` / `remove` / `changeStatus` / `run` / `export` | 增删改+启停+执行 |
| 监控/任务日志 | `monitor:job:query` / `remove` / `clean` / `export` | 查/删/清空/导出 |
| 工具/代码生成 | `tool:gen:list` / `importTable` / `download` / `synchDb` / `edit` / `remove` / `preview` | 导入/下载/同步/编辑/删除/预览 |
| 系统/user | `system:user:import` | 导入 |

### 8.2 数据权限

通过 `@DataScope` 注解 + `DataScopeAspect` 在 SQL 上追加 `dept_id IN (...)` 等条件,支持五种 `dataScope`:

| 值 | 含义 |
|---|---|
| 1 | 全部 |
| 2 | 自定义(`sys_role_dept.dept_id` 范围) |
| 3 | 本部门 |
| 4 | 本部门及下级 |
| 5 | 本人(`create_by = #{userId}`) |

---

## 9. 边界与不做清单

| 项 | 说明 |
|---|---|
| 业务流程 | 走 `master-extend-flowable` |
| 微服务化 | 不做 |
| SSO 联邦 | 不做(后续 keycloak 集成) |
| SB 2 → 3 升级 | 走 `springboot3` 分支 |
| TS 化 | 走 `springboot2_TypeScript` / `springboot3_TypeScript` 等 |
| 移动端完整业务 | master 仅做登录 + 通知 + 个人中心,业务流程/审批不做 |
| 国际化词条 | 已引入,词条待补 |

---

## 10. 验收标准

| # | 验收项 | 验收方法 |
|---|---|---|
| AC-01 | `mvn clean compile` 0 ERROR | 终端命令 |
| AC-02 | `mvn spring-boot:run` 启动成功,看到「基础平台启动成功」 | 终端 |
| AC-03 | 7 个子模块 `parent.version` = 3.8.2 | `mvn validate` 或 IDE |
| AC-04 | `psql -f file-sql/basicdb-vue3.sql` 恢复后,`information_schema.tables WHERE table_schema='basicdb'` 返回 34 | 终端 |
| AC-05 | admin/admin123 登录 PC 端成功,看到完整菜单 | 浏览器 |
| AC-06 | admin/admin123 登录 web-mp H5 成功,看到工作台 | 浏览器 |
| AC-07 | 锁屏→输入密码→解锁 成功 | 浏览器 |
| AC-08 | 新建角色→分配菜单→绑定用户→用户重新登录可见新菜单 | 浏览器 |
| AC-09 | 工具/代码生成 → 导入 `sys_demo` → 下载 zip 解压到对应目录 → 重启可访问 | 终端 + 浏览器 |
| AC-10 | 监控/定时任务 → 新建 Cron → 启动 → 任务日志可见 | 浏览器 |
| AC-11 | 操作日志可导出 Excel | 浏览器 |
| AC-12 | XSS 测试:输入 `<script>alert(1)</script>` → 后端拒绝 | 终端/浏览器 |
| AC-13 | 重复提交测试:同 URL 同 body 500ms 内两次 → 第二次被拒绝 | 浏览器 |
| AC-14 | swagger 可访问:`/swagger-ui.html` | 浏览器 |

---

## 附录A 待确认问题清单

| 编号 | 问题 | 建议默认 | 阻塞 |
|---|---|---|---|
| A-1 | 移动端是否需要走完整动态路由? | 暂用本地 `pages.json`,动态路由留待 M2 | 否 |
| A-2 | web-mp 是否实现锁屏? | 暂不实现,留待 M2 | 否 |
| A-3 | web-mp Token 失效后是否自动重登? | 先做 401 跳转登录,refresh token 留待 M2 | 否 |
| A-4 | 操作日志保留周期? | 90 天(`sys_oper_log` 物理保留,后续可定时清理) | 否 |
| A-5 | 多端登录是否互踢? | 当前 Token 无设备指纹,不互踢;若需要,新增 `token_service` 强制下线 | 否 |
| A-6 | 通知公告是否支持富文本(图片/附件)? | 暂为 `text`,富文本编辑器已就位,后续按需启用 | 否 |
| A-7 | 代码生成器是否支持主子表? | 暂不支持,后续迭代 | 否 |

---

## 附录B 业务回填模板

```markdown
### 模块: <名称>
| 字段 | 类型 | 必填 | 校验 |
|---|---|---|---|
| ... | ... | ... | ... |
```

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿,覆盖 8 大业务模块 + 8 个跨域场景 |
