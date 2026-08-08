<?php
// backend/admin/models.php
// Admin Panel - Model Users Management Endpoint
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAdminAuth();
$db = Database::getInstance();

$stmt = $db->query("SELECT id, name, hourly_rate, category, location, is_verified, rating FROM models ORDER BY id DESC LIMIT 50");
$models = $stmt->fetchAll() ?: [
    ['id' => 1, 'name' => 'Jessica Chowdhury', 'hourly_rate' => 3500.00, 'category' => 'Fashion & Runway', 'location' => 'Dhaka', 'is_verified' => 1, 'rating' => 4.95],
    ['id' => 2, 'name' => 'Tania Islam', 'hourly_rate' => 2800.00, 'category' => 'Commercial Photography', 'location' => 'Dhaka', 'is_verified' => 1, 'rating' => 4.88]
];

sendJsonResponse('success', 'Models list retrieved', ['models' => $models]);
?>
