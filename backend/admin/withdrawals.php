<?php
declare(strict_types=1);

// backend/admin/withdrawals.php
// Withdrawal Payout Requests (PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';
require_once __DIR__ . '/layout.php';

checkAdminAuth();

$msg = '';
$msgType = 'success';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $action = $_POST['action'] ?? '';
    $wdId = $_POST['wd_id'] ?? '';
    if ($action === 'payout') {
        $msg = "Withdrawal {$wdId} processed and payment sent.";
        $msgType = 'success';
    } elseif ($action === 'reject') {
        $msg = "Withdrawal {$wdId} rejected and balance restored to applicant.";
        $msgType = 'danger';
    }
}

$withdrawals = [
    [
        'id' => 'WD-50021',
        'applicant' => 'Jessica A. (Model)',
        'amount' => '৳35,000',
        'method' => 'bKash Personal',
        'account' => '01712345678',
        'status' => 'Pending',
        'badge' => 'badge-pending',
        'date' => '30 Sep 2025 09:30'
    ],
    [
        'id' => 'WD-50020',
        'applicant' => 'Hasan Ali (Agent)',
        'amount' => '৳80,000',
        'method' => 'City Bank Transfer',
        'account' => '2051234567890',
        'status' => 'Paid',
        'badge' => 'badge-approved',
        'date' => '29 Sep 2025 19:20'
    ]
];

renderAdminHeader('Withdrawals Management', 'withdrawals');
?>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: #0f172a;">Withdrawals & Payouts</h2>
        <p class="text-secondary mb-0">Authorize model earnings withdrawals and agent commission liquidations.</p>
    </div>
</div>

<?php if ($msg): ?>
    <div class="alert alert-<?= $msgType ?> alert-dismissible fade show" role="alert">
        <i class="bi bi-info-circle-fill me-2"></i><?= htmlspecialchars($msg) ?>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
<?php endif; ?>

<div class="card card-custom p-0 overflow-hidden mb-4">
    <div class="table-responsive">
        <table class="table table-custom align-middle">
            <thead>
                <tr>
                    <th class="ps-4">Withdrawal ID</th>
                    <th>Applicant</th>
                    <th>Amount</th>
                    <th>Payment Method</th>
                    <th>Account / Phone</th>
                    <th>Requested At</th>
                    <th>Status</th>
                    <th class="text-end pe-4">Actions</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($withdrawals as $w): ?>
                <tr>
                    <td class="ps-4 fw-bold text-danger">#<?= $w['id'] ?></td>
                    <td class="fw-semibold text-dark"><?= htmlspecialchars($w['applicant']) ?></td>
                    <td class="fw-bold text-dark fs-6"><?= $w['amount'] ?></td>
                    <td><span class="badge bg-light text-dark border"><?= $w['method'] ?></span></td>
                    <td class="font-monospace small"><?= htmlspecialchars($w['account']) ?></td>
                    <td class="text-muted small"><?= $w['date'] ?></td>
                    <td><span class="badge-status <?= $w['badge'] ?>"><?= $w['status'] ?></span></td>
                    <td class="text-end pe-4">
                        <?php if ($w['status'] === 'Pending'): ?>
                        <form method="POST" class="d-inline">
                            <input type="hidden" name="wd_id" value="<?= $w['id'] ?>">
                            <button type="submit" name="action" value="payout" class="btn btn-sm btn-success fw-semibold">Release Payout</button>
                            <button type="submit" name="action" value="reject" class="btn btn-sm btn-outline-danger">Reject</button>
                        </form>
                        <?php else: ?>
                            <span class="badge bg-light text-muted border">Disbursed</span>
                        <?php endif; ?>
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
