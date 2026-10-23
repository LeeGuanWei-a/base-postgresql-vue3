# 基础平台(base-postgresql-vue3) 数据库设计 — `springboot4` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot4`** 分支
> 前置依赖:《需求文档》v0.1、《概要设计》v0.1
> 本文档是数据落点:schema / 表 / 索引 / 字段注释 / 迁移 / 备份。所有字段以《需求文档》为准,不在此文档内自创新字段。
> **本分支与 `springboot3` / `master` 数据库完全一致**:`basicdb` schema / **34 张表 + 5 函数**;本分支**不新增、不删除、不修改任何表 / 字段 / 索引 / 约束 / 函数**。
> **Spring Boot 3 → 4 升级是纯应用层升级**(JDK 21 + Jackson 3 + Security 7 + starter 4),**不触及数据层**。

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

> 主键策略:全库统一 **MyBatis-Plus `ASSIGN_ID`(雪花)**(MP **3.5.17**,⬆️ 由 3.5.7 升),非 PostgreSQL 原生 `bigserial`。
> 🔶 **MP 3.5.17 的 `ASSIGN_ID` 行为与 3.5.7 一致**(雪花算法未变),已有数据的主键语义不受影响。

### 1.3 数据库与角色约定

| 项 | 值 |
|---|---|
| PostgreSQL 版本 | **≥ 15.1** |
| 角色 | `system` / password `system`(`OWNER`) |
| 库 | `basicdb` / `OWNER=system` |
| 字符集 | 默认 UTF8 |
| 时区 | `Asia/Shanghai`(应用侧 `time-zone: GMT+8`,SB4 由 `spring.jackson.time-zone` 配置) |
| 数据加载方式 | `COPY ... FROM stdin`(`pg_dump` 导出,**只能**用 `psql` 导入) |
| 函数数量 | **5**(`date_format` / `exec` / `find_in_set` / `ifnull` / `tabledef`) |
| 🔶 JDBC 驱动 | PostgreSQL JDBC(**版本由 SB 4.1.1 BOM 管理**,较 SB3 更新;URL 格式不变) |

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

> **为什么零变更**:SB 3 → SB 4 的升级范围是 ① JDK 17 → 21 ② SB 3.5.16 → 4.1.1 ③ **Jackson 2 → 3**(序列化库)④ Security 6 → 7 ⑤ starter 换 `-boot-4-` 变体 ⑥ Quartz 改 starter —— **全部是应用层/依赖层/序列化层变化,与数据库 schema 无关**。
>
> 特别说明:**Jackson 3 只改变 JSON 序列化的"代码路径",不改变数据落库的"存储格式"** —— 数据库里存的仍是 `bigint` / `varchar` / `datetime`,与 Jackson 版本无关。

### 1.6 🔶 升级对数据层的间接影响(需知悉但不改结构)

虽然不改 schema,但 SB4 升级带来 **4 处值得注意的间接影响**:

| # | 影响点 | 说明 | 是否需改 DDL |
|---|---|---|---|
| 1 | **`bigint` 字段的 JSON 呈现** | `BigNumberSerializer` 对超出 JS 安全整数(`±2^53-1`)的值输出**字符串**;**DB 里仍是 `bigint`**,只有 API 响应变了 | ❌ 否 |
| 2 | **`datetime` 字段的 JSON 格式** | 由 `@JsonFormat` 注解控制;**注解包未变**(`com.fasterxml.jackson.annotation`),格式与 SB3 **一致** | ❌ 否 |
| 3 | **`null` 字段输出** | 由 `@JsonInclude` 控制;**注解包未变**,行为一致 | ❌ 否 |
| 4 | **`qrtz_*` 表不再被使用** | Quartz 用 **RAMJobStore**(内存),`qrtz_*` 表**存在但不写入/不读取** | ❌ 否(表保留) |

> **结论**:无任何 DDL 变更;唯一需要关注的是 **`bigint` 大数在 API 层变字符串**(但这是既有行为,不是 SB4 新引入)。

### 1.7 🔶 与 `springboot3` 的数据库差异 — **无**

| 对比项 | `springboot3` | 本分支 | 差异 |
|---|---|---|---|
| schema | `basicdb` | `basicdb` | 无 |
| 表数量 | 34 | 34 | 无 |
| 函数数量 | 5 | 5 | 无 |
| `sys_config` 数据 | 含 `sys.account.captchaEnabled` | 同 | 无 |
| 验证码 | kaptcha 2.3.3 | **kaptcha 2.3.3**(未换 tianai) | 无 |
| `qrtz_*` 使用情况 | 手写依赖(可能使用) | **RAMJobStore,不使用** | **行为差异,非结构差异** |
| SQL 脚本 | `basicdb-vue3.sql` | 同(逐字节一致) | 无 |

