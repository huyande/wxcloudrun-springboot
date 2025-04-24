-- banner_image表
CREATE TABLE IF NOT EXISTS `banner_image` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `img` varchar(255) NOT NULL COMMENT '图片地址',
  `url` varchar(255) DEFAULT NULL COMMENT '跳转链接',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Banner图片表'; 