-- 创建成就分类表
CREATE TABLE IF NOT EXISTS `achievement_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category_name` varchar(100) NOT NULL COMMENT '分类名称',
  `achievement_name` varchar(100) NOT NULL COMMENT '成就名称',
  `status` tinyint(4) DEFAULT 0 COMMENT '状态 0-正常 1-删除',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category_name` (`category_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4; 