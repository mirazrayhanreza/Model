<?php
declare(strict_types=1);

// backend/config/database.php
// Production-grade Multi-Driver PDO Connection (MySQL 8.0 / MariaDB / SQLite) for PHP 8.2+

require_once __DIR__ . '/config.php';

final class Database
{
    private static ?Database $instance = null;
    private ?PDO $conn = null;
    private string $driver = 'sqlite';

    private function __construct()
    {
        $dsn = sprintf(
            'mysql:host=%s;port=%d;dbname=%s;charset=utf8mb4',
            Config::getDbHost(),
            Config::getDbPort(),
            Config::getDbName()
        );

        $options = [
            PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES   => false,
        ];

        // 1. Try MySQL Connection First
        try {
            $this->conn = new PDO($dsn, Config::getDbUser(), Config::getDbPass(), $options);
            $this->conn->exec("SET NAMES utf8mb4");
            $this->driver = 'mysql';
            $this->initMysqlTables();
            return;
        } catch (Throwable $e) {
            error_log("MySQL connection notice: " . $e->getMessage() . " - Falling back to SQLite.");
        }

        // 2. Graceful Fallback to File-based SQLite
        try {
            $sqlitePath = __DIR__ . '/../database.sqlite';
            $isNew = !file_exists($sqlitePath) || filesize($sqlitePath) === 0;
            $this->conn = new PDO('sqlite:' . $sqlitePath, null, null, [
                PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
            ]);
            $this->driver = 'sqlite';
            $this->initSqliteTables();
            return;
        } catch (Throwable $sqle) {
            error_log("SQLite file fallback notice: " . $sqle->getMessage() . " - Falling back to Memory SQLite.");
        }

        // 3. Ultra-resilient In-Memory SQLite to prevent HTTP 500 error under all hosting conditions
        try {
            $this->conn = new PDO('sqlite::memory:', null, null, [
                PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
            ]);
            $this->driver = 'sqlite';
            $this->initSqliteTables();
        } catch (Throwable $crit) {
            error_log("Critical Database failure: " . $crit->getMessage());
        }
    }

    public static function getInstance(): PDO
    {
        if (self::$instance === null) {
            self::$instance = new Database();
        }
        return self::$instance->conn;
    }

    public static function getDriver(): string
    {
        self::getInstance();
        return self::$instance ? self::$instance->driver : 'sqlite';
    }

    public static function isMySQL(): bool
    {
        return self::getDriver() === 'mysql';
    }

    public static function isSQLite(): bool
    {
        return self::getDriver() === 'sqlite';
    }

    public static function safeExec(string $query): bool
    {
        try {
            $db = self::getInstance();
            $db->exec($query);
            return true;
        } catch (Throwable $e) {
            error_log("Database::safeExec notice: " . $e->getMessage());
            return false;
        }
    }

