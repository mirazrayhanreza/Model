<?php
declare(strict_types=1);

// backend/api/b2b_orders.php
// REST API Endpoint - B2B & P2P Cash Agent Orders with Escrow Protection (PHP 8.2)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../enums/OrderStatus.php';

use App\Enums\OrderStatus;

$db = Database::getInstance();
$method = $_SERVER['REQUEST_METHOD'] ?? 'GET';
$input = json_decode(file_get_contents('php://input'), true) ?? $_POST;

if ($method === 'GET') {
    $orderId = filter_input(INPUT_GET, 'order_id', FILTER_DEFAULT);
    $userId = filter_input(INPUT_GET, 'user_id', FILTER_DEFAULT);
    $agentId = filter_input(INPUT_GET, 'agent_id', FILTER_DEFAULT);

    $sql = "SELECT * FROM b2b_orders WHERE 1=1";
    $params = [];

    if ($orderId) {
        $sql .= " AND order_id = ?";
        $params[] = $orderId;
    }
    if ($userId) {
        $sql .= " AND user_id = ?";
        $params[] = $userId;
    }
    if ($agentId) {
        $sql .= " AND agent_id = ?";
        $params[] = $agentId;
    }
    $sql .= " ORDER BY created_at DESC";

    $stmt = $db->prepare($sql);
    $stmt->execute($params);
    $orders = $stmt->fetchAll(PDO::FETCH_ASSOC);

    sendJsonResponse('success', 'B2B orders retrieved', ['orders' => $orders]);
}

if ($method === 'POST') {
    $action = $input['action'] ?? 'CREATE';

    match ($action) {
        'CREATE' => handleCreateOrder($db, $input),
        'SUBMIT_PROOF' => handleSubmitPaymentProof($db, $input),
        'RELEASE' => handleReleaseFunds($db, $input),
        'DISPUTE' => handleDisputeOrder($db, $input),
        default => sendJsonResponse('error', 'Invalid B2B order action', [], 400),
    };
}

function handleCreateOrder(PDO $db, array $input): void
{
    $orderId = 'B2B_' . strtoupper(bin2hex(random_bytes(4)));
    $userId = sanitizeInput($input['user_id'] ?? 'user_default');
    $userName = sanitizeInput($input['user_name'] ?? 'User');
    $agentId = sanitizeInput($input['agent_id'] ?? 'AGENT_001');
    $agentName = sanitizeInput($input['agent_name'] ?? 'Cash Agent');
    $country = sanitizeInput($input['country'] ?? 'Bangladesh');
    $type = sanitizeInput($input['type'] ?? 'DEPOSIT');
    $amount = (float)($input['amount'] ?? 0.0);
    $paymentMethod = sanitizeInput($input['payment_method'] ?? 'bKash');
    $accountNumber = sanitizeInput($input['agent_account_number'] ?? '');
    $accountHolder = sanitizeInput($input['agent_account_holder'] ?? '');

    if ($amount <= 0) {
        sendJsonResponse('error', 'Invalid amount specified', [], 400);
    }

    $stmt = $db->prepare("INSERT INTO b2b_orders (
        order_id, user_id, userName, agent_id, agent_name, country, type,
        amount, currency, payment_method, agent_account_number, agent_account_holder,
        status, created_at, updated_at
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'BDT', ?, ?, ?, ?, ?, ?)");

    $now = (int)(microtime(true) * 1000);
    $stmt->execute([
        $orderId, $userId, $userName, $agentId, $agentName, $country, $type,
        $amount, $paymentMethod, $accountNumber, $accountHolder,
        OrderStatus::PENDING_PAYMENT->value, $now, $now
    ]);

    sendJsonResponse('success', 'B2B Escrow Order generated successfully', [
        'order_id' => $orderId,
        'status' => OrderStatus::PENDING_PAYMENT->value
    ], 201);
}

function handleSubmitPaymentProof(PDO $db, array $input): void
{
    $orderId = sanitizeInput($input['order_id'] ?? '');
    $proofUrl = sanitizeInput($input['proof_screenshot_url'] ?? '');
    $txnRef = sanitizeInput($input['transaction_ref'] ?? '');

    $stmt = $db->prepare("UPDATE b2b_orders SET
        status = ?,
        proof_screenshot_url = ?,
        transaction_ref = ?,
        updated_at = ?
        WHERE order_id = ?");

    $stmt->execute([
        OrderStatus::PAYMENT_SUBMITTED->value,
        $proofUrl,
        $txnRef,
        (int)(microtime(true) * 1000),
        $orderId
    ]);

    sendJsonResponse('success', 'Payment proof submitted. Agent notified.', [
        'order_id' => $orderId,
        'status' => OrderStatus::PAYMENT_SUBMITTED->value
    ]);
}

function handleReleaseFunds(PDO $db, array $input): void
{
    $orderId = sanitizeInput($input['order_id'] ?? '');

    $stmt = $db->prepare("UPDATE b2b_orders SET
        status = ?,
        updated_at = ?
        WHERE order_id = ?");

    $stmt->execute([
        OrderStatus::RELEASED->value,
        (int)(microtime(true) * 1000),
        $orderId
    ]);

    sendJsonResponse('success', 'Escrow funds successfully released', [
        'order_id' => $orderId,
        'status' => OrderStatus::RELEASED->value
    ]);
}

function handleDisputeOrder(PDO $db, array $input): void
{
    $orderId = sanitizeInput($input['order_id'] ?? '');
    $reason = sanitizeInput($input['dispute_reason'] ?? 'Payment reference does not match statement');

    $stmt = $db->prepare("UPDATE b2b_orders SET
        status = ?,
        dispute_status = 'OPEN_DISPUTE',
        dispute_reason = ?,
        updated_at = ?
        WHERE order_id = ?");

    $stmt->execute([
        OrderStatus::DISPUTED->value,
        $reason,
        (int)(microtime(true) * 1000),
        $orderId
    ]);

    sendJsonResponse('success', 'Dispute ticket registered. Administrator reviewing.', [
        'order_id' => $orderId,
        'status' => OrderStatus::DISPUTED->value
    ]);
}
