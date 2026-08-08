-- Database Schema for Modol Connect Backend
-- MySQL 8.0 / InnoDB Engine

CREATE DATABASE IF NOT EXISTS `modol_connect_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `modol_connect_db`;

-- 1. Admins Table
CREATE TABLE IF NOT EXISTS `admins` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(150) UNIQUE NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `role` VARCHAR(50) DEFAULT 'SUPER_ADMIN',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

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
) ENGINE=InnoDB;

-- 3. Users (Clients) Table
CREATE TABLE IF NOT EXISTS `users` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(150) UNIQUE NOT NULL,
    `phone` VARCHAR(20) UNIQUE NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `role` VARCHAR(20) DEFAULT 'USER',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 4. Models Table
CREATE TABLE IF NOT EXISTS `models` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT,
    `name` VARCHAR(100) NOT NULL,
    `hourly_rate` DECIMAL(10,2) NOT NULL,
    `category` VARCHAR(50) NOT NULL,
    `location` VARCHAR(100) DEFAULT 'Dhaka',
    `is_verified` TINYINT(1) DEFAULT 0,
    `rating` DECIMAL(3,2) DEFAULT 4.90,
    `FOREIGN KEY` (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 5. Bookings Table
CREATE TABLE IF NOT EXISTS `bookings` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `booking_code` VARCHAR(20) UNIQUE NOT NULL,
    `client_id` INT NOT NULL,
    `model_id` INT NOT NULL,
    `event_date` VARCHAR(50) NOT NULL,
    `total_amount` DECIMAL(12,2) NOT NULL,
    `payment_type` ENUM('ONLINE', 'CASH') DEFAULT 'CASH',
    `payment_status` ENUM('PENDING', 'PAID', 'ESCROW_HELD', 'RELEASED') DEFAULT 'PENDING',
    `booking_status` ENUM('PENDING', 'ACCEPTED', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 6. Cash Collections Table
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
) ENGINE=InnoDB;

-- 7. Wallets & Transactions
CREATE TABLE IF NOT EXISTS `wallets` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_type` ENUM('ADMIN', 'MODEL', 'AGENT') NOT NULL,
    `user_id` INT NOT NULL,
    `balance` DECIMAL(12,2) DEFAULT 0.00,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `withdraw_requests` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_type` ENUM('MODEL', 'AGENT') NOT NULL,
    `user_id` INT NOT NULL,
    `amount` DECIMAL(12,2) NOT NULL,
    `payment_method` VARCHAR(50) DEFAULT 'bKash Agent',
    `status` ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Insert Seed Admin & Cash Agent
INSERT INTO `admins` (`name`, `email`, `password`, `role`) 
VALUES ('System Admin', 'admin@modolconnect.com', '$2y$10$e8T8e...hashed_password', 'SUPER_ADMIN');

INSERT INTO `cash_agents` (`agent_code`, `name`, `phone`, `email`, `password`, `commission_rate`, `wallet_balance`)
VALUES ('AGENT001', 'Agent Sumon', '+8801700000001', 'sumon@agent.com', '$2y$10$e8T8e...hashed_password', 5.00, 12500.00);
