<?php
// backend/agent/profile.php
// Cash Agent - Profile Management
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAgentAuth();

sendJsonResponse('success', 'Agent profile retrieved', [
    'name' => 'Agent Sumon',
    'agent_code' => 'AGENT001',
    'phone' => '+8801700000001',
    'email' => 'sumon@agent.com',
    'assigned_area' => 'Dhanmondi & Mirpur, Dhaka',
    'status' => 'ACTIVE'
]);
?>
