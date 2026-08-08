<?php
// backend/index.php
// Central API & Portal Router

require_once __DIR__ . '/config/config.php';

sendJsonResponse('success', 'Modol Connect Backend Service API operational.', [
    'version' => '2.1.0',
    'environment' => 'Production PHP 8.2',
    'modules' => [
        'Admin Portal' => BASE_URL . 'admin/dashboard.php',
        'Cash Agent Portal' => BASE_URL . 'agent/dashboard.php',
        'API Specs' => BASE_URL . 'api/'
    ]
]);
?>
