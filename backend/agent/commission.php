<?php
// backend/agent/commission.php
// Cash Agent - Commission Structure & History (5.0%)
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAgentAuth();

sendJsonResponse('success', 'Agent commission rate & history', [
    'commission_rate_percent' => 5.0,
    'recent_commissions' => [
        ['booking_id' => 'BK89562', 'collected_amount' => 3500.00, 'commission_5_percent' => 175.00, 'date' => '18 May 2025'],
        ['booking_id' => 'BK89560', 'collected_amount' => 2500.00, 'commission_5_percent' => 125.00, 'date' => '18 May 2025']
    ]
]);
?>
