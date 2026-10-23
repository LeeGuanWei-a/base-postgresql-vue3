# 基础平台(base-postgresql-vue3) 数据库设计 — `springboot3` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot3`** 分支
> 前置依赖:《需求文档》v0.1、《概要设计》v0.1
> 本文档是数据落点:schema / 表 / 索引 / 字段注释 / 迁移 / 备份。所有字段以《需求文档》为准,不在此文档内自创新字段。
> **本分支与 `master` 数据库完全一致**:`basicdb` schema / **34 张表 + 5 函数**;本分支**不新增、不删除、不修改任何表 / 字段 / 索引 / 约束 / 函数**。Spring Boot 2 → 3 升级是**纯应用层升级,不触及数据层**。

---

## 1. 总览

### 1.1 Schema 布局

| Schema | 表数 | 内容 |
|---|---|---|
| `basicdb` | **34** | 全部业务表(代码生成 2 + Quartz 11 + 系统 21) |
| `public` | 0(本项目不使用) | — |

> 全部对象由 `file-sql/basicdb-vue3.sql` 一份脚本恢复。
> **JDBC URL 必须带 `currentSchema=basicdb`**,否则 MyBatis-Plus 找不到表。
> 🔶 **已实测核对**(`Select-String "^CREATE TABLE"` 计数 = **34**),与 `master` 分支一致。

### 1.2 命名约定

| 类别 | 前缀 | 示例 |
|---|---|---|
| 业务系统表 | `sys_` | `sys_user` / `sys_role` / `sys_menu` |
| 代码生成表 | `gen_` | `gen_table` / `gen_table_column` |
| Quartz 内部表 | `qrtz_` | `qrtz_triggers` / `qrtz_job_details` |
| 通用字段 | — | `create_by` / `create_time` / `update_by` / `update_time` / `remark` / `del_flag` |
| 逻辑删除 | `del_flag` | `0=未删` / `2=已删`(约定值) |
| 状态 | `status` | `0=正常` / `1=停用 / 关闭` |

> 主键策略:全库统一 **MyBatis-Plus `ASSIGN_ID`(雪花)**(由 `mybatis-plus.global-config.db-config.id-type=ASSIGN_ID` 控制),非 PostgreSQL 原生 `bigserial`。这是 RuoYi 默认行为,不要改。
> 🔶 本分支 MP 版本升到 **3.5.7**,`ASSIGN_ID` 行为不变(雪花算法实现一致,生成的 ID 格式相同)。

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
| 🔶 JDBC 驱动 | PostgreSQL JDBC(SB 3 由 BOM 管理,版本较 `master` 更新;**URL 格式不变**) |

### 1.4 不使用(与 `master` 一致)

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
| SQL 脚本变更 | **无**(`file-sql/basicdb-vue3.sql` 与 `master` 逐字节一致) |
| 数据迁移 | **不需要** |
| 回滚时的数据回滚 | **不需要** |

> **为什么零变更**:Spring Boot 2 → 3 的升级范围是 ① JDK 11 → 17 ② Java EE → Jakarta EE(包名)③ Spring Framework 5 → 6 ④ Spring Security 5 → 6 ⑤ springfox → springdoc ⑥ starter 变体 —— **全部是应用层/依赖层变化,与数据库 schema 无关**。
>
> 这与「MySQL → PostgreSQL」那种**数据层迁移**有本质区别:后者必须改 DDL、改函数、重导数据;本分支**一行 SQL 都不用改**。

### 1.6 🔶 升级对数据层的间接影响(需知悉但不改结构)

虽不改 schema,但升级带来 3 处**数据内容/写入方式**的细微变化:

