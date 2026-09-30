<?php
declare(strict_types=1);

// backend/admin/deposits.php
// Deposit Request Verification & Approval (PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';
require_once __DIR__ . '/layout.php';

checkAdminAuth();

$msg = '';
$msgType = 'success';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $action = $_POST['action'] ?? '';
    $depId = $_POST['dep_id'] ?? '';
    if ($action === 'approve') {
        $msg = "Deposit {$depId} approved and credited to client wallet.";
        $msgType = 'success';
    } elseif ($action === 'reject') {
        $msg = "Deposit {$depId} rejected.";
        $msgType = 'danger';
    }
}

$deposits = [
    [
        'id' => 'DEP-10025',
        'user' => 'Rahim Uddin',
        'amount' => '৳10,000',
        'method' => 'bKash Personal',
        'agent' => 'Dhaka Agent #1024',
        'txn_id' => 'TXN987654321',
        'status' => 'Pending',
        'badge' => 'badge-pending',
        'date' => '30 Sep 2025 10:45',
        'proof' => 'https://images.unsplash.com/photo-1556742049-0a670f4a4591?w=400&h=250&fit=crop'
    ],
    [
        'id' => 'DEP-10024',
        'user' => 'Maria K.',
        'amount' => '$200.00',
        'method' => 'USDT (TRC20)',
        'agent' => 'Automated Node',
        'txn_id' => 'TXN554433221',
        'status' => 'Approved',
        'badge' => 'badge-approved',
        'date' => '29 Sep 2025 21:40',
        'proof' => 'https://images.unsplash.com/photo-1563013544-824ae1b704d3?w=400&h=250&fit=crop'
    ]
];

renderAdminHeader('Deposits Management', 'deposits');
?>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: #0f172a;">Deposits Verification</h2>
        <p class="text-secondary mb-0">Approve incoming cash-in, mobile money (bKash/Nagad), and crypto deposits.</p>
    </div>
</div>

<?php if ($msg): ?>
    <div class="alert alert-<?= $msgType ?> alert-dismissible fade show" role="alert">
        <i class="bi bi-check-circle-fill me-2"></i><?= htmlspecialchars($msg) ?>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
<?php endif; ?>

<div class="card card-custom p-0 overflow-hidden mb-4">
    <div class="table-responsive">
        <table class="table table-custom align-middle">
            <thead>
                <tr>
                    <th class="ps-4">Deposit ID</th>
                    <th>User</th>
                    <th>Amount</th>
                    <th>Method</th>
                    <th>Agent / Route</th>
                    <th>Transaction Ref</th>
                    <th>Date</th>
                    <th>Status</th>
                    <th class="text-end pe-4">Actions</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($deposits as $d): ?>
                <tr>
                    <td class="ps-4 fw-bold text-primary">#<?= $d['id'] ?></td>
                    <td class="fw-semibold text-dark"><?= htmlspecialchars($d['user']) ?></td>
                    <td class="fw-bold text-success fs-6"><?= $d['amount'] ?></td>
                    <td><span class="badge bg-light text-dark border"><?= $d['method'] ?></span></td>
                    <td class="text-secondary"><?= htmlspecialchars($d['agent']) ?></td>
                    <td class="font-monospace small"><?= htmlspecialchars($d['txn_id']) ?></td>
                    <td class="text-muted small"><?= $d['date'] ?></td>
                    <td><span class="badge-status <?= $d['badge'] ?>"><?= $d['status'] ?></span></td>
                    <td class="text-end pe-4">
                        <?php if ($d['status'] === 'Pending'): ?>
                        <form method="POST" class="d-inline">
                            <input type="hidden" name="dep_id" value="<?= $d['id'] ?>">
                            <button type="submit" name="action" value="approve" class="btn btn-sm btn-success fw-semibold">Approve</button>
                            <button type="submit" name="action" value="reject" class="btn btn-sm btn-outline-danger">Reject</button>
                        </form>
                        <?php else: ?>
                            <span class="badge bg-light text-muted border">Completed</span>
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
