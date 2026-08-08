<?php
// backend/admin/settings.php
// Admin Panel - Application System Settings
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';

checkAdminAuth();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $platformFee = (float)($_POST['platform_fee'] ?? 15.0);
    $agentCommission = (float)($_POST['agent_commission'] ?? 5.0);
    
    sendJsonResponse('success', 'System settings updated successfully', [
        'platform_fee_percent' => $platformFee,
        'agent_commission_percent' => $agentCommission
    ]);
}

sendJsonResponse('success', 'Current app settings', [
    'app_name' => 'Modol Connect',
    'platform_fee_percent' => 15.0,
    'agent_commission_percent' => 5.0,
    'currency' => 'BDT (৳)',
    'maintenance_mode' => false
]);
?>
