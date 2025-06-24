-- 课时记录本 - 数据库表结构
-- 基于产品设计文档的数据库设计

-- 课程信息表
CREATE TABLE courses (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
    mid INT DEFAULT NULL COMMENT '成员id',
    name VARCHAR(100) NOT NULL COMMENT '课程名称',
    total_lessons INT NOT NULL DEFAULT 0 COMMENT '总课时数',
    start_date DATE DEFAULT NULL COMMENT '开始日期',
    weekdays VARCHAR(20) DEFAULT NULL COMMENT '上课周期（1,2,3,4,5,6,7 对应周一到周日，逗号分隔）',
    teacher VARCHAR(50) DEFAULT NULL COMMENT '授课老师',
    location VARCHAR(100) DEFAULT NULL COMMENT '上课地点',
    points_enabled BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否启用积分奖励',
    points_per_lesson INT DEFAULT 0 COMMENT '每次完成课时获得的积分（1-100分）',
    notes VARCHAR(100) DEFAULT NULL COMMENT '备注信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_mid (mid),
    INDEX idx_name (name),
    INDEX idx_start_date (start_date)
) COMMENT='课程信息表';

-- 课时日志表
CREATE TABLE course_logs (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    course_id INT NOT NULL COMMENT '课程ID',
    mid INT DEFAULT NULL COMMENT '成员id',
    lesson_date DATE NOT NULL COMMENT '上课日期',
    lesson_notes VARCHAR(100) DEFAULT NULL COMMENT '本节课备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    INDEX idx_course_id (course_id),
    INDEX idx_mid (mid),
    INDEX idx_lesson_date (lesson_date),
    UNIQUE KEY idx_course_date (course_id, lesson_date) COMMENT '同一课程同一天只能有一条记录'
) COMMENT='课时日志表'; 