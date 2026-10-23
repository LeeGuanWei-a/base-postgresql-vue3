# 基础平台(base-postgresql-vue3) 数据库设计 — `springboot2-tianai-captcha` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot2-tianai-captcha`** 分支
> 前置依赖:《需求文档》v0.1、《概要设计》v0.1
> 本文档是数据落点:schema/表/索引/字段注释/迁移/备份。所有字段以《需求文档》为准,不在此文档内自创新字段。
> **本分支与 `master` 数据库完全一致**:`basicdb` schema / 34 张表 + 5 函数;**本分支不新增任何数据库对象**。验证码凭证在 Redis 内存中,落库仅 `sys_config` 中 `sys.account.captchaEnabled` 总开关(沿用 master)。

---

## 1. 总览

### 1.1 Schema 布局

| Schema | 表数 | 内容 |
|---|---|---|
| `basicdb` | **34** | 全部业务表(代码生成 2 + Quartz 11 + 业务 21) |
| `public` | 0(本项目不使用) | — |

> 全部对象由 `file-sql/basicdb-vue3.sql` 一份脚本恢复(沿用 master)。
> **JDBC URL 必须带 `currentSchema=basicdb`**,否则 MyBatis-Plus 找不到表。

### 1.2 命名约定(沿用 master)

| 类别 | 前缀 | 示例 |
|---|---|---|
| 业务系统表 | `sys_*` | `sys_user` / `sys_role` / `sys_menu` |
| 代码生成表 | `gen_*` | `gen_table` / `gen_table_column` |
| Quartz 内部表 | `qrtz_*` | `qrtz_triggers` / `qrtz_job_details` |
| 通用字段 | — | `create_by` / `create_time` / `update_by` / `update_time` / `remark` / `del_flag` |
| 逻辑删除 | `del_flag` | `0=未删` / `2=已删` |
| 状态 | `status` | `0=正常` / `1=停用` |

> 主键策略:全库统一 **MyBatis-Plus `ASSIGN_ID`(雪花)**,非 PostgreSQL 原生 `bigserial`。

### 1.3 数据库与角色约定(沿用 master)

| 项 | 值 |
|---|---|
| PostgreSQL 版本 | **≥ 15.1** |
| 角色 | `system` / password `system`(`OWNER`) |
| 库 | `basicdb` / `OWNER=system` |
| 字符集 | UTF8 |
| 时区 | `Asia/Shanghai` |
| 数据加载方式 | `COPY ... FROM stdin`(`pg_dump` 导出) |
| 函数数量 | 5(`date_format` / `exec` / `find_in_set` / `ifnull` / `tabledef`) |

### 1.4 本分支独有改动 — **无**

> **本分支不修改任何表结构、不新增表、不新增字段**。
> `sys_config` 中 `sys.account.captchaEnabled` 是 master 已有的 KV,本分支**复用**而非新增。

### 1.5 不使用(沿用 master)

- 不使用外键约束
- 不使用 PostgreSQL 原生 schema 隔离以外的方案
- 不使用触发器
- **本分支额外不使用**:kaptcha 相关的表 / 字段(已下线;`pom.xml` 仅留死引用)

---

## 2. 通用(系统级)表 — `sys_*`(沿用 master,21 张)

> 全部带 `create_by` / `create_time` / `update_by` / `update_time` / `remark` / `del_flag`(逻辑删除)。
> 本节只列与**行为验证码**相关的 `sys_config`,其它表字段详见《数据库设计》对应版本。

### 2.1 `sys_config` 全局参数(行为验证码总开关)

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

#### 行为验证码相关 KV(沿用 master 的 KV 机制)

| Key | 默认值 | 说明 |
|---|---|---|
| `sys.account.captchaEnabled` | `true` | 行为验证码总开关;`true` 走 `validateTianaiCaptcha`;`false` 跳过(测试用) |
| `user.password.lockOpen` | `false` | 账号级密码错误锁定(与锁屏**无关**) |
| `user.password.maxRetryCount` | `5` | 密码最大重试次数 |
| `user.password.lockTime` | `10`(分钟) | 账号锁定时长 |

### 2.2 `sys_user` 用户(行为验证码无关,仅与登录相关)

> 行为验证码失败时:**不**直接停用用户;`SysLoginService.validateTianaiCaptcha` 失败仅抛 `CaptchaException` + 落 `sys_logininfor`。
> **密码错误 N 次**才会触发 `user.password.*` 锁定机制(沿用 master)。

### 2.3 `sys_logininfor` 登录日志(行为验证码失败落点)

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| info_id | bigint | PK | 雪花 |
| user_name | varchar(50) | Y | 登录名 |
| ipaddr | varchar(128) | N | IP |
| login_location | varchar(255) | N | 归属地 |
| browser | varchar(50) | N | 浏览器 |
| os | varchar(50) | N | 操作系统 |
| status | char(1) | Y | `0=成功 1=失败` |
| **msg** | varchar(255) | N | 失败原因(本分支:`"验证码 id 缺失"` / `"验证码轨迹缺失"` / `"验证码错误"` / `"密码错误"` / `"账号停用"`,**不区分 tianai vs kaptcha**) |
| login_time | datetime | Y | — |

