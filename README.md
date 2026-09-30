# Basic Admin (Spring Boot 3.5.16 + Vue 3 + Element Plus + TypeScript)

基于若依 RuoYi-Vue 3.8.2 改造,从 **Spring Boot 2.5.14** 升级到 **Spring Boot 3.5.16**,**前端从 JavaScript 升级到 TypeScript** 的实战记录。

> 本分支 `springboot3_TypeScript` 基于 `springboot3` 切出,叠加了 `springboot2_TypeScript` 分支里完成的**前端 JS → TS 化**改造。
>
> 仓库另有 `springboot2_TypeScript`(SB 2.5.14 + TS)、`springboot4`(SB 4.1.1 + JS)等分支。

---

## 前端说明

本仓库前端为 **TypeScript 版**:

- 框架:Vue 3.5.26 + Element Plus 2.13.1
- 构建:Vite 6.4.1
- 语言:**TypeScript 5.6.3**(`tsconfig.json` 存在,`vue-tsc: 2.1.10` 做类型检查)
- 配置:`@vue/tsconfig: 0.5.1` 作为 tsconfig 基础
- 自动导入:`auto-imports.d.ts` / `components.d.ts` / `global.d.ts`
- 类型目录:`src/types/api/**.ts`(通用 + 业务模块全覆盖)

> JS 版在 `springboot3` / `springboot4` 分支,package.json 不含 `typescript`,本分支**未涉及**。

---

## 一、升级前后对照(基于 `git diff 5b344b7..a4c182a`)

| 组件 | SB 2.5.14 (起点,5b344b7) | SB 3.5.16 (本次升级,a4c182a) |
|---|---|---|
| **JDK** | **11** | **17** |
| Spring Boot | 2.5.14 | **3.5.16** |
| Spring Framework | 5.3.x | **6.1.x** |
| Spring Security | 5.5.x | **6.x** |
| MyBatis-Plus | 3.5.1 | **3.5.7** |
| MyBatis-Spring-Boot-Starter | (注释掉,未启用) | **3.0.5** |
| Druid | 1.2.8 | **1.2.28**(`druid-spring-boot-starter` → `druid-spring-boot-3-starter`) |
| PageHelper | 1.4.1 | **2.1.1** |
| Springdoc-openapi | — (用 springfox 3.0.0) | **2.9.0** |
| Hutool | hutool-bom 5.8.3 | **hutool-core 5.8.27** |
| yauaa(User-Agent 解析) | bitwalker 1.21 | **yauaa 8.2.0**(换库) |
| Lombok | 未指定 | **1.18.46** |
| kaptcha | 2.3.2 | **2.3.3** |
| poi | 4.1.2 | **5.5.1** |
| commons-io | 2.11.0 | **2.22.0** |
| oshi | 6.1.6 | **7.4.4** |
| swagger | springfox 3.0.0 | **移除,springdoc 替代** |
| jakarta.servlet | (javax.servlet) | **jakarta.servlet-api 6.x** |
| fastjson | 2.0.4(注释掉,未启用) | **移除** |

---

## 二、Spring Boot 2 → 3 主要破坏性变更(本次踩坑)

### 2.1 Java EE → Jakarta EE(**最大破坏性**)

javax.* 包名**全部改为** jakarta.*:

| javax | jakarta |
|---|---|
| `javax.servlet.http.HttpServletRequest` | `jakarta.servlet.http.HttpServletRequest` |
| `javax.servlet.Filter` / `FilterChain` / `FilterConfig` | `jakarta.servlet.*` |
| `javax.annotation.*` | `jakarta.annotation.*` |
| `javax.validation.*` | `jakarta.validation.*` |
| `javax.persistence.*` | `jakarta.persistence.*` |
| `javax.sql.DataSource` | `javax.sql.DataSource`(**没变** —— Java SE) |
| `javax.crypto.*` | 不变(Java SE) |

**项目里所有 controller / filter / interceptor / utils 都要改 import**。本次涉及 ~50 个文件。

### 2.2 springfox → springdoc-openapi

- springfox 3.0.0 **不再维护**,与 SB 3 / Spring 6 兼容性差。
- 改用 `springdoc-openapi-starter-webmvc-ui:2.9.0`:
  - 访问地址 `http://host:port/swagger-ui/index.html`
  - 配置文件从 `application.yml` 里 `swagger:` 节点改到 `springdoc:` 节点
  - 注解从 `@Api` / `@ApiOperation`(io.swagger)改到 `@Operation` / `@Tag`(io.swagger.v3.oas.annotations)
  - 原 `SwaggerConfig.java` 改写

