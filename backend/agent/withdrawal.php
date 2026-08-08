<?php
// backend/agent/withdrawal.php
// Cash Agent - Request Withdrawal Endpoint
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAgentAuth();
$agentId = $_SESSION['user_id'] ?? 1;
$db = Database::getInstance();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $amount = (float)($_POST['amount'] ?? 0.0);
    $method = sanitizeInput($_POST['payment_method'] ?? 'bKash Agent');

    $stmt = $db->prepare("INSERT INTO withdraw_requests (user_type, user_id, amount, payment_method, status) VALUES ('AGENT', ?, ?, ?, 'PENDING')");
    $stmt->execute([$agentId, $amount, $method]);

    sendJsonResponse('success', 'Withdrawal request submitted for Admin approval', ['requested_amount' => $amount, 'method' => $method]);
}

sendJsonResponse('success', 'Withdrawal page ready');
?>
