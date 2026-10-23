# Basic Admin (Spring Boot 3.5.16 + Vue 3 + Element Plus)

基于若依 RuoYi-Vue 3.8.2 改造,与 `master` 分支的差异有两点:**① 后端从 Spring Boot 2.5.14 升级到 Spring Boot 3.5.16(JDK 11 → 17)**;**② 把 kaptcha 字符验证码整体替换为 tianai-captcha 行为验证码**。前端两端(`web-ui` / `web-mp`)保持 **JavaScript 版**。

> 当前分支:`springboot3-tianai-captcha` ——「**master + 后端 SB 3.5.16 升级 + tianai-captcha 行为验证码**」。
> 仓库另有 `master` / `springboot2-extend-flowable` / `springboot2-tianai-captcha` / `springboot2_TypeScript` / `springboot3` / `springboot3_TypeScript` / `springboot4` / `springboot4_TypeScript` 等分支,本分支**只描述 springboot3-tianai-captcha**。
> 行为验证码支持 **SLIDER(滑动)/ ROTATE(旋转)/ WORD_IMAGE_CLICK(文字点选)** 三种类型,PC 与移动端可切换;kaptcha 已从源码中删除(仅 javadoc 里保留兼容说明)。

---

## 前端说明

本分支前端**两端都是 JavaScript 版**(与 `master` 一致,区别于 `springboot3_TypeScript`):

### 1.1 PC 管理端 `web-ui/`

- 框架:Vue 3.5.26 + Element Plus 2.13.1
- 构建:Vite 6.4.1
- 代码:纯 `.js` / `.vue`,**无 `typescript` 依赖**(package.json 不含 `typescript`)

> TS 版在 `springboot2_TypeScript` / `springboot3_TypeScript` / `springboot4_TypeScript` 分支,本分支**未涉及**。

### 1.2 移动端 `web-mp/`(本分支新增,与 master 一致)

- 框架:**uni-app 3.0 (alpha)** + Vue 3.4.21(组合式 API `<script setup>`)
- UI:**uview-plus** 3.3.62(uView 的 Vue3 版本,组件名带 `up-` 前缀)
- 构建:Vite 5.2.8
- 一份代码同时跑 **H5 / 微信小程序 / Android / iOS / 其它小程序**
- 状态管理:Pinia 2.0.36
- 加密:jsencrypt(对接后端 RSA 登录)
- 语言:**JavaScript**(无 TypeScript)

> 详细说明见下文**「七、移动端 `web-mp`」** 章节。

---

## 一、当前后端状态(基于 `git show HEAD:pom.xml`)

> 本分支有**两处**与 `master` 不同:**① 后端升级到 SB 3.5.16 / JDK 17**;**② kaptcha 字符验证码 → tianai-captcha 1.5.2 行为验证码**。前端两端、SQL 脚本(34 表)、业务逻辑与 master 保持一致。

| 组件 | 版本 | 备注 |
|---|---|---|
| **JDK** | **17** | ★ 由 11 升到 17 |
| Spring Boot | **3.5.16** | ★ 由 2.5.14 升到 3.5.16 |
| Spring Framework | **6.1.x** | ★ 由 5.3.x 升到 6.1.x |
| Spring Security | **6.x** | ★ 由 5.5.x 升到 6.x |
| MyBatis-Plus | **3.5.7** | + `mybatis-plus-spring-boot3-starter` |
| MyBatis-Spring-Boot-Starter | **3.0.5** | ★ 新启用(原版注释) |
| Druid | **1.2.28** | starter 改名为 `druid-spring-boot-3-starter` |
| PageHelper | **2.1.1** | |
| Springdoc-openapi | **2.9.0** | ★ 替代 springfox 3.0.0(后者已不维护) |
| Hutool | **hutool-core 5.8.27** | 由 boms 模式改为单 artifact |
| yauaa(User-Agent 解析) | **8.2.0** | ★ 替代 bitwalker 1.21 |
| Lombok | **1.18.46** | 新增 |
| **行为验证码** | **tianai-captcha 1.5.2** | ★ `tianai-captcha-springboot-starter`,Spring Boot 3 / **jakarta.servlet** 兼容;前端自实现组件(SDK 1.5.6);**取代 kaptcha 字符验证码** |
| ~~kaptcha~~ | ~~2.3.3~~ | **已下线**(源码中无引用,`pom.xml` 保留属性仅为回滚方便) |
| poi | **5.5.1** | |
| commons-io | **2.22.0** | |
| oshi | **7.4.4** | |
| swagger | **移除** | springdoc 替代 |
| jakarta.servlet | **6.x** | ★ javax.servlet → jakarta.servlet(Java EE → Jakarta EE) |
| jaxb-api | **2.3.1** | ★ JDK 17 默认移除 XML 绑定,显式引入 |
| fastjson | **移除** | |

