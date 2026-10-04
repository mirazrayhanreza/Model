-- Database Schema for Modol Connect Backend (aaPanel / MySQL 8.0 / MariaDB)
-- Ready for direct phpMyAdmin / aaPanel Import without Permission or Constraint Errors

SET FOREIGN_KEY_CHECKS = 0;
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";

-- 1. Admins Table
CREATE TABLE IF NOT EXISTS `admins` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(150) UNIQUE NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `role` VARCHAR(50) DEFAULT 'SUPER_ADMIN',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Cash Agents Table (Binance-Style B2B P2P Agent Profile)
CREATE TABLE IF NOT EXISTS `cash_agents` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `agent_code` VARCHAR(50) UNIQUE NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(30) UNIQUE NOT NULL,
    `email` VARCHAR(150) UNIQUE NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `country` VARCHAR(50) DEFAULT 'Bangladesh',
    `city` VARCHAR(100) DEFAULT 'Dhaka',
    `currency` VARCHAR(10) DEFAULT 'BDT',
    `buy_rate` DECIMAL(12,4) DEFAULT 122.5000,
    `sell_rate` DECIMAL(12,4) DEFAULT 120.8000,
    `min_limit` DECIMAL(12,2) DEFAULT 500.00,
    `max_limit` DECIMAL(12,2) DEFAULT 500000.00,
    `daily_limit` DECIMAL(12,2) DEFAULT 500000.00,
    `payment_methods` VARCHAR(255) DEFAULT 'bKash, Nagad, Rocket, Upay, Bank Transfer, Cash',
    `commission_rate` DECIMAL(5,2) DEFAULT 5.00,
    `wallet_balance` DECIMAL(12,2) DEFAULT 0.00,
    `available_balance` DECIMAL(12,2) DEFAULT 50000.00,
    `orders_count` INT DEFAULT 1250,
    `total_orders` INT DEFAULT 1250,
    `completion_rate` VARCHAR(20) DEFAULT '99.4%',
    `avg_release_time` VARCHAR(20) DEFAULT '2.4 min',
    `rating` DECIMAL(3,2) DEFAULT 4.95,
    `is_online` TINYINT(1) DEFAULT 1,
    `is_verified` TINYINT(1) DEFAULT 1,
    `status` VARCHAR(30) DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2.1 Countries Table (Dynamic Multi-Country Architecture)
CREATE TABLE IF NOT EXISTS `countries` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `country_name` VARCHAR(100) NOT NULL,
    `iso_code` VARCHAR(10) NOT NULL,
    `phone_code` VARCHAR(15) NOT NULL,
    `currency_code` VARCHAR(10) NOT NULL,
    `flag` VARCHAR(20) DEFAULT '🌐',
    `status` VARCHAR(30) DEFAULT 'Active',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2.2 Payment Methods Table (Dynamic Country-linked Payment Methods)
