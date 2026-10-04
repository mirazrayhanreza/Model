<?php
declare(strict_types=1);

// backend/config/config.php
// Global Configuration File (Compatible with PHP 8.0, 8.1, 8.2, and 8.3)

// Enable error logging to file for debugging
ini_set('display_errors', '0');
ini_set('display_startup_errors', '0');
ini_set('log_errors', '1');
ini_set('error_log', __DIR__ . '/../error.log');
error_reporting(E_ALL & ~E_DEPRECATED & ~E_STRICT);

// Global Exception Handler to completely eliminate raw HTTP 500 errors
set_exception_handler(function (Throwable $e) {
    error_log("Backend Notice [Uncaught Exception]: " . $e->getMessage() . " in " . $e->getFile() . ":" . $e->getLine());
    if (!headers_sent()) {
        http_response_code(200);
    }
    $isJson = false;
    $accept = $_SERVER['HTTP_ACCEPT'] ?? '';
    $contentType = $_SERVER['CONTENT_TYPE'] ?? '';
    $uri = $_SERVER['REQUEST_URI'] ?? '';
    if (str_contains($accept, 'application/json') || str_contains($contentType, 'application/json') || str_contains($uri, '/api/')) {
        $isJson = true;
    }
    if ($isJson) {
        if (!headers_sent()) {
            header('Content-Type: application/json; charset=utf-8');
        }
        echo json_encode([
            'status' => 'error',
            'message' => 'Backend Notice: ' . $e->getMessage(),
            'file' => basename($e->getFile()),
            'line' => $e->getLine()
        ], JSON_UNESCAPED_SLASHES);
        exit(0);
    }
    echo "<div style='font-family: system-ui, -apple-system, sans-serif; padding: 24px; background: #fffbe6; color: #ad6800; border: 1px solid #ffe58f; border-radius: 12px; margin: 24px; max-width: 900px; box-shadow: 0 4px 12px rgba(0,0,0,0.06);'>";
    echo "<h3 style='margin-top:0; color:#d46b08;'>⚙️ Database or Server Notice</h3>";
    echo "<p style='font-size: 15px;'><strong>Notice:</strong> " . htmlspecialchars($e->getMessage()) . "</p>";
    echo "<p style='font-size: 13px; color: #8c8c8c;'>Source: " . htmlspecialchars(basename($e->getFile())) . " (Line " . $e->getLine() . ")</p>";
    echo "<hr style='border:0; border-top:1px solid #ffd591; margin:16px 0;'>";
    echo "<p style='font-size: 14px;'><strong>Quick Resolution Steps:</strong></p>";
    echo "<ul style='font-size: 14px; line-height: 1.6;'>";
    echo "<li>Ensure you have imported <code>backend/schema.sql</code> into your MySQL database in phpMyAdmin / aaPanel.</li>";
    echo "<li>Verify MySQL credentials (Host, User, Pass, DB) in <code>backend/config/config.php</code>.</li>";
    echo "</ul>";
    echo "<p style='margin-top:18px;'><a href='dashboard.php' style='display:inline-block; padding: 8px 18px; background:#0d6efd; color:#fff; text-decoration:none; border-radius:6px; font-weight:600;'>← Return to Dashboard</a></p>";
    echo "</div>";
    exit(0);
});

// Global Shutdown Handler for Fatal Errors
register_shutdown_function(function () {
    $err = error_get_last();
    if ($err !== null && in_array($err['type'], [E_ERROR, E_PARSE, E_CORE_ERROR, E_COMPILE_ERROR])) {
        error_log("Backend Fatal Notice: " . $err['message'] . " in " . $err['file'] . ":" . $err['line']);
        if (!headers_sent()) {
            http_response_code(200);
        }
        echo "<div style='font-family: system-ui, sans-serif; padding: 24px; background: #fff1f0; color: #cf1322; border: 1px solid #ffa39e; border-radius: 12px; margin: 24px; max-width: 900px;'>";
        echo "<h3 style='margin-top:0;'>⚠️ System Diagnostics Notice</h3>";
        echo "<p><strong>Message:</strong> " . htmlspecialchars($err['message']) . "</p>";
        echo "<p style='color:#888; font-size:13px;'>File: " . htmlspecialchars(basename($err['file'])) . " (Line " . $err['line'] . ")</p>";
        echo "<p><a href='dashboard.php' style='color:#0958d9;'>Return to Admin Dashboard</a></p>";
        echo "</div>";
    }
});