---

## 2. 通用(系统级)表 — `sys_*`(21 张)

> 全部带 `create_by` / `create_time` / `update_by` / `update_time` / `remark` / `del_flag`。
> **所有字段定义与 `springboot3` 完全一致**。本节对每张表额外标注「🔶 SB4 影响」,体现本分支的序列化相关须知。

### 2.1 `sys_user` 用户

| 字段 | 类型 | 必填 | 注释 | 🔶 SB4 影响 |
|---|---|---|---|---|
| user_id | bigint | PK | 雪花 | **大数 → 超 2^53 时 JSON 为字符串** |
| dept_id | bigint | Y | 部门 | 同上 |
| user_name | varchar(30) | Y | 登录名,唯一 | — |
| nick_name | varchar(30) | Y | 昵称 | — |
| user_type | varchar(2) | N | `00=系统用户` | — |
| email | varchar(50) | N | — | — |
| phonenumber | varchar(11) | N | — | — |
| sex | char(1) | N | `0=男 1=女 2=未知` | — |
| avatar | varchar(100) | N | 头像 URL | — |
| password | varchar(100) | Y | BCrypt 散列 | `@JsonIgnore` 不输出(注解包未变) |
| status | char(1) | Y | `0=正常 1=停用` | — |
| login_ip | varchar(128) | N | 最近登录 IP | — |
| login_date | datetime | N | 最近登录时间 | **`@JsonFormat` 格式化**(注解包未变) |
| create_by / create_time / update_by / update_time / remark | — | — | 通用 | `create_time` 走 `@JsonFormat` |
| del_flag | char(1) | Y | `0=未删 2=已删` | — |

索引:`uk_user_name(user_name)` / `idx_dept_id(dept_id)` / `idx_status(status)`

### 2.2 `sys_role` 角色

| 字段 | 类型 | 必填 | 注释 | 🔶 SB4 影响 |
|---|---|---|---|---|
| role_id | bigint | PK | 雪花 | 大数 → 字符串 |
| role_name | varchar(30) | Y | 显示名 | — |
| role_key | varchar(100) | Y | 权限字符,唯一 | — |
| role_sort | int | Y | 显示顺序 | `int` 不触发大数逻辑 |
| data_scope | char(1) | Y | `1=全部 2=自定义 3=本部门 4=本部门及下 5=本人` | — |
| status | char(1) | Y | `0=正常 1=停用` | — |
| 通用字段 | — | — | — | — |

索引:`uk_role_key(role_key)`

### 2.3 `sys_menu` 菜单

| 字段 | 类型 | 必填 | 注释 | 🔶 SB4 影响 |
|---|---|---|---|---|
| menu_id | bigint | PK | 雪花 | 大数 → 字符串 |
| menu_name | varchar(50) | Y | 中文名 | — |
| menu_name_en / menu_name_zh_hant | varchar(50) | N | 英/繁(扩展) | — |
| parent_id | bigint | Y | `0=顶级` | 大数 → 字符串 |
| order_num | int | Y | 显示顺序 | — |
| path / component | varchar | N | 前端路由 / 组件 | — |
| is_frame / is_cache / visible / status / is_resource / show_tags | char(1) | — | 各开关 | — |
| menu_type | char(1) | Y | `M=目录 C=菜单 F=按钮` | — |
| perms | varchar(100) | N | 权限字符 | — |
| icon | varchar(100) | N | Element Plus icon | — |
| 通用字段 | — | — | — | — |

索引:`idx_parent_id(parent_id)` / `idx_status(status)`
> 🔶 菜单数据**无需变更**:前端路由与权限字符完全未动。

### 2.4 `sys_dept` 部门(自引用树)

| 字段 | 类型 | 必填 | 注释 | 🔶 SB4 影响 |
|---|---|---|---|---|
| dept_id | bigint | PK | 雪花 | 大数 → 字符串 |
| parent_id | bigint | Y | `0=顶级` | 同上 |
| ancestors | varchar(500) | N | `0,1,2` 链路 | — |
| dept_name | varchar(30) | Y | 同级唯一 | — |
| order_num | int | Y | 显示顺序 | — |
| leader | varchar(20) | N | 负责人 user_id | — |
| phone / email | varchar | N | — | — |
| status | char(1) | Y | `0=正常 1=停用` | — |
| 通用字段 | — | — | — | — |

索引:`idx_parent_id(parent_id)` / `idx_status(status)`

### 2.5 `sys_post` 岗位

