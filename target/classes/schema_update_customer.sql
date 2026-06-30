USE myshop;

-- 移除 orders 表中 uid 字段的外键约束和 NOT NULL 约束
ALTER TABLE orders DROP FOREIGN KEY orders_ibfk_1;
ALTER TABLE orders MODIFY COLUMN uid INT NULL COMMENT '用户ID（保留用于兼容旧数据）';

-- 确保 customer_id 字段存在（如果不存在则添加）
ALTER TABLE orders MODIFY COLUMN customer_id INT COMMENT '客户ID';

SELECT '订单表结构更新完成' AS result;