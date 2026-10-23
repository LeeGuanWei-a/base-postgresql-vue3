# 基础平台(base-postgresql-vue3) 数据库设计 — `springboot3_TypeScript` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot3_TypeScript`** 分支
> 前置依赖:《需求文档》v0.1、《概要设计》v0.1
> 本文档是数据落点:schema / 表 / 索引 / 字段注释 / 迁移 / 备份。所有字段以《需求文档》为准,不在此文档内自创新字段。
> **本分支与 `springboot3` / `master` 数据库完全一致**:`basicdb` schema / **34 张表 + 5 函数**;本分支**不新增、不删除、不修改任何表 / 字段 / 索引 / 约束 / 函数**。
> **前端 TypeScript 化不产生任何数据库变更** —— 类型是编译期契约,DB 是运行时存储,二者无耦合。

---

## 1. 总览

### 1.1 Schema 布局

| Schema | 表数 | 内容 |
|---|---|---|
| `basicdb` | **34** | 全部业务表(代码生成 2 + Quartz 11 + 系统 21) |
| `public` | 0(本项目不使用) | — |

> 全部对象由 `file-sql/basicdb-vue3.sql` 一份脚本恢复。
> **JDBC URL 必须带 `currentSchema=basicdb`**,否则 MyBatis-Plus 找不到表。
> 🔶 **已实测核对**(`Select-String "^CREATE TABLE"` 计数 = **34**),与 `springboot3` / `master` 完全一致。

### 1.2 命名约定

| 类别 | 前缀 | 示例 |
|---|---|---|
| 业务系统表 | `sys_` | `sys_user` / `sys_role` / `sys_menu` |
| 代码生成表 | `gen_` | `gen_table` / `gen_table_column` |
| Quartz 内部表 | `qrtz_*` | `qrtz_triggers` / `qrtz_job_details` |
| 通用字段 | — | `create_by` / `create_time` / `update_by` / `update_time` / `remark` / `del_flag` |
| 逻辑删除 | `del_flag` | `0=未删` / `2=已删` |
| 状态 | `status` | `0=正常` / `1=停用 / 关闭` |

> 主键策略:全库统一 **MyBatis-Plus `ASSIGN_ID`(雪花)**(MP 3.5.7,⬆️ 继承 `springboot3`),非 PostgreSQL 原生 `bigserial`。

### 1.3 数据库与角色约定

| 项 | 值 |
|---|---|
| PostgreSQL 版本 | **≥ 15.1** |
| 角色 | `system` / password `system`(`OWNER`) |
| 库 | `basicdb` / `OWNER=system` |
| 字符集 | 默认 UTF8 |
| 时区 | `Asia/Shanghai`(应用侧 `time-zone: GMT+8`) |
| 数据加载方式 | `COPY ... FROM stdin`(`pg_dump` 导出,**只能**用 `psql` 导入) |
| 函数数量 | **5**(`date_format` / `exec` / `find_in_set` / `ifnull` / `tabledef`) |

### 1.4 不使用(与 `springboot3` 一致)

- 不使用外键约束(FK)— 走应用层事务保证
- 不使用 PostgreSQL 原生 schema 隔离以外的方案
- 不使用触发器(`gen_table` 元数据除外)

### 1.5 🔶 本分支独有说明 — **数据库零变更**

| 项 | 结论 |
|---|---|
| 新增表 | **0** |
| 删除表 | **0** |
| 新增字段 | **0** |
| 修改字段类型 | **0** |
| 新增 / 删除索引 | **0 / 0** |
| 新增 / 删除函数 | **0 / 0** |
| 新增 / 删除序列 | **0 / 0** |
| SQL 脚本变更 | **无**(`file-sql/basicdb-vue3.sql` 与 `springboot3` 逐字节一致) |
| 数据迁移 | **不需要** |
| 回滚时的数据回滚 | **不需要** |

> **为什么零变更**:本分支改动范围是 ① 前端 `.js` → `.ts` ② 新增 `tsconfig.json` / `types/` ③ `vue-tsc` 工具链。**三者都是编译期/构建期改造,与数据库 schema 无关**。
>
> 对比:若采用「前端类型由 DB 表结构自动生成(code-first → type-first)」的设计(本分支**未采用**),才需要引入代码生成链路;当前 `types/` 是**手工镜像后端 DTO**,不直连 DB。

### 1.6 🔶 前端类型 ↔ 数据库字段的间接关系(本分支需明示)

```
PostgreSQL 列 (user_name varchar(30))
      │  MyBatis-Plus 映射
      ▼
Java 实体 (SysUser.userName : String)
      │  Jackson 序列化
      ▼
JSON 响应 ({"userName": "admin"})
      │  🔶 前端 interface 声明(手工镜像)
      ▼
TS 类型 (SysUser.userName: string)
```

**关键结论**:

| 场景 | 是否影响前端类型 | 说明 |
|---|---|---|
| DB 列**改名**(`user_name` → `user_login`) | **是** | Java 实体改名 → JSON 字段改名 → **必须手工同步 `types/`**;**TS 不会自动发现**(类型自身仍自洽,R29) |
| DB 列**加长**(`varchar(30)` → `varchar(50)`) | **否** | 前端都是 `string` |
| DB 列**加枚举值**(`data_scope` 从 1~5 扩大到 1~6) | **是** | 前端联合类型 `'1' \| '2' \| ... \| '6'` 需同步扩展 |
| DB 列**改为可空** | **是** | 前端字段从 `string` 变 `string \| undefined` |
| DB 列**精度变化**(`bigint` → `numeric`) | **是** | 前端类型可能需从 `number` 改 `string`(避免 JS 精度丢失) |
| 新增表/字段(不对外暴露) | **否** | 只要不进 VO 就不影响前端 |