| 字段 | 类型 | 必填 | 注释 | 🔶 SB4 影响 |
|---|---|---|---|---|
| post_id | bigint | PK | 雪花 | 大数 → 字符串 |
| post_code | varchar(64) | Y | 唯一 | — |
| post_name | varchar(50) | Y | — | — |
| post_sort | int | Y | 显示顺序 | — |
| status | char(1) | Y | `0=正常 1=停用` | — |
| 通用字段 | — | — | — | — |

索引:`uk_post_code(post_code)`

### 2.6 关联表(4 张)

```sql
sys_user_role  (user_id, role_id)            -- 复合 PK
sys_role_menu  (role_id, menu_id)            -- 复合 PK
sys_role_dept  (role_id, dept_id)            -- 复合 PK
sys_user_post  (user_id, post_id)            -- 复合 PK
```

> 🔶 两列都是 `bigint`,**大数时 JSON 呈现为字符串**;前端做 ID 比较时需按字符串处理(与 SB3 一致)。

### 2.7 `sys_dict_type` 字典类型

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| dict_id | bigint | PK | 雪花 |
| dict_name | varchar(100) | Y | — |
| dict_type | varchar(100) | Y | 类型字符串,唯一 |
| status | char(1) | Y | `0=正常 1=停用` |
| 通用字段 | — | — | — |

索引:`uk_dict_type(dict_type)`

### 2.8 `sys_dict_data` 字典值

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| dict_code | bigint | PK | 雪花 |
| dict_sort | int | Y | 显示顺序 |
| dict_label | varchar(100) | Y | 显示文本 |
| dict_value | varchar(100) | Y | 存储值 |
| dict_type | varchar(100) | Y | 类型 |
| css_class / list_class | varchar(100) | N | 前端样式 |
| is_default | char(1) | N | `Y=默认 N=否` |
| status | char(1) | Y | `0=正常 1=停用` |
| 通用字段 | — | — | — |

索引:`idx_dict_type(dict_type)`
> 🔶 **字典缓存**:`DictUtils` 把字典值放 Redis,**JSON 由 Jackson 3 序列化**;`sys_dict::<dictType>` 的缓存值结构不变。

### 2.9 `sys_config` 全局参数(🔶 验证码总开关所在)

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| config_id | int | PK | 自增 |
| config_name | varchar(100) | Y | — |
| config_key | varchar(100) | Y | 唯一 |
| config_value | varchar(500) | Y | — |
| config_type | char(1) | Y | `N=普通 Y=系统` |
| is_builtin | char(1) | N | `0=否 1=是` |
| 通用字段 | — | — | — |

索引:`uk_config_key(config_key)`

> 🔶 **关键 KV**:
>
> | config_key | 用途 | 本分支 |
> |---|---|---|
> | **`sys.account.captchaEnabled`** | 是否显示验证码 | **kaptcha 开关**(未换 tianai) |
> | `sys.account.registerUser` | 是否显示注册入口 | 一致 |
> | `sys.index.skinName` / `sys.index.sideTheme` | 主题 | 一致 |
>
> 🔶 **注意**:SB4 的 Jackson 时区**不在 `sys_config`**,而在 `application.yml` 的 `spring.jackson.time-zone: GMT+8`(SB3 由 `ApplicationConfig` 的 Customizer 设置,本分支已删该 Bean)。

### 2.10 `sys_notice` 通知公告

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| notice_id | int | PK | 自增 |
| notice_title | varchar(50) | Y | — |
| notice_type | char(1) | Y | `1=通知 2=公告` |
| notice_content | text | Y | — |
| status | char(1) | Y | `0=正常 1=关闭` |
| 通用字段 | — | — | — |

### 2.11 `sys_notice_read` 通知已读

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| read_id | bigint | PK | 雪花 |
| user_id | bigint | Y | 用户 |
| notice_id | bigint | Y | 通知 |
| read_time | datetime | Y | — |
| 通用字段 | — | — | — |

索引:`uk_user_notice(user_id, notice_id)` 唯一

### 2.12 `sys_oper_log` 操作日志

| 字段 | 类型 | 必填 | 注释 | 🔶 SB4 影响 |
|---|---|---|---|---|
| oper_id | bigint | PK | 雪花 | 大数 → 字符串 |
| title | varchar(50) | N | 模块名 | — |
| business_type | int | Y | 业务类型 0~9 | — |
| method | varchar(100) | Y | 方法名 | — |
| request_method | varchar(10) | N | GET/POST | — |
| operator_type | int | Y | 0=其它 1=后台 2=手机 | — |
| oper_name / dept_name | varchar(50) | N | 操作人 / 部门 | — |
| oper_url | varchar(255) | N | URL | — |
| oper_ip | varchar(128) | N | IP | — |
| oper_param / json_result | varchar(2000) | N | 入参 / 出参 JSON | **入参序列化走 Jackson 3**(`JsonUtils`) |
| status | int | Y | `0=成功 1=失败` | — |
| error_msg | varchar(2000) | N | 错误信息 | — |
| oper_time | datetime | Y | — | `@JsonFormat` |
| cost_time | bigint | N | 耗时 ms | 大数 → 字符串 |

