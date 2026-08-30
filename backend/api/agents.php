<?php
declare(strict_types=1);

// backend/api/agents.php
// REST API Endpoint - Payment Agents Directory & Management (PHP 8.2)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';

$db = Database::getInstance();
$method = $_SERVER['REQUEST_METHOD'] ?? 'GET';

if ($method === 'GET') {
    $country = filter_input(INPUT_GET, 'country', FILTER_DEFAULT) ?? 'Bangladesh';
    $search = filter_input(INPUT_GET, 'q', FILTER_DEFAULT) ?? '';

    $sql = "SELECT id, name, agent_code, country, phone, payment_method, account_number, account_holder, commission_rate, min_limit, max_limit, available_balance, allowed_methods, supports_deposit, supports_withdraw, verification_status, is_online, rating FROM payment_agents WHERE 1=1";
    $params = [];

    if (!empty($country) && $country !== 'All') {
        $sql .= " AND country = ?";
        $params[] = $country;
    }
    if (!empty($search)) {
        $sql .= " AND (name LIKE ? OR agent_code LIKE ? OR allowed_methods LIKE ?)";
        $wild = "%{$search}%";
        $params[] = $wild;
        $params[] = $wild;
        $params[] = $wild;
    }

    $sql .= " ORDER BY is_online DESC, rating DESC";
    $stmt = $db->prepare($sql);
    $stmt->execute($params);
    $agents = $stmt->fetchAll(PDO::FETCH_ASSOC);

    sendJsonResponse('success', 'Payment agents list retrieved', [
        'total' => count($agents),
        'agents' => $agents
    ]);
}

sendJsonResponse('error', 'Method Not Allowed', [], 405);
