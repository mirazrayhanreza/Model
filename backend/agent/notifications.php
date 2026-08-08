<?php
// backend/agent/notifications.php
// Cash Agent - Notifications List
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAgentAuth();

sendJsonResponse('success', 'Agent notifications list', [
    'notifications' => [
        ['id' => 1, 'title' => 'Cash Collection Verified', 'message' => 'Admin verified your collection CC88520 of ৳2,500. ৳125 commission added to wallet.', 'time' => '10 mins ago'],
        ['id' => 2, 'title' => 'New Collection Assignment', 'message' => 'Collect ৳3,500 cash from Client Al Amin (BK89562).', 'time' => '1 hour ago']
    ]
]);
?>
