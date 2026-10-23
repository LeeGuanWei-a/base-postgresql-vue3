# 基础平台(base-postgresql-vue3) 数据库设计 — `springboot3-tianai-captcha` 分支

> 文档版本:v0.1 · 状态:初稿
> 适用范围:仓库 **`springboot3-tianai-captcha`** 分支
> 前置依赖:《需求文档》v0.1、《概要设计》v0.1
> 本文档是数据落点:schema / 表 / 索引 / 字段注释 / 迁移 / 备份。所有字段以《需求文档》为准,不在此文档内自创新字段。
> **本分支与 `master` / `springboot3` 数据库完全一致**:`basicdb` schema / **34 张表 + 5 函数**;本分支**不新增、不删除、不修改任何表 / 字段 / 索引 / 约束 / 函数**。
> **验证码改造不产生任何数据库变更** —— 验证码实例在 tianai 内存缓存,校验凭证在 Redis。

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

> **为什么零变更**:验证码改造的范围是 ① kaptcha → tianai-captcha(应用层)② 验证码实例(内存)③ 校验凭证(Redis)。**三处都与数据库无关**。
>
> 对比:若采用「验证码答案落库」或「验证码审计落表」的设计(本分支**未采用**),才需要新增表/字段。

### 1.6 🔶 验证码相关的非数据库存储(必须知道)

**验证码完全不落库**,分布在两处运行期存储:

| 存储 | Key / 位置 | 内容 | TTL | 生命周期 |
|---|---|---|---|---|
| **tianai 内存缓存** | 内部 `captchaId` | 验证码实例(背景图/模板图/答案/轨迹 baseline) | tianai 内部 TTL | `generate()` 写入 → `matching()` 消费销毁 / 超时过期 |
| **Redis** | `captcha:verified:<id>` | `"1"`(已校验标记) | **120s** | `/captcha/check` 成功写入 → `/login` 命中后 **DEL** / 超时过期 |

**结论**:

| 影响 | 说明 |
|---|---|
| 应用重启 | **内存实例全部丢失**(用户需重新生成;无业务影响) |
| Redis 重启 | 凭证丢失 → `/login` 走 `matching()` 兜底(但内存实例若也丢了则失败,需重新生成) |
| 数据库重启 | **完全无影响**(验证码与 DB 无关) |
| 备份 | **验证码无需备份** |
| 多实例部署 | ⚠️ **内存实例不共享** → 多实例需会话粘滞(sticky session)或改用 Redis 版 `ResourceStore`/缓存(当前未做) |

> ⚠️ **多实例部署注意事项**:当前 `LocalMemoryResourceStore` + tianai 默认内存缓存意味着「实例 A 生成、实例 B 校验」会失败。若要多实例,需:
> 1. Nginx `ip_hash` / sticky cookie 保证同会话打到同实例,或
> 2. 换 tianai 的分布式缓存实现(1.5.x 支持自定义 CaptchaCacheStore)
>
> 这是**部署层约束**,不是数据库变更。

### 1.7 🔶 与 `springboot3` 的数据库差异 — **无**

| 对比项 | `springboot3` | 本分支 | 差异 |
|---|---|---|---|
| schema | `basicdb` | `basicdb` | 无 |
| 表数量 | 34 | 34 | 无 |
| 函数数量 | 5 | 5 | 无 |
| `sys_config` 数据 | 含 `sys.account.captchaEnabled` | 同 | 无(仍复用该 KV) |
| `sys_logininfor.msg` | 验证码失败原因 | 同(**文本内容不同**) | 仅**数据内容**差异,无结构差异 |
| SQL 脚本 | `basicdb-vue3.sql` | 同(逐字节一致) | 无 |

---

## 2. 通用(系统级)表 — `sys_*`(21 张)

> 全部带 `create_by` / `create_time` / `update_by` / `update_time` / `remark` / `del_flag`。
> **所有字段定义与 `springboot3` 完全一致**;本节只对**验证码相关**的表做重点说明,其余以索引方式引用。

### 2.1 `sys_config` 全局参数(🔶 验证码总开关所在)

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| config_id | int | PK | 自增 |
| config_name | varchar(100) | Y | — |
| **config_key** | varchar(100) | Y | 唯一 |
| **config_value** | varchar(500) | Y | — |
| config_type | char(1) | Y | `N=普通 Y=系统` |
| is_builtin | char(1) | N | `0=否 1=是` |
| 通用字段 | — | — | — |

索引:`uk_config_key(config_key)`

#### 🔶 验证码相关 KV(**结构不变,复用 `master` 的 KV 机制**)