---

## 二、本分支是什么

`springboot3` 分支以 **master 分支**为基线,只做了一件事:

> **把后端从 Spring Boot 2.5.14 一次性升级到 Spring Boot 3.5.16**,
> 前端两端(`web-ui` / `web-mp`)、业务功能、数据库脚本**完全不动**。

具体而言:

1. **JDK 11 → 17**
2. **Spring Boot 2.5.14 → 3.5.16**(`spring-boot.version` 重命名)
3. **Spring Framework 5.3.x → 6.1.x**
4. **Spring Security 5.5.x → 6.x**
5. **Java EE → Jakarta EE**(`javax.*` → `jakarta.*`,~50 个文件改 import)
6. **springfox 3.0.0 → springdoc-openapi 2.9.0**(`SwaggerConfig` 完全重写,
   注解从 `io.swagger` 改到 `io.swagger.v3.oas.annotations`)
7. **Spring Security 5 → 6**:`WebSecurityConfigurerAdapter` 废弃,改用 `SecurityFilterChain` Bean;
   `authorizeRequests()` → `authorizeHttpRequests()`;`antMatchers()` → `requestMatchers()`
8. **Druid / MyBatis-Plus starter 改名**:`druid-spring-boot-starter` → `druid-spring-boot-3-starter`;
   `mybatis-plus-boot-starter` → `mybatis-plus-spring-boot3-starter`
9. **大量第三方库升级**:Hutool boms → hutool-core、bitwalker → yauaa、oshi 7.x、poi 5.x、commons-io 2.22 等
10. **Lombok 1.18.46**(新增,显式声明版本)
11. **jaxb-api 2.3.1**(新增,JDK 17 默认不带 XML 绑定)
12. **Redis 配置改名**:`spring.redis.*` → `spring.data.redis.*`(SB 3 警告,SB 4 强制)
13. **web-mp 移动端** 与 master 一致(JS 版)

`springboot3` 与 `master` 的**唯一差异**就是上面 1–12 的后端升级;前端代码、
SQL 脚本、业务功能、目录结构**完全相同**。

---

## 三、相对 master 的具体改动(后端)

### 3.1 父 `pom.xml`

```xml
<!-- properties -->
<java.version>11 → 17</java.version>
<springboot.version → spring-boot.version> 2.5.14 → 3.5.16</spring-boot.version>
<mybatis-plus-boot-starter.version> 3.5.1 → 3.5.7</mybatis-plus-boot-starter.version>
<mybatis-spring-boot.version> (注释) → 3.0.5</mybatis-spring-boot.version>
<druid.version> 1.2.8 → 1.2.28</druid.version>
<pagehelper.boot.version> 1.4.1 → 2.1.1</pagehelper.boot.version>
<oshi.version> 6.1.6 → 7.4.4</oshi.version>
<kaptcha.version> 2.3.2 → 2.3.3</kaptcha.version>
<poi.version> 4.1.2 → 5.5.1</poi.version>
<commons.io.version> 2.11.0 → 2.22.0</commons.io.version>

<!-- 新增 -->
<lombok.version>1.18.46</lombok.version>
<hutool-core.version>5.8.27</hutool-core.version>
<yauaa.version>8.2.0</yauaa.version>
<jaxb-api.version>2.3.1</jaxb-api.version>
<springdoc.version>2.9.0</springdoc.version>
<commons-fileupload.version>1.5</commons-fileupload.version>

<!-- 移除 -->
<swagger.version>3.0.0</swagger.version>     <!-- springfox 移除 -->
<bitwalker.version>1.21</bitwalker.version>  <!-- yauaa 替代 -->
<fastjson.version>2.0.4</fastjson.version>   <!-- fastjson 移除 -->
<hutool.version>5.8.3</hutool.version>       <!-- 改 hutool-core -->

<!-- dependencyManagement -->
- springfox-boot-starter, swagger-models      ← 移除
+ springdoc-openapi-starter-webmvc-ui        ← 新增
+ hutool-core                                ← 新增(单 artifact 模式)
+ jaxb-api                                  ← 新增
+ yauaa                                     ← 新增
+ mybatis-spring-boot-starter               ← 新增(启用,原来注释掉)
- mybatis-plus-boot-starter                 ← 移除
+ mybatis-plus-spring-boot3-starter         ← 替换
- druid-spring-boot-starter
+ druid-spring-boot-3-starter
```