> ⚠️ **最大陷阱**:第 1 行。**类型文件忘记同步时,`vue-tsc` 不会报错**(因为类型与自身一致,只是与后端不一致)。这是本分支唯一需要人工纪律保障的环节(列入 code review checklist)。

### 1.7 🔶 与 `springboot3` 的数据库差异 — **无**

| 对比项 | `springboot3` | 本分支 | 差异 |
|---|---|---|---|
| schema | `basicdb` | `basicdb` | 无 |
| 表数量 | 34 | 34 | 无 |
| 函数数量 | 5 | 5 | 无 |
| `sys_config` 数据 | 含 `sys.account.captchaEnabled` | 同 | 无 |
| 验证码 | kaptcha 2.3.3 | **kaptcha 2.3.3**(未换 tianai) | 无 |
| SQL 脚本 | `basicdb-vue3.sql` | 同(逐字节一致) | 无 |

---

## 2. 通用(系统级)表 — `sys_*`(21 张)

> 全部带 `create_by` / `create_time` / `update_by` / `update_time` / `remark` / `del_flag`。
> **所有字段定义与 `springboot3` 完全一致**。本节对每张表额外标注「🔶 前端类型文件」,体现本分支的类型契约映射。

### 2.1 `sys_user` 用户

| 字段 | 类型 | 必填 | 注释 | 🔶 前端类型 |
|---|---|---|---|---|
| user_id | bigint | PK | 雪花 | `number` |
| dept_id | bigint | Y | 部门 | `number` |
| user_name | varchar(30) | Y | 登录名,唯一 | `string` |
| nick_name | varchar(30) | Y | 昵称 | `string` |
| user_type | varchar(2) | N | `00=系统用户` | `string` |
| email | varchar(50) | N | — | `string \| undefined` |
| phonenumber | varchar(11) | N | — | `string \| undefined` |
| sex | char(1) | N | `0=男 1=女 2=未知` | `'0' \| '1' \| '2'` |
| avatar | varchar(100) | N | 头像 URL | `string \| undefined` |
| password | varchar(100) | Y | BCrypt 散列 | `string`(响应通常不返回) |
| status | char(1) | Y | `0=正常 1=停用` | `'0' \| '1'` |
| login_ip | varchar(128) | N | 最近登录 IP | `string \| undefined` |
| login_date | datetime | N | 最近登录时间 | `string \| undefined` |
| create_by / create_time / update_by / update_time / remark | — | — | 通用 | `string?` |
| del_flag | char(1) | Y | `0=未删 2=已删` | `'0' \| '2'` |

索引:`uk_user_name(user_name)` / `idx_dept_id(dept_id)` / `idx_status(status)`
> 🔶 契约:`web-ui/src/types/api/system/user.ts` → `SysUser` / `SysUserQuery`

### 2.2 `sys_role` 角色

| 字段 | 类型 | 必填 | 注释 | 🔶 前端类型 |
|---|---|---|---|---|
| role_id | bigint | PK | 雪花 | `number` |
| role_name | varchar(30) | Y | 显示名 | `string` |
| role_key | varchar(100) | Y | 权限字符,唯一 | `string` |
| role_sort | int | Y | 显示顺序 | `number` |
| data_scope | char(1) | Y | `1=全部 2=自定义 3=本部门 4=本部门及下 5=本人` | `'1' \| '2' \| '3' \| '4' \| '5'` |
| status | char(1) | Y | `0=正常 1=停用` | `'0' \| '1'` |
| 通用字段 | — | — | — | — |

索引:`uk_role_key(role_key)`
> 🔶 契约:`types/api/system/role.ts` → `SysRole`

### 2.3 `sys_menu` 菜单

| 字段 | 类型 | 必填 | 注释 | 🔶 前端类型 |
|---|---|---|---|---|
| menu_id | bigint | PK | 雪花 | `number` |
| menu_name | varchar(50) | Y | 中文名 | `string` |
| menu_name_en | varchar(50) | N | 英文(扩展) | `string \| undefined` |
| menu_name_zh_hant | varchar(50) | N | 繁体(扩展) | `string \| undefined` |
| parent_id | bigint | Y | `0=顶级` | `number` |
| order_num | int | Y | 显示顺序 | `number` |
| path | varchar(200) | N | 前端路由 path | `string \| undefined` |
| component | varchar(255) | N | 前端组件路径 | `string \| undefined` |
| is_frame | char(1) | N | `1=外链 0=内链` | `'0' \| '1'` |
| is_cache | char(1) | N | `0=缓存 1=不缓存` | `'0' \| '1'` |
| menu_type | char(1) | Y | `M=目录 C=菜单 F=按钮` | `'M' \| 'C' \| 'F'` |
| visible | char(1) | N | `0=显示 1=隐藏` | `'0' \| '1'` |
| status | char(1) | Y | `0=正常 1=停用` | `'0' \| '1'` |
| perms | varchar(100) | N | 权限字符(`F` 必填) | `string \| undefined` |
| icon | varchar(100) | N | Element Plus icon | `string \| undefined` |
| is_resource | char(1) | N | 资源标识(扩展) | `'0' \| '1'` |
| show_tags | char(1) | N | 是否显示标签(扩展) | `'0' \| '1'` |
| 通用字段 | — | — | — | — |