| 影响点 | 说明 | 是否需改 DDL |
|---|---|---|
| **`sys_logininfor.browser` / `os` 字段内容** | UA 解析从 bitwalker → **yauaa**,同一 UA 串的解析结果文本可能不同(如浏览器名写法、版本格式) | ❌ 否(字段仍是 `varchar(50)`) |
| **`sys_logininfor.os` 长度** | yauaa 输出的 OS 串比 bitwalker 更详细(如 `Windows 10` → `Windows 10 (10.0)`),极端情况可能超 50 字符 | ❌ 否(写入前截断/兜底,见 §7.3) |
| **`sys_oper_log.oper_param` / `json_result`** | Jackson 仍为 2.x(SB 3 默认),序列化格式不变 | ❌ 否 |
| **雪花 ID 生成** | MP 3.5.1 → 3.5.7,算法不变 | ❌ 否 |
| **`qrtz_*` 表** | Quartz 版本由 SB 3 BOM 管理,表结构仍是 Quartz 2.3+ | ❌ 否 |

> **结论**:无任何 DDL 变更;唯一需要关注的是 `browser`/`os` 的**内容格式**变化,不影响功能。

---

## 2. 通用(系统级)表 — `sys_*`(21 张)

> 全部带 `create_by` / `create_time` / `update_by` / `update_time` / `remark` / `del_flag`(逻辑删除)。
> **🔶 所有字段定义与 `master` 完全一致**;本节标注「🔶 升级相关」的仅是有内容含义变化的字段。

### 2.1 `sys_user` 用户

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| user_id | bigint | PK | 雪花 |
| dept_id | bigint | Y | 部门 |
| user_name | varchar(30) | Y | 登录名,唯一 |
| nick_name | varchar(30) | Y | 昵称 |
| user_type | varchar(2) | N | `00=系统用户` |
| email | varchar(50) | N | — |
| phonenumber | varchar(11) | N | — |
| sex | char(1) | N | `0=男 1=女 2=未知` |
| avatar | varchar(100) | N | 头像 URL |
| password | varchar(100) | Y | BCrypt 散列 |
| status | char(1) | Y | `0=正常 1=停用` |
| login_ip | varchar(128) | N | 最近登录 IP |
| login_date | datetime | N | 最近登录时间 |
| create_by / create_time / update_by / update_time / remark | — | — | 通用 |
| del_flag | char(1) | Y | `0=未删 2=已删` |

索引:
- 唯一索引:`uk_user_name(user_name)`
- 普通索引:`idx_dept_id(dept_id)` / `idx_status(status)`

### 2.2 `sys_role` 角色

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| role_id | bigint | PK | 雪花 |
| role_name | varchar(30) | Y | 显示名 |
| role_key | varchar(100) | Y | 权限字符,唯一 |
| role_sort | int | Y | 显示顺序 |
| data_scope | char(1) | Y | `1=全部 2=自定义 3=本部门 4=本部门及下 5=本人` |
| status | char(1) | Y | `0=正常 1=停用` |
| 通用字段 | — | — | — |

索引:`uk_role_key(role_key)`

### 2.3 `sys_menu` 菜单

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| menu_id | bigint | PK | 雪花 |
| menu_name | varchar(50) | Y | 中文名 |
| menu_name_en | varchar(50) | N | 英文(扩展) |
| menu_name_zh_hant | varchar(50) | N | 繁体(扩展) |
| parent_id | bigint | Y | `0=顶级` |
| order_num | int | Y | 显示顺序 |
| path | varchar(200) | N | 前端路由 path |
| component | varchar(255) | N | 前端组件路径 |
| is_frame | char(1) | N | `1=外链 0=内链` |
| is_cache | char(1) | N | `0=缓存 1=不缓存` |
| menu_type | char(1) | Y | `M=目录 C=菜单 F=按钮` |
| visible | char(1) | N | `0=显示 1=隐藏` |
| status | char(1) | Y | `0=正常 1=停用` |
| perms | varchar(100) | N | 权限字符(`F` 类型必填) |
| icon | varchar(100) | N | Element Plus icon |
| is_resource | char(1) | N | 资源标识(扩展) |
| show_tags | char(1) | N | 是否显示标签(扩展) |
| 通用字段 | — | — | — |

索引:`idx_parent_id(parent_id)` / `idx_status(status)`
> 🔶 菜单数据**无需变更**:前端路由与权限字符完全未动。

