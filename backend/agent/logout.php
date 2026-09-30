<?php
declare(strict_types=1);

// backend/agent/logout.php
// Agent Logout
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';

session_unset();
session_destroy();

if (isApiRequest()) {
    sendJsonResponse('success', 'Agent logged out successfully.');
} else {
    header('Location: login.php');
    exit(0);
}
?>