CREATE TABLE IF NOT EXISTS `payment_methods` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `country_id` INT NOT NULL,
    `method_name` VARCHAR(100) NOT NULL,
    `method_type` VARCHAR(50) DEFAULT 'Mobile Wallet',
    `logo` VARCHAR(255) DEFAULT '',
    `min_amount` DECIMAL(12,2) DEFAULT 100.00,
    `max_amount` DECIMAL(12,2) DEFAULT 500000.00,
    `status` VARCHAR(30) DEFAULT 'Active',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX (`country_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2.3 Countries and Currencies Legacy Table
CREATE TABLE IF NOT EXISTS `countries_currencies` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `country_code` VARCHAR(10) NOT NULL,
    `country_name` VARCHAR(100) NOT NULL,
    `flag` VARCHAR(20) DEFAULT '🌐',
    `currency_code` VARCHAR(10) NOT NULL,
    `currency_symbol` VARCHAR(10) NOT NULL,
    `rate_to_usd` DECIMAL(12,4) NOT NULL DEFAULT 1.0000,
    `min_deposit` DECIMAL(12,2) DEFAULT 500.00,
    `min_withdrawal` DECIMAL(12,2) DEFAULT 1000.00,
    `status` VARCHAR(30) DEFAULT 'Active',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Users Table
CREATE TABLE IF NOT EXISTS `users` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `uid` VARCHAR(64) UNIQUE,
    `name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(150) UNIQUE NOT NULL,
    `phone` VARCHAR(30) NULL,
    `password` VARCHAR(255) DEFAULT '',
    `country` VARCHAR(50) DEFAULT 'Bangladesh',
    `city` VARCHAR(100) DEFAULT 'Dhaka',
    `role` VARCHAR(20) DEFAULT 'USER',
    `avatar_url` VARCHAR(255) DEFAULT NULL,
    `wallet_balance` DECIMAL(12,2) DEFAULT 0.00,
    `currency` VARCHAR(20) DEFAULT 'BDT (৳)',
    `kyc_status` VARCHAR(30) DEFAULT 'NONE',
    `is_verified` TINYINT(1) DEFAULT 1,
    `status` VARCHAR(30) DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Models Table
CREATE TABLE IF NOT EXISTS `models` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `uid` VARCHAR(64) UNIQUE,
    `user_id` INT NULL,
    `name` VARCHAR(100) NOT NULL,
    `hourly_rate` DECIMAL(10,2) NOT NULL DEFAULT 1500.00,
    `daily_rate` DECIMAL(10,2) DEFAULT 8000.00,
    `category` VARCHAR(50) NOT NULL DEFAULT 'Fashion',
    `location` VARCHAR(100) DEFAULT 'Dhaka',
    `is_online` TINYINT(1) DEFAULT 1,
    `is_verified` TINYINT(1) DEFAULT 1,
    `rating` DECIMAL(3,2) DEFAULT 4.90,
    `review_count` INT DEFAULT 128,
    `avatar_url` VARCHAR(255) DEFAULT NULL,
    `portfolio_images` TEXT NULL,
    `bio` TEXT NULL,
    `skills` VARCHAR(255) NULL,
    `languages` VARCHAR(255) NULL,
    `services` VARCHAR(255) DEFAULT 'Fashion & Runway, Commercial, Editorial',
    `availability_days` VARCHAR(100) DEFAULT 'All Days',
    `gender` VARCHAR(20) DEFAULT 'Female',
    `age` INT DEFAULT 23,
    `height_cm` INT DEFAULT 172,
    `image_res_name` VARCHAR(100) DEFAULT 'model_ayesha',
    `country` VARCHAR(50) DEFAULT 'Bangladesh',
    `phone` VARCHAR(30) NULL,
    `email` VARCHAR(150) NULL,
    `status` VARCHAR(30) DEFAULT 'AVAILABLE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
    `status` VARCHAR(50) DEFAULT 'CONFIRMED',
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
    `timestamp` BIGINT NOT NULL DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. B2B Escrow Orders Table
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
    `created_at` BIGINT NOT NULL DEFAULT 0,
    `updated_at` BIGINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Cash Collections Table
CREATE TABLE IF NOT EXISTS `cash_collections` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `collection_code` VARCHAR(30) UNIQUE NOT NULL,
    `booking_id` VARCHAR(30) NOT NULL,
    `agent_id` INT NOT NULL,
    `client_email` VARCHAR(150) NOT NULL,
    `amount` DECIMAL(12,2) NOT NULL,
    `status` VARCHAR(30) DEFAULT 'PENDING',
    `receipt_photo_url` VARCHAR(255) NULL,
    `collected_at` TIMESTAMP NULL,
    `verified_by_admin` TINYINT(1) DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Wallets & Withdraw Requests
CREATE TABLE IF NOT EXISTS `wallets` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_type` VARCHAR(30) NOT NULL,
    `user_id` VARCHAR(50) NOT NULL,
    `balance` DECIMAL(12,2) DEFAULT 0.00,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `withdraw_requests` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_type` VARCHAR(30) NOT NULL,
    `user_id` VARCHAR(50) NOT NULL,
    `amount` DECIMAL(12,2) NOT NULL,
    `payment_method` VARCHAR(50) DEFAULT 'bKash Agent',
    `status` VARCHAR(30) DEFAULT 'PENDING',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. Notifications Table
CREATE TABLE IF NOT EXISTS `notifications` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(200) NOT NULL,
    `message` TEXT NOT NULL,
    `target` VARCHAR(30) DEFAULT 'ALL',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. GPS Live Escort & Tracking Table
CREATE TABLE IF NOT EXISTS `gps_escort_sessions` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `session_code` VARCHAR(50) UNIQUE NOT NULL,
    `booking_id` VARCHAR(50) NOT NULL,
    `model_id` INT DEFAULT NULL,
    `user_id` INT DEFAULT NULL,
    `model_lat` DECIMAL(10, 7) NOT NULL DEFAULT 23.7745000,
    `model_lng` DECIMAL(10, 7) NOT NULL DEFAULT 90.4120000,
    `user_lat` DECIMAL(10, 7) NOT NULL DEFAULT 23.7937000,
    `user_lng` DECIMAL(10, 7) NOT NULL DEFAULT 90.4066000,
    `distance_km` DECIMAL(6, 2) DEFAULT 2.40,
    `eta_minutes` INT DEFAULT 7,
    `speed_kmh` INT DEFAULT 28,
    `model_battery` INT DEFAULT 86,
    `user_battery` INT DEFAULT 92,
    `status` VARCHAR(50) DEFAULT 'EN_ROUTE',
    `transport_mode` VARCHAR(100) DEFAULT 'Car (Escort)',
    `sos_active` TINYINT(1) DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10.1 Disputes Table
CREATE TABLE IF NOT EXISTS `disputes` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `case_code` VARCHAR(50) UNIQUE NOT NULL,
    `type` VARCHAR(100) NOT NULL,
    `ref_code` VARCHAR(100) NOT NULL,
    `user_name` VARCHAR(100) NOT NULL,
    `agent_name` VARCHAR(100) NOT NULL,
    `amount` VARCHAR(50) NOT NULL,
    `status` VARCHAR(50) DEFAULT 'Open',
    `timer` VARCHAR(50) DEFAULT 'Active 2h',
    `user_statement` TEXT NULL,
    `agent_statement` TEXT NULL,
    `proof_img` VARCHAR(255) NULL,
    `chat_logs` TEXT NULL,
    `device_ip` VARCHAR(255) NULL,
    `wallet_log` VARCHAR(255) NULL,
    `resolution_notes` TEXT NULL,
    `resolved_at` DATETIME NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10.2 Payment Gateways Table (Google Pay, Alipay, Apple Pay & Dynamic Gateways)
CREATE TABLE IF NOT EXISTS `payment_gateways` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `gateway_id` VARCHAR(50) UNIQUE NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `type` VARCHAR(50) DEFAULT 'GLOBAL',
    `environment` VARCHAR(20) DEFAULT 'SANDBOX',
    `merchant_id` VARCHAR(255) NULL,
    `merchant_name` VARCHAR(100) NULL,
    `api_key` VARCHAR(255) NULL,
    `secret_key` TEXT NULL,
    `public_key` TEXT NULL,
    `webhook_secret` VARCHAR(255) NULL,
    `currency` VARCHAR(10) DEFAULT 'BDT',
    `min_amount` DECIMAL(12,2) DEFAULT 100.00,
    `max_amount` DECIMAL(12,2) DEFAULT 1000000.00,
    `fee_percent` DECIMAL(5,2) DEFAULT 1.50,
    `is_enabled` TINYINT(1) DEFAULT 1,
    `supported_cards` VARCHAR(255) DEFAULT 'VISA, MASTERCARD, AMEX',
    `instructions` TEXT NULL,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10.1 Calls Table (WebRTC & Telephony Audio Session Logs)
CREATE TABLE IF NOT EXISTS `calls` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `call_id` VARCHAR(50) UNIQUE NOT NULL,
    `caller_id` VARCHAR(100) NOT NULL,
    `caller_name` VARCHAR(100) NOT NULL,
    `receiver_id` VARCHAR(100) NOT NULL,
    `receiver_name` VARCHAR(100) NOT NULL,
    `call_type` VARCHAR(50) DEFAULT 'Audio Call (WebRTC)',
    `duration_seconds` INT DEFAULT 0,
    `duration_text` VARCHAR(50) DEFAULT '00:00',
    `quality` VARCHAR(50) DEFAULT 'HD Voice (Opus 48kHz)',
    `status` VARCHAR(50) DEFAULT 'Completed',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_caller` (`caller_id`),
    INDEX `idx_receiver` (`receiver_id`),
    INDEX `idx_call_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. Initial Data Seeding
INSERT INTO `admins` (`id`, `name`, `email`, `password`, `role`) VALUES
(1, 'System Admin (Miraz Reza)', 'hmmirazreza2@gmail.com', 'Miraz@647291', 'SUPER_ADMIN')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `email` = VALUES(`email`), `password` = VALUES(`password`);

INSERT INTO `cash_agents` (`id`, `agent_code`, `name`, `phone`, `email`, `password`, `commission_rate`, `wallet_balance`, `country`, `city`, `currency`, `buy_rate`, `sell_rate`, `min_limit`, `max_limit`, `available_balance`, `orders_count`, `rating`, `status`) VALUES
(1, 'AGENT001', 'Dhaka Central Cash Express #01', '+8801700000001', 'sumon@agent.com', '$2y$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm', 5.00, 250000.00, 'Bangladesh', 'Dhaka', 'BDT', 122.5000, 120.8000, 500.00, 500000.00, 50000.00, 2540, 4.95, 'ACTIVE'),
(2, 'AGENT002', 'Dubai Deira Exchange Agent #02', '+97143214321', 'deira@agent.com', '$2y$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm', 4.50, 45000.00, 'UAE', 'Deira, Dubai', 'AED', 3.6700, 3.6500, 50.00, 100000.00, 45000.00, 1850, 4.92, 'ACTIVE')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `users` (`id`, `uid`, `name`, `email`, `phone`, `password`, `country`, `city`, `role`, `wallet_balance`, `currency`, `kyc_status`, `is_verified`, `status`) VALUES
(1, 'usr_1012', 'Rahim Uddin', 'rahim.uddin@gmail.com', '+880 1711 223344', '$2y$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm', 'Bangladesh', 'Dhaka', 'USER', 12500.00, 'BDT (৳)', 'VERIFIED', 1, 'ACTIVE'),
(2, 'usr_1013', 'Karim Khan', 'karim.khan@gmail.com', '+880 1822 334455', '$2y$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm', 'Bangladesh', 'Chittagong', 'USER', 2000.00, 'BDT (৳)', 'SUBMITTED', 0, 'ACTIVE'),
(3, 'usr_1014', 'Faisal Al-Mansoor', 'faisal.mansoor@uaenet.ae', '+971 55 987 6543', '$2y$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm', 'UAE', 'Dubai', 'VIP', 8400.00, 'AED (د.إ)', 'VERIFIED', 1, 'ACTIVE'),
(4, 'usr_1015', 'Tan Wei Ming', 'tan.weiming@klmail.my', '+60 19 888 7766', '$2y$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm', 'Malaysia', 'Kuala Lumpur', 'USER', 3500.00, 'MYR (RM)', 'VERIFIED', 1, 'ACTIVE')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `models` (`id`, `uid`, `name`, `hourly_rate`, `daily_rate`, `category`, `location`, `country`, `phone`, `email`, `rating`, `review_count`, `avatar_url`, `status`) VALUES
(1, 'mod_1', 'Jessica Chowdhury', 3500.00, 18000.00, 'Fashion & Runway', 'Gulshan, Dhaka', 'Bangladesh', '+880 1711 998877', 'jessica.c@modolconnect.com', 4.95, 142, 'https://images.unsplash.com/photo-1534528741775-53994a69daeb', 'AVAILABLE'),
(2, 'mod_2', 'Tania Islam', 2800.00, 14000.00, 'Commercial Photography', 'Banani, Dhaka', 'Bangladesh', '+880 1822 445566', 'tania.i@modolconnect.com', 4.88, 98, 'https://images.unsplash.com/photo-1517841905240-472988babdf9', 'AVAILABLE'),
(3, 'mod_3', 'Nila Akter', 3200.00, 16000.00, 'Bridal & Editorial', 'Chittagong', 'Bangladesh', '+880 1912 345678', 'nila.akter@modolconnect.com', 4.92, 115, 'https://images.unsplash.com/photo-1524504388940-b1c1722653e1', 'AVAILABLE')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `countries` (`id`, `country_name`, `iso_code`, `phone_code`, `currency_code`, `flag`, `status`) VALUES
(1, 'Bangladesh', 'BD', '+880', 'BDT', '🇧🇩', 'Active'),
(2, 'India', 'IN', '+91', 'INR', '🇮🇳', 'Active'),
(3, 'UAE', 'AE', '+971', 'AED', '🇦🇪', 'Active'),
(4, 'Malaysia', 'MY', '+60', 'MYR', '🇲🇾', 'Active'),
(5, 'United States', 'US', '+1', 'USD', '🇺🇸', 'Active')
ON DUPLICATE KEY UPDATE `country_name` = VALUES(`country_name`);

INSERT INTO `payment_methods` (`id`, `country_id`, `method_name`, `method_type`, `min_amount`, `max_amount`, `status`) VALUES
(1, 1, 'bKash', 'Mobile Wallet', 100.00, 50000.00, 'Active'),
(2, 1, 'Nagad', 'Mobile Wallet', 100.00, 50000.00, 'Active'),
(3, 1, 'Rocket', 'Mobile Wallet', 100.00, 30000.00, 'Active'),
(4, 1, 'City Bank Transfer', 'Bank Transfer', 1000.00, 1000000.00, 'Active'),
(5, 3, 'FAB Bank Transfer', 'Bank Transfer', 500.00, 200000.00, 'Active'),
(6, 4, 'DuitNow', 'Mobile Wallet', 50.00, 50000.00, 'Active')
ON DUPLICATE KEY UPDATE `method_name` = VALUES(`method_name`);

INSERT INTO `disputes` (`id`, `case_code`, `type`, `ref_code`, `user_name`, `agent_name`, `amount`, `status`, `timer`, `user_statement`, `agent_statement`, `proof_img`, `device_ip`, `wallet_log`) VALUES
(1, 'DSP-901', 'P2P Order Payment Discrepancy', '#P20184', 'Hasan Ali', 'Agent 004 (Gulshan Escrow)', '৳10,000', 'Open', 'Active 2h', 'Payment done via bKash counter.', 'Statement was not updated at 09:15 AM.', 'https://images.unsplash.com/photo-1559526324-4b87b5e36e44', '103.114.98.22', 'Agent Escrow Hold: ৳10,000')
ON DUPLICATE KEY UPDATE `case_code` = VALUES(`case_code`);

INSERT INTO `payment_gateways` (`id`, `gateway_id`, `name`, `type`, `environment`, `merchant_id`, `merchant_name`, `api_key`, `currency`, `min_amount`, `max_amount`, `fee_percent`, `is_enabled`, `supported_cards`, `instructions`) VALUES
(1, 'google_pay', 'Google Pay', 'DIGITAL_WALLET', 'SANDBOX', 'BCR2DN4T77889900', 'Modol Connect Enterprise', 'gpay_pub_live_998877665544332211', 'BDT', 100.00, 500000.00, 1.50, 1, 'VISA, MASTERCARD, AMEX, DISCOVER', '1-tap biometric checkout via Google Pay wallet.'),
(2, 'alipay', 'Alipay (支付宝)', 'GLOBAL_WALLET', 'SANDBOX', '2088102148765432', 'Modol Connect Hong Kong', '2021000119887766', 'CNY', 50.00, 300000.00, 1.80, 1, 'Alipay Wallet, China UnionPay', 'Cross-border Alipay QR and mobile deep-link payment.'),
(3, 'apple_pay', 'Apple Pay', 'DIGITAL_WALLET', 'SANDBOX', 'merchant.com.modolconnect.app', 'Modol Connect Global Inc', 'appl_id_887766554433', 'USD', 10.00, 10000.00, 1.50, 1, 'Visa, MasterCard, Amex, Apple Card', 'Secure tokenized Apple Pay enclave checkout.')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `calls` (`id`, `call_id`, `caller_id`, `caller_name`, `receiver_id`, `receiver_name`, `call_type`, `duration_seconds`, `duration_text`, `quality`, `status`, `created_at`) VALUES
(1, 'CALL-8821', 'usr_1012', 'Rahim Uddin', 'mod_1', 'Jessica Chowdhury', 'Audio Call (WebRTC)', 860, '14m 20s', 'HD Voice (Opus 48kHz)', 'Completed', '2025-09-30 11:00:00'),
(2, 'CALL-8820', 'usr_1013', 'Karim Khan', 'mod_2', 'Tania Islam', 'Audio Call (WebRTC)', 312, '05m 12s', 'HD Voice (Opus 48kHz)', 'Completed', '2025-09-29 18:30:00'),
(3, 'CALL-8819', 'usr_1012', 'Hasan Ali', 'AGENT001', 'Dhaka Central Cash Express', 'Voice Call (P2P)', 105, '01m 45s', 'Standard Voice', 'Completed', '2025-09-29 09:10:00')
ON DUPLICATE KEY UPDATE `status` = VALUES(`status`);

SET FOREIGN_KEY_CHECKS = 1;
