-- ==============================================
-- 智能仓储系统 - 完整数据库表结构
-- ==============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS myshop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE myshop;

-- ==============================================
-- 先删除有外键依赖的表（顺序很重要）
-- ==============================================
DROP TABLE IF EXISTS t_order_detail;
DROP TABLE IF EXISTS t_outbound_order;
DROP TABLE IF EXISTS t_inbound_order;
DROP TABLE IF EXISTS t_purchase_order;
DROP TABLE IF EXISTS t_purchase_plan;
DROP TABLE IF EXISTS t_delivery_order;
DROP TABLE IF EXISTS t_inventory_check_record;
DROP TABLE IF EXISTS t_inventory_check;
DROP TABLE IF EXISTS t_inventory;
DROP TABLE IF EXISTS t_favorite;
DROP TABLE IF EXISTS t_address;
DROP TABLE IF EXISTS cart;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS t_customer;
DROP TABLE IF EXISTS t_supplier;
DROP TABLE IF EXISTS t_product;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS role;

-- ==============================================
-- 1. 角色表
-- ==============================================
CREATE TABLE role (
    rid INT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    roleName VARCHAR(50) NOT NULL COMMENT '角色名称'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ==============================================
-- 2. 用户表
-- ==============================================
CREATE TABLE user (
    uid INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    name VARCHAR(50) NOT NULL COMMENT '用户姓名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    telephone BIGINT COMMENT '手机号',
    createTime DATETIME COMMENT '创建时间',
    roleId INT COMMENT '角色ID',
    FOREIGN KEY (roleId) REFERENCES role(rid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ==============================================
-- 3. 商品表
-- ==============================================
CREATE TABLE t_product (
    pid INT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    pname VARCHAR(100) NOT NULL COMMENT '商品名称',
    price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    pfile VARCHAR(255) COMMENT '商品图片路径',
    description TEXT COMMENT '商品描述',
    min_stock INT DEFAULT 0 COMMENT '库存下限',
    max_stock INT DEFAULT 1000 COMMENT '库存上限'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ==============================================
-- 4. 供应商表
-- ==============================================
CREATE TABLE t_supplier (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '供应商ID',
    supplier_name VARCHAR(100) NOT NULL COMMENT '供应商名称',
    contact VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(255) COMMENT '地址',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- ==============================================
-- 5. 客户表
-- ==============================================
CREATE TABLE t_customer (
    customer_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '客户ID',
    name VARCHAR(100) NOT NULL COMMENT '客户名称',
    contact VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(255) COMMENT '地址',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- ==============================================
-- 6. 销售订单表
-- ==============================================
CREATE TABLE orders (
    oid INT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    uid INT NOT NULL COMMENT '用户ID',
    customer_id INT COMMENT '客户ID',
    status INT DEFAULT 0 COMMENT '订单状态（0:待付款,1:待制作,2:待配送,3:已完成,4:已取消）',
    totalPrice DECIMAL(10,2) NOT NULL COMMENT '订单总价',
    orderTime DATETIME COMMENT '下单时间',
    deliveryTime DATETIME COMMENT '配送时间',
    FOREIGN KEY (uid) REFERENCES user(uid),
    FOREIGN KEY (customer_id) REFERENCES t_customer(customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ==============================================
-- 7. 购物车表
-- ==============================================
CREATE TABLE cart (
    cid INT AUTO_INCREMENT PRIMARY KEY COMMENT '购物车ID',
    uid INT NOT NULL COMMENT '用户ID',
    pid INT NOT NULL COMMENT '商品ID',
    quantity INT DEFAULT 1 COMMENT '商品数量',
    addedTime DATETIME COMMENT '加入时间',
    FOREIGN KEY (uid) REFERENCES user(uid),
    FOREIGN KEY (pid) REFERENCES t_product(pid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ==============================================
-- 8. 收藏表
-- ==============================================
CREATE TABLE t_favorite (
    favorite_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏ID',
    uid INT NOT NULL COMMENT '用户ID',
    pid INT NOT NULL COMMENT '商品ID',
    favoriteTime DATETIME COMMENT '收藏时间',
    quantity INT DEFAULT 1 COMMENT '数量',
    FOREIGN KEY (uid) REFERENCES user(uid),
    FOREIGN KEY (pid) REFERENCES t_product(pid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- ==============================================
-- 9. 地址表
-- ==============================================
CREATE TABLE t_address (
    address_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '地址ID',
    uid INT NOT NULL COMMENT '用户ID',
    consigneeName VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    phoneNumber VARCHAR(20) NOT NULL COMMENT '联系电话',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    district VARCHAR(50) COMMENT '区县',
    detailedAddress VARCHAR(255) NOT NULL COMMENT '详细地址',
    isDefault TINYINT(1) DEFAULT 0 COMMENT '是否默认地址(0:否,1:是)',
    createTime DATETIME COMMENT '创建时间',
    updateTime DATETIME COMMENT '更新时间',
    FOREIGN KEY (uid) REFERENCES user(uid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- ==============================================
-- 10. 库存表
-- ==============================================
CREATE TABLE t_inventory (
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

-- ==============================================
-- 11. 订单详情表
-- ==============================================
CREATE TABLE t_order_detail (
    opid INT AUTO_INCREMENT PRIMARY KEY COMMENT '订单详情ID',
    oid INT NOT NULL COMMENT '订单ID',
    pid INT NOT NULL COMMENT '商品ID',
    quantity INT DEFAULT 1 COMMENT '商品数量',
    price DECIMAL(10,2) NOT NULL COMMENT '商品单价',
    FOREIGN KEY (oid) REFERENCES orders(oid),
    FOREIGN KEY (pid) REFERENCES t_product(pid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单详情表';

-- ==============================================
-- 12. 入库单表
-- ==============================================
CREATE TABLE t_inbound_order (
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

-- ==============================================
-- 13. 出库单表
-- ==============================================
CREATE TABLE t_outbound_order (
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

-- ==============================================
-- 14. 采购计划表
-- ==============================================
CREATE TABLE t_purchase_plan (
    plan_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '采购计划ID',
    plan_no VARCHAR(50) UNIQUE NOT NULL COMMENT '计划编号',
    product_id INT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '计划采购数量',
    reason VARCHAR(255) COMMENT '采购原因',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    status INT DEFAULT 0 COMMENT '状态（0=待审核，1=已审核，2=已执行）',
    FOREIGN KEY (product_id) REFERENCES t_product(pid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购计划表';

-- ==============================================
-- 15. 采购订单表
-- ==============================================
CREATE TABLE t_purchase_order (
    po_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '采购订单ID',
    po_no VARCHAR(50) UNIQUE NOT NULL COMMENT '采购订单号',
    supplier_id INT NOT NULL COMMENT '供应商ID',
    product_id INT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '采购数量',
    price DECIMAL(10,2) COMMENT '单价',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    status INT DEFAULT 0 COMMENT '状态（0=待审核，1=已审核，2=已发货，3=已到货）',
    plan_id INT COMMENT '关联采购计划ID',
    FOREIGN KEY (supplier_id) REFERENCES t_supplier(supplier_id),
    FOREIGN KEY (product_id) REFERENCES t_product(pid),
    FOREIGN KEY (plan_id) REFERENCES t_purchase_plan(plan_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单表';

-- ==============================================
-- 16. 发货单表
-- ==============================================
CREATE TABLE t_delivery_order (
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

-- ==============================================
-- 17. 盘点任务表
-- ==============================================
CREATE TABLE t_inventory_check (
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

-- ==============================================
-- 18. 盘点记录表
-- ==============================================
CREATE TABLE t_inventory_check_record (
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
-- 初始化基础数据
-- ==============================================

-- 角色数据
INSERT INTO role (rid, roleName) VALUES 
(1, '仓库管理员'),
(2, '采购专员'),
(3, '销售专员');

-- 用户数据（初始管理员账户）
INSERT INTO user (uid, name, password, telephone, createTime, roleId) VALUES 
(1, 'admin', '123456', 13800138000, NOW(), 1),
(2, '采购专员', '123456', 13800138001, NOW(), 2),
(3, '销售专员', '123456', 13800138002, NOW(), 3);

-- 供应商数据
INSERT INTO t_supplier (supplier_id, supplier_name, contact, phone, address) VALUES 
(1, '深圳电子科技有限公司', '张明', '13800138001', '广东省深圳市南山区科技园'),
(2, '上海精密仪器厂', '李华', '13900139002', '上海市浦东新区张江高科技园区'),
(3, '北京物资贸易公司', '王强', '13700137003', '北京市朝阳区望京SOHO');

-- 客户数据
INSERT INTO t_customer (customer_id, name, contact, phone, address) VALUES 
(1, '广州商贸有限公司', '陈芳', '13600136001', '广东省广州市天河区珠江新城'),
(2, '成都零售连锁集团', '刘洋', '13500135002', '四川省成都市锦江区春熙路'),
(3, '武汉批发中心', '赵敏', '13400134003', '湖北省武汉市江汉区江汉路');

-- 商品数据
INSERT INTO t_product (pid, pname, price, pfile, description, min_stock, max_stock) VALUES 
(1, '智能仓储管理系统V1.0', 9999.00, 'product1.png', '基于Spring Boot的智能仓储管理解决方案', 10, 100),
(2, '工业级扫码枪', 599.00, 'product2.png', '高精度无线扫码枪，支持一维码/二维码', 50, 500),
(3, 'RFID标签(100个装)', 150.00, 'product3.png', '高频RFID电子标签，适用于仓储管理', 100, 1000),
(4, '仓库货架管理软件', 2999.00, 'product4.png', '专业货架库存管理系统', 5, 50),
(5, '手持终端PDA', 2500.00, 'product5.png', '工业级手持数据采集终端', 20, 200);

-- 库存数据
INSERT INTO t_inventory (inventory_id, product_id, quantity, min_stock, max_stock, batch_no, warehouse_location) VALUES 
(1, 1, 50, 10, 100, 'B20240101', 'A区-01架'),
(2, 2, 200, 50, 500, 'B20240102', 'A区-02架'),
(3, 3, 500, 100, 1000, 'B20240103', 'B区-01架'),
(4, 4, 25, 5, 50, 'B20240104', 'B区-02架'),
(5, 5, 80, 20, 200, 'B20240105', 'C区-01架');

SELECT '数据库表创建完成' AS result;