### 2.4 `sys_dept` 部门(自引用树)

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| dept_id | bigint | PK | 雪花 |
| parent_id | bigint | Y | `0=顶级` |
| ancestors | varchar(500) | N | `0,1,2` 链路 |
| dept_name | varchar(30) | Y | 同级唯一 |
| order_num | int | Y | 显示顺序 |
| leader | varchar(20) | N | 负责人 user_id |
| phone | varchar(11) | N | — |
| email | varchar(50) | N | — |
| status | char(1) | Y | `0=正常 1=停用` |
| 通用字段 | — | — | — |

索引:`idx_parent_id(parent_id)` / `idx_status(status)`

### 2.5 `sys_post` 岗位

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| post_id | bigint | PK | 雪花 |
| post_code | varchar(64) | Y | 唯一 |
| post_name | varchar(50) | Y | — |
| post_sort | int | Y | 显示顺序 |
| status | char(1) | Y | `0=正常 1=停用` |
| 通用字段 | — | — | — |

索引:`uk_post_code(post_code)`

### 2.6 关联表(4 张)

```sql
sys_user_role  (user_id, role_id)            -- 复合 PK
sys_role_menu  (role_id, menu_id)            -- 复合 PK
sys_role_dept  (role_id, dept_id)            -- 复合 PK
sys_user_post  (user_id, post_id)            -- 复合 PK
```

均无独立主键,两列复合;支持 `user_id` / `role_id` 单列索引用于反查。

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
| css_class | varchar(100) | N | 前端样式类 |
| list_class | varchar(100) | N | 列表样式 |
| is_default | char(1) | N | `Y=默认 N=否` |
| status | char(1) | Y | `0=正常 1=停用` |
| 通用字段 | — | — | — |

索引:`idx_dict_type(dict_type)`

### 2.9 `sys_config` 全局参数

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

> 🔶 **升级相关**:`sys_config` 里**没有**与 Spring Boot 版本相关的配置项(SB 配置在 `application*.yml`)。因此升级**不需要改任何 `sys_config` 数据**。
> 与验证码相关的 `sys.account.captchaEnabled` 仍是同一 KV(本分支仍是 kaptcha 2.3.3)。

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

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| oper_id | bigint | PK | 雪花 |
| title | varchar(50) | N | 模块名 |
| business_type | int | Y | 业务类型 0~9 |
| method | varchar(100) | Y | 方法名 |
| request_method | varchar(10) | N | GET/POST |
| operator_type | int | Y | 0=其它 1=后台 2=手机 |
| oper_name | varchar(50) | N | 操作人 |
| dept_name | varchar(50) | N | 部门 |
| oper_url | varchar(255) | N | URL |
| oper_ip | varchar(128) | N | IP |
| oper_param | varchar(2000) | N | 入参 JSON |
| json_result | varchar(2000) | N | 出参 JSON |
| status | int | Y | `0=成功 1=失败` |
| error_msg | varchar(2000) | N | 错误信息 |
| oper_time | datetime | Y | — |
| cost_time | bigint | N | 耗时 ms |

索引:`idx_business_type(business_type)` / `idx_oper_time(oper_time)`

### 2.13 `sys_logininfor` 登录日志(🔶 内容受 yauaa 影响)

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| info_id | bigint | PK | 雪花 |
| user_name | varchar(50) | Y | 登录名 |
| ipaddr | varchar(128) | N | IP |
| login_location | varchar(255) | N | 归属地 |
| **browser** | varchar(50) | N | 🔶 由 **yauaa** 解析(原 bitwalker);文本格式可能不同 |
| **os** | varchar(50) | N | 🔶 同上;yauaa 输出更详细,**可能超 50 字符** |
| status | char(1) | Y | `0=成功 1=失败` |
| msg | varchar(255) | N | 消息 |
| login_time | datetime | Y | — |

索引:`idx_user_name(user_name)` / `idx_login_time(login_time)`

