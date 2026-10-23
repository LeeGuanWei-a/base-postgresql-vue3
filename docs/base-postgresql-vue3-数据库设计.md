# 基础平台(base-postgresql-vue3) 数据库设计

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 `master` 分支
> 前置依赖:《需求文档》v0.1、《概要设计》v0.1
> 本文档是数据落点:schema/表/索引/字段注释/迁移/备份。所有字段以《需求文档》为准,不在此文档内自创新字段。

---

## 1. 总览

### 1.1 Schema 布局

| Schema | 表数 | 内容 |
|---|---|---|
| `basicdb` | **34** | 全部业务表(代码生成 2 + Quartz 11 + 业务 21) |
| `public` | 0(本项目不使用) | — |

> 全部对象由 `file-sql/basicdb-vue3.sql` 一份脚本恢复。
> **JDBC URL 必须带 `currentSchema=basicdb`**,否则 MyBatis-Plus 找不到表。

### 1.2 命名约定

| 类别 | 前缀 | 示例 |
|---|---|---|
| 业务系统表 | `sys_` | `sys_user` / `sys_role` / `sys_menu` |
| 代码生成表 | `gen_` | `gen_table` / `gen_table_column` |
| Quartz 内部表 | `qrtz_` | `qrtz_triggers` / `qrtz_job_details` |
| 通用字段 | — | `create_by` / `create_time` / `update_by` / `update_time` / `remark` / `del_flag` |
| 逻辑删除 | `del_flag` | `0=未删` / `2=已删`(约定值) |
| 状态 | `status` | `0=正常` / `1=停用/关闭` |

> 主键策略:全库统一 **MyBatis-Plus `ASSIGN_ID`(雪花)**(由 `mybatis-plus.global-config.db-config.id-type=ASSIGN_ID` 控制),非 PostgreSQL 原生 `bigserial`。这是 RuoYi 默认行为,不要改。

### 1.3 数据库与角色约定

| 项 | 值 |
|---|---|
| PostgreSQL 版本 | **≥ 15.1** |
| 角色 | `system` / password `system`(`OWNER`) |
| 库 | `basicdb` / `OWNER=system` |
| 字符集 | 默认 UTF8 |
| 时区 | `Asia/Shanghai`(应用侧 `time-zone: GMT+8`) |

### 1.4 不使用

- 不使用外键约束(FK)— 走应用层事务保证
- 不使用 PostgreSQL 原生 schema 隔离以外的方案
- 不使用触发器(`gen_table` 元数据除外)

---

## 2. 通用(系统级)表 — `sys_*`

> 全部带 `create_by` / `create_time` / `update_by` / `update_time` / `remark` / `del_flag`(逻辑删除)。

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

### 2.6 `sys_user_role` / `sys_role_menu` / `sys_role_dept` / `sys_user_post`(关联表)

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

### 2.10 `sys_notice` 通知公告

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| notice_id | int | PK | 自增 |
| notice_title | varchar(50) | Y | — |
| notice_type | char(1) | Y | `1=通知 2=公告` |
| notice_content | text | Y | — |
| status | char(1) | Y | `0=正常 1=关闭` |
| 通用字段 | — | — | — |

### 2.11 `sys_notice_read` 通知已读(由 `create-sys-notice-read.sql` 补建)

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

### 2.13 `sys_logininfor` 登录日志

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| info_id | bigint | PK | 雪花 |
| user_name | varchar(50) | Y | 登录名 |
| ipaddr | varchar(128) | N | IP |
| login_location | varchar(255) | N | 归属地 |
| browser | varchar(50) | N | 浏览器 |
| os | varchar(50) | N | 操作系统 |
| status | char(1) | Y | `0=成功 1=失败` |
| msg | varchar(255) | N | 消息 |
| login_time | datetime | Y | — |

索引:`idx_user_name(user_name)` / `idx_login_time(login_time)`

### 2.14 `sys_demo` 业务示例(`business-core`)

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| name | varchar(50) | Y | — |
| code | varchar(50) | Y | — |
| description | varchar(255) | N | — |
| 通用字段 | — | — | — |