索引:`idx_business_type(business_type)` / `idx_oper_time(oper_time)`

> 🔶 **`oper_param` / `json_result` 是关键**:`LogAspect` 用 `JsonUtils`(Jackson 3)把入参/出参序列化成字符串再落库。**Jackson 3 的输出格式与 2.x 一致**,所以历史数据可读性不受影响;但若某字段原本靠 Jackson 2 的宽松特性序列化,Jackson 3 更严格可能抛异常 → 已被 `JsonUtils` 的 try-catch 兜住(返回 null)。

### 2.13 `sys_logininfor` 登录日志(🔶 yauaa + Jackson 3)

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| info_id | bigint | PK | 雪花 |
| user_name | varchar(50) | Y | 登录名 |
| ipaddr | varchar(128) | N | IP |
| login_location | varchar(255) | N | 归属地 |
| browser | varchar(50) | N | 浏览器(yauaa 解析) |
| os | varchar(50) | N | 操作系统(yauaa 解析) |
| status | char(1) | Y | `0=成功 1=失败` |
| msg | varchar(255) | N | 消息 |
| login_time | datetime | Y | — |

索引:`idx_user_name(user_name)` / `idx_login_time(login_time)`
> ⚠️ `browser`/`os` 由 yauaa 8.2.0 解析(⬆️ 继承),输出更长,写入前建议截断(见 §7.3)。

### 2.14 `sys_job` 定时任务(🔶 RAMJobStore 但定义仍落库)

| 字段 | 类型 | 必填 | 注释 | 🔶 SB4 影响 |
|---|---|---|---|---|
| job_id | bigint | PK | 雪花 | 大数 → 字符串 |
| job_name | varchar(64) | Y | — | — |
| job_group | varchar(64) | Y | `DEFAULT` / `SYSTEM` | — |
| invoke_target | varchar(500) | Y | `ryTask.ryNoParams` | — |
| cron_expression | varchar(255) | Y | Cron | — |
| misfire_policy | varchar(20) | Y | `0~3` | — |
| concurrent | char(1) | Y | `0=允许 1=禁止` | — |
| status | char(1) | Y | `0=运行 1=暂停` | — |
| 通用字段 | — | — | — | `@JsonFormat` |

> 🔶 **关键**:本分支 Quartz 用 **RAMJobStore**,**`sys_job` 表照常使用**(任务定义持久化),但 Quartz 自己的 `qrtz_*` 表不参与。这意味着:
> - ✅ 任务**定义**不丢(重启后可在管理页看到,但状态需重新启用)
> - ❌ 任务**运行态**(触发器/下次执行时间)**重启丢失**

### 2.15 `sys_job_log` 任务日志

| 字段 | 类型 | 必填 | 注释 | 🔶 SB4 影响 |
|---|---|---|---|---|
| job_log_id | bigint | PK | 雪花 | 大数 → 字符串 |
| job_name / job_group | varchar(64) | Y | — | — |
| invoke_target | varchar(500) | Y | — | — |
| job_message | varchar(500) | N | — | — |
| status | char(1) | Y | `0=成功 1=失败` | — |
| exception_info | text | N | 异常堆栈 | — |
| create_time | datetime | Y | — | — |

> 🔶 **照常落库**(`AbstractQuartzJob.after` 写),与 RAMJobStore 无关。

### 2.16 `sys_demo` 业务示例

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| name | varchar(50) | Y | — |
| code | varchar(50) | Y | — |
| description | varchar(255) | N | — |
| 通用字段 | — | — | — |

### 2.17 `sys_files` 文件元数据

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| name | varchar(255) | Y | 原始名 |
| original_name | varchar(255) | N | — |
| url | varchar(500) | Y | 路径 |
| size | bigint | N | 字节(**大数 → 字符串**) |
| mime_type | varchar(100) | N | — |
| 通用字段 | — | — | — |

### 2.18 `sys_i18n` 国际化

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| lang | varchar(20) | Y | `zh-CN` / `en-US` 等 |
| key | varchar(100) | Y | — |
| value | varchar(500) | Y | — |
| 通用字段 | — | — | — |

索引:`uk_lang_key(lang, key)` 唯一

### 2.19 ⚠️ 关于 `sys_sms_message`(再次确认不存在)

