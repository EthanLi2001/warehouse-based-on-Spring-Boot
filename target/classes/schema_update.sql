DELIMITER $$

-- 创建存储过程来安全地添加列
CREATE PROCEDURE AddColumnIfNotExists(
    IN tableName VARCHAR(100),
    IN columnName VARCHAR(100),
    IN columnDefinition VARCHAR(255)
)
BEGIN
    DECLARE columnExists INT DEFAULT 0;
    
    SELECT COUNT(*) INTO columnExists
    FROM information_schema.COLUMNS
    WHERE table_schema = DATABASE() 
      AND table_name = tableName 
      AND column_name = columnName;
    
    IF columnExists = 0 THEN
        SET @sql = CONCAT('ALTER TABLE ', tableName, ' ADD COLUMN ', columnName, ' ', columnDefinition);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$

DELIMITER ;

USE myshop;

-- ==============================================
-- 1. 修改现有表结构
-- ==============================================
CALL AddColumnIfNotExists('t_product', 'min_stock', 'INT DEFAULT 0 COMMENT ''库存下限''');
CALL AddColumnIfNotExists('t_product', 'max_stock', 'INT DEFAULT 1000 COMMENT ''库存上限''');
CALL AddColumnIfNotExists('orders', 'customer_id', 'INT COMMENT ''客户ID''');

-- 删除存储过程
DROP PROCEDURE IF EXISTS AddColumnIfNotExists;

-- ==============================================
-- 2. 创建缺失的表（使用CREATE TABLE IF NOT EXISTS）
-- ==============================================

