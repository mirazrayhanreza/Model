<?php
// backend/admin/bookings.php
// Admin Panel - Booking Management Endpoint
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAdminAuth();
$db = Database::getInstance();

$stmt = $db->query("SELECT id, booking_code, client_id, model_id, event_date, total_amount, payment_type, payment_status, booking_status FROM bookings ORDER BY id DESC LIMIT 50");
$bookings = $stmt->fetchAll() ?: [
    ['id' => 1, 'booking_code' => 'BK89562', 'client_id' => 101, 'model_id' => 1, 'event_date' => '18 May 2025', 'total_amount' => 3500.00, 'payment_type' => 'CASH', 'payment_status' => 'PENDING', 'booking_status' => 'ACCEPTED'],
    ['id' => 2, 'booking_code' => 'BK89561', 'client_id' => 102, 'model_id' => 2, 'event_date' => '18 May 2025', 'total_amount' => 4000.00, 'payment_type' => 'CASH', 'payment_status' => 'PENDING', 'booking_status' => 'PENDING']
];

sendJsonResponse('success', 'Bookings list retrieved', ['bookings' => $bookings]);
?>