> **本分支 `file-sql/basicdb-vue3.sql` 中不存在 `sys_sms_message` 表**(实测 `Select-String "sys_sms_message"` 命中 **0**)。
>
> `master` 早期文档曾列出该表,属**误记**;`springboot3` / `springboot2_TypeScript` / `springboot3-tianai-captcha` / `springboot3_TypeScript` 均已纠正,本分支沿用。
>
> **`sys_*` 21 张实测清单**:`sys_config` / `sys_demo` / `sys_dept` / `sys_dict_data` / `sys_dict_type` / `sys_files` / `sys_i18n` / `sys_job` / `sys_job_log` / `sys_logininfor` / `sys_menu` / `sys_notice` / `sys_notice_read` / `sys_oper_log` / `sys_post` / `sys_role` / `sys_role_dept` / `sys_role_menu` / `sys_user` / `sys_user_post` / `sys_user_role`

---

## 3. 代码生成表 — `gen_*`(2 张)

### 3.1 `gen_table`

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| table_id | bigint | PK | 雪花 |
| table_name | varchar(200) | Y | 表名 |
| table_comment | varchar(500) | Y | 注释 |
| class_name | varchar(100) | Y | 生成的类名 |
| tpl_category | varchar(200) | Y | `crud` / `tree` |
| package_name | varchar(100) | Y | 生成包路径 |
| module_name | varchar(30) | Y | 模块名 |
| business_name | varchar(30) | Y | 业务名 |
| function_name | varchar(50) | Y | 功能名 |
| function_author | varchar(50) | Y | 作者 |
| gen_type | char(1) | Y | `0=zip 下载 1=生成到项目` |
| gen_path | varchar(200) | Y | 生成路径 |
| options | varchar(1000) | N | 其它选项 JSON |
| 通用字段 | — | — | — |

### 3.2 `gen_table_column`

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| column_id | bigint | PK | 雪花 |
| table_id | bigint | Y | 引用 gen_table |
| column_name | varchar(200) | Y | 列名 |
| column_comment | varchar(500) | Y | 注释 |
| column_type | varchar(100) | Y | 数据库类型 |
| java_type | varchar(100) | Y | Java 类型 |
| java_field | varchar(100) | Y | Java 字段名 |
| is_pk / is_increment / is_required / is_insert / is_edit / is_list / is_query | char(1) | Y | 各开关 |
| query_type | varchar(200) | Y | `EQ` / `LIKE` 等 |
| html_type | varchar(200) | Y | `input` / `select` 等 |
| dict_type | varchar(200) | N | 关联字典 |
| sort | int | Y | 排序 |

索引:`idx_table_id(table_id)`

> ⚠️ **⬆️ 继承缺陷(R34)**:代码生成器模板仍产 **SB 2 风格 Java**(`javax.*` + springfox 注解)。生成后需手工改 `jakarta.*` + `io.swagger.v3`。与 SB4 升级无关,属同一基线的遗留问题。

---

## 4. Quartz 定时任务表 — `qrtz_*`(11 张,🔶 **本分支不使用**)

> ⚠️ **本分支最重要的数据层须知**:Quartz 改用 **`spring-boot-starter-quartz` + RAMJobStore(内存)**,这 11 张表**存在但既不写入也不读取**。

| 表 | 用途 | 🔶 本分支状态 |
|---|---|---|
| `qrtz_blob_triggers` | Blob 类型 Trigger | **不使用** |
| `qrtz_calendars` | 日历 | **不使用** |
| `qrtz_cron_triggers` | Cron Trigger | **不使用** |
| `qrtz_fired_triggers` | 已触发的 Trigger | **不使用** |
| `qrtz_job_details` | Job 详情 | **不使用** |
| `qrtz_locks` | 分布式锁 | **不使用** |
| `qrtz_paused_trigger_grps` | 暂停组 | **不使用** |
| `qrtz_scheduler_state` | 调度器状态 | **不使用** |
| `qrtz_simple_triggers` | Simple Trigger | **不使用** |
| `qrtz_simprop_triggers` | 带参数 Trigger | **不使用** |
| `qrtz_triggers` | Trigger 主表 | **不使用** |

**为什么保留这些表?**

| 理由 | 说明 |
|---|---|
| 脚本零变更 | 34 表结构不动(AC-41) |
| 未来可切回 | 若要切 JDBC JobStore(M4),表已就绪 |
| 回滚友好 | 回退到 `springboot3` 的 jar 时表可直接使用 |

**切 JDBC JobStore 的前提(M4 可选)**:

| 项 | 说明 |
|---|---|
| 配置 | `spring.quartz.job-store-type: jdbc` + `spring.quartz.properties.org.quartz.jobStore.driverDelegateClass: org.quartz.impl.jdbcjobstore.PostgreSQLDelegate` |
| 数据源 | starter 会用应用主数据源 |
| ⚠️ 已知坑 | 若依曾遇到 PG `varchar(1)` 字段与 Quartz 的布尔/字符映射问题;若切换需专门验证 |
| 收益 | 任务持久化、重启不丢、多实例可协同(`qrtz_locks` 生效) |

**RAMJobStore 的行为影响**:

| 场景 | 后果 |
|---|---|
| 应用重启 | 所有触发计划丢失;`sys_job` 定义仍在,需在管理页重新启用 |
| 多实例部署 | **每个实例各自调度同一任务 → 重复执行**(无分布式锁) |
| `sys_job_log` | 照常写入(与 JobStore 无关) |

---

## 5. 函数(5 个,由 SQL 脚本创建)

| 函数 | 签名 | 用途 |
|---|---|---|
| `date_format` | `(indate anyelement, informate text) RETURNS text` | 兼容 MySQL 日期格式化 |
| `exec` | `(sqlstring character varying) RETURNS character varying` | 动态执行 SQL(代码生成器查询元数据) |
| `find_in_set` | `(bigint, character varying) RETURNS boolean` | 兼容 MySQL 逗号串查找 |
| `ifnull` | `(param1 text, param2 text) RETURNS text` | 兼容 MySQL NULL 兜底 |
| `tabledef` | `(oid) RETURNS text` | 取表 DDL 定义 |

> 🔶 本分支**未改**(实测 `CREATE FUNCTION` 计数 = 5);与 Java / Jackson 版本无关(DB 侧 PL/pgSQL)。

---

## 6. 业务示例与扩展点

### 6.1 业务示例(`sys_demo`)

见 §2.16。

### 6.2 扩展点

- **新增业务模块**:在 `business-core` 下新增 `domain / mapper / service / controller`,对应 `sys_xxx` 表,**不要修改 `basic-admin-core`**
- **新增扩展能力**:在 `extend-*` 模式新增子模块,挂到 `pom.xml` 的 `dependencyManagement` + `modules`
- **代码生成产物**:落 `gen_table`,不落物理表
- **🔶 新增字段时的 SB4 注意事项**:
  1. 实体上的 Jackson 注解用 **`com.fasterxml.jackson.annotation.*`**(**不要**改成 `tools.jackson.annotation.*`,该包不存在)
  2. 若字段是 `bigint` 且可能超 2^53 → 前端按字符串处理(现有 `BigNumberSerializer` 已覆盖)
  3. `datetime` 字段用 `@JsonFormat` 指定格式(包名不变)
  4. 任何 DDL 变更必须同步 `file-sql/basicdb-vue3.sql` 并在 §12 登记

---

## 7. 索引与查询说明

### 7.1 关键查询索引(⬆️ 与 `springboot3` 一致)

| 关键查询 | 命中索引 | 备注 |
|---|---|---|
| `select * from sys_user where user_name=?` | `uk_user_name` | 登录 |
| `select * from sys_role where role_key=?` | `uk_role_key` | 角色校验 |
| `select * from sys_menu where parent_id=? and status=0` | `idx_parent_id` | 菜单树 |
| `select * from sys_dept where parent_id=? and status=0` | `idx_parent_id` | 部门树 |
| `select * from sys_user_role where user_id=?` | 复合 (user_id, role_id) | 权限加载 |
| `select * from sys_role_menu where role_id=?` | 复合 (role_id, menu_id) | 权限加载 |
| `select * from sys_dict_data where dict_type=? and status=0` | `idx_dict_type` | 字典缓存 |
| `select * from sys_config where config_key=?` | `uk_config_key` | 参数读取 |
| `select * from sys_oper_log where business_type=? and oper_time between ? and ?` | `idx_business_type` / `idx_oper_time` | 操作日志 |
| `select * from sys_logininfor where status=? and login_time between ? and ?` | `idx_login_time` | 登录日志 |
| `select * from sys_notice_read where user_id=? and notice_id=?` | `uk_user_notice` | 唯一命中 |
| `select * from sys_job where status='0'` | 全表(任务量小) | **任务定义查询**(RAMJobStore 下仍从 DB 读) |
| `select * from gen_table where table_name=?` | 物理表主键 | 反查 |
| ~~`select * from qrtz_triggers ...`~~ | — | **本分支不再查询** |

### 7.2 🔶 SB4 升级对查询行为的影响

