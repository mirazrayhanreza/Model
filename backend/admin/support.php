<?php
declare(strict_types=1);

// backend/admin/support.php
// Customer Support Tickets & Helpdesk (PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';
require_once __DIR__ . '/layout.php';

checkAdminAuth();

$tickets = [
    ['id' => 'TCK-201', 'subject' => 'Need assistance linking bKash merchant account', 'user' => 'Agent 012', 'priority' => 'High', 'badge' => 'badge-disputed', 'status' => 'Open', 'date' => 'Today, 10:00 AM'],
    ['id' => 'TCK-202', 'subject' => 'Update KYC ID document request', 'user' => 'Jessica A.', 'priority' => 'Medium', 'badge' => 'badge-pending', 'status' => 'In Progress', 'date' => 'Yesterday'],
    ['id' => 'TCK-203', 'subject' => 'Escrow payout status inquiry', 'user' => 'Karim Khan', 'priority' => 'Low', 'badge' => 'badge-approved', 'status' => 'Resolved', 'date' => '28 Sep 2025']
];

renderAdminHeader('Support Tickets', 'support');
?>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: #0f172a;">Support Helpdesk</h2>
        <p class="text-secondary mb-0">Helpdesk tickets from users, models, and cash agents.</p>
    </div>
</div>

<div class="card card-custom p-0 overflow-hidden mb-4">
    <div class="table-responsive">
        <table class="table table-custom align-middle">
            <thead>
                <tr>
                    <th class="ps-4">Ticket</th>
                    <th>Subject</th>
                    <th>Requester</th>
                    <th>Priority</th>
                    <th>Created</th>
                    <th>Status</th>
                    <th class="text-end pe-4">Action</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($tickets as $tk): ?>
                <tr>
                    <td class="ps-4 fw-bold text-primary">#<?= $tk['id'] ?></td>
                    <td class="fw-semibold text-dark"><?= htmlspecialchars($tk['subject']) ?></td>
                    <td class="text-secondary"><?= htmlspecialchars($tk['user']) ?></td>
                    <td><span class="badge-status <?= $tk['badge'] ?>"><?= $tk['priority'] ?></span></td>
                    <td class="text-muted small"><?= $tk['date'] ?></td>
                    <td><span class="badge bg-light text-dark border"><?= $tk['status'] ?></span></td>
                    <td class="text-end pe-4">
                        <button class="btn btn-sm btn-outline-primary fw-semibold">Open Ticket</button>
                    </td>
                </tr>
                <?php endforeach; ?>
            </tbody>
        </table>
    </div>
</div>

<?php
renderAdminFooter();
?>