索引:`idx_user_name(user_name)` / `idx_login_time(login_time)`

### 2.4 其它 18 张 `sys_*` 表(沿用 master,本分支未碰)

详见 master 分支的《数据库设计》§2,本节不重复:

- `sys_role` / `sys_menu` / `sys_dept` / `sys_post` / `sys_user_role` / `sys_role_menu` / `sys_role_dept` / `sys_user_post`
- `sys_dict_type` / `sys_dict_data`
- `sys_notice` / `sys_notice_read`
- `sys_oper_log`(操作日志;行为验证码失败**不**入此表)
- `sys_demo` / `sys_files` / `sys_i18n` / `sys_sms_message`(注:master 已删,本分支亦无)

---

## 3. 代码生成表 — `gen_*`(2 张,沿用 master)

### 3.1 `gen_table`

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| table_id | bigint | PK | 雪花 |
| table_name | varchar(200) | Y | — |
| table_comment | varchar(500) | Y | — |
| class_name | varchar(100) | Y | — |
| tpl_category | varchar(200) | Y | `crud` / `tree` |
| package_name | varchar(100) | Y | — |
| module_name | varchar(30) | Y | — |
| business_name | varchar(30) | Y | — |
| function_name | varchar(50) | Y | — |
| function_author | varchar(50) | Y | — |
| gen_type | char(1) | Y | `0=zip 1=生成到项目` |
| gen_path | varchar(200) | Y | — |
| options | varchar(1000) | N | JSON |
| 通用字段 | — | — | — |

### 3.2 `gen_table_column`

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| column_id | bigint | PK | 雪花 |
| table_id | bigint | Y | — |
| column_name | varchar(200) | Y | — |
| column_comment | varchar(500) | Y | — |
| column_type | varchar(100) | Y | — |
| java_type | varchar(100) | Y | — |
| java_field | varchar(100) | Y | — |
| is_pk | char(1) | Y | — |
| is_increment | char(1) | Y | — |
| is_required | char(1) | Y | — |
| is_insert | char(1) | Y | — |
| is_edit | char(1) | Y | — |
| is_list | char(1) | Y | — |
| is_query | char(1) | Y | — |
| query_type | varchar(200) | Y | — |
| html_type | varchar(200) | Y | — |
| dict_type | varchar(200) | N | — |
| sort | int | Y | — |

索引:`idx_table_id(table_id)`

---

## 4. Quartz 定时任务表 — `qrtz_*`(11 张,沿用 master)

> Quartz 2.3+ 标准表结构,本项目不修改。

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

---

## 5. 业务示例与扩展点(沿用 master)

### 5.1 业务示例(`sys_demo`)

沿用 master。`business-core` 模块的示例表。

### 5.2 扩展点

- **新增业务模块**:在 `business-core` 下新增 `domain / mapper / service / controller`,对应 `sys_xxx` 表,**不要修改 `basic-admin-core`**
- **新增扩展能力**:在 `extend-*` 模式新增子模块,挂到 `pom.xml` 的 `dependencyManagement` + `modules`
- **行为验证码配置**:通过 `sys_config` 中 `sys.account.captchaEnabled` KV 切换(沿用 master KV 机制)
- **行为验证码类型扩展**:在 `TianaiCaptchaConfig.tianaiResourceStore` 中加新类型资源,本仓库已支持 3 种;新增类型需配合前端组件

---

## 6. 索引与查询说明(本分支独有)

| 关键查询 | 命中索引 | 备注 |
|---|---|---|
| `select * from sys_user where user_name=?` | `uk_user_name` | 登录场景 |
| `select * from sys_config where config_key='sys.account.captchaEnabled'` | `uk_config_key` | 行为验证码总开关 |
| `select * from sys_logininfor where status='1' and msg like '%验证码%' and login_time between ? and ?` | `idx_login_time` | 行为验证码失败审计 |
| `select * from sys_oper_log where business_type=? and oper_time between ? and ?` | `idx_business_type` / `idx_oper_time` | 操作日志(行为验证码失败不入此表) |
| `select * from qrtz_triggers where TRIGGER_GROUP=?` | Quartz 自带 | 调度 |
| `select * from gen_table where table_name=?` | 物理表主键 | 反查 |
| `Redis GET captcha:verified:<id>` | 内存 | 行为验证码凭证(120s TTL) |

---

## 7. DDL 约定(沿用 master)