索引:`idx_parent_id(parent_id)` / `idx_status(status)`
> 🔶 契约:`types/api/system/menu.ts` → `SysMenu`;`types/api/menu.ts` → `RouterVO` / `MetaVO`(由 `buildMenus` 产出,非 DB 直映)

### 2.4 `sys_dept` 部门(自引用树)

| 字段 | 类型 | 必填 | 注释 | 🔶 前端类型 |
|---|---|---|---|---|
| dept_id | bigint | PK | 雪花 | `number` |
| parent_id | bigint | Y | `0=顶级` | `number` |
| ancestors | varchar(500) | N | `0,1,2` 链路 | `string \| undefined` |
| dept_name | varchar(30) | Y | 同级唯一 | `string` |
| order_num | int | Y | 显示顺序 | `number` |
| leader | varchar(20) | N | 负责人 user_id | `string \| undefined` |
| phone | varchar(11) | N | — | `string \| undefined` |
| email | varchar(50) | N | — | `string \| undefined` |
| status | char(1) | Y | `0=正常 1=停用` | `'0' \| '1'` |
| 通用字段 | — | — | — | — |

索引:`idx_parent_id(parent_id)` / `idx_status(status)`
> 🔶 契约:`types/api/system/dept.ts` → `SysDept`(树形含 `children?: SysDept[]`)

### 2.5 `sys_post` 岗位

| 字段 | 类型 | 必填 | 注释 | 🔶 前端类型 |
|---|---|---|---|---|
| post_id | bigint | PK | 雪花 | `number` |
| post_code | varchar(64) | Y | 唯一 | `string` |
| post_name | varchar(50) | Y | — | `string` |
| post_sort | int | Y | 显示顺序 | `number` |
| status | char(1) | Y | `0=正常 1=停用` | `'0' \| '1'` |
| 通用字段 | — | — | — | — |

索引:`uk_post_code(post_code)`
> 🔶 契约:`types/api/system/post.ts` → `SysPost`

### 2.6 关联表(4 张)

```sql
sys_user_role  (user_id, role_id)            -- 复合 PK
sys_role_menu  (role_id, menu_id)            -- 复合 PK
sys_role_dept  (role_id, dept_id)            -- 复合 PK
sys_user_post  (user_id, post_id)            -- 复合 PK
```

> 🔶 **前端不直接消费**这 4 张表;它们通过 `SysUser.roleIds` / `SysRole.menuIds` / `SysRole.deptIds` 等 `number[]` 字段间接体现。

### 2.7 `sys_dict_type` 字典类型

| 字段 | 类型 | 必填 | 注释 | 🔶 前端类型 |
|---|---|---|---|---|
| dict_id | bigint | PK | 雪花 | `number` |
| dict_name | varchar(100) | Y | — | `string` |
| dict_type | varchar(100) | Y | 类型字符串,唯一 | `string` |
| status | char(1) | Y | `0=正常 1=停用` | `'0' \| '1'` |
| 通用字段 | — | — | — | — |

索引:`uk_dict_type(dict_type)`
> 🔶 契约:`types/api/system/dict.ts` → `SysDictType`

### 2.8 `sys_dict_data` 字典值

| 字段 | 类型 | 必填 | 注释 | 🔶 前端类型 |
|---|---|---|---|---|
| dict_code | bigint | PK | 雪花 | `number` |
| dict_sort | int | Y | 显示顺序 | `number` |
| dict_label | varchar(100) | Y | 显示文本 | `string` |
| dict_value | varchar(100) | Y | 存储值 | `string` |
| dict_type | varchar(100) | Y | 类型 | `string` |
| css_class | varchar(100) | N | 前端样式类 | `string \| undefined` |
| list_class | varchar(100) | N | 列表样式(el-tag type) | `string \| undefined` |
| is_default | char(1) | N | `Y=默认 N=否` | `'Y' \| 'N'` |
| status | char(1) | Y | `0=正常 1=停用` | `'0' \| '1'` |
| 通用字段 | — | — | — | — |

索引:`idx_dict_type(dict_type)`
> 🔶 契约:`types/api/system/dict.ts` → `SysDictData`;
> 🔶 派生类型:`utils/dict.ts` 的 `interface DictDataOption { label; value; elTagType?; elTagClass? }`(由 `dict_label` / `dict_value` / `list_class` / `css_class` 映射)

### 2.9 `sys_config` 全局参数(🔶 验证码总开关所在)

| 字段 | 类型 | 必填 | 注释 | 🔶 前端类型 |
|---|---|---|---|---|
| config_id | int | PK | 自增 | `number` |
| config_name | varchar(100) | Y | — | `string` |
| config_key | varchar(100) | Y | 唯一 | `string` |
| config_value | varchar(500) | Y | — | `string` |
| config_type | char(1) | Y | `N=普通 Y=系统` | `'N' \| 'Y'` |
| is_builtin | char(1) | N | `0=否 1=是` | `'0' \| '1'` |
| 通用字段 | — | — | — | — |

索引:`uk_config_key(config_key)`

