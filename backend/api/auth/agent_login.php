<?php
// backend/api/auth/agent_login.php
// REST API Endpoint - Cash Agent Login
require_once __DIR__ . '/../../config/database.php';
require_once __DIR__ . '/../../config/config.php';

header('Content-Type: application/json');
$input = json_decode(file_get_contents('php://input'), true);

$agentCode = trim($input['agent_code'] ?? '');
$password = $input['password'] ?? '';

if ($agentCode === 'AGENT001' && $password === 'agent123') {
    $_SESSION['user_role'] = 'CASH_AGENT';
    $_SESSION['user_id'] = 1;
    $_SESSION['user_name'] = 'Agent Sumon';

    sendJsonResponse('success', 'Cash Agent login successful', [
        'auth_token' => 'jwt_modol_agent_' . bin2hex(random_bytes(16)),
        'agent' => [
            'id' => 1,
            'code' => 'AGENT001',
            'name' => 'Agent Sumon',
            'role' => 'CASH_AGENT',
            'commission_rate' => '5.0%'
        ]
    ]);
} else {
    sendJsonResponse('error', 'Invalid Cash Agent credentials', [], 401);
}
?>
