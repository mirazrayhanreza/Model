<?php
declare(strict_types=1);

// backend/api/agents.php
// REST API Endpoint - Live B2B Cash Agents Directory & Registration (PHP 8.2)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';

$db = Database::getInstance();
$method = $_SERVER['REQUEST_METHOD'] ?? 'GET';

if ($method === 'GET') {
    $country = filter_input(INPUT_GET, 'country', FILTER_DEFAULT) ?? '';
    $search = filter_input(INPUT_GET, 'q', FILTER_DEFAULT) ?? '';

    $sql = "SELECT id, name, agent_code, country, city, phone, email, currency, payment_methods, commission_rate, buy_rate, sell_rate, min_limit, max_limit, available_balance, total_orders, completion_rate, avg_release_time, is_online, is_verified, rating, status FROM cash_agents WHERE status = 'ACTIVE'";
    $params = [];

    if (!empty($country) && $country !== 'All') {
        $sql .= " AND (country = ? OR currency = ?)";
        $params[] = $country;
        $params[] = $country;
    }
    if (!empty($search)) {
        $sql .= " AND (name LIKE ? OR agent_code LIKE ? OR payment_methods LIKE ? OR city LIKE ?)";
        $wild = "%{$search}%";
        $params[] = $wild;
        $params[] = $wild;
        $params[] = $wild;
        $params[] = $wild;
    }

    $sql .= " ORDER BY is_online DESC, rating DESC, id DESC";
    $stmt = $db->prepare($sql);
    $stmt->execute($params);
    $agents = $stmt->fetchAll(PDO::FETCH_ASSOC);

    sendJsonResponse('success', 'Live cash agents retrieved', [
        'total' => count($agents),
        'agents' => $agents
    ]);
}

if ($method === 'POST') {
    $rawInput = file_get_contents('php://input');
    $body = json_decode($rawInput, true) ?: $_POST;

    $agentCode = trim((string)($body['agent_code'] ?? ''));
    $name = trim((string)($body['name'] ?? ''));
    $phone = trim((string)($body['phone'] ?? ''));
    $email = trim((string)($body['email'] ?? ''));
    $password = (string)($body['password'] ?? '');
    $country = trim((string)($body['country'] ?? 'Bangladesh'));
    $city = trim((string)($body['city'] ?? 'Dhaka'));
    $currency = trim((string)($body['currency'] ?? 'BDT'));
    $paymentMethods = trim((string)($body['payment_methods'] ?? 'bKash, Nagad, Bank Transfer'));
    $commissionRate = (float)($body['commission_rate'] ?? 1.50);
    $buyRate = (float)($body['buy_rate'] ?? 122.50);
    $sellRate = (float)($body['sell_rate'] ?? 120.80);
    $minLimit = (float)($body['min_limit'] ?? 500.00);
    $maxLimit = (float)($body['max_limit'] ?? 100000.00);
    $availableBalance = (float)($body['available_balance'] ?? 0.00);

    if (empty($name) || empty($phone)) {
        sendJsonResponse('error', 'Agent name and phone number are required', [], 400);
    }

    if (empty($agentCode)) {
        $agentCode = 'AG' . random_int(1000, 9999);
    }

    try {
        // Check if agent exists
        $check = $db->prepare("SELECT id FROM cash_agents WHERE phone = ? OR agent_code = ? LIMIT 1");
        $check->execute([$phone, $agentCode]);
        $existing = $check->fetch(PDO::FETCH_ASSOC);

        if ($existing) {
            $update = $db->prepare("
                UPDATE cash_agents SET 
                    name = ?, country = ?, city = ?, currency = ?, payment_methods = ?,
                    commission_rate = ?, buy_rate = ?, sell_rate = ?, min_limit = ?, max_limit = ?,
                    status = 'ACTIVE'
                WHERE id = ?
            ");
            $update->execute([
                $name, $country, $city, $currency, $paymentMethods,
                $commissionRate, $buyRate, $sellRate, $minLimit, $maxLimit,
                $existing['id']
            ]);
            sendJsonResponse('success', 'Cash Agent profile updated', ['agent_code' => $agentCode, 'id' => $existing['id']]);
        } else {
            $hashedPass = !empty($password) ? password_hash($password, PASSWORD_BCRYPT) : password_hash('Agent@123', PASSWORD_BCRYPT);
            $insert = $db->prepare("
                INSERT INTO cash_agents (
                    agent_code, name, phone, email, password, country, city, currency,
                    payment_methods, commission_rate, buy_rate, sell_rate, min_limit, max_limit,
                    available_balance, total_orders, completion_rate, avg_release_time, is_online, is_verified, rating, status
                ) VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?, ?,
                    ?, 0, '100%', '2.0 min', 1, 1, 5.0, 'ACTIVE'
                )
            ");
            $insert->execute([
                $agentCode, $name, $phone, $email, $hashedPass, $country, $city, $currency,
                $paymentMethods, $commissionRate, $buyRate, $sellRate, $minLimit, $maxLimit,
                $availableBalance
            ]);
            sendJsonResponse('success', 'Live Cash Agent registered successfully', ['agent_code' => $agentCode]);
        }
    } catch (Throwable $e) {
        sendJsonResponse('error', 'Database error: ' . $e->getMessage(), [], 500);
    }
}

sendJsonResponse('error', 'Method Not Allowed', [], 405);