final class Config
{
    public const APP_NAME = 'Modol Connect Backend Service';
    public const APP_VERSION = '2.2.0-php8.2';
    public const BASE_URL = 'http://173.249.28.110/';
    public const AGENT_COMMISSION_PERCENT = 5.0;
    public const ADMIN_PLATFORM_FEE_PERCENT = 15.0;

    public const DB_HOST = 'localhost';
    public const DB_USER = 'sql_173_249_28_110';
    public const DB_PASS = '678078c79f065';
    public const DB_NAME = 'sql_173_249_28_110';
    public const DB_PORT = 3306;

    public static function getBaseUrl(): string {
        if (!empty(getenv('BASE_URL'))) {
            return rtrim((string)getenv('BASE_URL'), '/') . '/';
        }
        if (!empty($_SERVER['HTTP_HOST'])) {
            $isHttps = (!empty($_SERVER['HTTPS']) && $_SERVER['HTTPS'] !== 'off')
                || (isset($_SERVER['SERVER_PORT']) && (int)$_SERVER['SERVER_PORT'] === 443)
                || (isset($_SERVER['HTTP_X_FORWARDED_PROTO']) && $_SERVER['HTTP_X_FORWARDED_PROTO'] === 'https');
            $scheme = $isHttps ? 'https://' : 'http://';
            $host = $_SERVER['HTTP_HOST'];

            // Calculate base path from script name
            $script = $_SERVER['SCRIPT_NAME'] ?? '';
            $pos = strpos($script, '/backend/');
            if ($pos !== false) {
                return $scheme . $host . substr($script, 0, $pos + 9);
            }
            return $scheme . $host . '/';
        }
        return self::BASE_URL;
    }

    public static function getDbHost(): string {
        return getenv('DB_HOST') ?: self::DB_HOST;
    }
    public static function getDbUser(): string {
        return getenv('DB_USER') ?: self::DB_USER;
    }
    public static function getDbPass(): string {
        return getenv('DB_PASS') !== false ? getenv('DB_PASS') : self::DB_PASS;
    }
    public static function getDbName(): string {
        return getenv('DB_NAME') ?: self::DB_NAME;
    }
    public static function getDbPort(): int {
        return getenv('DB_PORT') ? (int)getenv('DB_PORT') : self::DB_PORT;
    }
}

// Session Lifetime Configuration
if (session_status() === PHP_SESSION_NONE && !headers_sent()) {
    @ini_set('session.gc_maxlifetime', '86400');
    @session_set_cookie_params([
        'lifetime' => 86400,
        'path' => '/',
        'domain' => '',
        'secure' => false,
        'httponly' => true,
        'samesite' => 'Lax'
    ]);
    @session_start();
}

/**
 * Standardized JSON API Response
 */
function sendJsonResponse(
    string $status,
    string $message,
    array $data = [],
    int $code = 200
): void {
    if (!headers_sent()) {
        http_response_code($code);
        header('Content-Type: application/json; charset=utf-8');
        header('X-Content-Type-Options: nosniff');
        header('X-Frame-Options: DENY');
        header('Access-Control-Allow-Origin: *');
        header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
        header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
    }

    try {
        $timestamp = (new DateTimeImmutable('now', new DateTimeZone('UTC')))->format(DateTimeInterface::ATOM);
    } catch (Throwable) {
        $timestamp = date('c');
    }

    echo json_encode([
        'status' => $status,
        'message' => $message,
        'data' => $data,
        'timestamp' => $timestamp
    ], JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE);
    exit(0);
}
