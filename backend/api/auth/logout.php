<?php
// backend/api/auth/logout.php
// REST API Endpoint - Logout
require_once __DIR__ . '/../../config/config.php';

session_unset();
session_destroy();

sendJsonResponse('success', 'Logged out successfully');
?>