| 项 | 变化 | 影响 |
|---|---|---|
| JDBC 驱动 | 由 SB 4.1.1 BOM 管理(版本更新) | URL/参数格式不变;建议回归一次连接与事务 |
| MyBatis-Plus 3.5.7 → **3.5.17** | 分页 SQL 生成逻辑微调 | 分页结果应一致;**`PaginationInnerInterceptor` 需 `mybatis-plus-jsqlparser-4.9`** |
| MyBatis-Spring-Boot 3.0.5 → **4.1.0** | `SqlSessionFactory` 装配 | `MybatisConfig` / `MybatisPlusConfig` 已适配 |
| PageHelper 2.1.1 → **4.1.1** | 方言识别策略调整 | PG 方言仍自动识别;建议回归分页总数 |
| Druid 1.2.28(`-boot-3-` → `-boot-4-`) | 监控统计口径 | `/druid/` 页面数据正常即可 |
| **Jackson 3** | **只影响 JSON,不影响 SQL** | 查询与结果映射不受影响 |

> **结论**:**不需要为 SB4 升级新增任何索引**;但建议回归「分页 + 复杂查询 + 大结果集」。

### 7.3 🔶 `sys_logininfor.browser` / `os` 的 yauaa 影响(⬆️ 继承)

| 项 | 说明 |
|---|---|
| 解析库 | yauaa 8.2.0(⬆️ SB3 已引入) |
| 影响字段 | `browser`(varchar 50)/ `os`(varchar 50) |
| 风险 | yauaa 输出更详细,极端情况可能超 50 字符 |
| 处置(不改 DDL) | 写入前 `safeUaValue` 截断 + `"Unknown"` 置空兜底 |

```java
private static String safeUaValue(String v, int maxLen) {
    if (v == null || "Unknown".equalsIgnoreCase(v.trim())) return "";
    v = v.trim();
    return v.length() > maxLen ? v.substring(0, maxLen) : v;
}
```

---

## 8. DDL 约定

| 项 | 约定 |
|---|---|
| 主键 | MyBatis-Plus 雪花(`ASSIGN_ID`),`bigint`,**不要**用 `bigserial` |
| 通用字段 | `create_by` varchar(64) / `create_time` datetime / `update_by` varchar(64) / `update_time` datetime / `remark` varchar(500) / `del_flag` char(1) default '0' |
| 逻辑删除 | `del_flag=2` 视为已删(MP `logic-delete-value=2` / `logic-not-delete-value=0`) |
| 必填 | SQL 层 `NOT NULL`,应用层 `jakarta.validation` 的 `@NotBlank` / `@NotNull` |
| 字符串 | varchar(N);枚举用 `char(1)` |
| 时间 | `datetime`(格式由 `@JsonFormat` 控制,**注解包 `com.fasterxml.jackson.annotation` 不变**) |
| 金额 | 若新增,**必须** `numeric(p,s)`,**禁止** `float` / `double`;JSON 侧建议 `@JsonSerialize(using = BigNumberSerializer)` 或 `ToStringSerializer`(避免 JS 精度丢失) |
| 字符集 | UTF8 |
| 时间戳 | 不用 `timestamp with time zone`,用 `datetime` |
| 归档 | 当前不做物理归档,只逻辑删除 |
| 迁移工具 | 暂时**不**用 Flyway / Liquibase,变更直接改 `basicdb-vue3.sql` 并附说明(见 §12) |
| UA 相关字段 | `browser` / `os` 由 yauaa 写入,`varchar(50)` 建议写入前截断(§7.3) |
| **🔶 bigint 字段** | 超过 `±2^53-1` 时 JSON 输出**字符串**(`BigNumberSerializer`);前端按字符串处理 |
| **🔶 Jackson 注解** | **只用 `com.fasterxml.jackson.annotation.*`**;不要用 `tools.jackson.annotation.*`(不存在) |
| **🔶 升级期约定** | **本次 SB4 升级不改 DDL**;`qrtz_*` 表保留不用 |

---

## 9. 外部引擎表

### 9.1 分支对照

| 引擎 / 能力 | 所在分支 | 本分支是否包含 |
|---|---|---|
| Flowable (`act_*` / `flw_*`) | `springboot2-extend-flowable` / `springboot4-extend-flowable`(`basicdb_flowable`,86 表 + 2 视图) | ❌ 不含 |
| tianai-captcha(Redis 凭证,不落库) | `springboot2-tianai-captcha` / `springboot3-tianai-captcha` | ❌ 不含(本分支仍是 **kaptcha 2.3.3**) |
| **Quartz `qrtz_*`** | **本分支存在但 RAMJobStore 不使用** | ⚠️ 见 §4 |

### 9.2 运行期非 DB 存储(⬆️ 与 `springboot3` 一致,🔶 序列化方式变)