| config_key | 默认值 | 说明 | 本分支是否改 |
|---|---|---|---|
| **`sys.account.captchaEnabled`** | `true` | **行为验证码总开关**;`true` → 走 `validateTianaiCaptcha`;`false` → 跳过(测试用) | **否**(沿用同一 key,语义一致) |
| `sys.account.registerUser` | `false` | 是否开放注册 | 否 |
| `user.password.lockOpen` | `false` | 账号级密码错误锁定(与锁屏无关) | 否 |
| `user.password.maxRetryCount` | `5` | 密码最大重试次数 | 否 |
| `user.password.lockTime` | `10`(分钟) | 账号锁定时长 | 否 |

> **关键**:验证码从 kaptcha 换成 tianai **不需要改任何 `sys_config` 数据** —— 总开关键名与语义完全一致。这意味着切换分支时数据库无需做数据迁移。

### 2.2 `sys_logininfor` 登录日志(🔶 验证码失败落点)

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| info_id | bigint | PK | 雪花 |
| user_name | varchar(50) | Y | 登录名 |
| ipaddr | varchar(128) | N | IP |
| login_location | varchar(255) | N | 归属地 |
| browser | varchar(50) | N | 浏览器(⬆️ yauaa 解析,见 §7.3) |
| os | varchar(50) | N | 操作系统(⬆️ 同上) |
| status | char(1) | Y | `0=成功 1=失败` |
| **msg** | varchar(255) | N | **失败原因**(🔶 本分支验证码失败写入下列 4 类) |
| login_time | datetime | Y | — |

索引:`idx_user_name(user_name)` / `idx_login_time(login_time)`

#### 🔶 验证码失败写入的 `msg` 值(实测自代码)

| msg 内容 | 触发点 |
|---|---|
| `行为验证码 id 缺失` | `validateTianaiCaptcha` — `captchaId` 为空 |
| `行为验证码轨迹缺失` | `validateTianaiCaptcha` — `captchaData` 为空 |
| `行为验证码轨迹解析失败` | `jsonToTrack` 抛异常 |
| tianai 返回的 `msg`,或 `user.jcaptcha.error` 文案 | `matching()` 失败 |

> **注意**:
> - `msg` 字段长度 255,上述文案均远小于限制,无截断风险。
> - **历史数据是 kaptcha 时代的**(如「验证码错误」),与本分支新数据**混在同一列**,无法从 `msg` 区分验证码类型(R30)。若需区分,可在新文案前加前缀(会影响前端映射,故未做)。

#### 🔶 验证码失败专项查询

```sql
-- 按时间范围查验证码失败
SELECT user_name, ipaddr, msg, login_time
FROM sys_logininfor
WHERE status = '1'
  AND msg LIKE '%验证码%'
  AND login_time BETWEEN :start AND :end
ORDER BY login_time DESC;

-- 按 IP 统计验证码失败次数(用于识别撞库,可作为 M5 限流规则依据)
SELECT ipaddr, count(*) AS fail_cnt
FROM sys_logininfor
WHERE status = '1' AND msg LIKE '%验证码%'
  AND login_time > now() - interval '1 hour'
GROUP BY ipaddr
HAVING count(*) > 20
ORDER BY fail_cnt DESC;
```

> 第二个查询可直接支撑「同 IP 失败 N 次切难类型」的 M5 需求(不需要新表)。

### 2.3 `sys_user` 用户(与验证码无直接关系)

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
| 通用字段 | — | — | — |
| del_flag | char(1) | Y | `0=未删 2=已删` |

索引:`uk_user_name(user_name)` / `idx_dept_id(dept_id)` / `idx_status(status)`

> 🔶 **行为验证码失败不会直接停用用户**:仅抛 `CaptchaException` + 落 `sys_logininfor`。只有**密码错误 N 次**才触发 `user.password.*` 锁定。

### 2.4 其余 18 张 `sys_*` 表(⬆️ 与 `springboot3` 一致,本分支未碰)