### 3.2 7 个子模块 `pom.xml`

- 7 个子模块的 `parent.version` 从 `3.8.2` → **`3.9.2`**
- `basic-common/pom.xml`:
  - `spring-boot-starter-web` → `spring-boot-starter-webmvc`(若依 3.9.2 原版就这样)
  - hutool 从 `cn.hutool:hutool-bom:import` 改 `cn.hutool:hutool-core`
  - 加 `javax.xml.bind:jaxb-api`
- `basic-framework/pom.xml`:
  - `spring-boot-starter-aop` 保留(若依 3.9.2 原版就这样)
  - 删 `javax.servlet:servlet-api`(用 jakarta 替代)
  - `druid-spring-boot-3-starter`
- `extend-quartz/pom.xml`:`org.quartz-scheduler:quartz` 单独依赖
- `extend-generator/pom.xml`:`druid-spring-boot-3-starter`

### 3.3 配置文件 `application.yml` / `application-dev.yml` / `application-prod.yml`

- `swagger:` 节点删除(springfox 没了)
- `springdoc:` 节点新增
- `spring.redis.host/port/database/password` 改 `spring.data.redis.*`(SB 3 警告,SB 4 强制)
- `datasource.druid.statViewServlet.url-pattern: /druid/*` 显式声明
- `xxl.job` 节点删除(若依原版里有,本项目不用,本次移除)

### 3.4 代码侧主要改动

| 文件 | 主要改动 |
|---|---|
| `SwaggerConfig.java` | 完全重写,springfox → springdoc-openapi v3 |
| `SecurityConfig.java` | `WebSecurityConfigurerAdapter` → `SecurityFilterChain` Bean;`authorizeRequests()` → `authorizeHttpRequests()`;`antMatchers()` → `requestMatchers()` |
| `UserAgentConfig.java` | **新增**,yauaa `UserAgentAnalyzer` 单例 Bean |
| `ResourcesConfig.java` | `WebMvcConfigurerAdapter` → `WebMvcConfigurer` |
| `DruidConfig.java` | 包名 `com.alibaba.druid.spring.boot.autoconfigure` → `com.alibaba.druid.spring.boot3.autoconfigure` |
| `Xss.java` / `XssValidator.java` | `javax.servlet.*` → `jakarta.servlet.*` |
| `XssFilter.java` / `RepeatableFilter.java` / `RepeatedlyRequestWrapper.java` / `XssHttpServletRequestWrapper.java` | 同上 |
| `JwtAuthenticationTokenFilter.java` | 同上 |
| `AuthenticationEntryPointImpl.java` | 同上 |
| `LogoutSuccessHandlerImpl.java` | 同上 |
| `AsyncFactory.java` | 同上 |
| `PermitAllUrlProperties.java` | `antPathMatcher` 注入,`RequestMappingInfo` 处理逻辑微调 |
| `SysDept.java` / `SysDictType.java` / `SysDictData.java` / `SysMenu.java` / `SysRole.java` / `SysUser.java` | 主要是 `javax.validation.*` → `jakarta.validation.*` |
| 多个 `*Controller.java`(`SysConfig` / `SysDictData` / `SysDictType` / `SysPost` / `SysRole` / `SysUser` / `SysOperlog` / `SysLogininfor` / `GenController` / `SysJob` / `SysNotice` 等) | 主要是 swagger 注解从 `io.swagger` 改到 `io.swagger.v3.oas.annotations` |
| `SysLoginService.java` / `TokenService.java` | jakarta import + yauaa 替代 bitwalker 后的 API 调用 |
| `SysConfigServiceImpl.java` / `SysDictTypeServiceImpl.java` / `SysFilesServiceImpl.java` / `SysUserServiceImpl.java` | `@Cacheable` / `@CacheEvict` 等缓存注解微调 |
| `HttpHelper.java` / `IpUtils.java` / `ServletUtils.java` / `FileUtils.java` / `BeanValidators.java` / `ExcelUtil.java` | jakarta / commons-* 升级后的 API 微调 |
| `CaptchaController.java` / `CommonController.java` | jakarta + swagger 注解换 |
| `SysConfigMapper.xml` / `SysUserMapper.xml` / `SysRoleMapper.xml` / `SysDeptMapper.xml` / `SysDeptMapper.java` / `SysMenuMapper.java` | SQL 微调(锁屏解锁关联) |
| `GenTable.java` / `GenTableColumn.java` / `SysNotice.java` / `SysPost.java` | jakarta validation |
| `MybatisConfig.java` / `FilterConfig.java` / `ServerConfig.java` / `SameUrlDataInterceptor.java` / `RepeatSubmitInterceptor.java` / `ShutdownManager.java` / `LogAspect.java` / `GlobalExceptionHandler.java` | jakarta import + 业务无关小改 |
| `TestController.java` / `SwaggerController.java` | swagger 注解换 `io.swagger.v3` |
| `SysLoginController.java` | 锁屏解锁相关接口(与 master 同源) |

