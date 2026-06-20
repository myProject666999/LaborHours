-- =====================================================
-- 车间工时统计系统数据库脚本
-- Database: MySQL 8.0+
-- =====================================================

CREATE DATABASE IF NOT EXISTS labor_hours DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE labor_hours;

-- =====================================================
-- 1. 工人表
-- =====================================================
DROP TABLE IF EXISTS sys_worker;
CREATE TABLE sys_worker (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    worker_no VARCHAR(32) NOT NULL COMMENT '工人工号',
    worker_name VARCHAR(64) NOT NULL COMMENT '工人姓名',
    gender TINYINT DEFAULT 1 COMMENT '性别：1-男，2-女',
    phone VARCHAR(20) COMMENT '联系电话',
    team_id BIGINT COMMENT '班组ID',
    position VARCHAR(32) COMMENT '岗位',
    status TINYINT DEFAULT 1 COMMENT '状态：1-在职，0-离职',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_worker_no (worker_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工人信息表';

-- =====================================================
-- 2. 班组表
-- =====================================================
DROP TABLE IF EXISTS sys_team;
CREATE TABLE sys_team (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    team_no VARCHAR(32) NOT NULL COMMENT '班组编号',
    team_name VARCHAR(64) NOT NULL COMMENT '班组名称',
    leader_id BIGINT COMMENT '班组长ID',
    workshop VARCHAR(64) COMMENT '所属车间',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_team_no (team_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班组表';

-- =====================================================
-- 3. 生产订单表
-- =====================================================
DROP TABLE IF EXISTS prod_order;
CREATE TABLE prod_order (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
    order_name VARCHAR(128) NOT NULL COMMENT '订单名称',
    product_name VARCHAR(128) COMMENT '产品名称',
    product_spec VARCHAR(128) COMMENT '产品规格',
    plan_qty INT COMMENT '计划数量',
    order_date DATE COMMENT '下单日期',
    plan_start_date DATE COMMENT '计划开始日期',
    plan_end_date DATE COMMENT '计划完成日期',
    priority TINYINT DEFAULT 2 COMMENT '优先级：1-高，2-中，3-低',
    status TINYINT DEFAULT 1 COMMENT '状态：0-未开始，1-进行中，2-已完成，3-已取消',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产订单表';

-- =====================================================
-- 4. 工序表
-- =====================================================
DROP TABLE IF EXISTS prod_process;
CREATE TABLE prod_process (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    process_code VARCHAR(32) NOT NULL COMMENT '工序编码',
    process_name VARCHAR(64) NOT NULL COMMENT '工序名称',
    process_type TINYINT DEFAULT 1 COMMENT '工序类型：1-直接工序，2-辅助工序',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    standard_hour DECIMAL(10,2) COMMENT '标准工时（小时/单位）',
    description VARCHAR(255) COMMENT '工序描述',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_process_code (process_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工序表';

-- =====================================================
-- 5. 工单表（订单工序关联，即具体的生产任务）
-- =====================================================
DROP TABLE IF EXISTS prod_work_order;
CREATE TABLE prod_work_order (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    work_order_no VARCHAR(32) NOT NULL COMMENT '工单号',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    process_id BIGINT NOT NULL COMMENT '工序ID',
    plan_qty INT COMMENT '计划数量',
    completed_qty INT DEFAULT 0 COMMENT '已完成数量',
    standard_hour DECIMAL(10,2) COMMENT '标准工时（小时）',
    plan_start_time DATETIME COMMENT '计划开始时间',
    plan_end_time DATETIME COMMENT '计划结束时间',
    actual_start_time DATETIME COMMENT '实际开始时间',
    actual_end_time DATETIME COMMENT '实际结束时间',
    assign_worker_id BIGINT COMMENT '指派工人ID',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待开工，1-进行中，2-已暂停，3-已完成，4-已取消',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_work_order_no (work_order_no),
    KEY idx_order_id (order_id),
    KEY idx_process_id (process_id),
    KEY idx_worker_id (assign_worker_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单表';

-- =====================================================
-- 6. 报工记录表（核心业务表）
-- =====================================================
DROP TABLE IF EXISTS labor_record;
CREATE TABLE labor_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    record_no VARCHAR(32) NOT NULL COMMENT '报工记录编号',
    worker_id BIGINT NOT NULL COMMENT '工人ID',
    work_order_id BIGINT COMMENT '工单ID',
    order_id BIGINT COMMENT '订单ID（冗余）',
    process_id BIGINT COMMENT '工序ID（冗余）',
    labor_type TINYINT DEFAULT 1 COMMENT '工时类型：1-直接工时，2-辅助工时',
    report_type TINYINT DEFAULT 1 COMMENT '报工类型：1-按时长报工，2-按数量报工',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    work_hours DECIMAL(10,2) NOT NULL COMMENT '工作时长（小时）',
    completed_qty INT DEFAULT 0 COMMENT '完成数量',
    unit_hour DECIMAL(10,4) COMMENT '单位工时（小时/件）',
    is_overtime TINYINT DEFAULT 0 COMMENT '是否加班：0-否，1-是',
    status TINYINT DEFAULT 1 COMMENT '状态：1-有效，0-作废',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_record_no (record_no),
    KEY idx_worker_id (worker_id),
    KEY idx_work_order_id (work_order_id),
    KEY idx_order_id (order_id),
    KEY idx_process_id (process_id),
    KEY idx_start_time (start_time),
    KEY idx_labor_type (labor_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报工记录表';

-- =====================================================
-- 7. 工时日汇总表
-- =====================================================
DROP TABLE IF EXISTS labor_daily_summary;
CREATE TABLE labor_daily_summary (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    summary_date DATE NOT NULL COMMENT '统计日期',
    worker_id BIGINT NOT NULL COMMENT '工人ID',
    order_id BIGINT COMMENT '订单ID',
    process_id BIGINT COMMENT '工序ID',
    direct_hours DECIMAL(10,2) DEFAULT 0 COMMENT '直接工时',
    indirect_hours DECIMAL(10,2) DEFAULT 0 COMMENT '辅助工时',
    overtime_hours DECIMAL(10,2) DEFAULT 0 COMMENT '加班工时',
    total_hours DECIMAL(10,2) DEFAULT 0 COMMENT '总工时',
    completed_qty INT DEFAULT 0 COMMENT '完成数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_date_worker_order_process (summary_date, worker_id, order_id, process_id),
    KEY idx_worker_id (worker_id),
    KEY idx_order_id (order_id),
    KEY idx_process_id (process_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工时日汇总表';

-- =====================================================
-- 初始化测试数据
-- =====================================================

-- 班组数据
INSERT INTO sys_team (team_no, team_name, workshop, status) VALUES
('T001', '冲压一班', '一车间', 1),
('T002', '焊接一班', '二车间', 1),
('T003', '装配一班', '三车间', 1),
('T004', '喷涂一班', '四车间', 1);

-- 工人数据
INSERT INTO sys_worker (worker_no, worker_name, gender, phone, team_id, position, status) VALUES
('W001', '张三', 1, '13800138001', 1, '冲压工', 1),
('W002', '李四', 1, '13800138002', 1, '冲压工', 1),
('W003', '王五', 1, '13800138003', 2, '焊工', 1),
('W004', '赵六', 2, '13800138004', 2, '焊工', 1),
('W005', '孙七', 1, '13800138005', 3, '装配工', 1),
('W006', '周八', 2, '13800138006', 3, '装配工', 1),
('W007', '吴九', 1, '13800138007', 4, '喷涂工', 1),
('W008', '郑十', 1, '13800138008', 1, '班组长', 1);

-- 工序数据
INSERT INTO prod_process (process_code, process_name, process_type, sort_order, standard_hour, description, status) VALUES
('P001', '下料', 1, 1, 0.50, '原材料下料工序', 1),
('P002', '冲压成型', 1, 2, 1.20, '冲压成型工序', 1),
('P003', '焊接', 1, 3, 2.00, '焊接组装工序', 1),
('P004', '打磨', 1, 4, 0.80, '表面打磨工序', 1),
('P005', '喷涂', 1, 5, 1.50, '表面喷涂工序', 1),
('P006', '装配', 1, 6, 1.00, '产品装配工序', 1),
('P007', '检验', 1, 7, 0.30, '质量检验工序', 1),
('P008', '设备维护', 2, 10, NULL, '设备日常维护', 1),
('P009', '物料搬运', 2, 11, NULL, '物料搬运转运', 1),
('P010', '现场清扫', 2, 12, NULL, '现场5S清扫', 1);

-- 订单数据
INSERT INTO prod_order (order_no, order_name, product_name, product_spec, plan_qty, order_date, plan_start_date, plan_end_date, priority, status, remark) VALUES
('O20240601001', 'A型支架订单100件', 'A型支架', '100x50x200mm', 100, '2024-06-01', '2024-06-10', '2024-06-20', 1, 1, '客户A的紧急订单'),
('O20240601002', 'B型壳体订单200件', 'B型壳体', '200x150x80mm', 200, '2024-06-02', '2024-06-12', '2024-06-25', 2, 1, '常规订单'),
('O20240601003', 'C型底座订单50件', 'C型底座', '300x200x50mm', 50, '2024-06-03', '2024-06-15', '2024-06-30', 3, 0, '小批量订单'),
('O20240601004', 'D型盖板订单150件', 'D型盖板', '180x120x10mm', 150, '2024-06-05', '2024-06-10', '2024-06-22', 2, 1, '客户B订单');

-- 工单数据
INSERT INTO prod_work_order (work_order_no, order_id, process_id, plan_qty, standard_hour, plan_start_time, plan_end_time, assign_worker_id, status, remark) VALUES
('WO20240601001', 1, 1, 100, 50.00, '2024-06-10 08:00:00', '2024-06-11 18:00:00', 1, 3, '下料工序已完成'),
('WO20240601002', 1, 2, 100, 120.00, '2024-06-12 08:00:00', '2024-06-14 18:00:00', 1, 1, '冲压进行中'),
('WO20240601003', 1, 3, 100, 200.00, '2024-06-15 08:00:00', '2024-06-18 18:00:00', 3, 0, '待开工'),
('WO20240601004', 2, 1, 200, 100.00, '2024-06-12 08:00:00', '2024-06-13 18:00:00', 2, 1, '下料进行中'),
('WO20240601005', 2, 2, 200, 240.00, '2024-06-14 08:00:00', '2024-06-17 18:00:00', 2, 0, '待开工'),
('WO20240601006', 2, 3, 200, 400.00, '2024-06-18 08:00:00', '2024-06-22 18:00:00', 3, 0, '待开工'),
('WO20240601007', 4, 5, 150, 225.00, '2024-06-10 08:00:00', '2024-06-14 18:00:00', 7, 1, '喷涂进行中'),
('WO20240601008', 3, 6, 50, 50.00, '2024-06-20 08:00:00', '2024-06-25 18:00:00', 5, 0, '待开工');

-- 报工记录测试数据（模拟历史数据）
INSERT INTO labor_record (record_no, worker_id, work_order_id, order_id, process_id, labor_type, report_type, start_time, end_time, work_hours, completed_qty, unit_hour, is_overtime, status, remark) VALUES
('R20240610001', 1, 1, 1, 1, 1, 2, '2024-06-10 08:00:00', '2024-06-10 12:00:00', 4.00, 8, 0.50, 0, 1, '上午下料'),
('R20240610002', 1, 1, 1, 1, 1, 2, '2024-06-10 13:00:00', '2024-06-10 17:00:00', 4.00, 8, 0.50, 0, 1, '下午下料'),
('R20240610003', 2, 4, 2, 1, 1, 2, '2024-06-10 08:00:00', '2024-06-10 12:00:00', 4.00, 10, 0.40, 0, 1, '上午下料'),
('R20240610004', 7, 7, 4, 5, 1, 2, '2024-06-10 08:00:00', '2024-06-10 12:00:00', 4.00, 3, 1.33, 0, 1, '上午喷涂'),
('R20240610005', 1, NULL, NULL, 8, 2, 1, '2024-06-10 17:00:00', '2024-06-10 18:00:00', 1.00, 0, NULL, 0, 1, '设备维护'),
('R20240611001', 1, 1, 1, 1, 1, 2, '2024-06-11 08:00:00', '2024-06-11 12:00:00', 4.00, 10, 0.40, 0, 1, '上午下料'),
('R20240611002', 1, 1, 1, 1, 1, 2, '2024-06-11 13:00:00', '2024-06-11 17:00:00', 4.00, 10, 0.40, 0, 1, '下午下料'),
('R20240611003', 2, 4, 2, 1, 1, 2, '2024-06-11 08:00:00', '2024-06-11 12:00:00', 4.00, 12, 0.33, 0, 1, '上午下料'),
('R20240611004', 7, 7, 4, 5, 1, 2, '2024-06-11 08:00:00', '2024-06-11 12:00:00', 4.00, 4, 1.00, 0, 1, '上午喷涂'),
('R20240611005', 3, NULL, NULL, 9, 2, 1, '2024-06-11 09:00:00', '2024-06-11 10:00:00', 1.00, 0, NULL, 0, 1, '物料搬运'),
('R20240612001', 1, 2, 1, 2, 1, 2, '2024-06-12 08:00:00', '2024-06-12 12:00:00', 4.00, 4, 1.00, 0, 1, '冲压成型'),
('R20240612002', 1, 2, 1, 2, 1, 2, '2024-06-12 13:00:00', '2024-06-12 17:00:00', 4.00, 3, 1.33, 0, 1, '冲压成型'),
('R20240612003', 2, 4, 2, 1, 1, 2, '2024-06-12 08:00:00', '2024-06-12 12:00:00', 4.00, 15, 0.27, 0, 1, '下料'),
('R20240612004', 7, 7, 4, 5, 1, 2, '2024-06-12 08:00:00', '2024-06-12 12:00:00', 4.00, 5, 0.80, 0, 1, '喷涂'),
('R20240612005', 5, NULL, NULL, 10, 2, 1, '2024-06-12 16:00:00', '2024-06-12 17:00:00', 1.00, 0, NULL, 0, 1, '现场清扫');