> 🔶 **关键 KV**(前端通过 `getConfigKey(key)` 读取,类型见 `types/global.d.ts`):
>
> | config_key | 前端用途 | 本分支 |
> |---|---|---|
> | **`sys.account.captchaEnabled`** | 登录页是否显示验证码 | **kaptcha 开关**(未换 tianai,语义一致) |
> | `sys.account.registerUser` | 是否显示注册入口 | 一致 |
> | `sys.index.skinName` | 主题皮肤 | 一致 |
> | `sys.index.sideTheme` | 侧边栏主题 | 一致 |
>
> 🔶 契约:`types/api/system/config.ts` → `SysConfig`

### 2.10 `sys_notice` 通知公告

| 字段 | 类型 | 必填 | 注释 | 🔶 前端类型 |
|---|---|---|---|---|
| notice_id | int | PK | 自增 | `number` |
| notice_title | varchar(50) | Y | — | `string` |
| notice_type | char(1) | Y | `1=通知 2=公告` | `'1' \| '2'` |
| notice_content | text | Y | — | `string` |
| status | char(1) | Y | `0=正常 1=关闭` | `'0' \| '1'` |
| 通用字段 | — | — | — | — |

> 🔶 契约:`types/api/system/notice.ts` → `SysNotice`

### 2.11 `sys_notice_read` 通知已读

| 字段 | 类型 | 必填 | 注释 | 🔶 前端类型 |
|---|---|---|---|---|
| read_id | bigint | PK | 雪花 | `number` |
| user_id | bigint | Y | 用户 | `number` |
| notice_id | bigint | Y | 通知 | `number` |
| read_time | datetime | Y | — | `string` |
| 通用字段 | — | — | — | — |

索引:`uk_user_notice(user_id, notice_id)` 唯一

### 2.12 `sys_oper_log` 操作日志

| 字段 | 类型 | 必填 | 注释 | 🔶 前端类型 |
|---|---|---|---|---|
| oper_id | bigint | PK | 雪花 | `number` |
| title | varchar(50) | N | 模块名 | `string \| undefined` |
| business_type | int | Y | 业务类型 0~9 | `number` |
| method | varchar(100) | Y | 方法名 | `string` |
| request_method | varchar(10) | N | GET/POST | `string \| undefined` |
| operator_type | int | Y | 0=其它 1=后台 2=手机 | `number` |
| oper_name | varchar(50) | N | 操作人 | `string \| undefined` |
| dept_name | varchar(50) | N | 部门 | `string \| undefined` |
| oper_url | varchar(255) | N | URL | `string \| undefined` |
| oper_ip | varchar(128) | N | IP | `string \| undefined` |
| oper_param | varchar(2000) | N | 入参 JSON | `string \| undefined` |
| json_result | varchar(2000) | N | 出参 JSON | `string \| undefined` |
| status | int | Y | `0=成功 1=失败` | `number` |
| error_msg | varchar(2000) | N | 错误信息 | `string \| undefined` |
| oper_time | datetime | Y | — | `string` |
| cost_time | bigint | N | 耗时 ms | `number \| undefined` |

索引:`idx_business_type(business_type)` / `idx_oper_time(oper_time)`
> 🔶 契约:`types/api/monitor/operlog.ts` → `SysOperLog`

### 2.13 `sys_logininfor` 登录日志(🔶 yauaa 影响 `browser`/`os`)

| 字段 | 类型 | 必填 | 注释 | 🔶 前端类型 |
|---|---|---|---|---|
| info_id | bigint | PK | 雪花 | `number` |
| user_name | varchar(50) | Y | 登录名 | `string` |
| ipaddr | varchar(128) | N | IP | `string \| undefined` |
| login_location | varchar(255) | N | 归属地 | `string \| undefined` |
| browser | varchar(50) | N | 浏览器(⬆️ yauaa 解析) | `string \| undefined` |
| os | varchar(50) | N | 操作系统(⬆️ yauaa 解析) | `string \| undefined` |
| status | char(1) | Y | `0=成功 1=失败` | `'0' \| '1'` |
| msg | varchar(255) | N | 消息 | `string \| undefined` |
| login_time | datetime | Y | — | `string` |

索引:`idx_user_name(user_name)` / `idx_login_time(login_time)`
> 🔶 契约:`types/api/monitor/logininfor.ts` → `SysLogininfor`
> ⚠️ `browser`/`os` 由 yauaa 8.2.0 解析(⬆️ 继承 `springboot3`),输出更长,写入前建议截断(见 §7.3)。

### 2.14 `sys_job` 定时任务

| 字段 | 类型 | 必填 | 注释 | 🔶 前端类型 |
|---|---|---|---|---|
| job_id | bigint | PK | 雪花 | `number` |
| job_name | varchar(64) | Y | — | `string` |
| job_group | varchar(64) | Y | `DEFAULT` / `SYSTEM` | `string` |
| invoke_target | varchar(500) | Y | `ryTask.ryNoParams` | `string` |
| cron_expression | varchar(255) | Y | Cron | `string` |
| misfire_policy | varchar(20) | Y | `0~3` | `'0' \| '1' \| '2' \| '3'` |
| concurrent | char(1) | Y | `0=允许 1=禁止` | `'0' \| '1'` |
| status | char(1) | Y | `0=运行 1=暂停` | `'0' \| '1'` |
| 通用字段 | — | — | — | — |