### 3.5 新增文件

- `UserAgentConfig.java` —— yauaa `UserAgentAnalyzer` 单例

### 3.6 删除文件

- `KaptchaTextCreator.java` —— 字符验证码文本生成器(kaptcha 下线)
- `CaptchaConfig.java` —— kaptcha `Producer`/`ServletRegistrationBean` 配置(kaptcha 下线)
- (SB 3 升级本身**没删任何**配置文件,只是改了内容)

### 3.7 行为验证码改造(tianai-captcha 1.5.2)

> 相对 `springboot3` 分支的增量改动。**SB 3 / jakarta.servlet 兼容版本**。

**后端(5 个文件)**

| 文件 | 改动 |
|---|---|
| `basic-framework/.../config/TianaiCaptchaConfig.java` | **新增**:`@Order(0)` `@Primary ResourceStore`;加载模板 + 6 张背景图 + **字体(1.5.2 通过 `type="font"` 资源注入,`FontCache` 监听)** |
| `basic-framework/.../config/TypeAwareImageCaptchaValidator.java` | **新增**:ROTATE 容差放宽到 **0.10(±36°)**,其余类型保持默认 0.02 |
| `basic-admin-api/.../common/CaptchaController.java` | **重写**:`GET /captchaImage?type=...`;返回 `{captchaOnOff, uuid, type, data}`;`CONCAT` 自动回退 `SLIDER`;扁平化 `viewData`,绝不下发 `expand` |
| `basic-admin-api/.../common/CaptchaCheckController.java` | **新增**:`POST /captcha/check` 旁路校验;`matching()` 通过后写 Redis 凭证 `captcha:verified:<id>`(120s) |
| `basic-common/.../model/LoginBody.java` | **新增字段** `captchaData`(ImageCaptchaTrack JSON);`uuid` 语义改为「验证码 id」 |

> `SysLoginService` 改造:`login(...)` 增加 `captchaData` 参数;新增 `validateTianaiCaptcha(...)`,**优先读 Redis 凭证,未命中走 `matching()`**,Redis 异常降级到 `matching()`。

**前端(2 端 + 2 个 api)**

| 文件 | 改动 |
|---|---|
| `web-ui/src/components/TianaiCaptcha/index.vue` | **新增**(27.5 KB):自实现 3 类型组件,**不依赖官方 web-sdk**;轨迹按 tianai 反机器人规则构造(首条补原点 / y 微抖动 / 末段减速);坐标用百分比定位 |
| `web-mp/src/components/tianai-captcha/tianai-captcha.vue` | **新增**:同上核心,适配 uni-app + uview-plus |
| `web-ui/src/api/login.js` / `web-mp/src/api/login.js` | **修改**:`/login` 携带 `captchaData` |
| `web-ui/src/views/login.vue` / `web-mp/src/pages/login/index.vue` | **修改**:接入组件,支持类型切换 |

**资源(6 张背景图 + 3 套模板)**

```
basic-framework/src/main/resources/META-INF/cut-image/
├── resource/   1.jpg  pic1.jpg  pic2.jpg  pic3.jpg  pic4.jpg  pic5.jpg  推荐格式.txt
└── template/   slider_1/{active,fixed}.png  slider_2/{active,fixed}.png  rotate_1/{active,fixed}.png
```

**运行时凭证(Redis,不落库)**

| Key | TTL | 说明 |
|---|---|---|
| `captcha:verified:<id>` | 120s | `/captcha/check` 校验通过后写入;`/login` 命中则跳过 `matching()` |

**关键点**

