-- ============================================================
-- 重置所有用户密码为 admin123（明文密码）
-- BCrypt 哈希: $2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2
-- 这是 RuoYi 官方 admin/ry 的默认密码哈希
-- ============================================================

-- 重置所有有效用户（status=0 正常）的密码
UPDATE sys_user
SET password = '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2'
WHERE del_flag = 0;

-- 如果想只重置某几个账号，用这个：
-- UPDATE sys_user SET password = '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2'
-- WHERE user_name IN ('admin', 'xiaoshen', 'xiaoxi', 'xiaoan', 'test001', 'test002', 'user1', '13249230230');

-- 验证
SELECT user_id, dept_id, user_name, nick_name, status, del_flag, password
FROM sys_user
ORDER BY user_id;
