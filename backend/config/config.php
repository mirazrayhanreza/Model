<?php
// backend/config/config.php
// Global Configuration File

define('APP_NAME', 'Modol Connect Backend');
define('BASE_URL', 'http://localhost/backend/');
define('AGENT_COMMISSION_PERCENT', 5.0); // 5% cash agent commission
define('ADMIN_PLATFORM_FEE_PERCENT', 15.0); // 15% platform fee

// Session Lifetime (24 hours)
ini_set('session.gc_maxlifetime', 86400);
session_set_cookie_params(86400);

if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

function sendJsonResponse($status, $message, $data = [], $code = 200) {
    http_response_code($code);
    header('Content-Type: application/json');
    echo json_encode([
        'status' => $status,
        'message' => $message,
        'data' => $data,
        'timestamp' => date('c')
    ]);
    exit();
}
?>
