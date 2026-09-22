-- 重置 admin 密码为 admin123
-- BCrypt 哈希 $2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2 是 RuoYi 官方默认 admin123 的哈希
-- 跑完这条后，前端可以用 admin / admin123 登录

UPDATE sys_user
SET password = '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2'
WHERE user_name = 'admin';

-- 验证
SELECT user_id, user_name, nick_name, password FROM sys_user WHERE user_name = 'admin';
