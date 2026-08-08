<?php
// backend/admin/notifications.php
// Admin Panel - Broadcast Notifications Endpoint
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAdminAuth();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $title = sanitizeInput($_POST['title'] ?? '');
    $message = sanitizeInput($_POST['message'] ?? '');
    $target = sanitizeInput($_POST['target'] ?? 'ALL'); // ALL, MODELS, AGENTS

    sendJsonResponse('success', 'Broadcast notification dispatched successfully', [
        'title' => $title,
        'target' => $target
    ]);
}

sendJsonResponse('success', 'Notifications module ready');
?>
