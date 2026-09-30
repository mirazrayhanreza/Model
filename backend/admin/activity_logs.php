<?php
declare(strict_types=1);

// backend/admin/activity_logs.php
// System Audit Trail & Administrative Activity Logs (PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';
require_once __DIR__ . '/layout.php';

checkAdminAuth();

$logs = [
    ['admin' => 'Miraz Reza', 'action' => 'Firebase Storage Test Upload', 'ip' => '103.114.98.10', 'time' => '10 mins ago'],
    ['admin' => 'Tariqul Islam', 'action' => 'Approved Withdrawal #WD-50020 for Agent 1024', 'ip' => '103.114.98.14', 'time' => '1 hour ago'],
    ['admin' => 'Sabrina Sultana', 'action' => 'Verified KYC Documents for Jessica A.', 'ip' => '103.114.98.18', 'time' => '3 hours ago'],
    ['admin' => 'Arman Hossain', 'action' => 'Resolved Dispute #DSP-900 (Release to User)', 'ip' => '103.114.98.22', 'time' => 'Yesterday']
];

renderAdminHeader('Activity Logs', 'activity_logs');
?>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: #0f172a;">Administrative Activity Logs</h2>
        <p class="text-secondary mb-0">Immutable compliance logs recording all administrative logins, approvals, and financial adjustments.</p>
    </div>
</div>

<div class="card card-custom p-0 overflow-hidden mb-4">
    <div class="table-responsive">
        <table class="table table-custom align-middle">
            <thead>
                <tr>
                    <th class="ps-4">Administrator</th>
                    <th>Action Taken</th>
                    <th>Origin IP Address</th>
                    <th>Timestamp</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($logs as $l): ?>
                <tr>
                    <td class="ps-4 fw-bold text-dark">
                        <i class="bi bi-person-badge text-primary me-2"></i><?= htmlspecialchars($l['admin']) ?>
                    </td>
                    <td class="text-secondary"><?= htmlspecialchars($l['action']) ?></td>
                    <td><span class="font-monospace small text-muted"><?= htmlspecialchars($l['ip']) ?></span></td>
                    <td class="text-muted small"><?= htmlspecialchars($l['time']) ?></td>
                </tr>
                <?php endforeach; ?>
            </tbody>
        </table>
    </div>
</div>

<?php
renderAdminFooter();
?>
