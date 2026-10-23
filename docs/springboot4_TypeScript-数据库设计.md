# 基础平台(base-postgresql-vue3) 数据库设计 — `springboot4_TypeScript` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot4_TypeScript`** 分支
> 前置依赖:《需求文档》v0.1、《概要设计》v0.1
> 本文档是数据落点:schema / 表 / 索引 / 字段注释 / 迁移 / 备份。
> **本分支与 `springboot4` / `master` 数据库完全一致**:`basicdb` schema / **34 张表 + 5 函数**;本分支**不新增、不删除、不修改任何表 / 字段 / 索引 / 约束 / 函数**。
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
> 🔶 **已实测核对**(`Select-String "^CREATE TABLE"` 计数 = **34**),与 `springboot4` / `master` 完全一致。

### 1.2 命名约定

| 类别 | 前缀 | 示例 |
|---|---|---|
| 业务系统表 | `sys_` | `sys_user` / `sys_role` / `sys_menu` |
| 代码生成表 | `gen_` | `gen_table` / `gen_table_column` |
| Quartz 内部表 | `qrtz_*` | `qrtz_triggers` / `qrtz_job_details` |
| 通用字段 | — | `create_by` / `create_time` / `update_by` / `update_time` / `remark` / `del_flag` |
| 逻辑删除 | `del_flag` | `0=未删` / `2=已删` |
| 状态 | `status` | `0=正常` / `1=停用 / 关闭` |

> 主键策略:MyBatis-Plus **`ASSIGN_ID`(雪花)**,`bigint`(MP 3.5.17,⬆️ 继承);非 PostgreSQL 原生 `bigserial`。

### 1.3 数据库与角色约定

| 项 | 值 |
|---|---|
| PostgreSQL 版本 | **≥ 15.1** |
| 角色 | `system` / password `system`(`OWNER`) |
| 库 | `basicdb-vue3` |
| schema | **`basicdb`** |
| 字符集 | 默认 UTF8 |
| 时区 | `Asia/Shanghai`(应用侧 `spring.jackson.time-zone: GMT+8`) |
| 数据加载方式 | `COPY ... FROM stdin`(**只能**用 `psql` 导入) |
| 函数数量 | **5**(`date_format` / `exec` / `find_in_set` / `ifnull` / `tabledef`) |

### 1.4 不使用(与 `springboot4` 一致)

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
| SQL 脚本变更 | **无**(`file-sql/basicdb-vue3.sql` 与 `springboot4` 逐字节一致) |
| 数据迁移 | **不需要** |
| 回滚时的数据回滚 | **不需要** |

> **为什么零变更**:本分支改动范围是 ① 前端 `.js` → `.ts` ② 新增 `tsconfig.json` / `types/` ③ `vue-tsc` 工具链。**三者都是编译期/构建期改造,与数据库 schema 无关**。
>
> 对比:若采用「前端类型由 DB 表结构自动生成(code-first → type-first)」的设计(本分支**未采用**),才需要引入生成链路;当前 `types/` 是**手工镜像后端 DTO**,不直连 DB。

### 1.6 🔶 前端类型 ↔ 数据库字段的间接关系(本分支需明示)

```
PostgreSQL 列 (user_name varchar(30))
      │  MyBatis-Plus 映射
      ▼
Java 实体 (SysUser.userName : String)
      │  Jackson 3 序列化(⬆️)
      ▼
JSON 响应 ({"userName": "admin"})
      │  🔶 前端 interface 声明(手工镜像)
      ▼
TS 类型 (SysUser.userName: string)
```

**关键结论**:

| 场景 | 是否影响前端类型 | 说明 |
|---|---|---|
| DB 列**改名** | **是** | Java 实体改名 → JSON 字段改名 → **必须手工同步 `types/`**;**TS 不会自动发现**(R43) |
| DB 列**加长**(`varchar(30)` → `varchar(50)`) | **否** | 前端都是 `string` |
| DB 列**加枚举值**(`data_scope` 1~5 → 1~6) | **是** | 前端联合类型需同步扩展 |
| DB 列**改为可空** | **是** | `string` → `string \| undefined` |
| **DB 列 `bigint` 且值可能 > 2^53** | **是** | JSON 变**字符串**(`BigNumberSerializer`)→ 前端类型应 `string` |
| DB 列**精度变化**(`bigint` → `numeric`) | **是** | 前端类型可能需从 `number` 改 `string` |
| 新增表/字段(不对外暴露) | **否** | 只要不进 VO 就不影响前端 |

> ⚠️ **最大陷阱**:第 1 行。**类型文件忘记同步时,`vue-tsc` 不会报错**(类型与自身一致,只是与后端不一致)。这是本分支唯一需要人工纪律保障的环节(列入 code review checklist)。

### 1.7 🔶 与 `springboot4` 的数据库差异 — **无**

| 对比项 | `springboot4` | 本分支 | 差异 |
|---|---|---|---|
| schema | `basicdb` | `basicdb` | 无 |
| 表数量 | 34 | 34 | 无 |
| 函数数量 | 5 | 5 | 无 |
| `sys_config` 数据 | 含 `sys.account.captchaEnabled` | 同 | 无 |
| 验证码 | kaptcha 2.3.3 | **kaptcha 2.3.3** | 无 |
| `qrtz_*` 使用 | RAMJobStore(不用) | 同 | 无 |
| SQL 脚本 | `basicdb-vue3.sql` | 同(逐字节一致) | 无 |

---

## 2. 通用(系统级)表 — `sys_*`(21 张)

> 全部带 `create_by` / `create_time` / `update_by` / `update_time` / `remark` / `del_flag`。
> **所有字段定义与 `springboot4` 完全一致**。本节对每张表额外标注「🔶 前端类型文件」,体现本分支的类型契约映射。

### 2.1 `sys_user` 用户

| 字段 | 类型 | 必填 | 注释 | 🔶 前端类型 |
|---|---|---|---|---|
| user_id | bigint | PK | 雪花 | `number`(超 2^53 时后端返回字符串) |
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
| menu_name_en / menu_name_zh_hant | varchar(50) | N | 英/繁(扩展) | `string \| undefined` |
| parent_id | bigint | Y | `0=顶级` | `number` |
| order_num | int | Y | 显示顺序 | `number` |
| path / component | varchar | N | 前端路由 / 组件 | `string \| undefined` |
| is_frame / is_cache / visible / status / is_resource / show_tags | char(1) | — | 各开关 | `'0' \| '1'` |
| menu_type | char(1) | Y | `M=目录 C=菜单 F=按钮` | `'M' \| 'C' \| 'F'` |
| perms | varchar(100) | N | 权限字符 | `string \| undefined` |
| icon | varchar(100) | N | Element Plus icon | `string \| undefined` |
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
| phone / email | varchar | N | — | `string \| undefined` |
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

> 🔶 前端**不直接消费**这 4 张表;通过 `SysUser.roleIds` / `SysRole.menuIds` / `SysRole.deptIds` 等 `number[]` 字段间接体现。

### 2.7 `sys_dict_type` / 2.8 `sys_dict_data`

| `sys_dict_type` | 说明 | 🔶 前端类型 |
|---|---|---|
| dict_id | bigint PK | `number` |
| dict_name | varchar(100) | `string` |
| dict_type | varchar(100) 唯一 | `string` |
| status | char(1) | `'0' \| '1'` |

| `sys_dict_data` | 说明 | 🔶 前端类型 |
|---|---|---|
| dict_code | bigint PK | `number` |
| dict_sort | int | `number` |
| dict_label | varchar(100) | `string` |
| dict_value | varchar(100) | `string` |
| dict_type | varchar(100) | `string` |
| css_class / list_class | varchar(100) | `string \| undefined` |
| is_default | char(1) | `'Y' \| 'N'` |
| status | char(1) | `'0' \| '1'` |

