<?php
// backend/agent/payment_history.php
// Cash Agent - Payment Logs & Deposits
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAgentAuth();

sendJsonResponse('success', 'Agent payment history retrieved', [
    'history' => [
        ['date' => '18 May 2025', 'type' => 'CASH_COLLECTED', 'amount' => 3500.00, 'commission_earned' => 175.00, 'status' => 'VERIFIED'],
        ['date' => '17 May 2025', 'type' => 'BANK_DEPOSIT', 'amount' => 12000.00, 'commission_earned' => 0.00, 'status' => 'COMPLETED']
    ]
]);
?>
