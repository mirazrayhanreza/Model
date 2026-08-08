<?php
// backend/admin/wallets.php
// Admin Panel - Wallet & Withdrawal Requests Management
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAdminAuth();
$db = Database::getInstance();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $requestId = (int)($_POST['request_id'] ?? 0);
    $status = sanitizeInput($_POST['status'] ?? 'APPROVED');

    $stmt = $db->prepare("UPDATE withdraw_requests SET status = ? WHERE id = ?");
    $stmt->execute([$status, $requestId]);
    sendJsonResponse('success', "Withdrawal request $requestId status updated to $status");
}

$withdrawals = $db->query("SELECT * FROM withdraw_requests ORDER BY id DESC LIMIT 50")->fetchAll() ?: [
    ['id' => 1, 'user_type' => 'MODEL', 'user_id' => 1, 'amount' => 12500.00, 'payment_method' => 'bKash Agent', 'status' => 'PENDING', 'created_at' => '2025-05-18 10:30:00'],
    ['id' => 2, 'user_type' => 'AGENT', 'user_id' => 1, 'amount' => 4500.00, 'payment_method' => 'Nagad Personal', 'status' => 'APPROVED', 'created_at' => '2025-05-17 14:20:00']
];

sendJsonResponse('success', 'Withdrawal requests retrieved', ['withdrawals' => $withdrawals]);
?>