> 🔶 契约:`types/api/monitor/job.ts` → `SysJob`

### 2.15 `sys_job_log` 任务日志

| 字段 | 类型 | 必填 | 注释 | 🔶 前端类型 |
|---|---|---|---|---|
| job_log_id | bigint | PK | 雪花 | `number` |
| job_name | varchar(64) | Y | — | `string` |
| job_group | varchar(64) | Y | — | `string` |
| invoke_target | varchar(500) | Y | — | `string` |
| job_message | varchar(500) | N | — | `string \| undefined` |
| status | char(1) | Y | `0=成功 1=失败` | `'0' \| '1'` |
| exception_info | text | N | 异常堆栈 | `string \| undefined` |
| create_time | datetime | Y | — | `string` |

> 🔶 契约:`types/api/monitor/jobLog.ts` → `SysJobLog`

### 2.16 `sys_demo` 业务示例(`business-core`)

| 字段 | 类型 | 必填 | 注释 | 🔶 前端类型 |
|---|---|---|---|---|
| id | bigint | PK | 雪花 | `number` |
| name | varchar(50) | Y | — | `string` |
| code | varchar(50) | Y | — | `string` |
| description | varchar(255) | N | — | `string \| undefined` |
| 通用字段 | — | — | — | — |

### 2.17 `sys_files` 文件元数据

| 字段 | 类型 | 必填 | 注释 | 🔶 前端类型 |
|---|---|---|---|---|
| id | bigint | PK | 雪花 | `number` |
| name | varchar(255) | Y | 原始名 | `string` |
| original_name | varchar(255) | N | — | `string \| undefined` |
| url | varchar(500) | Y | 路径 | `string` |
| size | bigint | N | 字节 | `number \| undefined` |
| mime_type | varchar(100) | N | — | `string \| undefined` |
| 通用字段 | — | — | — | — |

### 2.18 `sys_i18n` 国际化

| 字段 | 类型 | 必填 | 注释 | 🔶 前端类型 |
|---|---|---|---|---|
| id | bigint | PK | 雪花 | `number` |
| lang | varchar(20) | Y | `zh-CN` / `en-US` 等 | `string` |
| key | varchar(100) | Y | — | `string` |
| value | varchar(500) | Y | — | `string` |
| 通用字段 | — | — | — | — |

索引:`uk_lang_key(lang, key)` 唯一

### 2.19 ⚠️ 关于 `sys_sms_message`(再次确认不存在)

> **本分支 `file-sql/basicdb-vue3.sql` 中不存在 `sys_sms_message` 表**(实测 `Select-String "sys_sms_message"` 命中 **0**)。
>
> `master` 分支早期《数据库设计》v0.1 曾列出该表,属**误记**;`springboot3` / `springboot2_TypeScript` / `springboot3-tianai-captcha` 文档均已纠正,本分支沿用纠正结论。
>
> **`sys_*` 21 张实测清单**:`sys_config` / `sys_demo` / `sys_dept` / `sys_dict_data` / `sys_dict_type` / `sys_files` / `sys_i18n` / `sys_job` / `sys_job_log` / `sys_logininfor` / `sys_menu` / `sys_notice` / `sys_notice_read` / `sys_oper_log` / `sys_post` / `sys_role` / `sys_role_dept` / `sys_role_menu` / `sys_user` / `sys_user_post` / `sys_user_role`

---

## 3. 代码生成表 — `gen_*`(2 张)

### 3.1 `gen_table`

| 字段 | 类型 | 必填 | 注释 | 🔶 前端类型 |
|---|---|---|---|---|
| table_id | bigint | PK | 雪花 | `number` |
| table_name | varchar(200) | Y | 表名 | `string` |
| table_comment | varchar(500) | Y | 注释 | `string` |
| class_name | varchar(100) | Y | 生成的类名 | `string` |
| tpl_category | varchar(200) | Y | 模板分类 `crud` / `tree` | `'crud' \| 'tree'` |
| package_name | varchar(100) | Y | 生成包路径 | `string` |
| module_name | varchar(30) | Y | 模块名 | `string` |
| business_name | varchar(30) | Y | 业务名 | `string` |
| function_name | varchar(50) | Y | 功能名 | `string` |
| function_author | varchar(50) | Y | 作者 | `string` |
| gen_type | char(1) | Y | `0=zip 下载 1=生成到项目` | `'0' \| '1'` |
| gen_path | varchar(200) | Y | 生成路径 | `string` |
| options | varchar(1000) | N | 其它选项 JSON | `string \| undefined` |
| 通用字段 | — | — | — | — |

> 🔶 契约:`types/api/tool/gen.ts` → `GenTable`

### 3.2 `gen_table_column`

| 字段 | 类型 | 必填 | 注释 | 🔶 前端类型 |
|---|---|---|---|---|
| column_id | bigint | PK | 雪花 | `number` |
| table_id | bigint | Y | 引用 gen_table | `number` |
| column_name | varchar(200) | Y | 列名 | `string` |
| column_comment | varchar(500) | Y | 注释 | `string` |
| column_type | varchar(100) | Y | 数据库类型 | `string` |
| java_type | varchar(100) | Y | Java 类型 | `string` |
| java_field | varchar(100) | Y | Java 字段名 | `string` |
| is_pk / is_increment / is_required / is_insert / is_edit / is_list / is_query | char(1) | Y | 各开关 | `'0' \| '1'` |
| query_type | varchar(200) | Y | `EQ` / `LIKE` 等 | `string` |
| html_type | varchar(200) | Y | `input` / `select` 等 | `string` |
| dict_type | varchar(200) | N | 关联字典 | `string \| undefined` |
| sort | int | Y | 排序 | `number` |