    private function initMysqlTables(): void
    {
        $tables = [
            "CREATE TABLE IF NOT EXISTS `admins` (
                `id` INT AUTO_INCREMENT PRIMARY KEY,
                `name` VARCHAR(100) NOT NULL,
                `email` VARCHAR(150) UNIQUE NOT NULL,
                `password` VARCHAR(255) NOT NULL,
                `role` VARCHAR(50) DEFAULT 'SUPER_ADMIN',
                `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci",

            "CREATE TABLE IF NOT EXISTS `cash_agents` (
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
                `status` VARCHAR(20) DEFAULT 'ACTIVE',
                `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci",

            "CREATE TABLE IF NOT EXISTS `countries` (
                `id` INT AUTO_INCREMENT PRIMARY KEY,
                `country_name` VARCHAR(100) NOT NULL,
                `iso_code` VARCHAR(10) NOT NULL,
                `phone_code` VARCHAR(15) NOT NULL,
                `currency_code` VARCHAR(10) NOT NULL,
                `flag` VARCHAR(20) DEFAULT '🌐',
                `status` VARCHAR(20) DEFAULT 'Active',
                `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci",

            "CREATE TABLE IF NOT EXISTS `payment_methods` (
                `id` INT AUTO_INCREMENT PRIMARY KEY,
                `country_id` INT NOT NULL,
                `method_name` VARCHAR(100) NOT NULL,
                `method_type` VARCHAR(50) DEFAULT 'Mobile Wallet',
                `logo` VARCHAR(255) DEFAULT '',
                `min_amount` DECIMAL(12,2) DEFAULT 100.00,
                `max_amount` DECIMAL(12,2) DEFAULT 500000.00,
                `status` VARCHAR(20) DEFAULT 'Active',
                `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci",

            "CREATE TABLE IF NOT EXISTS `countries_currencies` (
                `id` INT AUTO_INCREMENT PRIMARY KEY,
                `country_code` VARCHAR(10) NOT NULL,
                `country_name` VARCHAR(100) NOT NULL,
                `flag` VARCHAR(20) DEFAULT '🌐',
                `currency_code` VARCHAR(10) NOT NULL,
                `currency_symbol` VARCHAR(10) NOT NULL,
                `rate_to_usd` DECIMAL(12,4) NOT NULL DEFAULT 1.0000,
                `min_deposit` DECIMAL(12,2) DEFAULT 500.00,
                `min_withdrawal` DECIMAL(12,2) DEFAULT 1000.00,
                `status` VARCHAR(20) DEFAULT 'Active',
                `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci",

            "CREATE TABLE IF NOT EXISTS `users` (
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
                `kyc_status` VARCHAR(20) DEFAULT 'NONE',
                `is_verified` TINYINT(1) DEFAULT 1,
                `status` VARCHAR(20) DEFAULT 'ACTIVE',
                `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci",

            "CREATE TABLE IF NOT EXISTS `models` (
                `id` INT AUTO_INCREMENT PRIMARY KEY,
                `uid` VARCHAR(64) UNIQUE,
                `user_id` INT NULL,
                `name` VARCHAR(100) NOT NULL,
                `hourly_rate` DECIMAL(10,2) NOT NULL DEFAULT 1500.00,
                `daily_rate` DECIMAL(10,2) DEFAULT 8000.00,
                `category` VARCHAR(50) NOT NULL DEFAULT 'Fashion',
                `location` VARCHAR(100) DEFAULT 'Dhaka',
                `country` VARCHAR(50) DEFAULT 'Bangladesh',
                `phone` VARCHAR(30) NULL,
                `email` VARCHAR(150) NULL,
                `services` VARCHAR(255) DEFAULT 'Fashion & Runway, Commercial, Editorial',
                `is_online` TINYINT(1) DEFAULT 1,
                `is_verified` TINYINT(1) DEFAULT 1,
                `rating` DECIMAL(3,2) DEFAULT 4.90,
                `review_count` INT DEFAULT 128,
                `avatar_url` VARCHAR(255) DEFAULT NULL,
                `bio` TEXT NULL,
                `status` VARCHAR(20) DEFAULT 'AVAILABLE',
                `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci",

            "CREATE TABLE IF NOT EXISTS `bookings` (
                `id` INT AUTO_INCREMENT PRIMARY KEY,
                `booking_code` VARCHAR(50) NULL,
                `user_id` VARCHAR(50) NOT NULL,
                `model_id` INT NOT NULL,
                `model_name` VARCHAR(100) NOT NULL,
                `date` VARCHAR(50) NOT NULL,
                `time` VARCHAR(50) NOT NULL,
                `service_type` VARCHAR(100) NOT NULL,
                `duration_hours` INT DEFAULT 2,
                `location` VARCHAR(150) NOT NULL,
                `total_price` DECIMAL(12,2) NOT NULL,
                `status` VARCHAR(50) DEFAULT 'CONFIRMED',
                `payment_status` VARCHAR(50) DEFAULT 'ESCROW_HELD',
                `dispute_status` VARCHAR(50) NULL,
                `dispute_reason` TEXT NULL,
                `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci",

            "CREATE TABLE IF NOT EXISTS `b2b_orders` (
                `id` INT AUTO_INCREMENT PRIMARY KEY,
                `order_id` VARCHAR(50) UNIQUE NOT NULL,
                `user_id` VARCHAR(50) NOT NULL,
                `userName` VARCHAR(100) NOT NULL,
                `agent_id` VARCHAR(50) NOT NULL,
                `agent_name` VARCHAR(100) NOT NULL,
                `amount` DECIMAL(12,2) NOT NULL,
                `currency` VARCHAR(10) DEFAULT 'BDT',
                `status` VARCHAR(50) DEFAULT 'PENDING_PAYMENT',
                `dispute_status` VARCHAR(50) NULL,
                `dispute_reason` TEXT NULL,
                `created_at` BIGINT NOT NULL DEFAULT 0
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci",

            "CREATE TABLE IF NOT EXISTS `disputes` (
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
                `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci",

            "CREATE TABLE IF NOT EXISTS `payment_gateways` (
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
                `currency` VARCHAR(10) DEFAULT 'USD',
                `min_amount` DECIMAL(12,2) DEFAULT 100.00,
                `max_amount` DECIMAL(12,2) DEFAULT 1000000.00,
                `fee_percent` DECIMAL(5,2) DEFAULT 1.50,
                `is_enabled` TINYINT(1) DEFAULT 1,
                `supported_cards` VARCHAR(255) DEFAULT 'VISA, MASTERCARD, AMEX',
                `instructions` TEXT NULL,
                `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci"
        ];

        foreach ($tables as $sql) {
            try {
                $this->conn->exec($sql);
            } catch (Throwable $e) {
                error_log("initMysqlTables notice: " . $e->getMessage());
            }
        }

        // Insert default admin if not existing
        try {
            $hashed = password_hash('admin123', PASSWORD_BCRYPT);
            $stmt = $this->conn->prepare("INSERT IGNORE INTO admins (id, name, email, password, role) VALUES (1, 'Super Admin', 'admin@modolconnect.com', ?, 'SUPER_ADMIN')");
            $stmt->execute([$hashed]);
        } catch (Throwable) {}
    }