> ⚠️ **🔶 唯一需要关注的字段**:`os` 是 `varchar(50)`,yauaa 输出可能的形态:
>
> | UA | bitwalker(旧) | yauaa(新) |
> |---|---|---|
> | Chrome 120 / Windows 10 | `Windows 10` | `Windows 10 (10.0)` |
> | Chrome / macOS 14 | `Mac OS X` | `Mac OS X 14.0` |
> | 微信内置 / iOS 17 | `iOS` | `iOS 17.1` |
> | 某些爬虫 | `Unknown` | `Unknown` 或超长串 |
>
> **处置**(不改 DDL):写入前做 `截断到 50 字符` + `"Unknown"` 置空兜底,见 §7.3。

### 2.14 `sys_job` 定时任务

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| job_id | bigint | PK | 雪花 |
| job_name | varchar(64) | Y | — |
| job_group | varchar(64) | Y | `DEFAULT` / `SYSTEM` |
| invoke_target | varchar(500) | Y | `ryTask.ryNoParams` |
| cron_expression | varchar(255) | Y | Cron |
| misfire_policy | varchar(20) | Y | `0~3` |
| concurrent | char(1) | Y | `0=允许 1=禁止` |
| status | char(1) | Y | `0=运行 1=暂停` |
| 通用字段 | — | — | — |

### 2.15 `sys_job_log` 任务日志

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| job_log_id | bigint | PK | 雪花 |
| job_name | varchar(64) | Y | — |
| job_group | varchar(64) | Y | — |
| invoke_target | varchar(500) | Y | — |
| job_message | varchar(500) | N | — |
| status | char(1) | Y | `0=成功 1=失败` |
| exception_info | text | N | 异常堆栈 |
| create_time | datetime | Y | — |

### 2.16 `sys_demo` 业务示例(`business-core`)

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
| size | bigint | N | 字节 |
| mime_type | varchar(100) | N | — |
| 通用字段 | — | — | — |

> 🔶 上传实现仍走 `commons-fileupload 1.5` + `FileUtils`(jakarta 版),字段语义不变。

### 2.18 `sys_i18n` 国际化

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| lang | varchar(20) | Y | `zh-CN` / `en-US` 等 |
| key | varchar(100) | Y | — |
| value | varchar(500) | Y | — |
| 通用字段 | — | — | — |

索引:`uk_lang_key(lang, key)` 唯一

### 2.19 ⚠️ 关于 `sys_sms_message`(纠正历史文档)

> **本分支 `file-sql/basicdb-vue3.sql` 中不存在 `sys_sms_message` 表**(实测 `Select-String "sys_sms_message"` 命中 **0**)。
>
> `master` 分支的早期《数据库设计》v0.1 曾列出该表(§2.17),属于**误记**。本分支文档**已剔除**,并在此明示。
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
| tpl_category | varchar(200) | Y | 模板分类 `crud` / `tree` |
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
| is_pk | char(1) | Y | `1=是` |
| is_increment | char(1) | Y | `1=是` |
| is_required | char(1) | Y | `1=必填` |
| is_insert | char(1) | Y | `1=插入字段` |
| is_edit | char(1) | Y | `1=编辑字段` |
| is_list | char(1) | Y | `1=列表字段` |
| is_query | char(1) | Y | `1=查询字段` |
| query_type | varchar(200) | Y | `EQ` / `LIKE` 等 |
| html_type | varchar(200) | Y | `input` / `select` 等 |
| dict_type | varchar(200) | N | 关联字典 |
| sort | int | Y | 排序 |

索引:`idx_table_id(table_id)`

> ⚠️ **🔶 升级相关(R20)**:`gen_table` / `gen_table_column` **表结构不变**,但**代码生成器产出的 Java 代码是 SB 2 风格**(`javax.*` + springfox 注解)。生成后需手工改 `jakarta.*` 与 `io.swagger.v3`。详见《详细设计》§4.4。

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

> 🔶 **升级相关**:Quartz 版本由 SB 3 BOM 管理(较 `master` 新),但**表结构仍是 Quartz 2.3+ 标准结构,无需变更**。`extend-quartz/pom.xml` 现显式声明 `org.quartz-scheduler:quartz`。
> **建议**:升级后首次启动前,先确认 `qrtz_*` 表数据能正常反序列化(`JobDataMap` 里存的是 `SysJob` 对象,若类结构变化会有反序列化风险 —— 本分支 `SysJob` **未改字段**,无风险)。