### 2.15 `sys_files` 文件

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| name | varchar(255) | Y | 原始名 |
| original_name | varchar(255) | N | — |
| url | varchar(500) | Y | 路径 |
| size | bigint | N | 字节 |
| mime_type | varchar(100) | N | — |
| 通用字段 | — | — | — |

### 2.16 `sys_i18n` 国际化

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| lang | varchar(20) | Y | `zh-CN` / `en-US` 等 |
| key | varchar(100) | Y | — |
| value | varchar(500) | Y | — |
| 通用字段 | — | — | — |

索引:`uk_lang_key(lang, key)` 唯一

### 2.17 `sys_sms_message` 短信记录

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| phone | varchar(20) | Y | — |
| content | varchar(500) | Y | — |
| status | char(1) | Y | `0=待发 1=已发 2=失败` |
| send_time | datetime | N | — |
| 通用字段 | — | — | — |

---

## 3. 代码生成表 — `gen_*`

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
| table_id | bigint | Y | 外键引用 gen_table |
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

---

## 4. Quartz 定时任务表 — `qrtz_*`(11 张)

> Quartz 2.3+ 标准表结构,由 `QuartzDisallowConcurrentExecution` / `AbstractQuartzJob` 自动维护。

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

> 字段结构与 Quartz 官方一致,本项目不修改;Quartz 自带索引。

---

## 5. 业务示例与扩展点

### 5.1 业务示例(`sys_demo`)

见 §2.14。

### 5.2 扩展点

- **新增业务模块**:在 `business-core` 下新增 `domain / mapper / service / controller`,对应 `sys_xxx` 表,**不要修改 `basic-admin-core`**
- **新增扩展能力**:在 `extend-*` 模式新增子模块,挂到 `pom.xml` 的 `dependencyManagement` + `modules`
- **代码生成产物**:落 `gen_table`,不落物理表

---

## 6. 索引与查询说明

| 关键查询 | 命中索引 | 备注 |
|---|---|---|
| `select * from sys_user where user_name=?` | `uk_user_name` | 登录场景 |
| `select * from sys_role where role_key=?` | `uk_role_key` | 角色校验 |
| `select * from sys_menu where parent_id=? and status=0` | `idx_parent_id` | 菜单树 |
| `select * from sys_dept where parent_id=? and status=0` | `idx_parent_id` | 部门树 |
| `select * from sys_user_role where user_id=?` | 复合 (user_id, role_id) | 权限加载 |
| `select * from sys_role_menu where role_id=?` | 复合 (role_id, menu_id) | 权限加载 |
| `select * from sys_dict_data where dict_type=? and status=0` | `idx_dict_type` | 字典缓存 |
| `select * from sys_oper_log where business_type=? and oper_time between ? and ?` | `idx_business_type` / `idx_oper_time` | 操作日志查询 |
| `select * from sys_logininfor where status=? and login_time between ? and ?` | `idx_login_time` | 登录日志查询 |
| `select * from sys_notice_read where user_id=? and notice_id=?` | `uk_user_notice` | 唯一命中 |
| `select * from qrtz_triggers where TRIGGER_GROUP=?` | Quartz 自带 | 调度 |
| `select * from gen_table where table_name=?` | 物理表主键 | 反查 |

---

## 7. DDL 约定

| 项 | 约定 |
|---|---|
| 主键 | MyBatis-Plus 雪花(`ASSIGN_ID`),PostgreSQL 字段类型 `bigint`,**不要**用 `bigserial` |
| 通用字段 | `create_by` varchar(64) / `create_time` datetime / `update_by` varchar(64) / `update_time` datetime / `remark` varchar(500) / `del_flag` char(1) default '0' |
| 逻辑删除 | `del_flag=2` 视为已删(对应 MP `logic-delete-value=2` / `logic-not-delete-value=0`) |
| 必填 | SQL 层 `NOT NULL`,应用层 `@NotBlank` / `@NotNull` |
| 字符串 | varchar(N),N 视业务给;枚举用 `char(1)` |
| 时间 | `datetime`(应用层 `yyyy-MM-dd HH:mm:ss` 由 Jackson 全局格式化) |
| 金额 | 本项目暂无金额字段;若新增,**必须** `numeric(p,s)`,**禁止** `float` / `double` |
| 字符集 | UTF8 |
| 时间戳 | 不用 `timestamp with time zone`,用 `datetime` |
| 归档 | 当前不做物理归档,只逻辑删除 |
| 迁移工具 | 暂时**不**用 Flyway / Liquibase,所有变更直接修改 `basicdb-vue3.sql` 并附变更说明(见 §9) |