索引:`idx_table_id(table_id)`
> 🔶 契约:`types/api/tool/gen.ts` → `GenTableColumn`

> ⚠️ **⬆️ 继承缺陷(R30)**:代码生成器模板产出 **SB2 风格 Java**(`javax.*` + springfox)+ **JS 风格前端**(`<script setup>` 无 `lang="ts"`、`api.js` 无类型)。生成后需**双份手工修正**:
> 1. Java:`javax.*` → `jakarta.*`;`io.swagger.annotations.*` → `io.swagger.v3.oas.annotations.*`
> 2. 前端:`api.js` → `api.ts` 补类型;`<script setup>` → `<script setup lang="ts">`;补 `types/api/**` interface 并 re-export
>
> 见 A-18(建议给 `extend-generator` 加 TS 模板)。

---

## 4. Quartz 定时任务表 — `qrtz_*`(11 张)

> Quartz 标准表结构,由 `QuartzDisallowConcurrentExecution` / `AbstractQuartzJob` 自动维护。

| 表 | 用途 |
|---|---|
| `qrtz_blob_triggers` | Blob 类型 Trigger |
| `qrtz_calendars` | 日历 |
| `qrtz_cron_triggers` | Cron Trigger |
| `qrtz_fired_triggers` | 已触发的 Trigger |
| `qrtz_job_details` | Job 详情 |
| `qrtz_locks` | 分布式锁 |
| `qrtz_paused_trigger_grps` | 暂停组 |
| `qrtz_scheduler_state` | 调度器状态 |
| `qrtz_simple_triggers` | Simple Trigger |
| `qrtz_simprop_triggers` | 带参数 Trigger |
| `qrtz_triggers` | Trigger 主表 |

> 🔶 本分支**未改**;Quartz 版本由 SB 3 BOM 管理(⬆️ 继承)。
> 🔶 **前端不直接消费** `qrtz_*`;通过 `sys_job` / `sys_job_log` 间接使用。

---

## 5. 函数(5 个,由 SQL 脚本创建)

| 函数 | 签名 | 用途 |
|---|---|---|
| `date_format` | `(indate anyelement, informate text) RETURNS text` | 兼容 MySQL 日期格式化 |
| `exec` | `(sqlstring character varying) RETURNS character varying` | 动态执行 SQL(代码生成器查询元数据) |
| `find_in_set` | `(bigint, character varying) RETURNS boolean` | 兼容 MySQL 逗号串查找 |
| `ifnull` | `(param1 text, param2 text) RETURNS text` | 兼容 MySQL NULL 兜底 |
| `tabledef` | `(oid) RETURNS text` | 取表 DDL 定义 |

> 🔶 本分支**未改**(实测 `CREATE FUNCTION` 计数 = 5);与前端 TS 化无关。

---

## 6. 业务示例与扩展点

### 6.1 业务示例(`sys_demo`)

见 §2.16。

### 6.2 扩展点

- **新增业务模块**:在 `business-core` 下新增 `domain / mapper / service / controller`,对应 `sys_xxx` 表,**不要修改 `basic-admin-core`**
- **新增扩展能力**:在 `extend-*` 模式新增子模块,挂到 `pom.xml` 的 `dependencyManagement` + `modules`
- **代码生成产物**:落 `gen_table`,不落物理表
- **🔶 新增字段时的前端联动**(本分支特有,5 步):
  1. 改 `file-sql/basicdb-vue3.sql`(DDL)
  2. 改 Java 实体 + Mapper XML
  3. **改 `web-ui/src/types/api/<模块>/<实体>.ts`**(必须!)
  4. 跑 `npx vue-tsc --noEmit` 验证
  5. 若为枚举字段,同步扩展前端联合类型(如 `'1' | '2' | ... `)

---

## 7. 索引与查询说明

### 7.1 关键查询索引(⬆️ 与 `springboot3` 一致)

| 关键查询 | 命中索引 | 备注 | 🔶 前端触发入口 |
|---|---|---|---|
| `select * from sys_user where user_name=?` | `uk_user_name` | 登录 | `views/login.vue` |
| `select * from sys_role where role_key=?` | `uk_role_key` | 角色校验 | `views/basic-admin/system/role/index.vue` |
| `select * from sys_menu where parent_id=? and status=0` | `idx_parent_id` | 菜单树 | `views/basic-admin/system/menu/index.vue` |
| `select * from sys_dept where parent_id=? and status=0` | `idx_parent_id` | 部门树 | `views/basic-admin/system/dept/index.vue` |
| `select * from sys_user_role where user_id=?` | 复合 (user_id, role_id) | 权限加载 | `store/modules/user.ts` |
| `select * from sys_role_menu where role_id=?` | 复合 (role_id, menu_id) | 权限加载 | `store/modules/permission.ts` |
| `select * from sys_dict_data where dict_type=? and status=0` | `idx_dict_type` | 字典缓存 | `utils/dict.ts` |
| `select * from sys_config where config_key=?` | `uk_config_key` | 参数读取 | `utils/index.ts`(`getConfigKey`) |
| `select * from sys_oper_log where business_type=? and oper_time between ? and ?` | `idx_business_type` / `idx_oper_time` | 操作日志 | `views/basic-admin/monitor/operlog/index.vue` |
| `select * from sys_logininfor where status=? and login_time between ? and ?` | `idx_login_time` | 登录日志 | `views/basic-admin/monitor/logininfor/index.vue` |
| `select * from sys_notice_read where user_id=? and notice_id=?` | `uk_user_notice` | 唯一命中 | `layout/components/HeaderNotice/index.vue` |
| `select * from qrtz_triggers where TRIGGER_GROUP=?` | Quartz 自带 | 调度 | `views/basic-admin/monitor/job/index.vue` |
| `select * from gen_table where table_name=?` | 物理表主键 | 反查 | `views/basic-admin/tool/gen/index.vue` |

