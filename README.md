# Basic Admin (Spring Boot 2.5.14 + Vue 3 + Element Plus)

基于若依 RuoYi-Vue 3.8.2 改造,**未做后端版本升级**,只是修复了 sql bug 和加了锁屏解锁功能。

> 当前分支:`master`,HEAD = `9c2f6e5 md`。
> 仓库另有 `springboot2_TypeScript` / `springboot3` / `springboot3_TypeScript` / `springboot4` / `springboot4_TypeScript` 等分支做不同技术栈组合,本分支**只描述 master**。

---

## 前端说明

本仓库前端为 **JavaScript 版**(无 TypeScript):

- 框架:Vue 3.5.26 + Element Plus 2.13.1
- 构建:Vite 6.4.1
- 代码:纯 `.js` / `.vue`,**无 `typescript` 依赖**(package.json 不含 `typescript`)

> TS 版在 `springboot2_TypeScript` / `springboot3_TypeScript` / `springboot4_TypeScript` 分支,本分支**未涉及**。

---

## 一、当前后端状态(基于 `git show HEAD:pom.xml`)

> 本分支**后端没升级**。基于若依 RuoYi-Vue 3.8.2 原始 SB 2.x 配置。

| 组件 | 版本 |
|---|---|
| **JDK** | **11** |
| Spring Boot | **2.5.14** |
| Spring Framework | 5.3.x |
| Spring Security | 5.5.x |
| MyBatis-Plus | 3.5.1 |
| Druid | 1.2.8(`druid-spring-boot-starter`,无 `-3-` 写法) |
| PageHelper | 1.4.1 |
| swagger | springfox 3.0.0 |
| Hutool | hutool-bom 5.8.3(bom 模式) |
| bitwalker | 1.21(用户代理字符串解析) |
| kaptcha | 2.3.2 |
| poi | 4.1.2 |
| commons-io | 2.11.0 |
| oshi | 6.1.6 |
| jakarta.servlet | (用 javax.servlet) |

---

## 二、git 历史(3 个 commit)

```
9c2f6e5  md                        ← 当前 HEAD
f3062b7  修复sql bug,锁屏解锁功能
5b344b7  first commit              ← 若依 RuoYi 3.8.2 原始
```

---

## 三、本分支的改动(相对 first commit)

### 3.1 commit `f3062b7 修复sql bug,锁屏解锁功能`

总计 8 个文件改动 / 74 行 + / 26 行 -:

| 文件 | 改动 |
|---|---|
| `SysLoginController.java` | +10 行(锁屏解锁相关接口) |
| `SysDeptMapper.java` / `SysDeptMapper.xml` | SQL 微调 |
| `SysMenuMapper.java` | SQL 微调 |
| `SysRoleMapper.xml` | SQL 调整 |
| `SysUserMapper.xml` | SQL 调整 |
| `SysRole.java` | 16 行调整(可能字段、验证规则) |
| `SysLoginService.java` | +40 行(锁屏解锁业务逻辑) |

### 3.2 commit `9c2f6e5 md`

只改 README.md 一行(就是现在这份 README 的前导文案)。

---

## 四、运行前 checklist

- [x] JDK 11 安装
- [x] Node.js + pnpm(前端)
- [x] `cd web-ui && pnpm install` 拉依赖
- [ ] `pnpm dev` 验证启动
- [x] 7 个子模块 `parent.version` 一致为 `3.8.2`
- [x] IDE 工程的 Project SDK 设为 JDK 11,Language level 11
- [x] `mvn clean compile` 0 ERROR
- [ ] 应用启动成功
- [ ] admin 登录成功
- [ ] 锁屏功能可用(本分支 `f3062b7` 改动的)

---

## 五、其他分支(参考)

本仓库其他分支(切到对应分支看其 README):

| 分支 | 做什么 |
|---|---|
| `springboot2_TypeScript` | 同 master 后端,前端 JS → TS 化 |
| `springboot3` | 后端 SB 2.5.14 → 3.5.16(JDK 11 → 17),Jakarta EE + Spring Security 6 + springdoc + ... |
| `springboot3_TypeScript` | `springboot3` + 前端 TS 化 |
| `springboot4` | 后端 SB 3.5.16 → 4.1.1(JDK 17 → 21),Jackson 3 + Spring Security 7 + Spring Framework 7 + ... |
| `springboot4_TypeScript` | `springboot4` + 前端 TS 化(最完整栈) |