索引:`uk_dict_type(dict_type)` / `idx_dict_type(dict_type)`
> 🔶 契约:`types/api/system/dict.ts` → `SysDictType` / `SysDictData`;
> 🔶 派生:`utils/dict.ts` 的 `DictDataOption { label; value; elTagType?; elTagClass? }`

### 2.9 `sys_config` 全局参数(kaptcha 总开关)

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

> 🔶 **关键 KV**(前端通过 `getConfigKey(key)` 读取,类型在 `types/global.d.ts`):
>
> | config_key | 前端用途 |
> |---|---|
> | **`sys.account.captchaEnabled`** | 登录页是否显示验证码(**kaptcha 开关**) |
> | `sys.account.registerUser` | 是否显示注册入口 |
> | `sys.index.skinName` / `sys.index.sideTheme` | 主题 |
>
> 🔶 **注意**:Jackson 3 的时区**不在 `sys_config`**,而在 `application.yml` 的 `spring.jackson.time-zone: GMT+8`。

### 2.10 ~ 2.18 其余 `sys_*`(⬆️ 一致)

| 表 | 关键字段 | 🔶 前端类型文件 |
|---|---|---|
| `sys_notice` | notice_id / notice_title / notice_type / content / status | `types/api/system/notice.ts` |
| `sys_notice_read` | read_id / user_id / notice_id / read_time(`uk_user_notice`) | — |
| `sys_oper_log` | oper_id / title / business_type / **oper_param / json_result**(Jackson 3 序列化)/ oper_time / cost_time | `types/api/monitor/operlog.ts` |
| `sys_logininfor` | info_id / user_name / ipaddr / **browser / os**(yauaa)/ status / msg / login_time | `types/api/monitor/logininfor.ts` |
| `sys_job` | job_id / job_name / invoke_target / cron_expression / status | `types/api/monitor/job.ts` |
| `sys_job_log` | job_log_id / job_name / status / exception_info | `types/api/monitor/jobLog.ts` |
| `sys_demo` | id / name / code / description | — |
| `sys_files` | id / name / url / **size(bigint)** / mime_type | — |
| `sys_i18n` | id / lang / key / value(`uk_lang_key`) | — |

### 2.19 ⚠️ 关于 `sys_sms_message`(再次确认不存在)

> **本分支 `file-sql/basicdb-vue3.sql` 中不存在 `sys_sms_message` 表**(实测 0 命中)。
> `master` 早期文档曾列出该表,属**误记**;后续各分支文档均已纠正,本分支沿用。

**`sys_*` 21 张实测清单**:`sys_config` / `sys_demo` / `sys_dept` / `sys_dict_data` / `sys_dict_type` / `sys_files` / `sys_i18n` / `sys_job` / `sys_job_log` / `sys_logininfor` / `sys_menu` / `sys_notice` / `sys_notice_read` / `sys_oper_log` / `sys_post` / `sys_role` / `sys_role_dept` / `sys_role_menu` / `sys_user` / `sys_user_post` / `sys_user_role`

---

## 3. 代码生成表 — `gen_*`(2 张)

### 3.1 `gen_table` / 3.2 `gen_table_column`

| `gen_table` | 说明 |
|---|---|
| table_id | bigint PK |
| table_name / table_comment / class_name | — |
| tpl_category | `crud` / `tree` |
| package_name / module_name / business_name / function_name / function_author | — |
| gen_type | `0=zip 下载 1=生成到项目` |
| gen_path / options | — |

| `gen_table_column` | 说明 |
|---|---|
| column_id / table_id | PK / FK |
| column_name / column_comment / column_type | — |
| java_type / java_field | — |
| is_pk / is_increment / is_required / is_insert / is_edit / is_list / is_query | 各开关 |
| query_type / html_type / dict_type / sort | — |