### 7.2 🔶 TS 化对查询性能的影响 — **无**

| 项 | 说明 |
|---|---|
| 运行时 | TS 类型**编译期擦除**,产物与 `springboot3` 等价,SQL 完全一致 |
| 查询次数 | 不变(同页面同接口) |
| 参数序列化 | `request.ts` 的 GET 参数处理逻辑与 `master` 一致 |
| 结论 | **不需要为 TS 化新增任何索引** |

### 7.3 🔶 `sys_logininfor.browser` / `os` 的 yauaa 影响(⬆️ 继承)

| 项 | 说明 |
|---|---|
| 解析库 | bitwalker 1.21 → **yauaa 8.2.0**(⬆️ SB3 升级已做) |
| 影响字段 | `browser`(varchar 50)/ `os`(varchar 50) |
| 风险 | yauaa 输出更详细(如 `Windows 10 (10.0)`),极端情况可能超 50 字符 |
| 处置(不改 DDL) | 写入前 `safeUaValue` 截断 + `"Unknown"` 置空兜底 |

```java
// AsyncFactory 内部(规范要求)
private static String safeUaValue(String v, int maxLen) {
    if (v == null || "Unknown".equalsIgnoreCase(v.trim())) return "";
    v = v.trim();
    return v.length() > maxLen ? v.substring(0, maxLen) : v;
}
```

> 🔶 **前端对应类型**:`SysLogininfor.browser?: string` / `os?: string`(可能为空串)。前端展示时建议 `|| '-'` 兜底。

---

## 8. DDL 约定

| 项 | 约定 |
|---|---|
| 主键 | MyBatis-Plus 雪花(`ASSIGN_ID`),`bigint`,**不要**用 `bigserial` |
| 通用字段 | `create_by` varchar(64) / `create_time` datetime / `update_by` varchar(64) / `update_time` datetime / `remark` varchar(500) / `del_flag` char(1) default '0' |
| 逻辑删除 | `del_flag=2` 视为已删(MP `logic-delete-value=2` / `logic-not-delete-value=0`) |
| 必填 | SQL 层 `NOT NULL`,应用层 `jakarta.validation` 的 `@NotBlank` / `@NotNull`(⬆️ SB3),**前端 `types/` 非可选字段** |
| 字符串 | varchar(N);枚举用 `char(1)` |
| 时间 | `datetime`(应用层 `yyyy-MM-dd HH:mm:ss`);🔶 前端类型统一 `string` |
| 金额 | 若新增,**必须** `numeric(p,s)`,**禁止** `float` / `double`;🔶 前端类型用 `string`(避免 JS 精度丢失) |
| 字符集 | UTF8 |
| 时间戳 | 不用 `timestamp with time zone`,用 `datetime` |
| 归档 | 当前不做物理归档,只逻辑删除 |
| 迁移工具 | 暂时**不**用 Flyway / Liquibase,变更直接改 `basicdb-vue3.sql` 并附说明(见 §12) |
| UA 相关字段 | `browser` / `os` 由 yauaa 写入,`varchar(50)` 建议写入前截断(§7.3) |
| **🔶 前端联动** | **任何 DDL 变更必须同步 `web-ui/src/types/api/**`**(见 §6.2);枚举字段需同步扩展联合类型 |
| **🔶 TS 化约定** | 本次前端改造**不改 DDL**;若必须改,需单独评审 |

---

## 9. 外部引擎表

### 9.1 分支对照

| 引擎 / 能力 | 所在分支 | 本分支是否包含 |
|---|---|---|
| Flowable (`act_*` / `flw_*`) | `springboot2-extend-flowable` / `springboot3-extend-flowable`(`basicdb_flowable`,86 表 + 2 视图) | ❌ 不含 |
| tianai-captcha(Redis 凭证,不落库) | `springboot2-tianai-captcha` / `springboot3-tianai-captcha` | ❌ 不含(本分支仍是 **kaptcha 2.3.3**) |

> 🔶 本分支**不预建 Flowable 表**,也**不含 tianai 变更**;验证码仍是 kaptcha(`sys_config.sys.account.captchaEnabled` 控制开关)。

### 9.2 运行期非 DB 存储(⬆️ 与 `springboot3` 一致)

| 存储 | Key | TTL |
|---|---|---|
| Redis | `login_tokens:<uuid>` | 30min |
| Redis | `sys_dict::<dictType>` | — |
| Redis | `REPEAT_SUBMIT:*` | 500ms |
| 本地磁盘 | `D:/ruoyi/uploadPath` | — |

