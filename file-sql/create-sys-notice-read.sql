-- ============================================================
-- 公告已读表 sys_notice_read
-- 适配 RuoYi-Vue3 HeaderNotice（listTop/markRead/markReadAll）
-- ============================================================

DROP TABLE IF EXISTS sys_notice_read;
CREATE TABLE sys_notice_read (
    read_id    BIGINT       NOT NULL,
    notice_id  BIGINT       NOT NULL,
    user_id    BIGINT       NOT NULL,
    read_time  TIMESTAMP    DEFAULT now(),
    CONSTRAINT pk_sys_notice_read PRIMARY KEY (read_id)
);

CREATE INDEX idx_notice_read_notice ON sys_notice_read (notice_id);
CREATE INDEX idx_notice_read_user   ON sys_notice_read (user_id);
CREATE UNIQUE INDEX uk_notice_read ON sys_notice_read (notice_id, user_id);

COMMENT ON TABLE  sys_notice_read IS '公告已读记录表';
COMMENT ON COLUMN sys_notice_read.read_id   IS '记录ID';
COMMENT ON COLUMN sys_notice_read.notice_id IS '公告ID';
COMMENT ON COLUMN sys_notice_read.user_id   IS '用户ID';
COMMENT ON COLUMN sys_notice_read.read_time IS '阅读时间';

-- 雪花 ID 序列（如果没有的话）
CREATE SEQUENCE IF NOT EXISTS sys_notice_read_read_id_seq START 1 INCREMENT 50;
ALTER TABLE sys_notice_read ALTER COLUMN read_id SET DEFAULT nextval('sys_notice_read_read_id_seq'::regclass);