- `matching()` **会消费** tianai 内部缓存:同一 `id` 只能匹配 1 次
- `pom.xml` 里 `kaptcha.version` / `kaptcha` 依赖**保留但不引用**(便于回滚);源码中 kaptcha 仅存在于 2 处 javadoc 说明,`grep -r "kaptcha" basic-*/src` 应为 0 命中
- 数据库**零变更**(`sys_config.sys.account.captchaEnabled` 仍是总开关 KV)

---

## 四、与 master 共享的部分

下面这些**与 master 完全一致**,本分支**不重复**:

| 维度 | 内容 |
|---|---|
| **前端 web-ui** | 与 master 一致(JS 版,Vue 3.5.26 + Element Plus 2.13.1 + Vite 6.4.1) |
| **前端 web-mp** | 与 master 一致(uni-app 3 + uview-plus 3 + Vite 5.2.8,JS 版) |
| **业务功能** | 锁屏解锁、SQL bug 修复等与 master 共同的改动全部继承 |
| **数据库脚本** | `file-sql/basicdb-vue3.sql`(34 张表 + 5 函数,`basicdb` schema) |
| **目录结构** | 7 个后端 maven module + `web-ui/` + `web-mp/` |
| **`application.yml` 数据源** | PostgreSQL `jdbc:postgresql://localhost:5432/basicdb?currentSchema=basicdb` |

---

## 五、运行前 checklist

- [x] **JDK 17 安装**(★ 与 master 不同)
- [x] Node.js + pnpm(前端)
- [x] `cd web-ui && pnpm install` 拉依赖(PC 端)
- [x] `cd web-mp && pnpm install` 拉依赖(移动端)
- [ ] `pnpm dev` 验证 PC 端启动(`web-ui`)
- [ ] `pnpm dev:h5` 验证移动端 H5 启动(`web-mp`)
- [x] 7 个子模块 `parent.version` 一致为 **`3.9.2`**(★ 与 master 不同)
- [x] IDE 工程的 Project SDK 设为 **JDK 17**,Language level **17**(★ 与 master 不同)
- [x] `mvn clean compile` 0 ERROR
- [ ] 应用启动成功
- [ ] admin 登录成功(PC / 移动端)

---

## 六、未做 / 后续可优化

> 这些事项是 SB 2 → SB 3 升级时**没做完**或**没做**的,留作下一阶段(也就是 `springboot4` 分支)处理。

- [ ] JDK 17 → 21(若依原版 SB 3.5.16 还是 17,本项目跟着用 17)
- [ ] Spring Boot 3.5.16 → 4.1.1(已经在 `springboot4` 分支做完了)
- [ ] Spring Security 6 → 7(`springboot4` 分支做了)
- [ ] Jackson 2 → Jackson 3(`springboot4` 分支做了)
- [ ] Spring Framework 6 → 7(`springboot4` 分支做了)
- [ ] 各种 deprecation 警告清理
- [ ] 前端 TypeScript 化(已经在 `springboot3_TypeScript` 分支做了)
- [ ] 集成 Flowable 流程引擎(已经在 `springboot2-extend-flowable` 分支做了)

---

## 七、其他分支(参考)

本仓库其他分支(切到对应分支看其 README):

| 分支 | 做什么 |
|---|---|
| `master` | RuoYi-Vue 3.8.2 **最小改造基线**(后端 SB 2.5.14 / JDK 11,前端 JS,加 web-mp,**kaptcha 字符验证码**) |
| `springboot2_TypeScript` | `master` + 前端两端 **全量 TypeScript 化**;后端保持 SB 2.5.14 |
| `springboot2-tianai-captcha` | `master` + **tianai-captcha 1.5.0** 行为验证码(后端仍 SB 2.5.14) |
| `springboot2-extend-flowable` | `master` + **Flowable 6.8.0 流程引擎**(新增 `extend-flowable` 子模块),schema 改 `basicdb_flowable`(86 表 + 2 视图) |
| `springboot3` | `master` + **后端 SB 2.5.14 → 3.5.16 / JDK 11 → 17**(Jakarta EE + Security 6 + springdoc);**仍是 kaptcha** |
| `springboot3-tianai-captcha` 🟢 | **当前分支**:`springboot3` + **tianai-captcha 1.5.2** 行为验证码(取代 kaptcha);前端 JS、DB 34 表均不动 |
| `springboot3_TypeScript` | `springboot3` + 前端 TS 化 |
| `springboot3-extend-flowable` | `springboot3` + `springboot2-extend-flowable` 的 Flowable 集成 |
| `springboot4` | 后端 SB 3.5.16 → **4.1.1**(JDK 17 → 21),Jackson 3 + Spring Security 7 + Spring Framework 7 + ... |
| `springboot4_TypeScript` | `springboot4` + 前端 TS 化(技术栈最完整) |
| `springboot4-extend-flowable` | `springboot4` + `springboot2-extend-flowable` 的 Flowable 集成 |

