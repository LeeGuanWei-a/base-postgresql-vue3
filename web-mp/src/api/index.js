/** API 统一出口（命名空间风格，与 web-ui 保持一致） */
import * as loginApi from './login'
import * as userApi from './system/user'
import * as roleApi from './system/role'
import * as deptApi from './system/dept'

export { loginApi, userApi, roleApi, deptApi }