### 2.3 SwaggerConfig 重写

- 老的 `SwaggerConfig` 用了 `springfox.documentation.*`,在 SB 3 完全用不了。
- 改用 `io.swagger.v3.oas.models.OpenAPI` + `springdoc` 自己的配置方式(bean)。

### 2.4 Spring Security 5 → 6 重大变更

- `WebSecurityConfigurerAdapter` **废弃**,改用 `SecurityFilterChain` Bean。
- `authorizeRequests()` 改名为 `authorizeHttpRequests()`。
- `antMatchers()` 改名为 `requestMatchers()`(或 `requestMatchers(AntPathRequestMatcher)`)。
- `and()` 链式风格被推荐,但 `.with(...)` DSL 仍可用。
- 密码编码 `BCryptPasswordEncoder` 用法不变。
- `JwtAuthenticationTokenFilter`、`AuthenticationEntryPointImpl`、`LogoutSuccessHandlerImpl` 实现机制不变,只改 import。

### 2.5 Druid starter 改名

| SB 2 | SB 3 |
|---|---|
| `druid-spring-boot-starter` | `druid-spring-boot-3-starter` |

包名没变(`com.alibaba.druid.spring.boot3.autoconfigure` 实际是 SB 3 才有的,SB 2 用 `com.alibaba.druid.spring.boot.autoconfigure`)。

### 2.6 MyBatis-Plus starter 改名

| SB 2 | SB 3 |
|---|---|
| `mybatis-plus-boot-starter` | `mybatis-plus-spring-boot3-starter` |

### 2.7 第三方包大量升级

- Hutool:**bom 模式**(`hutool-bom` 5.8.3) → **单 artifact 模式**(`hutool-core` 5.8.27),只引 core 不再 boms。
- yauaa 8.2.0 替代 bitwalker 1.21(用户代理字符串解析)。
- oshi 7.x 系统信息采集(API 有变化,`getOperatingSystem().getManufacturer()` 等)。
- jaxb-api:**JDK 11+ 默认移除 XML 绑定**,显式 `javax.xml.bind:jaxb-api:2.3.1` 引入(给 XSS 过滤、Excel 解析等用)。

### 2.8 Lombok / annotation 处理

- Lombok **1.18.30+** 才完整支持 JDK 17 + 枚举 + `@AllArgsConstructor`,本项目锁 `1.18.46`。
- JDK 17 的 `--add-opens` 需求大部分场景 Lombok 已处理;本项目 IDE 配 `JDK 17` 即可,无需手动加 jvm args。

### 2.9 Servlet 5 + Tomcat 10

- `jakarta.servlet:jakarta.servlet-api:5.x`(SB 2 时代是 4.x)。
- 内嵌 Tomcat 10.x,HTTP/2 默认开启。
- 业务代码对 servlet API 的调用几乎不变,只改包名。

### 2.10 Spring Framework 6 一些 deprecation

- `WebMvcConfigurerAdapter` 已删除,实现 `WebMvcConfigurer` 接口(SB 2 时代也支持,SB 3 强制要求)。
- `RequestMappingHandlerMapping` 等的 `setUseSuffixPatternMatch` / `useRegisteredSuffixPatternMatch` 方法已删除。
- `MediaType.APPLICATION_JSON_UTF8` 已删除,统一用 `MediaType.APPLICATION_JSON`。

### 2.11 yauaa 替代 bitwalker 解析 UA

- bitwalker `UserAgent` API 与 yauaa `UserAgentAnalyzer` API 完全不一样。
- 项目里 `UserAgentConfig`(若依原本没这个,本次新增)用 `@Bean UserAgentAnalyzer`,把 yauaa 的 100+ MB yaml 规则文件做成单例,避免每次请求都重载。

---

## 三、本次升级真实改动(基于 `git diff 5b344b7..a4c182a`)

实际改动 **50+ 个文件**,大致分类:

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
<swagger.version>3.0.0</swagger.version>  <!-- springfox 移除 -->
<bitwalker.version>1.21</bitwalker.version>  <!-- yauaa 替代 -->
<fastjson.version>2.0.4</fastjson.version>  <!-- fastjson 移除 -->
<commons.collections.version>3.2.2</commons.collections.version>
<hutool.version>5.8.3</hutool.version>  <!-- 改 hutool-core -->