> 命名约定:`springboot{N}` = 后端 SB 大版本基线;`_TypeScript` = 该基线 + 前端 TS 化;
> `-tianai-captcha` = 该基线 + 行为验证码(替代 kaptcha);`-extend-flowable` = 该基线 + Flowable 流程引擎。
> 本分支 `springboot3-tianai-captcha` = `springboot3` + 行为验证码,**不引入** Flowable、**不做**前端 TS 化。

---

## 八、数据库脚本 (`file-sql/basicdb-vue3.sql`)

> 本分支运行所需的 **PostgreSQL 全库结构 + 初始化数据** 都在 `file-sql/basicdb-vue3.sql`。
> 其它 `file-sql/*.sql` 是历史中间产物,本分支**不直接使用**;真正要执行的就是这一个。

### 8.1 脚本基本信息

| 项 | 值 |
|---|---|
| 文件路径 | `file-sql/basicdb-vue3.sql` |
| 来源 | `pg_dump` 导出(导出自 PostgreSQL 15.1,导出工具 pg_dump 17.0) |
| 数据库角色 | 所有对象 `OWNER TO system`(导入前**必须**先有 `system` 角色) |
| Schema | `basicdb`(脚本里所有表/函数/序列都在该 schema 下) |
| 表数量 | **34 张**(`gen_*` 2 + `qrtz_*` 11 + `sys_*` 21) |
| 函数数量 | 5 个(`date_format` / `exec` / `find_in_set` / `ifnull` / `tabledef`) |
| 数据加载方式 | `COPY ... FROM stdin;`(原生 psql `\copy` 流式导入,**不**走 `INSERT`) |
| PostgreSQL 要求 | **>= 15.1** |

### 8.2 表清单(共 34 张,均位于 `basicdb` schema)

#### 代码生成(`gen_*`,2 张)

| 表 | 用途 |
|---|---|
| `gen_table` | 代码生成业务表配置 |
| `gen_table_column` | 代码生成字段配置 |

#### 定时任务(`qrtz_*`,11 张)

`qrtz_blob_triggers` / `qrtz_calendars` / `qrtz_cron_triggers` / `qrtz_fired_triggers` /
`qrtz_job_details` / `qrtz_locks` / `qrtz_paused_trigger_grps` / `qrtz_scheduler_state` /
`qrtz_simple_triggers` / `qrtz_simprop_triggers` / `qrtz_triggers`

#### 业务系统(`sys_*`,21 张)

| 表 | 用途 |
|---|---|
| `sys_config` | 全局参数 |
| `sys_demo` | 演示样例 |
| `sys_dept` | 部门 |
| `sys_dict_data` | 字典值 |
| `sys_dict_type` | 字典类型 |
| `sys_files` | 文件 |
| `sys_i18n` | 国际化文案 |
| `sys_job` | 定时任务 |
| `sys_job_log` | 定时任务日志 |
| `sys_logininfor` | 登录日志 |
| `sys_menu` | 菜单(含 `is_resource` / `show_tags` / `menu_name_en` / `menu_name_zh_hant` 等扩展列) |
| `sys_notice` | 通知公告 |
| `sys_oper_log` | 操作日志 |
| `sys_post` | 岗位 |
| `sys_role` | 角色 |
| `sys_role_dept` | 角色-部门关联 |
| `sys_role_menu` | 角色-菜单关联 |
| `sys_sms_message` | 短信发送记录 |
| `sys_user` | 用户 |
| `sys_user_post` | 用户-岗位关联 |
| `sys_user_role` | 用户-角色关联 |

### 8.3 恢复步骤(全新数据库)

```bash
# 1) 建 system 角色和 basicdb 库(SQL 脚本本身不建角色,必须先建好)
psql -U postgres -h localhost -c "CREATE ROLE system LOGIN PASSWORD 'system';"
psql -U postgres -h localhost -c "CREATE DATABASE basicdb OWNER system;"

# 2) 导入 schema + 数据
psql -U system -h localhost -d basicdb -f file-sql/basicdb-vue3.sql

# 3) 验证(应返回 34)
psql -U system -h localhost -d basicdb -c \
  "SELECT count(*) FROM information_schema.tables WHERE table_schema='basicdb';"
```