> 🔶 **本分支不新增任何存储**(TS 化是纯编译期改造)。

---

## 10. 备份与恢复(⬆️ 与 `springboot3` 完全一致)

### 10.1 恢复(全新数据库)

```bash
# 1) 建角色与库
psql -U postgres -h localhost -c "CREATE ROLE system LOGIN PASSWORD 'system';"
psql -U postgres -h localhost -c "CREATE DATABASE basicdb OWNER system;"

# 2) 导入 schema + 数据(必须 psql,含 COPY FROM stdin)
psql -U system -h localhost -d basicdb -f file-sql/basicdb-vue3.sql

# 3) 验证表数量(期望 34)
psql -U system -h localhost -d basicdb -c \
  "SELECT count(*) FROM information_schema.tables WHERE table_schema='basicdb';"

# 4) 验证函数数量(期望 5)
psql -U system -h localhost -d basicdb -c \
  "SELECT count(*) FROM information_schema.routines WHERE routine_schema='basicdb';"
```

### 10.2 备份

```bash
pg_dump -U system -h localhost -d basicdb \
  --schema=basicdb --no-owner --no-privileges \
  --file=file-sql/basicdb-vue3.sql
```

### 10.3 注意事项

- 备份脚本**只能**用 `psql` 执行(含 `COPY ... FROM stdin`)
- 重置 admin 密码:跑 `file-sql/reset-admin-password.sql`
- 其它历史 SQL(`basic-sql-2023-03-02.sql` / `quartz.sql` / `ry_20210908.sql` / `fix-sys-menu-component.sql` / `create-sys-notice-read.sql`)**不要**作为运行入口
- 🔶 **TS 化不涉及备份策略变化**:前端 `types/**` 是源码(随 git 走),不需入库备份
- 🔶 **分支切换无需数据迁移**:`springboot3` ↔ 本分支共用同一份 SQL 与同一份 `sys_config` 数据

---

## 11. 表清单(全文汇总,共 34 张)

| 域 | 表 | 数量 | 大致行数 |
|---|---|---|---|
| 代码生成 | `gen_table` / `gen_table_column` | 2 | 小(<100) |
| Quartz | `qrtz_*`(11 张见 §4) | 11 | 取决于任务数 |
| 系统-鉴权 | `sys_user` / `sys_role` / `sys_menu` / `sys_user_role` / `sys_role_menu` / `sys_role_dept` / `sys_user_post` | 7 | 中 |
| 系统-组织 | `sys_dept` / `sys_post` | 2 | 中 |
| 系统-字典参数 | `sys_config`(验证码开关)/ `sys_dict_type` / `sys_dict_data` | 3 | 小~中 |
| 系统-通知 | `sys_notice` / `sys_notice_read` | 2 | 小 / 大 |
| 系统-审计 | `sys_oper_log` / `sys_logininfor` | 2 | 很大 / 大 |
| 系统-文件国际化 | `sys_files` / `sys_i18n` | 2 | 大 / 小 |
| 系统-定时任务 | `sys_job` / `sys_job_log` | 2 | 小 / 大 |
| 系统-示例 | `sys_demo` | 1 | 演示用 |
| **合计** | | **34** | |

**明细校验**:2(gen)+ 11(qrtz)+ 21(sys)= **34** ✅

> 🔶 `sys_user_online` 由 Redis 维护(`CacheConstants.LOGIN_TOKEN_KEY`),**不写库**,不计入 34。
> 🔶 `sys_sms_message` **不存在**(见 §2.19)。
> 🔶 **本分支独有改动:无**;所有表结构与 `springboot3` 完全一致。

---

## 12. 需求变更记录

| 变更 | 落点 |
|---|---|
| 数据库迁 PostgreSQL | §1 / §10 |
| `sys_notice_read` / `sys_menu` 扩展列 / `sys_i18n` / `sys_files` / `sys_demo` | `springboot3` DBD §2 |
| ⬆️ Spring Boot 2 → 3 升级(yauaa 影响 `browser`/`os`) | §2.13 / §7.3 |
| **🔶 前端 TypeScript 化** | **§1.5(数据库零变更 8 项)+ §1.6(类型↔DB 字段间接关系 6 种场景)+ §1.7(与 springboot3 零差异)+ §2 各表「🔶 前端类型」列 + §3.2(生成器产 JS 风格需手工 ts 化)+ §6.2(新增字段前端联动 5 步)+ §7.2(TS 对性能无影响)+ §8(DDL 约定加前端联动/TS 约定)** |
| **🔶 验证码仍是 kaptcha(明确)** | §1.7 / §9.1 |
| **🔶 纠正 `sys_sms_message` 误记** | §2.19 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot3` 数据库设计(v0.1)为基线,标注本分支**数据库零变更**(§1.5,8 项清单)+ 新增「§1.6 前端类型↔DB 字段间接关系(6 种场景影响表 + 最大陷阱提示)」「§1.7 与 springboot3 零 DB 差异」「§2 各表「🔶 前端类型」列(21 张 sys_* + 2 张 gen_*)」「§3.2 生成器双份手工修正」「§6.2 新增字段前端联动 5 步」「§7.2 TS 对查询性能无影响」「§8 DDL 约定补前端联动/TS 2 条」;实测核对 34 表 / 5 函数,确认 `sys_sms_message` 不存在(§2.19) |