---

## 5. 函数(5 个,由 SQL 脚本创建)

| 函数 | 签名 | 用途 |
|---|---|---|
| `date_format` | `(indate anyelement, informate text) RETURNS text` | 兼容 MySQL 日期格式化写法 |
| `exec` | `(sqlstring character varying) RETURNS character varying` | 动态执行 SQL(供代码生成器查询元数据) |
| `find_in_set` | `(bigint, character varying) RETURNS boolean` | 兼容 MySQL 的逗号分隔串查找 |
| `ifnull` | `(param1 text, param2 text) RETURNS text` | 兼容 MySQL 的 NULL 兜底 |
| `tabledef` | `(oid) RETURNS text` | 取表 DDL 定义 |

> 这些函数是**从 MySQL 迁 PostgreSQL 的兼容层**,`master` 已建立,本分支**未改**(实测 `CREATE FUNCTION` 计数 = 5)。
> 🔶 升级 SB 不影响这些函数(它们是 DB 侧 PL/pgSQL,与 Java 版本无关)。

---

## 6. 业务示例与扩展点

### 6.1 业务示例(`sys_demo`)

见 §2.16。

### 6.2 扩展点

- **新增业务模块**:在 `business-core` 下新增 `domain / mapper / service / controller`,对应 `sys_xxx` 表,**不要修改 `basic-admin-core`**
- **新增扩展能力**:在 `extend-*` 模式新增子模块,挂到 `pom.xml` 的 `dependencyManagement` + `modules`
- **代码生成产物**:落 `gen_table`,不落物理表
- **🔶 新增字段时的 SB3 注意事项**(本分支特有):
  1. 实体上的校验注解用 **`jakarta.validation.constraints.*`**(不是 javax)
  2. Controller 的 swagger 注解用 **`@Schema`**(不是 `@ApiModelProperty`)
  3. 若字段是 UA/浏览器相关,注意 yauaa 输出长度
  4. 有 DDL 变更必须同步 `file-sql/basicdb-vue3.sql` 并在文档 §12 登记

---

## 7. 索引与查询说明

### 7.1 关键查询索引(与 `master` 一致)

| 关键查询 | 命中索引 | 备注 |
|---|---|---|
| `select * from sys_user where user_name=?` | `uk_user_name` | 登录场景 |
| `select * from sys_role where role_key=?` | `uk_role_key` | 角色校验 |
| `select * from sys_menu where parent_id=? and status=0` | `idx_parent_id` | 菜单树 |
| `select * from sys_dept where parent_id=? and status=0` | `idx_parent_id` | 部门树 |
| `select * from sys_user_role where user_id=?` | 复合 (user_id, role_id) | 权限加载 |
| `select * from sys_role_menu where role_id=?` | 复合 (role_id, menu_id) | 权限加载 |
| `select * from sys_dict_data where dict_type=? and status=0` | `idx_dict_type` | 字典缓存 |
| `select * from sys_config where config_key=?` | `uk_config_key` | 参数读取 |
| `select * from sys_oper_log where business_type=? and oper_time between ? and ?` | `idx_business_type` / `idx_oper_time` | 操作日志查询 |
| `select * from sys_logininfor where status=? and login_time between ? and ?` | `idx_login_time` | 登录日志查询 |
| `select * from sys_notice_read where user_id=? and notice_id=?` | `uk_user_notice` | 唯一命中 |
| `select * from qrtz_triggers where TRIGGER_GROUP=?` | Quartz 自带 | 调度 |
| `select * from gen_table where table_name=?` | 物理表主键 | 反查 |

### 7.2 🔶 升级后查询行为差异(需知悉)

| 项 | 变化 | 影响 |
|---|---|---|
| MP 3.5.1 → 3.5.7 | 分页 SQL 生成逻辑微调 | 分页结果一致;若有自定义 `IPage` 用法需回归 |
| PageHelper 1.4.1 → 2.1.1 | 方言自动识别策略调整 | PostgreSQL 方言仍自动识别;建议回归分页总数 |
| MyBatis-Spring-Boot 3.0.5 | `SqlSessionFactory` 装配方式 | 原 `MybatisConfig` / `MybatisPlusConfig` 已适配 |
| Druid 1.2.8 → 1.2.28 | 监控统计口径 | `/druid/` 页面数据正常即可 |
| SB 3 路径匹配 | `PathPatternParser` 替代 `AntPathMatcher` | **不影响 SQL**,只影响 URL 匹配 |

