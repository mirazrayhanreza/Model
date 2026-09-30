<?php
declare(strict_types=1);

// backend/admin/reports.php
// Business Intelligence, Reports & Export Center (PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';
require_once __DIR__ . '/layout.php';

checkAdminAuth();

$reportCards = [
    ['title' => 'Revenue & Income Report', 'desc' => 'Net platform commissions, booking fee margins, and agent revenue.', 'icon' => 'bi-cash-coin', 'color' => 'text-success'],
    ['title' => 'Bookings & Escrow Report', 'desc' => 'Booking volume, fulfillment velocity, cancellations, and disputes.', 'icon' => 'bi-calendar2-range', 'color' => 'text-primary'],
    ['title' => 'User & Model Growth', 'desc' => 'Monthly new registrations, KYC approvals, and retention analytics.', 'icon' => 'bi-people', 'color' => 'text-danger'],
    ['title' => 'P2P Agent Network Report', 'desc' => 'P2P turnover, agent liquidity, turnaround time, and release speed.', 'icon' => 'bi-arrow-left-right', 'color' => 'text-warning'],
    ['title' => 'Deposits & Withdrawals Ledger', 'desc' => 'MFS (bKash/Nagad), bank wire, and crypto ledger settlement audit.', 'icon' => 'bi-wallet2', 'color' => 'text-info'],
    ['title' => 'Country & Currency Performance', 'desc' => 'Regional volume breakdown by Bangladesh, UAE, Malaysia, and US.', 'icon' => 'bi-globe-americas', 'color' => 'text-secondary']
];

renderAdminHeader('Reports & Analytics', 'reports');
?>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: #0f172a;">Reports & Analytics</h2>
        <p class="text-secondary mb-0">Generate audited financial records, tax summaries, and data exports.</p>
    </div>
    <div class="d-flex gap-2">
        <button class="btn btn-outline-danger btn-sm px-3 fw-semibold"><i class="bi bi-file-earmark-pdf me-1"></i> PDF</button>
        <button class="btn btn-outline-success btn-sm px-3 fw-semibold"><i class="bi bi-file-earmark-excel me-1"></i> Excel</button>
        <button class="btn btn-outline-primary btn-sm px-3 fw-semibold"><i class="bi bi-file-earmark-text me-1"></i> CSV</button>
        <button class="btn btn-outline-secondary btn-sm px-3 fw-semibold" onclick="window.print()"><i class="bi bi-printer me-1"></i> Print</button>
    </div>
</div>

<div class="row g-4 mb-4">
    <?php foreach ($reportCards as $rc): ?>
    <div class="col-md-6 col-xl-4">
        <div class="card card-custom p-4 h-100">
            <div class="d-flex align-items-center gap-3 mb-3">
                <div class="rounded-3 p-3 bg-light border <?= $rc['color'] ?> fs-4">
                    <i class="bi <?= $rc['icon'] ?>"></i>
                </div>
                <div>
                    <h6 class="fw-bold text-dark mb-0"><?= htmlspecialchars($rc['title']) ?></h6>
                    <small class="text-muted">Automated Monthly Schedule</small>
                </div>
            </div>
            <p class="text-secondary small mb-4"><?= htmlspecialchars($rc['desc']) ?></p>
            <div class="mt-auto d-flex gap-2">
                <button class="btn btn-sm btn-outline-primary w-100 fw-semibold">Generate</button>
                <button class="btn btn-sm btn-light border px-3" title="Export"><i class="bi bi-download"></i></button>
            </div>
        </div>
    </div>
    <?php endforeach; ?>
</div>

<?php
renderAdminFooter();
?>
