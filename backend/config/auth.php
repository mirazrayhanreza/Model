<?php
// backend/config/auth.php
// Authentication & Security Helper Functions

require_once __DIR__ . '/database.php';
require_once __DIR__ . '/config.php';

function checkAdminAuth() {
    if (!isset($_SESSION['user_role']) || $_SESSION['user_role'] !== 'ADMIN') {
        if (isApiRequest()) {
            sendJsonResponse('error', 'Unauthorized access. Admin privileges required.', [], 401);
        } else {
            header('Location: ' . BASE_URL . 'admin/login.php');
            exit();
        }
    }
}

function checkAgentAuth() {
    if (!isset($_SESSION['user_role']) || $_SESSION['user_role'] !== 'CASH_AGENT') {
        if (isApiRequest()) {
            sendJsonResponse('error', 'Unauthorized access. Cash Agent login required.', [], 401);
        } else {
            header('Location: ' . BASE_URL . 'agent/login.php');
            exit();
        }
    }
}

function isApiRequest() {
    return (
        (isset($_SERVER['HTTP_ACCEPT']) && strpos($_SERVER['HTTP_ACCEPT'], 'application/json') !== false) ||
        (isset($_SERVER['CONTENT_TYPE']) && strpos($_SERVER['CONTENT_TYPE'], 'application/json') !== false) ||
        strpos($_SERVER['REQUEST_URI'], '/api/') !== false
    );
}

function sanitizeInput($data) {
    return htmlspecialchars(stripslashes(trim($data)));
}
?>
