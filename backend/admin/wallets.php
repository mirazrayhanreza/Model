<?php
declare(strict_types=1);

// backend/admin/wallets.php
// Finance, Wallets & Transaction Ledger (PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';
require_once __DIR__ . '/layout.php';

checkAdminAuth();

$txns = [
    [
        'id' => 'TXN-90812',
        'user' => 'Rahim Uddin',
        'type' => 'Deposit',
        'type_color' => 'text-success',
        'amount' => '৳10,000',
        'fee' => '৳150',
        'method' => 'bKash (MFS)',
        'status' => 'Approved',
        'badge' => 'badge-approved',
        'date' => '30 Sep 2025 10:45'
    ],
    [
        'id' => 'TXN-90811',
        'user' => 'Jessica A.',
        'type' => 'Withdrawal',
        'type_color' => 'text-danger',
        'amount' => '৳25,000',
        'fee' => '৳250',
        'method' => 'Bank Wire (City Bank)',
        'status' => 'Pending',
        'badge' => 'badge-pending',
        'date' => '30 Sep 2025 09:30'
    ],
    [
        'id' => 'TXN-90810',
        'user' => 'Karim Khan',
        'type' => 'Booking Escrow',
        'type_color' => 'text-primary',
        'amount' => '৳15,000',
        'fee' => '৳2,250',
        'method' => 'Internal Wallet',
        'status' => 'Completed',
        'badge' => 'badge-completed',
        'date' => '29 Sep 2025 22:15'
    ],
    [
        'id' => 'TXN-90809',
        'user' => 'Faisal Al-Mansoor',
        'type' => 'Deposit',
        'type_color' => 'text-success',
        'amount' => '$500.00',
        'fee' => '$5.00',
        'method' => 'USDT (TRC20)',
        'status' => 'Approved',
        'badge' => 'badge-approved',
        'date' => '29 Sep 2025 21:40'
    ]
];

renderAdminHeader('Wallet & Finance', 'wallets');
?>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: #0f172a;">Wallet & Financial Ledger</h2>
        <p class="text-secondary mb-0">Platform liquidity, held escrow contracts, deposits, withdrawals, and fee commissions.</p>
    </div>
    <div class="d-flex gap-2">
        <a href="deposits.php" class="btn btn-outline-success fw-semibold">
            <i class="bi bi-box-arrow-in-down-right me-1"></i> Deposits
        </a>
        <a href="withdrawals.php" class="btn btn-outline-danger fw-semibold">
            <i class="bi bi-box-arrow-up-right me-1"></i> Withdrawals
        </a>
    </div>
</div>

<!-- 4 Top Finance Cards -->
<div class="row g-3 mb-4">
    <div class="col-xl-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Total Wallet Balance</div>
                <div class="fw-bold text-dark fs-4">৳8,520,000</div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-blue">
                <i class="bi bi-wallet2"></i>
            </div>
        </div>
    </div>
    <div class="col-xl-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Held in Escrow</div>
                <div class="fw-bold text-primary fs-4">৳1,220,000</div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-purple">
                <i class="bi bi-lock-fill"></i>
            </div>
        </div>
    </div>
    <div class="col-xl-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Today's Deposit</div>
                <div class="fw-bold text-success fs-4">৳450,000</div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-green">
                <i class="bi bi-arrow-down-left-circle-fill"></i>
            </div>
        </div>
    </div>
    <div class="col-xl-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Today's Withdrawal</div>
                <div class="fw-bold text-danger fs-4">৳280,000</div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-red">
                <i class="bi bi-arrow-up-right-circle-fill"></i>
            </div>
        </div>
    </div>
</div>

<!-- Transaction Ledger Card -->
<div class="card card-custom p-0 overflow-hidden mb-4">
    <div class="p-3 border-bottom d-flex justify-content-between align-items-center flex-wrap gap-2">
        <div class="d-flex align-items-center gap-2 flex-wrap">
            <span class="small fw-bold text-muted text-uppercase">Filters:</span>
            <button class="btn btn-sm btn-outline-secondary active">All</button>
            <button class="btn btn-sm btn-outline-secondary">Deposit</button>
            <button class="btn btn-sm btn-outline-secondary">Withdraw</button>
            <button class="btn btn-sm btn-outline-secondary">Booking</button>
            <button class="btn btn-sm btn-outline-secondary">Refund</button>
            <button class="btn btn-sm btn-outline-secondary">Commission</button>
            <button class="btn btn-sm btn-outline-secondary">Agent</button>
            <button class="btn btn-sm btn-outline-secondary">Escrow</button>
        </div>
        <div class="input-group input-group-sm" style="width: 220px;">
            <input type="text" class="form-control" placeholder="Search Txn ID...">
            <button class="btn btn-outline-secondary"><i class="bi bi-search"></i></button>
        </div>
    </div>

    <div class="table-responsive">
        <table class="table table-custom align-middle">
            <thead>
                <tr>
                    <th class="ps-4">Transaction ID</th>
                    <th>User</th>
                    <th>Type</th>
                    <th>Gross Amount</th>
                    <th>Platform Fee</th>
                    <th>Payment Method</th>
                    <th>Status</th>
                    <th>Date</th>
                    <th class="text-end pe-4">Action</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($txns as $t): ?>
                <tr>
                    <td class="ps-4 fw-bold text-muted"><?= $t['id'] ?></td>
                    <td class="fw-semibold text-dark"><?= htmlspecialchars($t['user']) ?></td>
                    <td><span class="fw-bold <?= $t['type_color'] ?>"><?= $t['type'] ?></span></td>
                    <td class="fw-bold text-dark fs-6"><?= $t['amount'] ?></td>
                    <td class="text-secondary"><?= $t['fee'] ?></td>
                    <td><span class="badge bg-light text-dark border"><?= $t['method'] ?></span></td>
                    <td><span class="badge-status <?= $t['badge'] ?>"><?= $t['status'] ?></span></td>
                    <td class="text-muted small"><?= $t['date'] ?></td>
                    <td class="text-end pe-4">
                        <button class="btn btn-sm btn-outline-secondary">Receipt</button>
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