<!-- dependencyManagement -->
- springfox-boot-starter, swagger-models  ← 移除
+ springdoc-openapi-starter-webmvc-ui  ← 新增
+ hutool-core  ← 新增(单 artifact 模式)
+ jaxb-api  ← 新增
+ yauaa  ← 新增
+ mybatis-spring-boot-starter  ← 新增(启用,原来注释掉)
- mybatis-plus-boot-starter  ← 移除
+ mybatis-plus-spring-boot3-starter  ← 替换
- pagehelper-spring-boot-starter 保留
- druid-spring-boot-starter
+ druid-spring-boot-3-starter
```

### 3.2 7 个子模块 `pom.xml`

- 7 个子模块的 `parent.version` 从 `3.8.2` → `3.9.2`
- `basic-common/pom.xml`:
  - `spring-boot-starter-web` 改 `spring-boot-starter-webmvc`(若依 3.9.2 原版就这样)
  - hutool 依赖从 `cn.hutool:hutool-bom:import` 改 `cn.hutool:hutool-core`
  - 加 `javax.xml.bind:jaxb-api`
- `basic-framework/pom.xml`:
  - `spring-boot-starter-aop`(若依 3.9.2 原版就这样,保留)
  - 删 `javax.servlet:servlet-api`(用 jakarta 替代)
  - `druid-spring-boot-3-starter`
- `extend-quartz/pom.xml`:`org.quartz-scheduler:quartz` 单独依赖
- `extend-generator/pom.xml`:`druid-spring-boot-3-starter`

### 3.3 `application.yml` / `application-dev.yml` / `application-prod.yml`

- `swagger:` 节点删除(springfox 没了)
- `springdoc:` 节点新增
- `spring.redis.host/port/database/password` 改 `spring.data.redis.*`(SB 3 警告,SB 4 强制)
- `datasource.druid.statViewServlet.url-pattern: /druid/*` 显式声明
- `xxl.job` 节点删除(若依原版里有,本项目不用,本次移除)

### 3.4 代码侧主要改动

| 文件 | 主要改动 |
|---|---|
| `SwaggerConfig.java` | 完全重写,springfox → springdoc-openapi v3 |
| `SecurityConfig.java` | `WebSecurityConfigurerAdapter` → `SecurityFilterChain` Bean,`authorizeRequests()` → `authorizeHttpRequests()`,`antMatchers()` → `requestMatchers()` |
| `UserAgentConfig.java` | **新增**,yauaa `UserAgentAnalyzer` 单例 Bean |
| `ResourcesConfig.java` | `WebMvcConfigurerAdapter` → `WebMvcConfigurer` |
| `DruidConfig.java` | 包名从 `com.alibaba.druid.spring.boot.autoconfigure` → `com.alibaba.druid.spring.boot3.autoconfigure` |
| `Xss.java` / `XssValidator.java` | `javax.servlet.*` → `jakarta.servlet.*` |
| `XssFilter.java` / `RepeatableFilter.java` / `RepeatedlyRequestWrapper.java` / `XssHttpServletRequestWrapper.java` | 同上 |
| `JwtAuthenticationTokenFilter.java` | 同上 |
| `AuthenticationEntryPointImpl.java` | 同上 |
| `LogoutSuccessHandlerImpl.java` | 同上 |
| `AsyncFactory.java` | 同上 |
| `PermitAllUrlProperties.java` | `antPathMatcher` 注入,`RequestMappingInfo` 处理逻辑微调 |
| `SysDept.java` / `SysDictType.java` / `SysDictData.java` / `SysMenu.java` / `SysRole.java` / `SysUser.java` | 主要是 `javax.validation.*` → `jakarta.validation.*` |
| `SysConfigController.java` / `SysDictDataController.java` / `SysDictTypeController.java` / `SysPostController.java` / `SysRoleController.java` / `SysUserController.java` / `SysOperlogController.java` / `SysLogininforController.java` / `GenController.java` / `SysJobController.java` | 主要是 swagger 注解从 io.swagger → io.swagger.v3.oas.annotations |
| `SysLoginService.java` / `TokenService.java` | 主要是 jakarta import + yauaa 替代 bitwalker 后的 API 调用 |
| `SysConfigServiceImpl.java` / `SysDictTypeServiceImpl.java` / `SysFilesServiceImpl.java` / `SysUserServiceImpl.java` | 主要是 `RedisCache.getCacheObject()` 等的 cache 注解微调 |
| `HttpHelper.java` / `IpUtils.java` / `ServletUtils.java` / `FileUtils.java` / `BeanValidators.java` / `ExcelUtil.java` / `SysLoginController.java` | 主要是 jakarta / commons-* 升级后的 API 微调 |
| `CaptchaController.java` / `CommonController.java` / `SysConfigMapper.xml` / `SysUserMapper.xml` / `SysRoleMapper.xml` / `SysDeptMapper.xml` / `SysDeptMapper.java` / `SysMenuMapper.java` | 主要是 jakarta / swagger / SQL 微调 |
| `GenTable.java` / `GenTableColumn.java` / `SysNotice.java` / `SysPost.java` | 主要是 jakarta validation |
| `SysConfig.java` / `SysFilesServiceImpl.java` 等 | `@DataSource` 等切面相关 |
| `MybatisConfig.java` / `FilterConfig.java` / `ServerConfig.java` / `SameUrlDataInterceptor.java` / `RepeatSubmitInterceptor.java` / `ShutdownManager.java` / `LogAspect.java` / `GlobalExceptionHandler.java` / `SysLoginController.java` / `SysLoginService.java` | 主要是 jakarta import + 业务无关小改 |
| `TestController.java` / `SwaggerController.java` | 主要是 swagger 注解换 io.swagger.v3 |
| `DemoController.java` / `GenTable.java` / `GenTableColumn.java` | 业务示例 |
| `pom.xml` 全部 | 父版本 + 子版本号、starter 名、第三方包版本调整 |

### 3.5 新增文件

- `UserAgentConfig.java` —— yauaa `UserAgentAnalyzer` 单例

### 3.6 删除文件

- (本次升级**没删任何**配置文件,只是改了内容)

---

## 四、未做 / 后续可优化

> 这些事项是 SB 2 → SB 3 升级时**没做完**或**没做**的,留作下一阶段(也就是 `springboot4` 分支)处理。

- [ ] JDK 17 → 21(若依原版 SB 3.5.16 还是 17,本项目跟着用 17)
- [ ] Spring Boot 3.5.16 → 4.1.1(已经在 `springboot4` 分支做完了)
- [ ] Spring Security 6 → 7(`springboot4` 分支做了)
- [ ] Jackson 2 → Jackson 3(`springboot4` 分支做了)
- [ ] Spring Framework 6 → 7(`springboot4` 分支做了)
- [ ] 各种 deprecation 警告清理:`WebMvcConfigurer` 接口新实现未 GA,继续 `@SuppressWarnings` 过渡
- [ ] `WebSecurityConfigurerAdapter` 已废弃(本项目已用 `SecurityFilterChain` Bean 替代)
- [ ] `@Deprecated` 的 `org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration` 等,在 `springboot4` 拆到 `spring-boot-jdbc` 子模块(本分支还在 `boot.autoconfigure.jdbc`,正常)

---

## 五、运行前 checklist

- [x] JDK 17 安装
- [x] 7 个子模块 `parent.version` 一致为 `3.9.2`
- [x] IDE 工程的 Project SDK 设为 JDK 17,Language level 17
- [x] `mvn clean compile` 0 ERROR
- [x] 应用启动成功
- [x] admin 登录成功
- [x] `/getInfo` 200
- [x] 在线用户列表正常

---

## 六、前端 JS → TypeScript 化(本分支新增)

> 本分支的 TS 化**复用**了 `springboot2_TypeScript` 分支的成果(整 web-ui 目录替换),后端仍是 SB 3.5.16。

### 6.1 改了什么

总计 ~196 个文件改动,git 把它识别为 ~50 个 `*.js` → `*.ts` rename + 一些 `.vue` / `.scss` modified + 新增 `src/types/**` 目录。

| 改动类型 | 文件数 | 说明 |
|---|---|---|
| `*.js` → `*.ts` rename | ~50 | `src/api/**`、`src/directive/**`、`src/plugins/**`、`src/router/index.js`、`src/store/**`、`src/utils/**`、`src/main.js`、`src/permission.js`、`src/settings.js` 等 |
| 新增 `src/types/**` | ~30 | `src/types/api/**.ts`(通用 + 业务模块全覆盖)、`components.d.ts`、`global.d.ts`、`index.ts` |
| 新增 `auto-imports.d.ts` | 1 | unplugin-auto-import 自动生成 |
| 新增 `tsconfig.json` | 1 | TS 编译配置 |
| 新增 `.npmrc` | 1 | pnpm 镜像源配置 |
| 新增 `src/assets/images/pay.png` | 1 | 支付二维码(占位图) |
| 新增 `src/layout/components/Copyright/index.vue` | 1 | 版权信息组件 |
| 新增 `src/layout/components/index.ts` | 1 | 组件统一出口 |
| `package.json` 改 | 1 | 新增 `typescript@5.6.3` / `vue-tsc@2.1.10` / `@vue/tsconfig@0.5.1` devDeps |
| `pnpm-lock.yaml` 改 | 1 | lock 文件随之更新 |
| 各种 `.vue` / `.scss` 微调 | ~100 | TS 化过程中对 `script setup` 类型标注、scss 变量引用等的伴随修改 |
| 删除 `web-ui/.gitignore` | 1 | 根 `.gitignore` 统一管理 |
| 删除 `web-ui/src/store/modules/app.js` / `web-ui/src/utils/auth.js` / `web-ui/src/layout/components/index.js` | 3 | 已被 `.ts` 替代 |
| 删除 `web-ui/src/assets/styles/basic.scss` | 1 | **本仓库**基础平台自定义主题色 `var(--el-color-primary: #1890ff)` 文件;TS 版 web-ui 没这文件,**已从 springboot3 单独 checkout 回来保留**(否则主题色变成 element-plus 默认蓝) |

### 6.2 应用方式

```bash
# 在 springboot3 分支基础上,整 web-ui 用 springboot2_TypeScript 的 TS 版替换
git rm -r web-ui/
git checkout springboot2_TypeScript -- web-ui/
# 恢复 springboot3 自己的 basic.scss(主题色)
git checkout springboot3 -- web-ui/src/assets/styles/basic.scss
```

### 6.3 新增 `package.json` devDependencies

```json
{
  "devDependencies": {
    "@vue/tsconfig": "0.5.1",     // Vue 3 推荐 tsconfig 基础
    "typescript": "5.6.3",          // TS 编译器
    "vue-tsc": "2.1.10"             // .vue 文件类型检查
  }
}
```

### 6.4 `src/types/` 目录

```
src/types/
├── api/
│   ├── common.ts        ← AjaxResult / TableDataInfo / R<T> / PageQuery 等通用类型
│   ├── index.ts
│   ├── login.ts         ← LoginBody / LoginResult / UserInfo
│   ├── menu.ts
│   ├── monitor/
│   │   ├── cache.ts
│   │   ├── job.ts
│   │   ├── jobLog.ts
│   │   ├── logininfor.ts
│   │   ├── online.ts
│   │   └── operlog.ts
│   ├── system/
│   │   ├── config.ts
│   │   ├── dept.ts
│   │   ├── dict.ts
│   │   ├── menu.ts
│   │   ├── notice.ts
│   │   ├── post.ts
│   │   ├── role.ts
│   │   └── user.ts
│   └── tool/
│       └── gen.ts
├── components.d.ts      ← unplugin-auto-import / vite-plugin-components 自动生成
├── global.d.ts          ← 全局 window / module 声明
└── index.ts             ← 类型统一出口
```

每个 `types/api/**.ts` 定义了**对应的 API 函数**的**参数类型**和**返回类型**(从后端 RuoYi 的 `AjaxResult` / `TableDataInfo` 风格入手)。

### 6.5 风险评估

- **后端 API 兼容**:`springboot2_TypeScript` 实际是 SB 2.5.14 后端调通的前端代码,但本分支后端是 SB 3.5.16,API URL 和响应格式未变(若依后端 API 是稳定契约),所以**兼容**。
- **`basic.scss` 主题色**:TS 版没带,会丢失本仓库自定义的 `--el-color-primary: #1890ff` 蓝色品牌色。已**手动恢复**。
- **`pnpm-lock.yaml`**:lock 文件用 springboot2_TypeScript 的版本,可能与本仓库之前装的依赖不完全一致。建议你 `cd web-ui && pnpm install` 重新拉一遍。

### 6.6 待办

- [ ] `cd web-ui && pnpm install` 重新拉依赖(锁文件来自 springboot2_TypeScript)
- [ ] `pnpm dev` 验证启动 + `pnpm build:prod` 验证生产构建
- [ ] 打开若干 `.vue` 文件,看 `<script setup lang="ts">` 的类型推断是否完整
- [ ] 调通 admin 登录 → `/getInfo` → 列表查询端到端,看类型不匹配是否报错

---

## 七、运行前 checklist(springboot3 + TS 综合)

- [x] JDK 17 安装
- [x] Node.js + pnpm(前端)
- [ ] `cd web-ui && pnpm install` 拉依赖(lock 文件来自 springboot2_TypeScript)
- [x] 7 个子模块 `parent.version` 一致为 `3.9.2`
- [x] IDE 工程的 Project SDK 设为 JDK 17,Language level 17
- [x] `mvn clean compile` 0 ERROR
- [x] 应用启动成功
- [ ] 前端 `pnpm dev` 启动成功 + 类型检查通过(`pnpm run type-check` 或 `vue-tsc --noEmit`)