如果本地 PostgreSQL 实例没有 `system` 角色,直接执行第 2 步会报
`ERROR: role "system" does not exist`,补一步 1) 即可。

### 8.4 重新备份成同款脚本

```bash
pg_dump -U system -h localhost -d basicdb \
  --schema=basicdb --no-owner --no-privileges \
  --file=file-sql/basicdb-vue3.sql
```

- 加 `--no-owner --no-privileges` 后,导出的脚本**不再**带 `OWNER TO system`、可移植到任意环境。
- 不加这两个开关,导出结果与本仓库现存的 `basicdb-vue3.sql` 格式一致。
- 脚本里所有 `data` 段都用了 `COPY ... FROM stdin;`,导入端必须用 `psql`。

### 8.5 默认账号

| 用户名 | 密码(明文) | 备注 |
|---|---|---|
| `admin` | `admin123` | 超级管理员,登录后请立刻改密 |

要重置可执行同目录的 `reset-admin-password.sql`。

### 8.6 与应用连接配置

```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/basicdb?currentSchema=basicdb
    username: system
    password: system
```

> `currentSchema=basicdb` 必须加,否则 MyBatis 找不到 `sys_user` / `sys_menu` 等表
> (脚本里所有表都建在 `basicdb` schema 下,不是 `public`)。

### 8.7 其它 `file-sql/*.sql` 说明(本分支**不直接使用**,留作历史)

| 文件 | 角色 |
|---|---|
| `basic-sql-2023-03-02.sql` | 早期 MySQL 5.7 全库脚本,**非 PostgreSQL**,本分支不要混用 |
| `quartz.sql` | 单独的 Quartz 建表脚本,**已被 `basicdb-vue3.sql` 内置的 11 张 `qrtz_*` 表覆盖** |
| `ry_20210908.sql` | RuoYi 3.8.2 原始 MySQL 全库脚本,**非 PostgreSQL**,对照用 |
| `reset-admin-password.sql` | 把 `admin` 密码重置回 `admin123` 的小工具,按需手动跑 |
| `reset-all-passwords.sql` | 把**所有**用户密码统一重置回默认的小工具,慎用 |
| `fix-sys-menu-component.sql` | 修 `sys_menu.component` 路径里 `system` 写错的,按需手动跑 |
| `create-sys-notice-read.sql` | 补建 `sys_notice_read`(用户已读记录)表,按需手动跑 |

> 一句话:**运行本项目,只跑 `basicdb-vue3.sql` 这一个文件就够了**。
> (注:`springboot2-extend-flowable` 分支已经把上述 7 个历史脚本里的部分文件删掉了,
> 本分支**仍保留**。)

---

## 九、移动端 `web-mp/`

> 本分支与 master 同样包含 `web-mp/` 移动端,**没有做 TS 化**(`vite.config.js`、`.js` 源文件),
> 与 `springboot3_TypeScript` 分支有别。本节是 `web-mp/README.md` 的摘要,
> 完整内容以子目录的 README 为准。

### 9.1 技术栈

| 项 | 版本 |
|---|---|
| uni-app | 3.0 (alpha,`@dcloudio/uni-app@3.0.0-4060620250520001`) |
| Vue | 3.4.21(**组合式 API** `<script setup>`) |
| Vite | 5.2.8 |
| UI | **uview-plus** 3.3.62(uView 的 Vue3 版本,组件名带 `up-` 前缀) |
| 状态管理 | Pinia 2.0.36 |
| 加密 | jsencrypt(对接后端 RSA 登录) |
| 时间 | dayjs 1.11.x |
| 国际化 | vue-i18n 9.13.1(已引入,词条待补) |
| 语言 | **JavaScript**(无 TypeScript) |
| 包管理 | pnpm |

### 9.2 与 web-ui / 后端的关系

- 与 `web-ui/`(PC 端)**同级目录**,**不**是 web-ui 的子项目,各自独立维护
- 不参与 maven 编译(`pom.xml` 的 module 列表里没有 `web-mp`)
- 对接后端:**直接打**本仓库的 `basic-admin-api`(Spring Boot 3.5 + Spring Security 6 + Jakarta EE),
  接口复用 `/login` / `/logout` / `/captchaImage` / `/getInfo` / `/getRouters` 等
- 默认账号:`admin / admin123`

### 9.3 目录结构