| 项 | 约定 |
|---|---|
| 主键 | MyBatis-Plus 雪花(`ASSIGN_ID`),**`bigint`**,**不要**用 `bigserial` |
| 通用字段 | `create_by` varchar(64) / `create_time` datetime / `update_by` varchar(64) / `update_time` datetime / `remark` varchar(500) / `del_flag` char(1) default '0' |
| 逻辑删除 | `del_flag=2` 视为已删 |
| 必填 | SQL 层 `NOT NULL`,应用层 `@NotBlank` / `@NotNull` |
| 字符串 | varchar(N),N 视业务给;枚举用 `char(1)` |
| 时间 | `datetime`(应用层 `yyyy-MM-dd HH:mm:ss`) |
| 字符集 | UTF8 |
| 迁移工具 | 暂时**不**用 Flyway / Liquibase,所有变更直接修改 `basicdb-vue3.sql` 并附变更说明 |
| **kaptcha 死引用** | `pom.xml` 留 `kaptcha` 依赖;**严禁**新增对它的引用;源码 `grep -r "kaptcha" basic-*/src` 应 0 命中 |

---

## 8. 外部引擎表(本分支不含)

| 引擎 | 所在分支 |
|---|---|
| Flowable (`act_*` / `flw_*`) | `springboot2-extend-flowable` 分支(`basicdb_flowable` schema) |
| **tianai-captcha** | **不在数据库中**;凭证存 Redis(TTL 120s),资源在 `classpath:META-INF/cut-image/` |

---

## 9. 备份与恢复(沿用 master)

### 9.1 恢复(全新数据库)

```bash
# 1) 建 system 角色和 basicdb 库
psql -U postgres -h localhost -c "CREATE ROLE system LOGIN PASSWORD 'system';"
psql -U postgres -h localhost -c "CREATE DATABASE basicdb OWNER system;"

# 2) 导入 schema + 数据(约 1~2 秒)
psql -U system -h localhost -d basicdb -f file-sql/basicdb-vue3.sql

# 3) 验证(应返回 34)
psql -U system -h localhost -d basicdb -c \
  "SELECT count(*) FROM information_schema.tables WHERE table_schema='basicdb';"
```

### 9.2 备份

```bash
pg_dump -U system -h localhost -d basicdb \
  --schema=basicdb --no-owner --no-privileges \
  --file=file-sql/basicdb-vue3.sql
```

### 9.3 注意事项

- **重置 admin 密码**:跑 `file-sql/reset-admin-password.sql`
- **重置所有密码**(慎用):跑 `file-sql/reset-all-passwords.sql`
- 其它历史 SQL(见 master README §6.7)本分支不直接使用
- **导入会先删掉同名对象再重建**;导入到已有库等同于覆盖,请先备份

### 9.4 行为验证码相关备份策略

- **凭证不持久化**:Redis 内存数据,重启即失;无需备份
- **总开关持久化**:`sys_config.sys.account.captchaEnabled` 随 `basicdb-vue3.sql` 备份
- **资源不持久化到 DB**:`META-INF/cut-image/*.jpg` 跟着 jar 走;不进 DB

---

## 10. 表清单(全文汇总,共 34 张)

| 域 | 表 | 大致行数 |
|---|---|---|
| 代码生成 | `gen_table` / `gen_table_column` | 小(<100) |
| Quartz | `qrtz_*` × 11 | 取决于任务数 |
| 业务 | `sys_config`(含 `sys.account.captchaEnabled` 总开关) | 小 |
| | `sys_demo` | 演示用 |
| | `sys_dept` | 中 |
| | `sys_dict_data` | 中 |
| | `sys_dict_type` | 小 |
| | `sys_files` | 大 |
| | `sys_i18n` | 小 |
| | `sys_job` / `sys_job_log` | 小 / 大 |
| | `sys_logininfor`(行为验证码失败落点)| 大 |
| | `sys_menu` | 中 |
| | `sys_notice` / `sys_notice_read` | 小 / 大 |
| | `sys_oper_log` | 很大 |
| | `sys_post` | 小 |
| | `sys_role` / `sys_role_dept` / `sys_role_menu` | 小 / 中 |
| | `sys_sms_message` | **master 已删,本分支亦无** |
| | `sys_user` / `sys_user_post` / `sys_user_role` | 中 |
| | `sys_user_online`(内存 Redis 维护,不写库) | — |

> 状态:`sys_user_online` 实际由 Redis 维护(`CacheConstants.LOGIN_TOKEN_KEY`),不写库。
> **本分支独有改动**:**无**;所有表结构与 `master` 完全一致。

---

## 11. 需求变更记录

| 变更 | 落点 |
|---|---|
| 数据库沿用 master | §1 / §9 |
| **行为验证码凭证**(Redis 内存,120s TTL) | §1.4 / §6 / §9.4 |
| **行为验证码总开关** `sys.account.captchaEnabled` | §2.1 |
| **行为验证码失败落 `sys_logininfor`**(不落 `sys_oper_log`) | §2.3 |
| **kaptcha 死引用**:`pom.xml` 保留;源码 0 命中 | §7 |
| **tianai-captcha 不入 DB**:资源 jar 内置;凭证 Redis | §8 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:覆盖 `basicdb` schema 全 34 张表(沿用 master)/ 行为验证码相关改动(Redis 凭证 + 总开关 KV + 失败日志) / 索引 / DDL 约定 / 备份 |
