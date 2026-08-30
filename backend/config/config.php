<?php
declare(strict_types=1);

// backend/config/config.php
// Global Configuration File (Compatible with PHP 8.0, 8.1, 8.2, and 8.3)

// Enable error display for debugging during setup
ini_set('display_errors', '0');
ini_set('display_startup_errors', '0');
error_reporting(E_ALL & ~E_DEPRECATED & ~E_STRICT);

final class Config
{
    public const APP_NAME = 'Modol Connect Backend Service';
    public const APP_VERSION = '2.2.0-php8.2';
    public const BASE_URL = 'https://app.modolconncet.fun/';
    public const AGENT_COMMISSION_PERCENT = 5.0;
    public const ADMIN_PLATFORM_FEE_PERCENT = 15.0;

    public const DB_HOST = 'localhost';
    public const DB_USER = 'u376877788_appu';
    public const DB_PASS = 'Miraz@2019m';
    public const DB_NAME = 'u376877788_app';
    public const DB_PORT = 3306;
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
