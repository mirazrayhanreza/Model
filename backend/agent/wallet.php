<?php
// backend/agent/wallet.php
// Cash Agent - Wallet Balance & Overview
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAgentAuth();

sendJsonResponse('success', 'Agent wallet info retrieved', [
    'agent_code' => 'AGENT001',
    'wallet_balance_bdt' => 12500.00,
    'total_commission_earned_bdt' => 2450.00,
    'pending_payouts_bdt' => 850.00
]);
?>