-- 创建客户表（如果不存在）
CREATE TABLE IF NOT EXISTS t_customer (
    customer_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '客户ID',
    name VARCHAR(100) NOT NULL COMMENT '客户名称',
    contact VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(255) COMMENT '地址',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- 创建库存表（如果不存在）
CREATE TABLE IF NOT EXISTS t_inventory (
    inventory_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '库存ID',
    product_id INT NOT NULL COMMENT '商品ID',
    quantity INT DEFAULT 0 COMMENT '当前库存数量',
    min_stock INT DEFAULT 0 COMMENT '库存下限',
    max_stock INT DEFAULT 1000 COMMENT '库存上限',
    batch_no VARCHAR(50) COMMENT '批次号',
    warehouse_location VARCHAR(100) COMMENT '仓库位置',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (product_id) REFERENCES t_product(pid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- 创建入库单表（如果不存在）
CREATE TABLE IF NOT EXISTS t_inbound_order (
    inbound_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '入库单ID',
    order_no VARCHAR(50) UNIQUE NOT NULL COMMENT '入库单号',
    type INT DEFAULT 1 COMMENT '入库类型（1=采购到货，2=退货入库）',
    supplier_id INT COMMENT '供应商ID',
    product_id INT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '入库数量',
    batch_no VARCHAR(50) COMMENT '批次号',
    operator INT COMMENT '操作人ID',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    status INT DEFAULT 0 COMMENT '状态（0=待审核，1=已入库）',
    FOREIGN KEY (supplier_id) REFERENCES t_supplier(supplier_id),
    FOREIGN KEY (product_id) REFERENCES t_product(pid),
    FOREIGN KEY (operator) REFERENCES user(uid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单表';

-- 创建出库单表（如果不存在）
CREATE TABLE IF NOT EXISTS t_outbound_order (
    outbound_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '出库单ID',
    order_no VARCHAR(50) UNIQUE NOT NULL COMMENT '出库单号',
    type INT DEFAULT 1 COMMENT '出库类型（1=销售出库，2=退货出库）',
    order_id INT COMMENT '关联销售订单ID',
    product_id INT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '出库数量',
    operator INT COMMENT '操作人ID',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    status INT DEFAULT 0 COMMENT '状态（0=待审核，1=已出库）',
    FOREIGN KEY (order_id) REFERENCES orders(oid),
    FOREIGN KEY (product_id) REFERENCES t_product(pid),
    FOREIGN KEY (operator) REFERENCES user(uid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单表';

-- 创建采购计划表（如果不存在）
CREATE TABLE IF NOT EXISTS t_purchase_plan (
    plan_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '采购计划ID',
    plan_no VARCHAR(50) UNIQUE NOT NULL COMMENT '计划编号',
    product_id INT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '计划采购数量',
    reason VARCHAR(255) COMMENT '采购原因',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    status INT DEFAULT 0 COMMENT '状态（0=待审核，1=已审核，2=已执行）',
    FOREIGN KEY (product_id) REFERENCES t_product(pid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购计划表';

-- 创建发货单表（如果不存在）
CREATE TABLE IF NOT EXISTS t_delivery_order (
    delivery_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '发货单ID',
    delivery_no VARCHAR(50) UNIQUE NOT NULL COMMENT '发货单号',
    order_id INT NOT NULL COMMENT '关联销售订单ID',
    logistics_company VARCHAR(100) COMMENT '快递公司',
    tracking_no VARCHAR(50) COMMENT '运单号',
    operator INT COMMENT '操作人ID',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    status INT DEFAULT 0 COMMENT '状态（0=待发货，1=已发货）',
    FOREIGN KEY (order_id) REFERENCES orders(oid),
    FOREIGN KEY (operator) REFERENCES user(uid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发货单表';

-- 创建盘点任务表（如果不存在）
CREATE TABLE IF NOT EXISTS t_inventory_check (
    check_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '盘点任务ID',
    check_no VARCHAR(50) UNIQUE NOT NULL COMMENT '盘点单号',
    warehouse_location VARCHAR(100) COMMENT '盘点位置',
    start_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    operator INT COMMENT '操作人ID',
    remark VARCHAR(255) COMMENT '备注',
    status INT DEFAULT 0 COMMENT '状态（0=进行中，1=已完成）',
    FOREIGN KEY (operator) REFERENCES user(uid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘点任务表';

-- 创建盘点记录表（如果不存在）
CREATE TABLE IF NOT EXISTS t_inventory_check_record (
    record_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '盘点记录ID',
    check_id INT NOT NULL COMMENT '关联盘点任务ID',
    product_id INT NOT NULL COMMENT '商品ID',
    expected_qty INT COMMENT '系统库存数量',
    actual_qty INT COMMENT '实际盘点数量',
    diff_qty INT COMMENT '差异数量',
    FOREIGN KEY (check_id) REFERENCES t_inventory_check(check_id),
    FOREIGN KEY (product_id) REFERENCES t_product(pid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘点记录表';

-- ==============================================
-- 初始化数据（仅当表为空时）
-- ==============================================

-- 初始化角色数据
INSERT IGNORE INTO role (rid, roleName) VALUES 
(1, '仓库管理员'),
(2, '采购专员'),
(3, '销售专员');

-- 初始化供应商数据
INSERT IGNORE INTO t_supplier (supplier_id, supplier_name, contact, phone, address) VALUES 
(1, '深圳电子科技有限公司', '张明', '13800138001', '广东省深圳市南山区科技园'),
(2, '上海精密仪器厂', '李华', '13900139002', '上海市浦东新区张江高科技园区'),
(3, '北京物资贸易公司', '王强', '13700137003', '北京市朝阳区望京SOHO');

-- 初始化客户数据
INSERT IGNORE INTO t_customer (customer_id, name, contact, phone, address) VALUES 
(1, '广州商贸有限公司', '陈芳', '13600136001', '广东省广州市天河区珠江新城'),
(2, '成都零售连锁集团', '刘洋', '13500135002', '四川省成都市锦江区春熙路'),
(3, '武汉批发中心', '赵敏', '13400134003', '湖北省武汉市江汉区江汉路');

SELECT '数据库更新完成' AS result;