索引:`idx_table_id(table_id)`
> 🔶 契约:`types/api/tool/gen.ts` → `GenTable` / `GenTableColumn`

> ⚠️ **⬆️ 继承缺陷(R44)**:代码生成器模板产出 **SB2 风格 Java**(`javax.*` + springfox)+ **JS 风格前端**(`<script setup>` 无 `lang="ts"`)。生成后需**双份手工修正**:
> 1. Java:`javax.*` → `jakarta.*`;`io.swagger.annotations.*` → `io.swagger.v3.oas.annotations.*`
> 2. 前端:`api.js` → `api.ts` 补类型;`<script setup>` → `<script setup lang="ts">`;补 `types/api/**` interface 并 re-export

---

## 4. Quartz 定时任务表 — `qrtz_*`(11 张,**RAMJobStore 不使用**)

| 表 | 用途 | 本分支状态 |
|---|---|---|
| `qrtz_blob_triggers` | Blob Trigger | **不使用** |
| `qrtz_calendars` | 日历 | **不使用** |
| `qrtz_cron_triggers` | Cron Trigger | **不使用** |
| `qrtz_fired_triggers` | 已触发 | **不使用** |
| `qrtz_job_details` | Job 详情 | **不使用** |
| `qrtz_locks` | 分布式锁 | **不使用** |
| `qrtz_paused_trigger_grps` | 暂停组 | **不使用** |
| `qrtz_scheduler_state` | 调度器状态 | **不使用** |
| `qrtz_simple_triggers` | Simple Trigger | **不使用** |
| `qrtz_simprop_triggers` | 带参数 | **不使用** |
| `qrtz_triggers` | Trigger 主表 | **不使用** |

> ⬆️ 继承自 `springboot4`:Quartz 用 **`spring-boot-starter-quartz` + RAMJobStore(内存)**,这 11 张表**存在但不写入/不读取**。
> **行为影响**:重启后动态任务运行态丢失(`sys_job` 定义仍在,需在管理页重新启用);**多实例会重复执行**(无分布式锁)。
> **保留原因**:脚本零变更、回滚友好、未来可切 JDBC JobStore。

---

## 5. 函数(5 个)

| 函数 | 用途 |
|---|---|
| `date_format` | 兼容 MySQL 日期格式化 |
| `exec` | 动态执行 SQL(代码生成器查元数据) |
| `find_in_set` | 兼容 MySQL 逗号串查找 |
| `ifnull` | 兼容 MySQL NULL 兜底 |
| `tabledef` | 取表 DDL |

> 🔶 与 `springboot4` 相同(5 个);与前端 TS 化无关。

---

## 6. 业务示例与扩展点

### 6.1 业务示例(`sys_demo`)

见 §2.10~2.18。

### 6.2 扩展点

- **新增业务模块**:在 `business-core` 下新增 `domain / mapper / service / controller`,对应 `sys_xxx` 表,**不要修改 `basic-admin-core`**
- **新增扩展能力**:在 `extend-*` 模式新增子模块,挂到 `pom.xml` 的 `dependencyManagement` + `modules`
- **代码生成产物**:落 `gen_table`,不落物理表
- **🔶 新增字段时的前端联动**(本分支特有,5 步):
  1. 改 `file-sql/basicdb-vue3.sql`(DDL)
  2. 改 Java 实体 + Mapper XML
  3. **改 `web-ui/src/types/api/<模块>/<实体>.ts`**(必须!)
  4. 跑 `npx vue-tsc --noEmit` 验证
  5. 若为枚举字段,同步扩展前端联合类型;若为 `bigint` 且可能超 2^53,前端类型用 `string`

---

## 7. 索引与查询说明

### 7.1 关键查询索引(⬆️ 与 `springboot4` 一致)

