<?php
declare(strict_types=1);

// backend/admin/chats.php
// Chat Monitoring & Moderation (PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';
require_once __DIR__ . '/layout.php';

checkAdminAuth();

$chats = [
    ['id' => 'CH-101', 'user' => 'Rahim Uddin', 'model' => 'Jessica A.', 'last_msg' => 'I have reached the location for the shoot.', 'time' => '10 mins ago', 'unread' => 0],
    ['id' => 'CH-102', 'user' => 'Karim Khan', 'model' => 'Maria K.', 'last_msg' => 'Looking forward to the fashion session tomorrow.', 'time' => '45 mins ago', 'unread' => 2],
    ['id' => 'CH-103', 'user' => 'Faisal Al-Mansoor', 'model' => 'Sophia L.', 'last_msg' => 'Booking contract confirmed with escrow.', 'time' => '3 hours ago', 'unread' => 0]
];

renderAdminHeader('Chat Management', 'chats');
?>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: #0f172a;">Chat Management</h2>
        <p class="text-secondary mb-0">Monitor communication threads between clients and models for safety and policy compliance.</p>
    </div>
</div>

<div class="card card-custom p-0 overflow-hidden mb-4">
    <div class="table-responsive">
        <table class="table table-custom align-middle">
            <thead>
                <tr>
                    <th class="ps-4">Thread ID</th>
                    <th>Client (User)</th>
                    <th>Model</th>
                    <th>Last Message</th>
                    <th>Timestamp</th>
                    <th class="text-end pe-4">Action</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($chats as $c): ?>
                <tr>
                    <td class="ps-4 fw-bold text-muted"><?= $c['id'] ?></td>
                    <td class="fw-semibold text-dark"><?= htmlspecialchars($c['user']) ?></td>
                    <td class="fw-semibold text-danger"><i class="bi bi-heart me-1"></i><?= htmlspecialchars($c['model']) ?></td>
                    <td class="text-secondary"><?= htmlspecialchars($c['last_msg']) ?></td>
                    <td class="text-muted small"><?= $c['time'] ?></td>
                    <td class="text-end pe-4">
                        <button class="btn btn-sm btn-outline-primary fw-semibold">Inspect Transcript</button>
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