```
web-mp/
├── index.html                 # H5 入口(vite 用)
├── vite.config.js             # Vite 配置
├── package.json
├── config.js                  # 全局配置(双份,同步到 src/)
├── manifest.json              # 应用清单(双份,同步到 src/)
├── pages.json                 # 页面注册(双份,同步到 src/)
├── sync-config.cjs            # 双份同步脚本
├── scripts/
│   └── fixup-mp-components.cjs  # 微信小程序组件 fixup 脚本
└── src/
    ├── main.js                # 入口:createSSRApp + Pinia + 显式注册 up- 组件
    ├── App.vue                # 根组件(@import uview-plus 全局样式)
    ├── uni.scss               # 全局 SCSS 变量入口
    ├── pages/                 # 业务页面
    │   ├── login/index.vue
    │   ├── index/index.vue    # 首页(TabBar)
    │   ├── workbench/index.vue
    │   ├── user/index.vue
    │   └── common/{notice,profile}/...
    ├── api/                   # 接口层(login / system.* ...)
    ├── store/                 # Pinia(user / permission / app)
    ├── utils/                 # config / auth / request / dict / jsencrypt
    ├── directive/permission.js  # v-hasPermi / v-hasRole
    ├── permission.js          # 全局导航守卫
    └── static/                # 静态资源(TabBar 图标等)
```

### 9.4 关键设计

- **登录流程**:`/captchaImage` → `/login`(RSA 公钥加密密码)→ `/getInfo` → `/getRouters`
- **Token 存储**:`uni.getStorageSync('Basic-Token')`
- **请求封装**:`utils/request.js` 实现拦截器(401 跳登录)
- **权限指令**:`v-hasPermi`(uniapp 3 编译时不支持自定义指令,用 `v-if="hasPermi([...])"` 替代)
- **双份 manifest/pages**:CLI build 找 `src/manifest.json`,dev 找根 `manifest.json`,
  所以两边各放一份,`sync-config.cjs` 同步
- **环境配置**:`src/config.js` 维护 `baseUrl`(默认 `http://localhost:8080`,
  H5 通过 `vite.config.js` 代理到 `/dev-api` → 8080;小程序/真机需改成局域网 IP)

### 9.5 运行

```bash
cd web-mp
pnpm install

# 开发
pnpm dev:h5            # 浏览器访问 http://localhost:8081
pnpm dev:mp-weixin     # 用微信开发者工具打开 dist/dev/mp-weixin
pnpm dev:app           # 编译到 unpackage/dist/dev/app

# 生产构建
pnpm build:h5          # 产物在 dist/build/h5
pnpm build:mp-weixin   # 产物在 dist/build/mp-weixin
pnpm build:app
```

### 9.6 待办(摘自子 README)

- [ ] 接入动态菜单(`/getRouters` → pages.json)
- [ ] 国际化(`vue-i18n` 已引入,词条待补)
- [ ] 消息推送 / WebSocket
- [ ] 修改密码页 + 个人中心编辑
- [ ] 锁屏(手势/PIN)
- [ ] TabBar 图标(`src/static/tabbar/` 需放 6 张 81x81 png)

---

## 十、目录速查

```
base-postgresql-vue3/
├── pom.xml                              # 父 pom(7 个后端子模块,无 web-mp)
├── README.md                            # 本文档
├── ry.sh / ry.bat                       # RuoYi 启动脚本(原版遗留)
├── basic-admin-api/                     # Spring Boot 启动模块(SB 3.5.16)
├── basic-admin-core/                    # 业务核心(system 模块)
├── basic-framework/                     # 框架配置(Security 6 / Jakarta)
├── basic-common/                        # 通用工具/基类
├── business-core/                       # 业务扩展点
├── extend-quartz/                       # Quartz 定时任务扩展
├── extend-generator/                    # 代码生成扩展
├── extend-flowable/                     # (仅 springboot2-extend-flowable 分支)
├── file-sql/                            # SQL 脚本
├── file-bin/                            # 二进制资源(jar 内置)
├── file-doc/                            # 文档资源(jar 内置)
├── web-ui/                              # PC 端前端(Vue3 + Element Plus,JavaScript)
└── web-mp/                              # 移动端前端(uni-app3 + uview-plus,JavaScript)
```

> **本分支与 master 的差异**:仅后端从 SB 2.5.14(JDK 11 / Security 5 / javax.* / springfox)
> 升级到 SB 3.5.16(JDK 17 / Security 6 / jakarta.* / springdoc);
> 前端两端、SQL 脚本、`web-mp` 移动端、目录结构**完全相同**。
