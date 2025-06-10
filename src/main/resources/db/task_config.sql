-- 任务配置表
CREATE TABLE IF NOT EXISTS `task_config` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `task_key` VARCHAR(50) NOT NULL UNIQUE COMMENT '任务唯一标识，关联user_daily_task_log.type',
    `title` VARCHAR(100) NOT NULL COMMENT '任务标题',
    `description` TEXT COMMENT '任务描述',
    `type` ENUM('daily', 'limited_time') NOT NULL DEFAULT 'daily' COMMENT '任务类型',
    `icon` VARCHAR(50) COMMENT '任务图标名称',
    `points` INT NOT NULL DEFAULT 0 COMMENT '完成奖励积分',
    `max_daily` INT DEFAULT 1 COMMENT '每日最大完成次数',
    
    -- 限时任务相关字段
    `start_time` DATETIME COMMENT '限时任务开始时间',
    `end_time` DATETIME COMMENT '限时任务结束时间', 
    `need_review` TINYINT(1) DEFAULT 0 COMMENT '是否需要审核：0-不需要，1-需要',
    `webview_url` VARCHAR(500) COMMENT '跳转的webview链接',
    
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '任务配置表';

-- 扩展现有的 user_daily_task_log 表
ALTER TABLE `user_daily_task_log` 
ADD COLUMN `status` ENUM('completed', 'pending', 'approved', 'rejected') NOT NULL DEFAULT 'completed' 
    COMMENT '任务状态: completed-已完成(无需审核), pending-待审核, approved-审核通过, rejected-审核拒绝';

ALTER TABLE `user_daily_task_log` 
ADD COLUMN `review_content` TEXT NULL COMMENT '用户提交的审核内容（如文字说明或图片URL）';

ALTER TABLE `user_daily_task_log` 
ADD COLUMN `remark` TEXT NULL COMMENT '管理员审核备注';

-- 插入示例任务配置
INSERT INTO `task_config` (`task_key`, `title`, `description`, `type`, `icon`, `points`, `max_daily`) VALUES
('daily_checkin', '每日签到', '每日首次签到，获得会员积分', 'daily', 'calendar-o', 1, 1),
('watch_ad', '看视频广告', '观看完整视频广告，获得会员积分', 'daily', 'tv-o', 2, 3);

-- 限时任务示例
INSERT INTO `task_config` (`task_key`, `title`, `description`, `type`, `icon`, `points`, `max_daily`, `start_time`, `end_time`, `need_review`) VALUES
('follow_wechat', '关注公众号', '关注我们的微信公众号，截图证明', 'limited_time', 'wechat-o', 10, 1, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1),
('share_moments', '分享朋友圈', '将小程序分享到朋友圈，获得额外积分', 'limited_time', 'share-o', 5, 1, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1);

-- 带 webview_url 的限时任务示例
INSERT INTO `task_config` (`task_key`, `title`, `description`, `type`, `icon`, `points`, `max_daily`, `start_time`, `end_time`, `need_review`, `webview_url`) VALUES
('survey', '参与问卷调查', '完成用户体验问卷调查', 'limited_time', 'question-o', 15, 1, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 0, 'https://example.com/survey'); 