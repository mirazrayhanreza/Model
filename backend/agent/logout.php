<?php
// backend/agent/logout.php
// Agent Logout
require_once __DIR__ . '/../config/config.php';

session_unset();
session_destroy();

sendJsonResponse('success', 'Agent logged out successfully.');
?>