### 7.3 🔶 `sys_logininfor.browser` / `os` 写入保护(建议实现)

```java
// AsyncFactory 内部(伪代码)
private static String safeUaValue(String v, int maxLen) {
    if (v == null || "Unknown".equalsIgnoreCase(v.trim())) return "";
    v = v.trim();
    return v.length() > maxLen ? v.substring(0, maxLen) : v;
}

// 写入前:
String browser = safeUaValue(agent.getValue("AgentName") + " " + agent.getValue("AgentVersion"), 50);
String os      = safeUaValue(agent.getValue("OperatingSystemName") + " " + agent.getValue("OperatingSystemVersion"), 50);
```

> 这样即使 yauaa 输出更长也不会触发 `value too long for type character varying(50)`,且避免把 `"Unknown"` 写进日志误导排查。

---

## 8. DDL 约定

| 项 | 约定 |
|---|---|
| 主键 | MyBatis-Plus 雪花(`ASSIGN_ID`),PostgreSQL 字段类型 `bigint`,**不要**用 `bigserial` |
| 通用字段 | `create_by` varchar(64) / `create_time` datetime / `update_by` varchar(64) / `update_time` datetime / `remark` varchar(500) / `del_flag` char(1) default '0' |
| 逻辑删除 | `del_flag=2` 视为已删(对应 MP `logic-delete-value=2` / `logic-not-delete-value=0`) |
| 必填 | SQL 层 `NOT NULL`,应用层 **`jakarta.validation`** 的 `@NotBlank` / `@NotNull` |
| 字符串 | varchar(N),N 视业务给;枚举用 `char(1)` |
| 时间 | `datetime`(应用层 `yyyy-MM-dd HH:mm:ss` 由 Jackson 全局格式化) |
| 金额 | 本项目暂无金额字段;若新增,**必须** `numeric(p,s)`,**禁止** `float` / `double` |
| 字符集 | UTF8 |
| 时间戳 | 不用 `timestamp with time zone`,用 `datetime` |
| 归档 | 当前不做物理归档,只逻辑删除 |
| 迁移工具 | 暂时**不**用 Flyway / Liquibase,所有变更直接修改 `basicdb-vue3.sql` 并附变更说明(见 §12) |
| 🔶 UA 相关字段 | `browser` / `os` 等由 yauaa 写入,varchar(50) **建议写入前截断**(见 §7.3) |
| 🔶 升级期约定 | **本次升级不改 DDL**;若必须改,需单独评审(会破坏「零变更、易回滚」特性) |

---

## 9. 外部引擎表

### 9.1 分支对照

| 引擎 / 能力 | 所在分支 | 本分支是否包含 |
|---|---|---|
| Flowable (`act_*` / `flw_*`) | `springboot2-extend-flowable` / `springboot3-extend-flowable`(`basicdb_flowable` schema,86 表 + 2 视图) | ❌ 不含 |
| tianai-captcha(Redis 凭证,不落库) | `springboot2-tianai-captcha` / `springboot3-tianai-captcha` | ❌ 不含(本分支仍是 kaptcha 2.3.3) |

> 🔶 本分支**不预建 Flowable 表**,也**不含 tianai-captcha 相关变更**;仍是 `kaptcha 2.3.3` 字符验证码(`sys_config.sys.account.captchaEnabled` 控制开关)。

### 9.2 运行期非 DB 存储

| 存储 | 内容 | 🔶 本分支影响 |
|---|---|---|
| Redis `login_tokens:<uuid>` | 登录会话(TTL 30min) | 配置键改 `spring.data.redis.*`,**存储内容不变** |
| Redis `sys_dict::<dictType>` | 字典缓存 | 无 |
| Redis `REPEAT_SUBMIT:*` | 重复提交标记 | 无 |
| 本地磁盘 `D:/ruoyi/uploadPath` | 上传文件 | 无 |