| 表 | 字段要点 | 本分支改动 |
|---|---|---|
| `sys_role` | `role_key` 唯一;`data_scope` 1~5 | 无 |
| `sys_menu` | `menu_type` M/C/F;`perms`;扩展列 `menu_name_en`/`menu_name_zh_hant`/`is_resource`/`show_tags` | 无(**菜单数据未改**) |
| `sys_dept` | 自引用树;`ancestors` | 无 |
| `sys_post` | `post_code` 唯一 | 无 |
| `sys_user_role` / `sys_role_menu` / `sys_role_dept` / `sys_user_post` | 4 张复合主键关联表 | 无 |
| `sys_dict_type` / `sys_dict_data` | `dict_type` 唯一 / 类型内 `dict_value` 唯一 | 无 |
| `sys_notice` / `sys_notice_read` | 通知 + 已读(`uk_user_notice` 唯一) | 无 |
| `sys_oper_log` | 操作日志(验证码失败**不入**此表) | 无 |
| `sys_demo` | `business-core` 示例 | 无 |
| `sys_files` | 文件元数据 | 无 |
| `sys_i18n` | `uk_lang_key(lang, key)` 唯一 | 无 |
| `sys_job` / `sys_job_log` | 定时任务 + 日志 | 无 |

> 详细字段见 `docs/springboot3-数据库设计.md` §2。

### 2.5 ⚠️ 关于 `sys_sms_message`(再次确认不存在)

> **本分支 `file-sql/basicdb-vue3.sql` 中不存在 `sys_sms_message` 表**(实测 `Select-String "sys_sms_message"` 命中 **0**)。
>
> `master` 分支早期《数据库设计》v0.1 曾列出该表,属**误记**;`springboot3` 与 `springboot2_TypeScript` 文档已纠正,本分支沿用纠正结论。
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
| is_pk / is_increment / is_required / is_insert / is_edit / is_list / is_query | char(1) | Y | 各开关 |
| query_type | varchar(200) | Y | `EQ` / `LIKE` 等 |
| html_type | varchar(200) | Y | `input` / `select` 等 |
| dict_type | varchar(200) | N | 关联字典 |
| sort | int | Y | 排序 |

索引:`idx_table_id(table_id)`

> ⚠️ **⬆️ 继承缺陷(R20)**:代码生成器模板仍产 **SB 2 风格 Java 代码**(`javax.*` + springfox 注解),生成后需手工改 jakarta + `io.swagger.v3`。与本分支验证码改造无关,但属同一基线的遗留问题。

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

> 🔶 本分支**未改** `qrtz_*`;Quartz 版本由 SB 3 BOM 管理(⬆️ 继承)。
> **建议**:升级/切换分支后首次启动前,确认 `qrtz_*` 数据能正常反序列化(`JobDataMap` 存 `SysJob` 对象;**本分支 `SysJob` 未改字段**,无风险)。

---

## 5. 函数(5 个,由 SQL 脚本创建)

| 函数 | 签名 | 用途 |
|---|---|---|
| `date_format` | `(indate anyelement, informate text) RETURNS text` | 兼容 MySQL 日期格式化 |
| `exec` | `(sqlstring character varying) RETURNS character varying` | 动态执行 SQL(代码生成器查询元数据) |
| `find_in_set` | `(bigint, character varying) RETURNS boolean` | 兼容 MySQL 逗号串查找 |
| `ifnull` | `(param1 text, param2 text) RETURNS text` | 兼容 MySQL NULL 兜底 |
| `tabledef` | `(oid) RETURNS text` | 取表 DDL 定义 |

> 🔶 本分支**未改**(实测 `CREATE FUNCTION` 计数 = 5);与 Java 版本无关(DB 侧 PL/pgSQL)。

---

## 6. 业务示例与扩展点

### 6.1 业务示例(`sys_demo`)

| 字段 | 类型 | 必填 | 注释 |
|---|---|---|---|
| id | bigint | PK | 雪花 |
| name | varchar(50) | Y | — |
| code | varchar(50) | Y | — |
| description | varchar(255) | N | — |
| 通用字段 | — | — | — |

### 6.2 扩展点

- **新增业务模块**:在 `business-core` 下新增 `domain / mapper / service / controller`,对应 `sys_xxx` 表,**不要修改 `basic-admin-core`**
- **新增扩展能力**:在 `extend-*` 模式新增子模块,挂到 `pom.xml` 的 `dependencyManagement` + `modules`
- **代码生成产物**:落 `gen_table`,不落物理表
- **🔶 扩充验证码背景图**:把图片放入 `basic-framework/src/main/resources/META-INF/cut-image/resource/`,**无需任何 DDL**(启动自动扫描)
- **🔶 新增验证码类型**:需改 `TianaiCaptchaConfig`(资源) + 前端组件,**不需要 DDL**
- **🔶 若要做验证码审计落库**(当前**未做**):需新建表(如 `sys_captcha_log`),这会**打破本分支「零 DB 变更」特性**,需单独评审

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
| `select * from sys_config where config_key=?` | `uk_config_key` | **验证码总开关** |
| `select * from sys_oper_log where business_type=? and oper_time between ? and ?` | `idx_business_type` / `idx_oper_time` | 操作日志 |
| `select * from sys_logininfor where status=? and login_time between ? and ?` | `idx_login_time` | **验证码失败查询** |
| `select * from sys_notice_read where user_id=? and notice_id=?` | `uk_user_notice` | 唯一命中 |
| `select * from qrtz_triggers where TRIGGER_GROUP=?` | Quartz 自带 | 调度 |
| `select * from gen_table where table_name=?` | 物理表主键 | 反查 |

