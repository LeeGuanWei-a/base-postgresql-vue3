-- =====================================================================================
--  AgentScope Java 2.x 能力展厅 —— 菜单注册脚本
--  适配分支：springboot4-extend-agentscope
--  目标库：PostgreSQL basicdb_agentscope-vue3 / schema basicdb_agentscope
--
--  说明：
--  1. 首次执行会创建「扩展功能」目录（若不存在），并在其下创建「AgentScope 能力展厅」菜单；
--  2. 脚本可重复执行（幂等）；
--  3. 菜单 component 指向 web-ui/src/views/basic-extend/agent/index.vue，
--     前端左侧导航由后端 /agentscope/capability/list 动态驱动。
-- =====================================================================================

BEGIN;

-- ----------------------------------------------------------------- 父级目录
INSERT INTO basicdb_agentscope.sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query,
     is_frame, is_cache, menu_type, visible, status, perms, icon,
     create_by, create_time, remark)
SELECT 1067, '扩展功能', 0, 4, 'basic-extend', NULL, '',
       '1', '0', 'M', '0', '0', '', 'component',
       'admin', now(), '扩展功能目录：流程、AgentScope 等'
WHERE NOT EXISTS (
    SELECT 1 FROM basicdb_agentscope.sys_menu WHERE menu_id = 1067
);

-- ----------------------------------------------------------------- 能力展厅菜单
INSERT INTO basicdb_agentscope.sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query,
     is_frame, is_cache, menu_type, visible, status, perms, icon,
     create_by, create_time, remark)
SELECT 1068, 'AgentScope 能力展厅', 1067, 1, 'agent', 'basic-extend/agent/index', '',
       '1', '0', 'C', '0', '0', 'agentscope:capability:list', 'cpu',
       'admin', now(), 'AgentScope Java 2.x 全部能力集成演示'
WHERE NOT EXISTS (
    SELECT 1 FROM basicdb_agentscope.sys_menu WHERE menu_id = 1068
);

COMMIT;

-- 校验
-- SELECT menu_id, parent_id, menu_name, path, component, perms
--   FROM basicdb_agentscope.sys_menu
--  WHERE menu_id IN (1067, 1068);
