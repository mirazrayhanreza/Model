<?php
declare(strict_types=1);

// backend/index.php
// Central API Gateway & Environment Router (PHP 8.2 Production)

require_once __DIR__ . '/config/config.php';

sendJsonResponse('success', 'Modol Connect PHP 8.2 API Gateway Operational', [
    'runtime' => [
        'php_version' => PHP_VERSION,
        'target_version' => '8.2+',
        'strict_types' => true,
        'status' => 'HEALTHY'
    ],
    'service' => [
        'name' => Config::APP_NAME,
        'version' => Config::APP_VERSION,
        'base_url' => Config::BASE_URL
    ],
    'endpoints' => [
        'auth_admin' => Config::BASE_URL . 'api/auth/admin_login.php',
        'auth_agent' => Config::BASE_URL . 'api/auth/agent_login.php',
        'models' => Config::BASE_URL . 'api/models.php',
        'bookings' => Config::BASE_URL . 'api/bookings.php',
        'b2b_orders' => Config::BASE_URL . 'api/b2b_orders.php',
        'payment_agents' => Config::BASE_URL . 'api/agents.php',
        'wallet' => Config::BASE_URL . 'api/wallet.php',
        'chat' => Config::BASE_URL . 'api/chat.php',
        'admin_dashboard' => Config::BASE_URL . 'admin/dashboard.php',
        'agent_dashboard' => Config::BASE_URL . 'agent/dashboard.php',
    ]
]);