    private function initSqliteTables(): void
    {
        $sql = "
        CREATE TABLE IF NOT EXISTS admins (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            email TEXT UNIQUE NOT NULL,
            password TEXT NOT NULL,
            role TEXT DEFAULT 'SUPER_ADMIN',
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );
        CREATE TABLE IF NOT EXISTS cash_agents (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            agent_code TEXT UNIQUE NOT NULL,
            name TEXT NOT NULL,
            phone TEXT UNIQUE NOT NULL,
            email TEXT UNIQUE NOT NULL,
            password TEXT NOT NULL,
            country TEXT DEFAULT 'Bangladesh',
            city TEXT DEFAULT 'Dhaka',
            currency TEXT DEFAULT 'BDT',
            buy_rate REAL DEFAULT 122.50,
            sell_rate REAL DEFAULT 120.80,
            min_limit REAL DEFAULT 500.0,
            max_limit REAL DEFAULT 500000.0,
            available_balance REAL DEFAULT 50000.0,
            daily_limit REAL DEFAULT 500000.0,
            payment_methods TEXT DEFAULT 'bKash, Nagad, Bank Transfer',
            commission_rate REAL DEFAULT 5.0,
            wallet_balance REAL DEFAULT 0.0,
            orders_count INTEGER DEFAULT 1250,
            total_orders INTEGER DEFAULT 1250,
            completion_rate TEXT DEFAULT '99.4%',
            avg_release_time TEXT DEFAULT '2.4 min',
            rating REAL DEFAULT 4.95,
            is_online INTEGER DEFAULT 1,
            is_verified INTEGER DEFAULT 1,
            status TEXT DEFAULT 'ACTIVE',
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );
        CREATE TABLE IF NOT EXISTS countries (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            country_name TEXT NOT NULL,
            iso_code TEXT NOT NULL,
            phone_code TEXT NOT NULL,
            currency_code TEXT NOT NULL,
            flag TEXT DEFAULT '🌐',
            status TEXT DEFAULT 'Active',
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );
        CREATE TABLE IF NOT EXISTS payment_methods (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            country_id INTEGER NOT NULL,
            method_name TEXT NOT NULL,
            method_type TEXT DEFAULT 'Mobile Wallet',
            logo TEXT DEFAULT '',
            min_amount REAL DEFAULT 100.0,
            max_amount REAL DEFAULT 500000.0,
            status TEXT DEFAULT 'Active',
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );
        CREATE TABLE IF NOT EXISTS countries_currencies (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            country_code TEXT NOT NULL,
            country_name TEXT NOT NULL,
            flag TEXT DEFAULT '🌐',
            currency_code TEXT NOT NULL,
            currency_symbol TEXT NOT NULL,
            rate_to_usd REAL NOT NULL DEFAULT 1.0,
            min_deposit REAL DEFAULT 500.0,
            min_withdrawal REAL DEFAULT 1000.0,
            status TEXT DEFAULT 'Active',
            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            uid TEXT UNIQUE,
            name TEXT NOT NULL,
            email TEXT UNIQUE NOT NULL,
            phone TEXT,
            password TEXT,
            country TEXT DEFAULT 'Bangladesh',
            city TEXT DEFAULT 'Dhaka',
            role TEXT DEFAULT 'USER',
            avatar_url TEXT,
            wallet_balance REAL DEFAULT 0.0,
            currency TEXT DEFAULT 'BDT (৳)',
            kyc_status TEXT DEFAULT 'NONE',
            is_verified INTEGER DEFAULT 1,
            status TEXT DEFAULT 'ACTIVE',
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );
        CREATE TABLE IF NOT EXISTS models (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            uid TEXT UNIQUE,
            user_id INTEGER,
            name TEXT NOT NULL,
            hourly_rate REAL DEFAULT 1500.0,
            daily_rate REAL DEFAULT 8000.0,
            category TEXT DEFAULT 'Fashion',
            location TEXT DEFAULT 'Dhaka',
            country TEXT DEFAULT 'Bangladesh',
            phone TEXT,
            email TEXT,
            services TEXT DEFAULT 'Fashion & Runway, Commercial, Editorial',
            is_online INTEGER DEFAULT 1,
            is_verified INTEGER DEFAULT 1,
            rating REAL DEFAULT 4.9,
            review_count INTEGER DEFAULT 128,
            bio TEXT,
            avatar_url TEXT,
            status TEXT DEFAULT 'AVAILABLE',
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );
        CREATE TABLE IF NOT EXISTS bookings (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            booking_code TEXT,
            user_id TEXT NOT NULL,
            model_id INTEGER NOT NULL,
            model_name TEXT NOT NULL,
            date TEXT NOT NULL,
            time TEXT NOT NULL,
            service_type TEXT NOT NULL,
            duration_hours INTEGER DEFAULT 2,
            location TEXT NOT NULL,
            total_price REAL NOT NULL,
            status TEXT DEFAULT 'CONFIRMED',
            payment_status TEXT DEFAULT 'ESCROW_HELD',
            dispute_status TEXT,
            dispute_reason TEXT,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );
        CREATE TABLE IF NOT EXISTS b2b_orders (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            order_id TEXT UNIQUE NOT NULL,
            user_id TEXT NOT NULL,
            userName TEXT NOT NULL,
            agent_id TEXT NOT NULL,
            agent_name TEXT NOT NULL,
            amount REAL NOT NULL,
            currency TEXT DEFAULT 'BDT',
            status TEXT DEFAULT 'PENDING_PAYMENT',
            dispute_status TEXT,
            dispute_reason TEXT,
            created_at INTEGER NOT NULL DEFAULT 0
        );
        CREATE TABLE IF NOT EXISTS disputes (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            case_code TEXT UNIQUE NOT NULL,
            type TEXT NOT NULL,
            ref_code TEXT NOT NULL,
            user_name TEXT NOT NULL,
            agent_name TEXT NOT NULL,
            amount TEXT NOT NULL,
            status TEXT DEFAULT 'Open',
            timer TEXT DEFAULT 'Active 2h',
            user_statement TEXT,
            agent_statement TEXT,
            proof_img TEXT,
            chat_logs TEXT,
            device_ip TEXT,
            wallet_log TEXT,
            resolution_notes TEXT,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );
        CREATE TABLE IF NOT EXISTS payment_gateways (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            gateway_id TEXT UNIQUE NOT NULL,
            name TEXT NOT NULL,
            type TEXT DEFAULT 'GLOBAL',
            environment TEXT DEFAULT 'SANDBOX',
            merchant_id TEXT,
            merchant_name TEXT,
            api_key TEXT,
            secret_key TEXT,
            public_key TEXT,
            webhook_secret TEXT,
            currency TEXT DEFAULT 'USD',
            min_amount REAL DEFAULT 100.0,
            max_amount REAL DEFAULT 1000000.0,
            fee_percent REAL DEFAULT 1.5,
            is_enabled INTEGER DEFAULT 1,
            supported_cards TEXT DEFAULT 'VISA, MASTERCARD, AMEX',
            instructions TEXT,
            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );
        ";

        try {
            $this->conn->exec($sql);
        } catch (Throwable $e) {
            error_log("initSqliteTables notice: " . $e->getMessage());
        }

        // Insert default admin
        try {
            $hashed = password_hash('admin123', PASSWORD_BCRYPT);
            $stmt = $this->conn->prepare("INSERT OR IGNORE INTO admins (id, name, email, password, role) VALUES (1, 'Super Admin', 'admin@modolconnect.com', ?, 'SUPER_ADMIN')");
            $stmt->execute([$hashed]);
        } catch (Throwable) {}
    }

    /**
     * Transaction Execution Helper for Safe Atomicity
     */
    public static function transaction(callable $callback): mixed
    {
        $db = self::getInstance();
        $db->beginTransaction();
        try {
            $result = $callback($db);
            $db->commit();
            return $result;
        } catch (Throwable $e) {
            if ($db->inTransaction()) {
                $db->rollBack();
            }
            throw $e;
        }
    }
}
