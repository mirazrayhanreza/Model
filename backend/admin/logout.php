<?php
// backend/admin/logout.php
// Admin Session Terminate
require_once __DIR__ . '/../config/config.php';

session_unset();
session_destroy();

sendJsonResponse('success', 'Admin session logged out successfully.');
?>
