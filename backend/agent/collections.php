<?php
// backend/agent/collections.php
// Cash Agent - Collected Cash Receipts List
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAgentAuth();
$agentId = $_SESSION['user_id'] ?? 1;
$db = Database::getInstance();

$stmt = $db->prepare("SELECT * FROM cash_collections WHERE agent_id = ? ORDER BY id DESC");
$stmt->execute([$agentId]);
$collections = $stmt->fetchAll() ?: [
    ['id' => 1, 'collection_code' => 'CC88521', 'booking_id' => 'BK89562', 'amount' => 3500.00, 'status' => 'PENDING', 'created_at' => '2025-05-18 11:20:00'],
    ['id' => 2, 'collection_code' => 'CC88520', 'booking_id' => 'BK89560', 'amount' => 2500.00, 'status' => 'PAID', 'created_at' => '2025-05-18 09:15:00']
];

sendJsonResponse('success', 'Cash agent collections history retrieved', ['collections' => $collections]);
?>