### 7.2 🔶 验证码相关查询(无新索引需求)

| 查询 | 说明 | 索引 |
|---|---|---|
| 读总开关 | `select config_value from sys_config where config_key='sys.account.captchaEnabled'` | `uk_config_key` |
| 验证码失败审计 | `select ... from sys_logininfor where msg like '%验证码%' and login_time ...` | `idx_login_time`(**足够**;msg 模糊匹配无法用索引,但登录日志查询本就按时间范围) |
| 按 IP 统计失败 | 同上 + `group by ipaddr` | `idx_login_time` |

> **结论**:**不需要为验证码新增任何索引**。验证码的实例与凭证都在内存/Redis,DB 只承担「总开关读取」与「失败日志写入」两件轻量事。

### 7.3 🔶 `sys_logininfor.browser` / `os` 的 yauaa 影响(⬆️ 继承自 `springboot3`)

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

> 与本分支验证码改造无直接关系,但因同在 `sys_logininfor` 写入路径上,一并说明。

---

## 8. DDL 约定

| 项 | 约定 |
|---|---|
| 主键 | MyBatis-Plus 雪花(`ASSIGN_ID`),`bigint`,**不要**用 `bigserial` |
| 通用字段 | `create_by` varchar(64) / `create_time` datetime / `update_by` varchar(64) / `update_time` datetime / `remark` varchar(500) / `del_flag` char(1) default '0' |
| 逻辑删除 | `del_flag=2` 视为已删(MP `logic-delete-value=2` / `logic-not-delete-value=0`) |
| 必填 | SQL 层 `NOT NULL`,应用层 `jakarta.validation` 的 `@NotBlank` / `@NotNull`(⬆️ SB3) |
| 字符串 | varchar(N);枚举用 `char(1)` |
| 时间 | `datetime`(应用层 `yyyy-MM-dd HH:mm:ss`) |
| 金额 | 若新增,**必须** `numeric(p,s)`,**禁止** `float` / `double` |
| 字符集 | UTF8 |
| 时间戳 | 不用 `timestamp with time zone`,用 `datetime` |
| 归档 | 当前不做物理归档,只逻辑删除 |
| 迁移工具 | 暂时**不**用 Flyway / Liquibase,变更直接改 `basicdb-vue3.sql` 并附说明(见 §12) |
| UA 相关字段 | `browser` / `os` 由 yauaa 写入,`varchar(50)` 建议写入前截断(见 §7.3) |
| **🔶 验证码相关** | **本次改造不改 DDL**;验证码实例/凭证**不入库**;若将来要做验证码审计表,需单独评审 |
| **🔶 多实例部署** | 当前验证码实例在**进程内存**,多实例需 sticky session 或改分布式缓存(**非 DB 变更**) |

---

## 9. 外部引擎表

### 9.1 分支对照

| 引擎 / 能力 | 所在分支 | 本分支是否包含 |
|---|---|---|
| Flowable (`act_*` / `flw_*`) | `springboot2-extend-flowable` / `springboot3-extend-flowable`(`basicdb_flowable`,86 表 + 2 视图) | ❌ 不含 |
| tianai-captcha | **本分支 / `springboot2-tianai-captcha`** | ✅ **含,但零 DB 对象**(内存 + Redis) |

> 🔶 本分支**不预建 Flowable 表**;tianai-captcha **不产生任何表**。

### 9.2 运行期非 DB 存储

| 存储 | Key / 位置 | 内容 | TTL | 本分支 |
|---|---|---|---|---|
| Redis | `login_tokens:<uuid>` | 登录会话 | 30min | ⬆️ 继承 |
| Redis | `sys_dict::<dictType>` | 字典缓存 | — | ⬆️ 继承 |
| Redis | `REPEAT_SUBMIT:*` | 重复提交标记 | 500ms | ⬆️ 继承 |
| **Redis** | **`captcha:verified:<id>`** | 验证码已校验标记 | **120s** | **🔶 新增** |
| **进程内存** | tianai 内部 | 验证码实例 | tianai TTL | **🔶 新增** |
| **进程内存** | `LocalMemoryResourceStore` / `FontCache` | 6 张背景图 + 3 模板 + `SIMSUN.TTC` | 进程生命周期 | **🔶 新增** |
| 本地磁盘 | `D:/ruoyi/uploadPath` | 上传文件 | — | ⬆️ 继承 |

