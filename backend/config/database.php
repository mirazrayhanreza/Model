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
            Config::DB_HOST,
            Config::DB_PORT,
            Config::DB_NAME
        );

        $options = [
            PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES   => false,
        ];

        try {
            $this->conn = new PDO($dsn, Config::DB_USER, Config::DB_PASS, $options);
            $this->conn->exec("SET NAMES utf8mb4");
        } catch (PDOException $e) {
            sendJsonResponse(
                'error',
                'Database connection failed: ' . $e->getMessage() . '. Please verify MySQL user privileges in cPanel/phpMyAdmin.',
                [
                    'db_name' => Config::DB_NAME,
                    'db_user' => Config::DB_USER,
                    'db_host' => Config::DB_HOST
                ],
                500
            );
        }
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
