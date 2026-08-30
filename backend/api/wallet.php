<?php
declare(strict_types=1);

// backend/api/wallet.php
// REST API Endpoint - Wallet, Deposits & Withdrawals (PHP 8.2)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../enums/DepositStatus.php';

use App\Enums\DepositStatus;

$db = Database::getInstance();
$method = $_SERVER['REQUEST_METHOD'] ?? 'GET';
$input = json_decode(file_get_contents('php://input'), true) ?? $_POST;

if ($method === 'GET') {
    $userId = filter_input(INPUT_GET, 'user_id', FILTER_DEFAULT);
    $type = filter_input(INPUT_GET, 'type', FILTER_DEFAULT) ?? 'ALL';

    if (empty($userId)) {
        sendJsonResponse('error', 'User ID required', [], 400);
    }

    $deposits = $db->prepare("SELECT * FROM deposit_requests WHERE user_id = ? ORDER BY timestamp DESC");
    $deposits->execute([$userId]);

    $withdrawals = $db->prepare("SELECT * FROM withdrawal_requests WHERE applicant_id = ? ORDER BY timestamp DESC");
    $withdrawals->execute([$userId]);

    $ledger = $db->prepare("SELECT * FROM ledger_entries WHERE user_id = ? ORDER BY timestamp DESC");
    $ledger->execute([$userId]);

    sendJsonResponse('success', 'Wallet data retrieved', [
        'deposit_requests' => $deposits->fetchAll(PDO::FETCH_ASSOC),
        'withdrawal_requests' => $withdrawals->fetchAll(PDO::FETCH_ASSOC),
        'ledger_history' => $ledger->fetchAll(PDO::FETCH_ASSOC),
    ]);
}

if ($method === 'POST') {
    $action = $input['action'] ?? 'DEPOSIT_REQUEST';

    if ($action === 'DEPOSIT_REQUEST') {
        $id = 'DEP-' . rand(10000, 99999);
        $userId = sanitizeInput($input['user_id'] ?? '');
        $userName = sanitizeInput($input['user_name'] ?? '');
        $agentId = sanitizeInput($input['agent_id'] ?? '');
        $agentName = sanitizeInput($input['agent_name'] ?? '');
        $country = sanitizeInput($input['country'] ?? 'Bangladesh');
        $paymentMethod = sanitizeInput($input['payment_method'] ?? 'bKash');
        $amount = (float)($input['amount'] ?? 0.0);
        $txnId = sanitizeInput($input['transaction_id'] ?? '');
        $proofUrl = sanitizeInput($input['proof_screenshot_url'] ?? '');
        $userNote = sanitizeInput($input['user_note'] ?? '');

        $stmt = $db->prepare("INSERT INTO deposit_requests (
            id, user_id, user_name, agent_id, agent_name, country, payment_method,
            amount, currency, transaction_id, proof_screenshot_url, user_note, status, timestamp
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'BDT', ?, ?, ?, ?, ?)");

        $stmt->execute([
            $id, $userId, $userName, $agentId, $agentName, $country, $paymentMethod,
            $amount, $txnId, $proofUrl, $userNote, DepositStatus::PENDING->value,
            (int)(microtime(true) * 1000)
        ]);

        sendJsonResponse('success', 'Deposit verification request submitted', [
            'request_id' => $id,
            'status' => DepositStatus::PENDING->value
        ], 201);
    }

    if ($action === 'WITHDRAWAL_REQUEST') {
        $id = 'WD-' . rand(50000, 99999);
        $applicantId = sanitizeInput($input['applicant_id'] ?? '');
        $applicantName = sanitizeInput($input['applicant_name'] ?? '');
        $applicantRole = sanitizeInput($input['applicant_role'] ?? 'USER');
        $amount = (float)($input['amount'] ?? 0.0);
        $method = sanitizeInput($input['payment_method'] ?? 'bKash');
        $accountNum = sanitizeInput($input['account_number'] ?? '');
        $accountHolder = sanitizeInput($input['account_holder'] ?? '');

        $stmt = $db->prepare("INSERT INTO withdrawal_requests (
            id, applicant_id, applicant_name, applicant_role, amount, currency,
            payment_method, account_number, account_holder, status, timestamp
        ) VALUES (?, ?, ?, ?, ?, 'BDT', ?, ?, ?, 'PENDING', ?)");

        $stmt->execute([
            $id, $applicantId, $applicantName, $applicantRole, $amount,
            $method, $accountNum, $accountHolder, (int)(microtime(true) * 1000)
        ]);

        sendJsonResponse('success', 'Withdrawal request queued for processing', [
            'request_id' => $id,
            'status' => 'PENDING'
        ], 201);
    }

    sendJsonResponse('error', 'Unknown wallet action', [], 400);
}
