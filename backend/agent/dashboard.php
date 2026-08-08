<?php
// backend/agent/dashboard.php
// Cash Agent Dashboard API & Controller

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAgentAuth();

$agentId = $_SESSION['user_id'] ?? 1;
$db = Database::getInstance();

$stmt = $db->prepare("SELECT * FROM cash_collections WHERE agent_id = ? ORDER BY id DESC LIMIT 20");
$stmt->execute([$agentId]);
$collections = $stmt->fetchAll() ?: [
    ['id' => 'CC88521', 'booking_id' => 'BK89562', 'amount' => 3500.00, 'status' => 'PENDING', 'date' => '18 May 2025'],
    ['id' => 'CC88520', 'booking_id' => 'BK89560', 'amount' => 2500.00, 'status' => 'PAID', 'date' => '18 May 2025']
];

sendJsonResponse('success', 'Agent dashboard data retrieved', [
    'agent_info' => [
        'name' => $_SESSION['user_name'] ?? 'Agent Sumon',
        'code' => 'AGENT001',
        'commission_rate' => 5.0
    ],
    'collections' => $collections
]);
?>