---

## 10. 备份与恢复(与 `master` 完全一致)

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

- 加 `--no-owner --no-privileges`:可移植
- 不加:对象 `OWNER TO system`(与现仓库一致)

### 10.3 注意事项

- 备份脚本**只能**用 `psql` 执行(因 `COPY ... FROM stdin`);不要用某些 GUI 工具的「执行 SQL」入口
- 重置 admin 密码:跑 `file-sql/reset-admin-password.sql`
- 其它历史 SQL(`basic-sql-2023-03-02.sql` / `quartz.sql` / `ry_20210908.sql` / `fix-sys-menu-component.sql` / `create-sys-notice-read.sql`)**不要**作为运行入口
- 🔶 **升级回滚时无需数据回滚**:DB 与升级前完全一致,回退 jar 即可

---

## 11. 表清单(全文汇总,共 34 张)

| 域 | 表 | 数量 | 大致行数 |
|---|---|---|---|
| 代码生成 | `gen_table` / `gen_table_column` | 2 | 小(<100) |
| Quartz | `qrtz_*`(11 张全列于 §4) | 11 | 取决于任务数 |
| 系统-鉴权 | `sys_user` / `sys_role` / `sys_menu` / `sys_user_role` / `sys_role_menu` / `sys_role_dept` / `sys_user_post` | 7 | 中 |
| 系统-组织 | `sys_dept` / `sys_post` | 2 | 中 |
| 系统-字典参数 | `sys_config` / `sys_dict_type` / `sys_dict_data` | 3 | 小~中 |
| 系统-通知 | `sys_notice` / `sys_notice_read` | 2 | 小 / 大 |
| 系统-审计 | `sys_oper_log` / `sys_logininfor` | 2 | 很大 / 大 |
| 系统-文件国际化 | `sys_files` / `sys_i18n` | 2 | 大 / 小 |
| 系统-定时任务 | `sys_job` / `sys_job_log` | 2 | 小 / 大 |
| 系统-示例 | `sys_demo` | 1 | 演示用 |
| **合计** | | **34** | |

**明细校验**:2(gen)+ 11(qrtz)+ 21(sys)= **34** ✅

> 🔶 `sys_user_online` 实际由 Redis 维护(`CacheConstants.LOGIN_TOKEN_KEY`),**不写库**,故不计入 34。
> 🔶 `sys_sms_message` **不存在**(见 §2.19)。

---

## 12. 需求变更记录

| 变更 | 落点 |
|---|---|
| 数据库迁 PostgreSQL | §1 / §10 |
| `sys_notice_read` 表 | §2.11 |
| `sys_menu` 扩展列(`menu_name_en` / `menu_name_zh_hant` / `is_resource` / `show_tags`) | §2.3 |
| `sys_i18n` 国际化表 | §2.18 |
| `sys_files` 文件元数据 | §2.17 |
| `sys_demo` 业务示例 | §2.16 |
| **🔶 Spring Boot 2 → 3 升级** | **§1.5(数据库零变更声明)+ §1.6(升级对数据层的间接影响)+ §2.13(yauaa 影响 `browser`/`os`)+ §4(Quartz 表结构不变)+ §7.2(查询行为差异)+ §7.3(UA 写入保护)+ §8(DDL 约定加 UA/升级期条目)** |
| **🔶 纠正 `sys_sms_message` 误记** | §2.19 |
| **🔶 代码生成模板未适配 SB3(R20)** | §3.2 注 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `master` 数据库设计(v0.1)为基线,标注本分支**数据库零变更**(§1.5,含 8 项零变更清单)+ 新增「§1.6 升级对数据层的间接影响(yauaa 改 `browser`/`os` 内容)」+ §2.13 UA 字段说明 + §4 Quartz 结构不变说明 + §7.2 升级后查询行为差异 + §7.3 UA 写入保护代码 + §8 DDL 约定补 2 条;实测核对 **34 表 / 5 函数**,纠正 `sys_sms_message` 误记(§2.19) |