---

## 8. 外部引擎表(本项目不含)

| 引擎 | 所在分支 |
|---|---|
| Flowable (`act_*` / `flw_*`) | `springboot2-extend-flowable` 分支(`basicdb_flowable` schema,86 表 + 2 视图) |

> 本分支不预建 Flowable 表。

---

## 9. 备份与恢复

### 9.1 恢复(全新数据库)

```bash
psql -U postgres -h localhost -c "CREATE ROLE system LOGIN PASSWORD 'system';"
psql -U postgres -h localhost -c "CREATE DATABASE basicdb OWNER system;"
psql -U system -h localhost -d basicdb -f file-sql/basicdb-vue3.sql
psql -U system -h localhost -d basicdb -c \
  "SELECT count(*) FROM information_schema.tables WHERE table_schema='basicdb';"
# 期望返回 34
```

### 9.2 备份

```bash
pg_dump -U system -h localhost -d basicdb \
  --schema=basicdb --no-owner --no-privileges \
  --file=file-sql/basicdb-vue3.sql
```

- 加 `--no-owner --no-privileges`:可移植
- 不加:对象 `OWNER TO system`(与现仓库一致)

### 9.3 注意事项

- 备份脚本**只能**用 `psql` 执行(因 `COPY ... FROM stdin`);不要用某些 GUI 工具的「执行 SQL」入口
- 重置 admin 密码:跑 `file-sql/reset-admin-password.sql`
- 其它历史 SQL(见 README §6.7)**不要**作为运行入口

---

## 10. 表清单(全文汇总,共 34 张)

| 域 | 表 | 大致行数 |
|---|---|---|
| 代码生成 | `gen_table` / `gen_table_column` | 小(<100) |
| Quartz | `qrtz_*` × 11 | 取决于任务数 |
| 业务 | `sys_config` | 小 |
| | `sys_demo` | 演示用 |
| | `sys_dept` | 中 |
| | `sys_dict_data` | 中 |
| | `sys_dict_type` | 小 |
| | `sys_files` | 大(按上传量) |
| | `sys_i18n` | 小 |
| | `sys_job` | 小(数十) |
| | `sys_job_log` | 大(按任务频率) |
| | `sys_logininfor` | 大 |
| | `sys_menu` | 中 |
| | `sys_notice` | 小 |
| | `sys_notice_read` | 大(按用户读量) |
| | `sys_oper_log` | 很大(按访问量) |
| | `sys_post` | 小 |
| | `sys_role` / `sys_role_dept` / `sys_role_menu` | 小/中 |
| | `sys_sms_message` | 中 |
| | `sys_user` / `sys_user_post` / `sys_user_role` | 中 |
| | `sys_user_online`(内存中维护) | 小 |

> 状态:`sys_user_online` 实际由 Redis 维护(`CacheConstants.LOGIN_TOKEN_KEY`),不写库。

---

## 11. 需求变更记录

| 变更 | 落点 |
|---|---|
| 数据库迁 PostgreSQL | §1 / §9 |
| `sys_notice_read` 表补建 | §2.11 |
| `sys_menu` 扩展列 | §2.3 |
| `sys_i18n` 国际化表 | §2.16 |
| `sys_sms_message` 短信记录 | §2.17 |
| `sys_files` 文件元数据 | §2.15 |
| `sys_demo` 业务示例 | §2.14 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:覆盖 schema 布局/21 张 sys_* / 2 张 gen_* / 11 张 qrtz_*/索引/恢复 |
