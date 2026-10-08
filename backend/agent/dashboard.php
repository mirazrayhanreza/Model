<?php
declare(strict_types=1);

// backend/agent/dashboard.php
// Production Cash Agent Dashboard API (PHP 8.2)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAgentAuth();

$db = Database::getInstance();
$agentCode = $_SESSION['agent_code'] ?? '';

try {
    $agent = $db->prepare("SELECT * FROM cash_agents WHERE agent_code = ? LIMIT 1");
    $agent->execute([$agentCode]);
    $agentData = $agent->fetch(PDO::FETCH_ASSOC);

    if (!$agentData) {
        $agentData = [
            'id' => 0,
            'agent_code' => $agentCode,
            'name' => $_SESSION['agent_name'] ?? 'Cash Agent',
            'wallet_balance' => 0.00,
            'commission_rate' => 1.50,
            'status' => 'PENDING'
        ];
    }

    $agentId = (string)($agentData['id'] ?? '0');
    $agentCodeStr = (string)($agentData['agent_code'] ?? $agentCode);

    $stmtPending = $db->prepare("SELECT COUNT(*) FROM b2b_orders WHERE (agent_id = ? OR agent_id = ?) AND status='PAYMENT_SUBMITTED'");
    $stmtPending->execute([$agentId, $agentCodeStr]);
    $pendingOrders = (int)($stmtPending->fetchColumn() ?: 0);

    $stmtCompleted = $db->prepare("SELECT COUNT(*) FROM b2b_orders WHERE (agent_id = ? OR agent_id = ?) AND status='RELEASED'");
    $stmtCompleted->execute([$agentId, $agentCodeStr]);
    $totalCompleted = (int)($stmtCompleted->fetchColumn() ?: 0);

    $stmtEarnings = $db->prepare("SELECT COALESCE(SUM(amount * 0.015), 0.0) FROM b2b_orders WHERE (agent_id = ? OR agent_id = ?) AND status='RELEASED' AND DATE(created_at) = CURRENT_DATE");
    $stmtEarnings->execute([$agentId, $agentCodeStr]);
    $todaysEarnings = (float)($stmtEarnings->fetchColumn() ?: 0.0);

    sendJsonResponse('success', 'Agent dashboard data loaded', [
        'agent' => $agentData,
        'metrics' => [
            'pending_verifications' => $pendingOrders,
            'completed_orders' => $totalCompleted,
            'todays_earnings' => $todaysEarnings,
            'current_balance' => (float)($agentData['wallet_balance'] ?? 0.00)
        ]
    ]);
} catch (Throwable $e) {
    sendJsonResponse('error', 'Agent statistics failed: ' . $e->getMessage(), [], 500);
}