| 存储 | Key | TTL | 🔶 本分支 |
|---|---|---|---|
| Redis | `login_tokens:<uuid>` | 30min | **Jackson 3 + defaultTyping + 白名单** |
| Redis | `sys_dict::<dictType>` | — | 同上 |
| Redis | `REPEAT_SUBMIT:*` | 500ms | 同上 |
| 本地磁盘 | `D:/ruoyi/uploadPath` | — | 不变 |
| **进程内存** | **Quartz RAMJobStore** | 进程生命周期 | **🔶 新增**:任务运行态在此,重启丢失 |

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
- 🔶 **SB4 升级不涉及备份策略变化**;DB 与升级前完全一致
- 🔶 **升级回滚无需数据回滚**:回退 jar 即可
- 🔶 **Redis 建议清空**(Jackson 2 脏数据),**不属于数据库备份范畴**但会影响升级首启

---

## 11. 表清单(全文汇总,共 34 张)

| 域 | 表 | 数量 | 大致行数 | 🔶 SB4 备注 |
|---|---|---|---|---|
| 代码生成 | `gen_table` / `gen_table_column` | 2 | 小(<100) | 生成物需手工改 jakarta |
| Quartz | `qrtz_*`(11 张见 §4) | 11 | **0(不使用)** | **RAMJobStore,表保留不用** |
| 系统-鉴权 | `sys_user` / `sys_role` / `sys_menu` / `sys_user_role` / `sys_role_menu` / `sys_role_dept` / `sys_user_post` | 7 | 中 | bigint → 大数字符串 |
| 系统-组织 | `sys_dept` / `sys_post` | 2 | 中 | 同上 |
| 系统-字典参数 | `sys_config` / `sys_dict_type` / `sys_dict_data` | 3 | 小~中 | 验证码开关;字典走 Redis |
| 系统-通知 | `sys_notice` / `sys_notice_read` | 2 | 小 / 大 | — |
| 系统-审计 | `sys_oper_log` / `sys_logininfor` | 2 | 很大 / 大 | **入参/出参走 Jackson 3 序列化** |
| 系统-文件国际化 | `sys_files` / `sys_i18n` | 2 | 大 / 小 | `size` 大数 → 字符串 |
| 系统-定时任务 | `sys_job` / `sys_job_log` | 2 | 小 / 大 | **定义与日志照常落库** |
| 系统-示例 | `sys_demo` | 1 | 演示用 | — |
| **合计** | | **34** | | |

**明细校验**:2(gen)+ 11(qrtz)+ 21(sys)= **34** ✅

> 🔶 `sys_user_online` 由 Redis 维护,**不写库**,不计入 34。
> 🔶 `sys_sms_message` **不存在**(见 §2.19)。
> 🔶 **本分支独有改动:无**;所有表结构与 `springboot3` 完全一致。

---

## 12. 需求变更记录

| 变更 | 落点 |
|---|---|
| 数据库迁 PostgreSQL | §1 / §10 |
| `sys_notice_read` / `sys_menu` 扩展列 / `sys_i18n` / `sys_files` / `sys_demo` | `springboot3` DBD §2 |
| ⬆️ Spring Boot 2 → 3 升级(yauaa 影响 `browser`/`os`) | §2.13 / §7.3 |
| **🔶 Spring Boot 3 → 4 升级(JDK 21 + Jackson 3)** | **§1.5(零变更 10 项)+ §1.6(4 处间接影响)+ §1.7(与 springboot3 零差异,含 qrtz 行为差异)+ §2 各表「SB4 影响」列 + §4(qrtz_* 不使用 + 切 JDBC 前提)+ §7.2(查询行为影响)+ §8(DDL 约定补 bigint/Jackson 注解/升级期 3 条)+ §9.2(内存 RAMJobStore)** |
| **🔶 `qrtz_*` 11 张表不再使用** | §4 / §11 |
| **🔶 纠正 `sys_sms_message` 误记** | §2.19 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot3` 数据库设计(v0.1)为基线,标注本分支**数据库零变更**(§1.5,10 项清单)+ 新增「§1.6 升级对数据层 4 处间接影响(bigint JSON / datetime 格式 / null 处理 / qrtz 不用)」「§1.7 与 springboot3 零 DB 差异(含 qrtz 行为差异)」「§2 各表「SB4 影响」列(21 张 sys_*)」「§4 qrtz_* 11 张不使用 + 切 JDBC 前提与已知坑 + RAMJobStore 行为影响表」「§7.2 SB4 对查询行为影响(JDBC/MP/pagehelper/Druid/Jackson)」「§8 DDL 约定补 bigint / Jackson 注解 / 升级期 3 条」「§9.2 内存 RAMJobStore」;实测核对 34 表 / 5 函数,确认 `sys_sms_message` 不存在(§2.19) |
