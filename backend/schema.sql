-- Database Schema for Modol Connect Backend (PHP 8.0 - 8.3 / MySQL 8.0 / MariaDB)
-- Target Database: u376877788_app

CREATE DATABASE IF NOT EXISTS `u376877788_app` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `u376877788_app`;

-- 1. Admins Table
CREATE TABLE IF NOT EXISTS `admins` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(150) UNIQUE NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `role` VARCHAR(50) DEFAULT 'SUPER_ADMIN',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Cash Agents Table
CREATE TABLE IF NOT EXISTS `cash_agents` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `agent_code` VARCHAR(20) UNIQUE NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20) UNIQUE NOT NULL,
    `email` VARCHAR(150) UNIQUE NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `commission_rate` DECIMAL(5,2) DEFAULT 5.00,
    `wallet_balance` DECIMAL(12,2) DEFAULT 0.00,
    `status` ENUM('ACTIVE', 'SUSPENDED') DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Users Table
CREATE TABLE IF NOT EXISTS `users` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(150) UNIQUE NOT NULL,
    `phone` VARCHAR(20) UNIQUE NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `role` VARCHAR(20) DEFAULT 'USER',
    `is_verified` TINYINT(1) DEFAULT 1,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Models Table
CREATE TABLE IF NOT EXISTS `models` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NULL,
    `name` VARCHAR(100) NOT NULL,
    `hourly_rate` DECIMAL(10,2) NOT NULL,
    `category` VARCHAR(50) NOT NULL DEFAULT 'Fashion',
    `location` VARCHAR(100) DEFAULT 'Dhaka',
    `is_online` TINYINT(1) DEFAULT 1,
    `is_verified` TINYINT(1) DEFAULT 1,
    `rating` DECIMAL(3,2) DEFAULT 4.90,
    `review_count` INT DEFAULT 0,
    `bio` TEXT NULL,
    `skills` VARCHAR(255) NULL,
    `languages` VARCHAR(255) NULL,
    `services` VARCHAR(255) NULL,
    `availability_days` VARCHAR(100) DEFAULT 'All Days',
    `gender` VARCHAR(20) DEFAULT 'Female',
    `age` INT DEFAULT 23,
    `height_cm` INT DEFAULT 172,
    `image_res_name` VARCHAR(100) DEFAULT 'model_ayesha',
    `country` VARCHAR(50) DEFAULT 'Bangladesh',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_models_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Bookings Table
CREATE TABLE IF NOT EXISTS `bookings` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `booking_code` VARCHAR(50) NULL,
    `user_id` VARCHAR(50) NOT NULL,
    `model_id` INT NOT NULL,
    `model_name` VARCHAR(100) NOT NULL,
    `model_photo` VARCHAR(100) DEFAULT 'model_1',
    `date` VARCHAR(50) NOT NULL,
    `time` VARCHAR(50) NOT NULL,
    `service_type` VARCHAR(100) NOT NULL,
    `duration_hours` INT DEFAULT 2,
    `location` VARCHAR(150) NOT NULL,
    `notes` TEXT NULL,
    `total_price` DECIMAL(12,2) NOT NULL,
    `status` VARCHAR(50) DEFAULT 'PAYMENT_RECEIVED',
    `payment_status` VARCHAR(50) DEFAULT 'ESCROW_HELD',
    `payment_method` VARCHAR(50) DEFAULT 'WALLET',
    `model_earnings` DECIMAL(12,2) DEFAULT 0.00,
    `platform_fee` DECIMAL(12,2) DEFAULT 0.00,
    `proof_selfie_url` TEXT NULL,
    `proof_photos` TEXT NULL,
    `proof_gps_location` VARCHAR(100) NULL,
    `proof_notes` TEXT NULL,
    `proof_submitted_time` BIGINT NULL,
    `user_rating` INT DEFAULT 5,
    `user_feedback` TEXT NULL,
    `dispute_status` VARCHAR(50) NULL,
    `dispute_reason` TEXT NULL,
    `timestamp` BIGINT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. B2B Escrow Orders
CREATE TABLE IF NOT EXISTS `b2b_orders` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `order_id` VARCHAR(50) UNIQUE NOT NULL,
    `user_id` VARCHAR(50) NOT NULL,
    `userName` VARCHAR(100) NOT NULL,
    `agent_id` VARCHAR(50) NOT NULL,
    `agent_name` VARCHAR(100) NOT NULL,
    `country` VARCHAR(50) DEFAULT 'Bangladesh',
    `type` VARCHAR(20) DEFAULT 'DEPOSIT',
    `amount` DECIMAL(12,2) NOT NULL,
    `currency` VARCHAR(10) DEFAULT 'BDT',
    `payment_method` VARCHAR(50) DEFAULT 'bKash',
    `agent_account_number` VARCHAR(50) NULL,
    `agent_account_holder` VARCHAR(100) NULL,
    `status` VARCHAR(50) DEFAULT 'PENDING_PAYMENT',
    `proof_screenshot_url` TEXT NULL,
    `transaction_ref` VARCHAR(100) NULL,
    `dispute_status` VARCHAR(50) NULL,
    `dispute_reason` TEXT NULL,
    `created_at` BIGINT NOT NULL,
    `updated_at` BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Cash Collections Table
CREATE TABLE IF NOT EXISTS `cash_collections` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `collection_code` VARCHAR(30) UNIQUE NOT NULL,
    `booking_id` VARCHAR(30) NOT NULL,
    `agent_id` INT NOT NULL,
    `client_email` VARCHAR(150) NOT NULL,
    `amount` DECIMAL(12,2) NOT NULL,
    `status` ENUM('PENDING', 'PAID', 'REJECTED') DEFAULT 'PENDING',
    `receipt_photo_url` VARCHAR(255) NULL,
    `collected_at` TIMESTAMP NULL,
    `verified_by_admin` TINYINT(1) DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. Wallets & Transactions
CREATE TABLE IF NOT EXISTS `wallets` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_type` ENUM('ADMIN', 'MODEL', 'AGENT', 'USER') NOT NULL,
    `user_id` VARCHAR(50) NOT NULL,
    `balance` DECIMAL(12,2) DEFAULT 0.00,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `withdraw_requests` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_type` ENUM('MODEL', 'AGENT') NOT NULL,
    `user_id` VARCHAR(50) NOT NULL,
    `amount` DECIMAL(12,2) NOT NULL,
    `payment_method` VARCHAR(50) DEFAULT 'bKash Agent',
    `status` ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed Initial Data
INSERT IGNORE INTO `admins` (`id`, `name`, `email`, `password`, `role`) 
VALUES 
(1, 'System Admin (Miraz Reza)', 'hmmirazreza2@gmail.com', '$2y$10$e8T8e...hashed_password', 'SUPER_ADMIN'),
(2, 'System Admin', 'admin@modolconnect.com', '$2y$10$e8T8e...hashed_password', 'SUPER_ADMIN');

INSERT IGNORE INTO `cash_agents` (`id`, `agent_code`, `name`, `phone`, `email`, `password`, `commission_rate`, `wallet_balance`)
VALUES (1, 'AGENT001', 'Agent Sumon', '+8801700000001', 'sumon@agent.com', '$2y$10$e8T8e...hashed_password', 5.00, 12500.00);
