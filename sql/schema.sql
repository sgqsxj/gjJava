-- ============================================================
-- 校园二手书交易管理系统 建表脚本
-- 数据库: campus_book
-- 字符集: utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS campus_book DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_book;

-- ------------------------------------------------------------
-- 1. 用户表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
    `nickname`    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `address`     VARCHAR(255) DEFAULT NULL COMMENT '收货地址',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ------------------------------------------------------------
-- 2. 图书分类表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name`        VARCHAR(50) NOT NULL COMMENT '分类名称',
    `sort`        INT         NOT NULL DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书分类表';

-- ------------------------------------------------------------
-- 3. 商品表（二手书）
-- stock 字段为后续并发控制（乐观锁防超卖）预留考核点
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `title`         VARCHAR(100)  NOT NULL COMMENT '书名',
    `author`        VARCHAR(100)  DEFAULT NULL COMMENT '作者',
    `publisher`     VARCHAR(100)  DEFAULT NULL COMMENT '出版社',
    `isbn`          VARCHAR(20)   DEFAULT NULL COMMENT 'ISBN',
    `category_id`   BIGINT        DEFAULT NULL COMMENT '分类ID',
    `seller_id`     BIGINT        NOT NULL COMMENT '卖家用户ID',
    `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    `price`         DECIMAL(10,2) NOT NULL COMMENT '售价',
    `condition`     VARCHAR(20)   DEFAULT NULL COMMENT '成色：全新/九成新/八成新/七成新及以下',
    `description`   TEXT          DEFAULT NULL COMMENT '商品描述',
    `cover_image`   VARCHAR(255)  DEFAULT NULL COMMENT '封面图片URL',
    `stock`         INT           NOT NULL DEFAULT 1 COMMENT '库存（乐观锁防超卖预留字段）',
    `status`        TINYINT       NOT NULL DEFAULT 1 COMMENT '状态：1-上架 0-下架',
    `view_count`    INT           NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category_id`),
    KEY `idx_seller` (`seller_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表（二手书）';

-- ------------------------------------------------------------
-- 4. 订单表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
    `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no`     VARCHAR(32)   NOT NULL COMMENT '订单号',
    `buyer_id`     BIGINT        NOT NULL COMMENT '买家用户ID',
    `seller_id`    BIGINT        NOT NULL COMMENT '卖家用户ID',
    `book_id`      BIGINT        NOT NULL COMMENT '商品ID',
    `book_title`   VARCHAR(100)  NOT NULL COMMENT '商品快照-书名',
    `book_price`   DECIMAL(10,2) NOT NULL COMMENT '商品快照-单价',
    `quantity`     INT           NOT NULL DEFAULT 1 COMMENT '购买数量',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    `address`      VARCHAR(255)  DEFAULT NULL COMMENT '收货地址',
    `status`       VARCHAR(20)   NOT NULL DEFAULT 'PENDING_PAYMENT' COMMENT '订单状态：PENDING_PAYMENT-待支付 PAID-已支付(待发货) SHIPPED-已发货(待收货) COMPLETED-已完成 CANCELLED-已取消',
    `pay_time`     DATETIME      DEFAULT NULL COMMENT '支付时间',
    `ship_time`    DATETIME      DEFAULT NULL COMMENT '发货时间',
    `finish_time`  DATETIME      DEFAULT NULL COMMENT '完成时间',
    `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_buyer` (`buyer_id`),
    KEY `idx_seller` (`seller_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ------------------------------------------------------------
-- 5. 收藏表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
    `book_id`     BIGINT   NOT NULL COMMENT '商品ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_book` (`user_id`, `book_id`),
    KEY `idx_book` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- ------------------------------------------------------------
-- 初始分类数据
-- ------------------------------------------------------------
INSERT INTO `category` (`name`, `sort`) VALUES
('教材教辅', 1),
('计算机', 2),
('文学小说', 3),
('历史人文', 4),
('经管励志', 5),
('外语学习', 6),
('艺术设计', 7),
('其他', 99);

-- ------------------------------------------------------------
-- 测试用户（密码均为 123456 的 MD5: e10adc3949ba59abbe56e057f20f883e）
-- ------------------------------------------------------------
INSERT INTO `user` (`username`, `password`, `nickname`, `phone`, `address`) VALUES
('buyer01', 'e10adc3949ba59abbe56e057f20f883e', '买家小明', '13800138001', '北京市海淀区某大学'),
('seller01', 'e10adc3949ba59abbe56e057f20f883e', '卖家小红', '13800138002', '北京市朝阳区某小区');
