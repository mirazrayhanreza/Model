<?php
declare(strict_types=1);

// backend/api/auth/logout.php
// Production REST Endpoint - Session Invalidation (PHP 8.2)

require_once __DIR__ . '/../../config/config.php';

if (session_status() === PHP_SESSION_ACTIVE) {
    $_SESSION = [];
    if (ini_get('session.use_cookies')) {
        $params = session_get_cookie_params();
        setcookie(
            session_name(),
            '',
            time() - 42000,
            $params['path'],
            $params['domain'],
            $params['secure'],
            $params['httponly']
        );
    }
    session_destroy();
}

sendJsonResponse('success', 'Logged out successfully');
