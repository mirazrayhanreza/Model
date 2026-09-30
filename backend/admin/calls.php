<?php
declare(strict_types=1);

// backend/admin/calls.php
// Call Logs & WebRTC Audio/Video Sessions (PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';
require_once __DIR__ . '/layout.php';

checkAdminAuth();

$calls = [
    ['id' => 'CALL-8821', 'caller' => 'Rahim Uddin', 'receiver' => 'Jessica A.', 'type' => 'Video Call (WebRTC)', 'duration' => '14m 20s', 'quality' => 'HD 1080p', 'status' => 'Completed', 'date' => '30 Sep 2025 11:00'],
    ['id' => 'CALL-8820', 'caller' => 'Karim Khan', 'receiver' => 'Maria K.', 'type' => 'Audio Call', 'duration' => '05m 12s', 'quality' => 'Opus 48kHz', 'status' => 'Completed', 'date' => '29 Sep 2025 18:30'],
    ['id' => 'CALL-8819', 'caller' => 'Hasan Ali', 'receiver' => 'Agent 004', 'type' => 'Voice Call', 'duration' => '01m 45s', 'quality' => 'Standard', 'status' => 'Missed', 'date' => '29 Sep 2025 09:10']
];

renderAdminHeader('Call Management', 'calls');
?>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: #0f172a;">Call Management</h2>
        <p class="text-secondary mb-0">WebRTC audio and video consultation sessions, telemetry, and quality logs.</p>
    </div>
</div>

<div class="card card-custom p-0 overflow-hidden mb-4">
    <div class="table-responsive">
        <table class="table table-custom align-middle">
            <thead>
                <tr>
                    <th class="ps-4">Call ID</th>
                    <th>Caller</th>
                    <th>Recipient</th>
                    <th>Session Type</th>
                    <th>Duration</th>
                    <th>Quality</th>
                    <th>Status</th>
                    <th>Date</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($calls as $cl): ?>
                <tr>
                    <td class="ps-4 fw-bold text-muted"><?= $cl['id'] ?></td>
                    <td class="fw-semibold text-dark"><?= htmlspecialchars($cl['caller']) ?></td>
                    <td class="fw-semibold text-dark"><?= htmlspecialchars($cl['receiver']) ?></td>
                    <td>
                        <i class="bi <?= str_contains($cl['type'], 'Video') ? 'bi-camera-video text-primary' : 'bi-telephone text-success' ?> me-1"></i>
                        <?= htmlspecialchars($cl['type']) ?>
                    </td>
                    <td class="text-secondary"><?= htmlspecialchars($cl['duration']) ?></td>
                    <td><span class="badge bg-light text-dark border"><?= htmlspecialchars($cl['quality']) ?></span></td>
                    <td>
                        <span class="badge-status <?= $cl['status'] === 'Completed' ? 'badge-approved' : 'badge-disputed' ?>">
                            <?= htmlspecialchars($cl['status']) ?>
                        </span>
                    </td>
                    <td class="text-muted small"><?= htmlspecialchars($cl['date']) ?></td>
                </tr>
                <?php endforeach; ?>
            </tbody>
        </table>
    </div>
</div>

<?php
renderAdminFooter();
?>
