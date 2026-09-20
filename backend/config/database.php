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
