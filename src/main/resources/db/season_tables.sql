-- 赛季配置表
CREATE TABLE IF NOT EXISTS `season_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mId` int(11) NOT NULL COMMENT '创建者ID',
  `name` varchar(255) NOT NULL COMMENT '赛季名称',
  `startTime` datetime DEFAULT NULL COMMENT '开始时间',
  `endTime` datetime DEFAULT NULL COMMENT '结束时间',
  `tokenName` varchar(255) DEFAULT NULL COMMENT '专属代币名称',
  `tokenIcon` varchar(255) DEFAULT NULL COMMENT '代币图标',
  `conversionRate` int(11) DEFAULT '0' COMMENT '上一赛季代币结转百分比(0-100)',
  `initialPoints` int(11) DEFAULT '0' COMMENT '赛季初始积分',
  `status` tinyint(4) DEFAULT '0' COMMENT '状态：0未开始 1进行中 2已结束 3已归档',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idxMId` (`mId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛季配置表';

-- 赛季规则表
CREATE TABLE IF NOT EXISTS `season_rules` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `seasonId` bigint(20) NOT NULL COMMENT '赛季ID',
  `mId` int(11) NOT NULL COMMENT '成员ID',
  `name` varchar(255) NOT NULL COMMENT '规则名称',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `iconType` varchar(50) DEFAULT NULL COMMENT '图标类型',
  `type` varchar(100) DEFAULT NULL COMMENT '规则分类',
  `typeSort` int(11) DEFAULT '0' COMMENT '分类排序',
  `weeks` varchar(20) DEFAULT NULL COMMENT '打卡周期',
  `content` text COMMENT '规则描述',
  `sort` int(11) DEFAULT '0' COMMENT '分类内排序',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态：0禁用 1启用',
  `point` int(11) DEFAULT '0' COMMENT '规则积分',
  `quickScore` int(11) DEFAULT NULL COMMENT '快捷固定积分',
  `enablePomodoro` tinyint(1) DEFAULT '0' COMMENT '是否开启番茄钟',
  `pomodoroTime` int(11) DEFAULT NULL COMMENT '番茄时长(分钟)',
  `dayLimit` int(11) DEFAULT NULL COMMENT '单日次数限制',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idxSeasonMid` (`seasonId`, `mId`),
  KEY `idxType` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛季规则表';

-- 赛季积分日志表
CREATE TABLE IF NOT EXISTS `season_point_logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `seasonId` bigint(20) NOT NULL COMMENT '赛季ID',
  `day` datetime NOT NULL COMMENT '日期',
  `mId` int(11) NOT NULL COMMENT '成员ID',
  `uid` int(11) DEFAULT NULL COMMENT '操作人ID',
  `ruleId` bigint(20) DEFAULT NULL COMMENT '规则ID',
  `num` int(11) NOT NULL COMMENT '积分值',
  `type` tinyint(4) DEFAULT '0' COMMENT '类型：0规则积分 1调整/补偿 2赛季转换 3愿望消耗',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `pomodoroTime` int(11) DEFAULT NULL COMMENT '番茄时长',
  `createdAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idxSeasonMid` (`seasonId`, `mId`),
  KEY `idxDay` (`day`),
  KEY `idxRuleId` (`ruleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛季积分日志表';

-- 赛季愿望表
CREATE TABLE IF NOT EXISTS `season_wishes` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `seasonId` bigint(20) NOT NULL COMMENT '赛季ID',
  `mId` int(11) NOT NULL COMMENT '成员ID',
  `img` varchar(255) DEFAULT NULL COMMENT '愿望图片',
  `title` varchar(255) NOT NULL COMMENT '愿望标题',
  `content` text COMMENT '愿望内容',
  `needPoint` int(11) NOT NULL COMMENT '所需积分',
  `unit` int(11) DEFAULT NULL COMMENT '单位数值',
  `unitType` varchar(50) DEFAULT NULL COMMENT '单位类型',
  `type` tinyint(4) DEFAULT '1' COMMENT '类型：0系统 1自定义',
  `status` tinyint(4) DEFAULT '0' COMMENT '状态：0待实现 1已实现',
  `createdAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idxSeasonMid` (`seasonId`, `mId`),
  KEY `idxStatus` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛季愿望表';

-- 赛季愿望日志表
CREATE TABLE IF NOT EXISTS `season_wish_logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `seasonId` bigint(20) NOT NULL COMMENT '赛季ID',
  `mId` int(11) NOT NULL COMMENT '成员ID',
  `uid` int(11) DEFAULT NULL COMMENT '操作人ID',
  `wId` bigint(20) NOT NULL COMMENT '愿望ID',
  `point` int(11) NOT NULL COMMENT '消耗积分',
  `info` varchar(255) DEFAULT NULL COMMENT '附加信息',
  `status` tinyint(4) DEFAULT '0' COMMENT '状态：0待使用 1已使用 -1撤销 2进行中',
  `amount` int(11) DEFAULT NULL COMMENT '兑换数量',
  `unitType` varchar(50) DEFAULT NULL COMMENT '单位类型',
  `endTime` datetime DEFAULT NULL COMMENT '完成时间',
  `unit` int(11) DEFAULT NULL COMMENT '单位值',
  `createdAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idxSeasonMid` (`seasonId`, `mId`),
  KEY `idxWId` (`wId`),
  KEY `idxStatus` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛季愿望日志表';

-- 赛季成就表
CREATE TABLE IF NOT EXISTS `season_rule_achievements` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `seasonId` bigint(20) NOT NULL COMMENT '赛季ID',
  `ruleId` int(11) NOT NULL COMMENT '规则ID',
  `title` varchar(255) NOT NULL COMMENT '成就标题',
  `img` varchar(255) DEFAULT NULL COMMENT '成就图片',
  `conditionType` varchar(50) NOT NULL COMMENT '条件类型，如"连续"、"累计"、"积分"',
  `conditionValue` int(11) NOT NULL COMMENT '条件值',
  `rewardType` varchar(50) NOT NULL COMMENT '奖励类型',
  `rewardValue` int(11) NOT NULL COMMENT '奖励值',
  `status` tinyint(4) DEFAULT '0' COMMENT '状态：0开启 1删除',
  `createdAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idxSeasonRule` (`seasonId`, `ruleId`),
  KEY `idxStatus` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛季成就表';

-- 赛季成就日志表
CREATE TABLE IF NOT EXISTS `season_rule_achievement_logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `seasonId` bigint(20) NOT NULL COMMENT '赛季ID',
  `sraId` bigint(20) NOT NULL COMMENT '赛季成就ID',
  `mId` int(11) NOT NULL COMMENT '成员ID',
  `rewardValue` int(11) DEFAULT NULL COMMENT '奖励值',
  `createdAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idxSeasonMid` (`seasonId`, `mId`),
  KEY `idxSraId` (`sraId`),
  KEY `idxSeasonSraMid` (`seasonId`, `sraId`, `mId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛季成就日志表';

-- 更新 member 表添加新字段
ALTER TABLE `member` 
ADD COLUMN `mode` varchar(20) DEFAULT null COMMENT '模式：NORMAL正常模式 SEASON赛季模式',
ADD COLUMN `currentSeasonId` bigint(20) DEFAULT NULL COMMENT '当前赛季ID'; 