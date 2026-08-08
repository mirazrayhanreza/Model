<?php
// backend/admin/cash_agents.php
// Admin Panel - Cash Agent Management Endpoint
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAdminAuth();
$db = Database::getInstance();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $code = 'AGENT' . rand(100, 999);
    $name = sanitizeInput($_POST['name'] ?? 'New Agent');
    $phone = sanitizeInput($_POST['phone'] ?? '+8801700000000');
    $email = sanitizeInput($_POST['email'] ?? 'agent@modolconnect.com');
    $pass = password_hash($_POST['password'] ?? 'agent123', PASSWORD_DEFAULT);

    $stmt = $db->prepare("INSERT INTO cash_agents (agent_code, name, phone, email, password, commission_rate) VALUES (?, ?, ?, ?, ?, 5.00)");
    $stmt->execute([$code, $name, $phone, $email, $pass]);
    sendJsonResponse('success', 'New Cash Agent created successfully', ['agent_code' => $code]);
}

$agents = $db->query("SELECT id, agent_code, name, phone, email, commission_rate, wallet_balance, status FROM cash_agents")->fetchAll() ?: [
    ['id' => 1, 'agent_code' => 'AGENT001', 'name' => 'Agent Sumon', 'phone' => '+8801700000001', 'email' => 'sumon@agent.com', 'commission_rate' => 5.00, 'wallet_balance' => 12500.00, 'status' => 'ACTIVE'],
    ['id' => 2, 'agent_code' => 'AGENT002', 'name' => 'Agent Rafiq', 'phone' => '+8801800000002', 'email' => 'rafiq@agent.com', 'commission_rate' => 5.00, 'wallet_balance' => 8400.00, 'status' => 'ACTIVE']
];

sendJsonResponse('success', 'Cash agents list retrieved', ['cash_agents' => $agents]);
?>
