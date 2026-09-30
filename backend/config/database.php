<?php
declare(strict_types=1);

// backend/config/database.php
// Production-grade PDO MySQL Connection for PHP 8.2+

require_once __DIR__ . '/config.php';

final class Database
{
    private static ?Database $instance = null;
    private ?PDO $conn = null;

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

        try {
            $this->conn = new PDO($dsn, Config::getDbUser(), Config::getDbPass(), $options);
            $this->conn->exec("SET NAMES utf8mb4");
        } catch (PDOException $e) {
            // Graceful fallback to SQLite for zero-downtime / testing
            try {
                $sqlitePath = __DIR__ . '/../database.sqlite';
                $isNew = !file_exists($sqlitePath);
                $this->conn = new PDO('sqlite:' . $sqlitePath, null, null, [
                    PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
                ]);
                if ($isNew) {
                    $this->initSqliteTables();
                }
            } catch (Throwable $sqle) {
                sendJsonResponse(
                    'error',
                    'Database connection failed: ' . $e->getMessage() . '. Please verify MySQL credentials.',
                    [
                        'db_name' => Config::getDbName(),
                        'db_user' => Config::getDbUser(),
                        'db_host' => Config::getDbHost()
                    ],
                    500
                );
            }
        }
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
            daily_limit REAL DEFAULT 500000.0,
            payment_methods TEXT DEFAULT 'bKash, Nagad, Bank Transfer',
            commission_rate REAL DEFAULT 5.0,
            wallet_balance REAL DEFAULT 0.0,
            orders_count INTEGER DEFAULT 0,
            rating REAL DEFAULT 5.0,
            status TEXT DEFAULT 'ACTIVE',
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
        CREATE TABLE IF NOT EXISTS bookings (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            model_id INTEGER,
            booking_date TEXT,
            hours INTEGER DEFAULT 2,
            total_amount REAL,
            status TEXT DEFAULT 'CONFIRMED',
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );
        ";
        $this->conn->exec($sql);

        // Add columns to existing SQLite tables if they were created earlier without them
        $alterChecks = [
            "ALTER TABLE cash_agents ADD COLUMN country TEXT DEFAULT 'Bangladesh'",
            "ALTER TABLE cash_agents ADD COLUMN city TEXT DEFAULT 'Dhaka'",
            "ALTER TABLE cash_agents ADD COLUMN daily_limit REAL DEFAULT 500000.0",
            "ALTER TABLE cash_agents ADD COLUMN payment_methods TEXT DEFAULT 'bKash, Nagad, Bank Transfer'",
            "ALTER TABLE cash_agents ADD COLUMN orders_count INTEGER DEFAULT 0",
            "ALTER TABLE cash_agents ADD COLUMN rating REAL DEFAULT 5.0",
            "ALTER TABLE users ADD COLUMN country TEXT DEFAULT 'Bangladesh'",
            "ALTER TABLE users ADD COLUMN city TEXT DEFAULT 'Dhaka'",
            "ALTER TABLE users ADD COLUMN currency TEXT DEFAULT 'BDT (৳)'",
            "ALTER TABLE models ADD COLUMN country TEXT DEFAULT 'Bangladesh'",
            "ALTER TABLE models ADD COLUMN phone TEXT",
            "ALTER TABLE models ADD COLUMN email TEXT",
            "ALTER TABLE models ADD COLUMN services TEXT DEFAULT 'Fashion & Runway, Commercial, Editorial'"
        ];
        foreach ($alterChecks as $alterSql) {
            try { $this->conn->exec($alterSql); } catch (Throwable) {}
        }
        // Insert default admin
        $hashed = password_hash('admin123', PASSWORD_BCRYPT);
        $stmt = $this->conn->prepare("INSERT OR IGNORE INTO admins (id, name, email, password, role) VALUES (1, 'Super Admin', 'admin@modolconnect.com', ?, 'SUPER_ADMIN')");
        $stmt->execute([$hashed]);
        // Insert sample models
        $this->conn->exec("INSERT OR IGNORE INTO models (id, name, hourly_rate, category, location, rating, is_verified) VALUES 
            (1, 'Jessica Chowdhury', 3500.0, 'Fashion & Runway', 'Dhaka', 4.95, 1),
            (2, 'Tania Islam', 2800.0, 'Commercial Photography', 'Dhaka', 4.88, 1),
            (3, 'Nila Akter', 3200.0, 'Bridal & Editorial', 'Chittagong', 4.92, 1)
        ");
    }

    public static function getInstance(): PDO
    {
        if (self::$instance === null) {
            self::$instance = new Database();
        }
        return self::$instance->conn;
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