| 关键查询 | 命中索引 | 🔶 前端触发入口 |
|---|---|---|
| `select * from sys_user where user_name=?` | `uk_user_name` | `views/login.vue` |
| `select * from sys_role where role_key=?` | `uk_role_key` | `views/basic-admin/system/role/index.vue` |
| `select * from sys_menu where parent_id=? and status=0` | `idx_parent_id` | `views/basic-admin/system/menu/index.vue` |
| `select * from sys_dept where parent_id=? and status=0` | `idx_parent_id` | `views/basic-admin/system/dept/index.vue` |
| `select * from sys_user_role where user_id=?` | 复合 | `store/modules/user.ts` |
| `select * from sys_role_menu where role_id=?` | 复合 | `store/modules/permission.ts` |
| `select * from sys_dict_data where dict_type=? and status=0` | `idx_dict_type` | `utils/dict.ts` |
| `select * from sys_config where config_key=?` | `uk_config_key` | `utils/index.ts`(`getConfigKey`) |
| `select * from sys_oper_log where ... oper_time between ?` | `idx_business_type` / `idx_oper_time` | `views/basic-admin/monitor/operlog/index.vue` |
| `select * from sys_logininfor where ... login_time between ?` | `idx_login_time` | `views/basic-admin/monitor/logininfor/index.vue` |
| `select * from sys_notice_read where user_id=? and notice_id=?` | `uk_user_notice` | `layout/components/HeaderNotice/index.vue` |
| `select * from sys_job where status='0'` | 全表(量小) | `views/basic-admin/monitor/job/index.vue` |
| `select * from gen_table where table_name=?` | 物理主键 | `views/basic-admin/tool/gen/index.vue` |
| ~~`select * from qrtz_triggers ...`~~ | — | **RAMJobStore,不再查询** |

### 7.2 🔶 TS 化对查询性能的影响 — **无**

| 项 | 说明 |
|---|---|
| 运行时 | TS 类型**编译期擦除**,产物与 `springboot4` 等价,SQL 完全一致 |
| 查询次数 | 不变(同页面同接口) |
| 参数序列化 | `request.ts` 的 GET 参数处理与 `master` 一致 |
| 结论 | **不需要为 TS 化新增任何索引** |

### 7.3 🔶 `sys_logininfor.browser` / `os` 的 yauaa 影响(⬆️ 继承)

| 项 | 说明 |
|---|---|
| 解析库 | yauaa 8.2.0 |
| 影响字段 | `browser` / `os`(varchar 50) |
| 风险 | yauaa 输出更详细(如 `Windows 10 (10.0)`),可能超 50 字符 |
| 处置(不改 DDL) | 写入前截断 + `"Unknown"` 置空 |

> 🔶 **前端对应类型**:`SysLogininfor.browser?: string` / `os?: string`(可能为空串),展示时建议 `|| '-'` 兜底。

---

## 8. DDL 约定

| 项 | 约定 |
|---|---|
| 主键 | MP 雪花(`ASSIGN_ID`),`bigint`,**不要** `bigserial` |
| 通用字段 | `create_by` varchar(64) / `create_time` datetime / `update_by` varchar(64) / `update_time` datetime / `remark` varchar(500) / `del_flag` char(1) default '0' |
| 逻辑删除 | `del_flag=2` 视为已删(MP `logic-delete-value=2` / `logic-not-delete-value=0`) |
| 必填 | SQL 层 `NOT NULL`,应用层 `jakarta.validation` 的 `@NotBlank` / `@NotNull`(⬆️ SB4),**前端 `types/` 非可选字段** |
| 字符串 | varchar(N);枚举用 `char(1)` |
| 时间 | `datetime`(格式由 `@JsonFormat` 控制,注解包 `com.fasterxml.jackson.annotation` 不变);🔶 前端类型统一 `string` |
| 金额 | 若新增,**必须** `numeric(p,s)`,**禁止** `float` / `double`;🔶 前端类型用 `string`(避免 JS 精度丢失) |
| 字符集 | UTF8 |
| 时间戳 | 不用 `timestamp with time zone` |
| 归档 | 只逻辑删除 |
| 迁移工具 | 不用 Flyway / Liquibase;变更直接改 `basicdb-vue3.sql` 并附说明(见 §12) |
| UA 相关字段 | `browser` / `os` 由 yauaa 写入,写入前截断(§7.3) |
| **🔶 bigint 字段** | 超 `±2^53-1` 时 JSON 为**字符串**;前端类型按需用 `string` |
| **🔶 Jackson 注解** | 只用 `com.fasterxml.jackson.annotation.*`(不要用 `tools.jackson.annotation.*`) |
| **🔶 前端联动** | **任何 DDL 变更必须同步 `web-ui/src/types/api/**`**(见 §6.2) |
| **🔶 TS 化约定** | 本次前端改造**不改 DDL** |