> ⚠️ **内存态的两处新增**(实例 + 资源)意味着:**单实例部署**是最简配置;多实例需额外方案(见 §1.6)。

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
- 🔶 **验证码无需备份**:实例在内存(重启即失效,无业务影响)、凭证在 Redis(TTL 120s,丢失只会让用户重过一次验证码)
- 🔶 **分支切换无需数据迁移**:`springboot3` ↔ 本分支共用同一份 SQL 与同一份 `sys_config` 数据

---

## 11. 表清单(全文汇总,共 34 张)

| 域 | 表 | 数量 | 大致行数 |
|---|---|---|---|
| 代码生成 | `gen_table` / `gen_table_column` | 2 | 小(<100) |
| Quartz | `qrtz_*`(11 张见 §4) | 11 | 取决于任务数 |
| 系统-鉴权 | `sys_user` / `sys_role` / `sys_menu` / `sys_user_role` / `sys_role_menu` / `sys_role_dept` / `sys_user_post` | 7 | 中 |
| 系统-组织 | `sys_dept` / `sys_post` | 2 | 中 |
| 系统-字典参数 | `sys_config`(**验证码总开关**)/ `sys_dict_type` / `sys_dict_data` | 3 | 小~中 |
| 系统-通知 | `sys_notice` / `sys_notice_read` | 2 | 小 / 大 |
| 系统-审计 | `sys_oper_log` / `sys_logininfor`(**验证码失败落点**) | 2 | 很大 / 大 |
| 系统-文件国际化 | `sys_files` / `sys_i18n` | 2 | 大 / 小 |
| 系统-定时任务 | `sys_job` / `sys_job_log` | 2 | 小 / 大 |
| 系统-示例 | `sys_demo` | 1 | 演示用 |
| **合计** | | **34** | |

**明细校验**:2(gen)+ 11(qrtz)+ 21(sys)= **34** ✅

> 🔶 `sys_user_online` 由 Redis 维护(`CacheConstants.LOGIN_TOKEN_KEY`),**不写库**,不计入 34。
> 🔶 `sys_sms_message` **不存在**(见 §2.5)。
> 🔶 **验证码相关表:0 张**(验证码不入库)。

---

## 12. 需求变更记录

| 变更 | 落点 |
|---|---|
| 数据库迁 PostgreSQL | §1 / §10 |
| `sys_notice_read` 表 | `springboot3` DBD §2.11 |
| `sys_menu` 扩展列 | `springboot3` DBD §2.3 |
| `sys_i18n` / `sys_files` / `sys_demo` | `springboot3` DBD §2 |
| ⬆️ Spring Boot 2 → 3 升级 | `springboot3` DBD(含 yauaa 对 `browser`/`os` 的影响) |
| **🔶 tianai-captcha 1.5.2 替换 kaptcha** | **§1.5(零变更 8 项声明)+ §1.6(非 DB 存储:内存 + Redis)+ §1.7(与 springboot3 零差异)+ §2.1(总开关 KV 复用)+ §2.2(验证码失败 msg 4 类 + 专项查询)+ §7.2(验证码无需新索引)+ §8(DDL 约定加验证码/多实例条目)+ §9.2(内存态两处新增)+ §11(验证码相关表 0 张)** |
| **🔶 纠正 `sys_sms_message` 误记** | §2.5 |
| **🔶 多实例部署约束(内存实例不共享)** | §1.6 / §8 |

---

## 版本历史

| 版本 | 日期 | 摘要 |
|---|---|---|
| v0.1 | 2026-10-23 | 初稿:以 `springboot3` 数据库设计(v0.1)为基线,标注本分支**数据库零变更**(§1.5,8 项清单)+ 新增「§1.6 验证码非 DB 存储(内存实例/资源 + Redis 凭证,含多实例约束)」「§1.7 与 springboot3 零 DB 差异」「§2.1 总开关 KV 复用」「§2.2 验证码失败 4 类 msg + 2 条专项 SQL」「§7.2 验证码无需新索引」「§9.2 内存态新增」「§11 验证码相关表 0 张」;实测核对 34 表 / 5 函数,确认 `sys_sms_message` 不存在(§2.5) |
