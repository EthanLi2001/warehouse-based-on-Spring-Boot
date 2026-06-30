# 🏭 智能仓储管理系统（Spring Boot）

## 📌 项目简介
本项目是一个基于 Spring Boot 的智能仓储管理系统，实现了商品入库、出库、库存管理等核心功能，适用于中小型仓储管理场景。

---

## 🧠 技术栈
- Spring Boot
- MyBatis / MyBatis-Plus
- MySQL
- HTML + Thymeleaf
- Maven

---

## ⚙️ 功能模块

### 👤 用户模块
- 登录 / 登出
- 用户权限管理

### 📦 商品管理
- 商品新增 / 修改 / 删除
- 商品分类管理

### 📊 仓库管理
- 入库管理
- 出库管理
- 库存查询

---

## 🏗️ 项目结构
src/
├── controller
├── service
├── mapper
├── entity
├── config


---

## 🗄️ 数据库说明

### 导入方式
1. 创建 MySQL 数据库：`warehouse`
2. 导入 sql/warehouse.sql
3. 修改 application.yml 配置

---

## 🚀 运行方式

```bash
# 克隆项目
git clone https://github.com/你的用户名/warehouse-based-on-spring-boot.git

# 进入项目
cd warehouse-based-on-spring-boot

# 启动项目
mvn spring-boot:run