---

## 9. 外部引擎表

| 引擎 / 能力 | 所在分支 | 本分支是否包含 |
|---|---|---|
| Flowable (`act_*` / `flw_*`) | `springboot4-extend-flowable`(`basicdb_flowable8`,90 表 + 2 视图) | ❌ 不含 |
| tianai-captcha(Redis 凭证,不落库) | `springboot2-tianai-captcha` / `springboot3-tianai-captcha` | ❌ 不含(本分支仍是 **kaptcha 2.3.3**) |

### 9.2 运行期非 DB 存储(⬆️ 与 `springboot4` 一致)

| 存储 | Key | TTL |
|---|---|---|
| Redis | `login_tokens:<uuid>` | 30min |
| Redis | `sys_dict::<dictType>` | — |
| Redis | `REPEAT_SUBMIT:*` | 500ms |
| 进程内存 | **Quartz RAMJobStore** | 进程 |
| 本地磁盘 | `D:/ruoyi/uploadPath` | — |

> 🔶 **本分支不新增任何存储**(TS 化纯编译期)。

---

## 10. 备份与恢复(⬆️ 与 `springboot4` 完全一致)

### 10.1 恢复(全新数据库)

```bash
# 1) 建角色与库
psql -U postgres -h localhost -c "CREATE ROLE system LOGIN PASSWORD 'system';"
psql -U postgres -h localhost -c "CREATE DATABASE \"basicdb-vue3\" OWNER system;"

# 2) 导入(必须 psql,含 COPY FROM stdin)
psql -U system -h localhost -d basicdb-vue3 -f file-sql/basicdb-vue3.sql

# 3) 验证表数量(期望 34)
psql -U system -h localhost -d basicdb-vue3 -c \
  "SELECT count(*) FROM information_schema.tables WHERE table_schema='basicdb';"

# 4) 验证函数数量(期望 5)
psql -U system -h localhost -d basicdb-vue3 -c \
  "SELECT count(*) FROM information_schema.routines WHERE routine_schema='basicdb';"
```

### 10.2 备份

```bash
pg_dump -U system -h localhost -d basicdb-vue3 \
  --schema=basicdb --no-owner --no-privileges \
  --file=file-sql/basicdb-vue3.sql
```

### 10.3 注意事项

- 备份脚本**只能**用 `psql` 执行(含 `COPY ... FROM stdin`)
- 重置 admin 密码:跑 `file-sql/reset-admin-password.sql`
- 其它历史 SQL(`basic-sql-2023-03-02.sql` / `quartz.sql` / `ry_20210908.sql` / `fix-sys-menu-component.sql` / `create-sys-notice-read.sql`)**不要**作为运行入口
- 🔶 **TS 化不涉及备份策略变化**:前端 `types/**` 是源码(随 git 走),不需入库备份
- 🔶 **分支切换无需数据迁移**:`springboot4` ↔ 本分支共用同一份 SQL 与 `sys_config` 数据

