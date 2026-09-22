-- ============================================================
-- 修正 sys_menu.component 路径（适配 RuoYi-Vue3 视图结构）
-- 把旧 basic-admin/、extend/、business/ 前缀替换为新路径
-- 替换原则：
--   basic-admin/system/* -> system/*
--   basic-admin/monitor/* -> monitor/*
--   extend/build/*  -> tool/build/*
--   extend/gen/*    -> tool/gen/*
--   extend/swagger/* -> tool/swagger/*
--   extend/job/*    -> monitor/job/*
--   business/*      -> business/*  (新增对应业务视图，本脚本先置 NULL 让前端显示空白但不影响)
-- ============================================================

BEGIN;

-- 1) 菜单管理
UPDATE sys_menu SET component = 'system/menu/index'
  WHERE menu_id = 102 AND component = 'basic-admin/system/menu/index';

-- 2) 用户管理
UPDATE sys_menu SET component = 'system/user/index'
  WHERE menu_id = 100 AND component = 'basic-admin/system/user/index';

-- 3) 角色管理
UPDATE sys_menu SET component = 'system/role/index'
  WHERE menu_id = 101 AND component = 'basic-admin/system/role/index';

-- 4) 部门管理
UPDATE sys_menu SET component = 'system/dept/index'
  WHERE menu_id = 103 AND component = 'basic-admin/system/dept/index';

-- 5) 岗位管理
UPDATE sys_menu SET component = 'system/post/index'
  WHERE menu_id = 104 AND component = 'basic-admin/system/post/index';

-- 6) 字典管理
UPDATE sys_menu SET component = 'system/dict/index'
  WHERE menu_id = 105 AND component = 'basic-admin/system/dict/index';

-- 7) 参数设置
UPDATE sys_menu SET component = 'system/config/index'
  WHERE menu_id = 106 AND component = 'basic-admin/system/config/index';

-- 8) 通知公告
UPDATE sys_menu SET component = 'system/notice/index'
  WHERE menu_id = 107 AND component = 'basic-admin/system/notice/index';

-- 9) 操作日志
UPDATE sys_menu SET component = 'monitor/operlog/index'
  WHERE menu_id = 500 AND component = 'basic-admin/monitor/operlog/index';

-- 10) 登录日志
UPDATE sys_menu SET component = 'monitor/logininfor/index'
  WHERE menu_id = 501 AND component = 'basic-admin/monitor/logininfor/index';

-- 11) 数据监控
UPDATE sys_menu SET component = 'monitor/druid/index'
  WHERE menu_id = 111 AND component = 'basic-admin/monitor/druid/index';

-- 12) 在线用户
UPDATE sys_menu SET component = 'monitor/online/index'
  WHERE menu_id = 109 AND component = 'basic-admin/monitor/online/index';

-- 13) 服务监控
UPDATE sys_menu SET component = 'monitor/server/index'
  WHERE menu_id = 112 AND component = 'basic-admin/monitor/server/index';

-- 14) 缓存监控
UPDATE sys_menu SET component = 'monitor/cache/index'
  WHERE menu_id = 113 AND component = 'basic-admin/monitor/cache/index';

-- 15) 表单构建 (extend/build -> tool/build)
UPDATE sys_menu SET component = 'tool/build/index'
  WHERE menu_id = 114 AND component = 'extend/build/index';

-- 16) 代码生成 (extend/gen -> tool/gen)
UPDATE sys_menu SET component = 'tool/gen/index'
  WHERE menu_id = 115 AND component = 'extend/gen/index';

-- 17) 系统接口 (extend/swagger -> tool/swagger)
UPDATE sys_menu SET component = 'tool/swagger/index'
  WHERE menu_id = 116 AND component = 'extend/swagger/index';

-- 18) 定时任务 (extend/job -> monitor/job)
UPDATE sys_menu SET component = 'monitor/job/index'
  WHERE menu_id = 110 AND component = 'extend/job/index';

-- 19) 国际化 (basic-admin/system/lang/index 前端没此视图，先置 NULL)
UPDATE sys_menu SET component = NULL
  WHERE menu_id = 1955896250820567041 AND component = 'basic-admin/system/lang/index';

-- 20) 表单示例 (business/demo/index 前端没此视图，先置 NULL)
UPDATE sys_menu SET component = NULL
  WHERE menu_id = 1061 AND component = 'business/demo/index';

-- 21) 示例表单 (business/demo/demoForm 前端没此视图，先置 NULL)
UPDATE sys_menu SET component = NULL
  WHERE menu_id = 1753950971045343233 AND component = 'business/demo/demoForm';

COMMIT;

-- 验证：菜单 + component 完整列表
SELECT menu_id, menu_name, parent_id, path, component, menu_type
FROM sys_menu
WHERE menu_type IN ('M', 'C')
ORDER BY parent_id NULLS FIRST, order_num;
