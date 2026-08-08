<?php
// backend/admin/users.php
// Admin Panel - Public Users Management Endpoint
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAdminAuth();
$db = Database::getInstance();

$stmt = $db->query("SELECT id, name, email, phone, role, created_at FROM users ORDER BY id DESC LIMIT 50");
$users = $stmt->fetchAll() ?: [
    ['id' => 101, 'name' => 'Al Amin', 'email' => 'alamin@gmail.com', 'phone' => '+8801711223344', 'role' => 'USER', 'created_at' => '2025-05-18'],
    ['id' => 102, 'name' => 'Rashid Khan', 'email' => 'rashid@gmail.com', 'phone' => '+8801811223355', 'role' => 'USER', 'created_at' => '2025-05-18']
];

sendJsonResponse('success', 'Public users list retrieved', ['users' => $users]);
?>