---

## 11. 表清单(全文汇总,共 34 张)

| 域 | 表 | 数量 | 大致行数 | 🔶 备注 |
|---|---|---|---|---|
| 代码生成 | `gen_table` / `gen_table_column` | 2 | 小(<100) | 生成物需手工 ts 化 |
| Quartz | `qrtz_*`(11 张) | 11 | **0(RAMJobStore 不用)** | 表保留 |
| 系统-鉴权 | `sys_user` / `sys_role` / `sys_menu` / `sys_user_role` / `sys_role_menu` / `sys_role_dept` / `sys_user_post` | 7 | 中 | bigint → 大数可能字符串 |
| 系统-组织 | `sys_dept` / `sys_post` | 2 | 中 | — |
| 系统-字典参数 | `sys_config`(验证码开关)/ `sys_dict_type` / `sys_dict_data` | 3 | 小~中 | 字典走 Redis |
| 系统-通知 | `sys_notice` / `sys_notice_read` | 2 | 小 / 大 | — |
| 系统-审计 | `sys_oper_log` / `sys_logininfor` | 2 | 很大 / 大 | Jackson 3 落参;yauaa 落 UA |
| 系统-文件国际化 | `sys_files` / `sys_i18n` | 2 | 大 / 小 | `size` 大数 |
| 系统-定时任务 | `sys_job` / `sys_job_log` | 2 | 小 / 大 | 定义与日志仍落库 |
| 系统-示例 | `sys_demo` | 1 | 演示用 | — |
| **合计** | | **34** | | |

**明细校验**:2(gen)+ 11(qrtz)+ 21(sys)= **34** ✅

> 🔶 `sys_user_online` 由 Redis 维护,**不写库**,不计入 34。
> 🔶 `sys_sms_message` **不存在**(见 §2.19)。
> 🔶 **本分支独有改动:无**;所有表结构与 `springboot4` 完全一致。

---

## 12. 需求变更记录

| 变更 | 落点 |
|---|---|
| 数据库迁 PostgreSQL | §1 / §10 |
| `sys_notice_read` / `sys_menu` 扩展列 / `sys_i18n` / `sys_files` / `sys_demo` | `springboot4` DBD §2 |
| ⬆️ SB2→3 / SB3→4 升级(Jackson 3 / yauaa) | `springboot3` / `springboot4` DBD |
| **🔶 前端 TypeScript 化** | **§1.5(零变更 10 项)+ §1.6(类型↔DB 字段间接关系 7 种场景,含 bigint→字符串)+ §1.7(与 springboot4 零差异)+ §2 各表「前端类型」列 + §3(生成器双份手工修正)+ §6.2(新增字段前端联动 5 步)+ §7.2(TS 对性能无影响)+ §8(DDL 约定补 bigint/Jackson 注解/前端联动/TS 4 条)** |
| **🔶 验证码仍是 kaptcha(明确)** | §2.9 / §9.1 |
| **🔶 `qrtz_*` RAMJobStore 不使用** | §4 |
| **🔶 纠正 `sys_sms_message` 误记** | §2.19 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot4` 数据库设计(v0.1)为基线,标注本分支**数据库零变更**(§1.5,10 项清单)+ 新增「§1.6 前端类型↔DB 字段间接关系(7 种场景,含 **bigint→JSON 字符串**)+ 最大陷阱提示」「§1.7 与 springboot4 零差异」「§2 各表「前端类型」列(21 张 sys_* + 2 张 gen_*)」「§3 生成器双份手工修正(SB2 Java + JS 前端)」「§4 `qrtz_*` RAMJobStore 不使用 + 行为影响」「§6.2 新增字段前端联动 5 步」「§7.2 TS 对查询性能无影响」「§8 DDL 约定补 4 条」;实测核对 34 表 / 5 函数,确认 `sys_sms_message` 不存在(§2.19) |
