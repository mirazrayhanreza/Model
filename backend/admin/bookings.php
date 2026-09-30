<?php
declare(strict_types=1);

// backend/admin/bookings.php
// Booking & Escrow Contract Management Interface (PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';
require_once __DIR__ . '/layout.php';

checkAdminAuth();

$msg = '';
$msgType = 'success';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $action = $_POST['action'] ?? '';
    $bookingId = $_POST['booking_id'] ?? '';

    if ($action === 'release') {
        $msg = "Payment for booking {$bookingId} released from Escrow to Model wallet successfully!";
        $msgType = 'success';
    } elseif ($action === 'refund') {
        $msg = "Booking {$bookingId} refunded in full to User wallet.";
        $msgType = 'info';
    } elseif ($action === 'dispute') {
        $msg = "Booking {$bookingId} transferred to Dispute Resolution Center.";
        $msgType = 'warning';
    } elseif ($action === 'cancel') {
        $msg = "Booking {$bookingId} cancelled.";
        $msgType = 'danger';
    }
}

$bookingsList = [
    [
        'id' => 'BK102850',
        'user' => 'Rahim Uddin',
        'model' => 'Jessica A.',
        'service' => '2 Hour Commercial Photoshoot',
        'date' => '27 Sep 2026',
        'amount' => 15000.00,
        'fee' => 2250.00,
        'model_earning' => 12750.00,
        'currency' => 'BDT (৳)',
        'status' => 'Active',
        'badge' => 'badge-approved',
        'escrow' => 'HELD IN ESCROW',
        'timeline' => [
            ['title' => 'Request Created', 'done' => true],
            ['title' => 'Model Accepted', 'done' => true],
            ['title' => 'Payment Received in Escrow', 'done' => true],
            ['title' => 'Booking In-Progress', 'done' => true],
            ['title' => 'Waiting Completion Verification', 'done' => false]
        ]
    ],
    [
        'id' => 'BK102849',
        'user' => 'Karim Khan',
        'model' => 'Maria K.',
        'service' => 'Full Day Editorial Modeling',
        'date' => '26 Sep 2026',
        'amount' => 28000.00,
        'fee' => 4200.00,
        'model_earning' => 23800.00,
        'currency' => 'BDT (৳)',
        'status' => 'Completed',
        'badge' => 'badge-completed',
        'escrow' => 'RELEASED TO MODEL',
        'timeline' => [
            ['title' => 'Request Created', 'done' => true],
            ['title' => 'Model Accepted', 'done' => true],
            ['title' => 'Payment Received', 'done' => true],
            ['title' => 'Booking Completed', 'done' => true],
            ['title' => 'Payment Released', 'done' => true]
        ]
    ],
    [
        'id' => 'BK102848',
        'user' => 'Faisal Al-Mansoor',
        'model' => 'Sophia L.',
        'service' => 'Runway High Fashion Show',
        'date' => '28 Sep 2026',
        'amount' => 3500.00,
        'fee' => 525.00,
        'model_earning' => 2975.00,
        'currency' => 'AED (د.إ)',
        'status' => 'Pending',
        'badge' => 'badge-pending',
        'escrow' => 'WAITING PAYMENT',
        'timeline' => [
            ['title' => 'Request Created', 'done' => true],
            ['title' => 'Model Accepted', 'done' => true],
            ['title' => 'Waiting Payment', 'done' => false],
            ['title' => 'Booking In-Progress', 'done' => false]
        ]
    ]
];

renderAdminHeader('Booking Management', 'bookings');
?>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: #0f172a;">Booking Management</h2>
        <p class="text-secondary mb-0">Monitor active bookings, held escrow balances, delivery timelines, and payouts.</p>
    </div>
    <div class="d-flex align-items-center gap-2">
        <span class="badge bg-primary-subtle text-primary border px-3 py-2 fw-semibold">
            <i class="bi bi-shield-lock-fill me-1"></i> 100% Escrow Protected
        </span>
    </div>
</div>

<?php if ($msg): ?>
    <div class="alert alert-<?= $msgType ?> alert-dismissible fade show" role="alert">
        <i class="bi bi-info-circle-fill me-2"></i><?= htmlspecialchars($msg) ?>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
<?php endif; ?>

<!-- Status Filter Tabs -->
<div class="card card-custom p-0 overflow-hidden mb-4">
    <div class="p-3 border-bottom d-flex justify-content-between align-items-center flex-wrap gap-2">
        <ul class="nav nav-pills small fw-semibold">
            <li class="nav-item"><a class="nav-link active" href="#">All Bookings (1,120)</a></li>
            <li class="nav-item"><a class="nav-link text-secondary" href="#">Pending (125)</a></li>
            <li class="nav-item"><a class="nav-link text-secondary" href="#">Active (35)</a></li>
            <li class="nav-item"><a class="nav-link text-secondary" href="#">Completed (840)</a></li>
            <li class="nav-item"><a class="nav-link text-secondary" href="#">Disputed (18)</a></li>
        </ul>
        <div class="input-group input-group-sm" style="width: 240px;">
            <input type="text" class="form-control" placeholder="Search booking #ID...">
            <button class="btn btn-outline-secondary"><i class="bi bi-search"></i></button>
        </div>
    </div>

    <!-- Table -->
    <div class="table-responsive">
        <table class="table table-custom align-middle">
            <thead>
                <tr>
                    <th class="ps-4">Booking ID</th>
                    <th>Client (User)</th>
                    <th>Model</th>
                    <th>Service & Duration</th>
                    <th>Date</th>
                    <th>Total Escrow</th>
                    <th>Platform Fee (15%)</th>
                    <th>Status</th>
                    <th class="text-end pe-4">Detail</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($bookingsList as $b): ?>
                <tr>
                    <td class="ps-4 fw-bold text-primary">#<?= $b['id'] ?></td>
                    <td class="fw-semibold text-dark"><?= htmlspecialchars($b['user']) ?></td>
                    <td>
                        <span class="fw-semibold text-danger"><i class="bi bi-heart me-1"></i><?= htmlspecialchars($b['model']) ?></span>
                    </td>
                    <td><?= htmlspecialchars($b['service']) ?></td>
                    <td class="text-muted small"><?= htmlspecialchars($b['date']) ?></td>
                    <td class="fw-bold text-dark fs-6"><?= number_format($b['amount'], 2) ?> <?= $b['currency'] ?></td>
                    <td class="text-success fw-semibold"><?= number_format($b['fee'], 2) ?></td>
                    <td><span class="badge-status <?= $b['badge'] ?>"><?= $b['status'] ?></span></td>
                    <td class="text-end pe-4">
                        <button class="btn btn-sm btn-outline-primary fw-semibold px-3" data-bs-toggle="modal" data-bs-target="#bookingModal<?= $b['id'] ?>">
                            Inspect
                        </button>
                    </td>
                </tr>

                <!-- Booking Detail Modal -->
                <div class="modal fade" id="bookingModal<?= $b['id'] ?>" tabindex="-1">
                    <div class="modal-dialog modal-lg modal-dialog-centered">
                        <div class="modal-content border-0 shadow-lg" style="border-radius: 16px;">
                            <div class="modal-header border-bottom py-3">
                                <div>
                                    <h5 class="modal-title fw-bold">Booking Contract #<?= $b['id'] ?></h5>
                                    <span class="badge-status <?= $b['badge'] ?>"><?= $b['status'] ?></span>
                                    <span class="badge bg-secondary ms-1"><?= $b['escrow'] ?></span>
                                </div>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body p-4">
                                <!-- Participants Card -->
                                <div class="row g-3 mb-4">
                                    <div class="col-sm-6">
                                        <div class="p-3 bg-light rounded-3 border">
                                            <small class="text-muted text-uppercase fw-bold d-block mb-1">Client (User)</small>
                                            <h6 class="fw-bold text-dark mb-0"><?= htmlspecialchars($b['user']) ?></h6>
                                            <small class="text-secondary">Verified Client</small>
                                        </div>
                                    </div>
                                    <div class="col-sm-6">
                                        <div class="p-3 bg-light rounded-3 border">
                                            <small class="text-muted text-uppercase fw-bold d-block mb-1">Model (Talent)</small>
                                            <h6 class="fw-bold text-danger mb-0"><?= htmlspecialchars($b['model']) ?></h6>
                                            <small class="text-secondary">Verified Talent</small>
                                        </div>
                                    </div>
                                </div>

                                <!-- Financial Breakdown -->
                                <div class="card bg-light border p-3 mb-4">
                                    <h6 class="fw-bold text-dark mb-3">Financial Escrow Breakdown</h6>
                                    <div class="row g-2 small">
                                        <div class="col-sm-4">
                                            <span class="text-muted d-block">Total Booking Price:</span>
                                            <strong class="fs-6 text-dark"><?= number_format($b['amount'], 2) ?> <?= $b['currency'] ?></strong>
                                        </div>
                                        <div class="col-sm-4">
                                            <span class="text-muted d-block">Platform Fee (15%):</span>
                                            <strong class="fs-6 text-success"><?= number_format($b['fee'], 2) ?> <?= $b['currency'] ?></strong>
                                        </div>
                                        <div class="col-sm-4">
                                            <span class="text-muted d-block">Model Net Earnings:</span>
                                            <strong class="fs-6 text-primary"><?= number_format($b['model_earning'], 2) ?> <?= $b['currency'] ?></strong>
                                        </div>
                                    </div>
                                </div>

                                <!-- Booking Timeline -->
                                <div class="mb-4">
                                    <h6 class="fw-bold text-dark mb-3">Booking Milestone Timeline</h6>
                                    <ul class="list-unstyled mb-0">
                                        <?php foreach ($b['timeline'] as $step): ?>
                                        <li class="d-flex align-items-center gap-2 mb-2">
                                            <?php if ($step['done']): ?>
                                                <i class="bi bi-check-circle-fill text-success fs-5"></i>
                                                <span class="fw-semibold text-dark small"><?= htmlspecialchars($step['title']) ?></span>
                                            <?php else: ?>
                                                <i class="bi bi-circle text-muted fs-5"></i>
                                                <span class="text-muted small"><?= htmlspecialchars($step['title']) ?></span>
                                            <?php endif; ?>
                                        </li>
                                        <?php endforeach; ?>
                                    </ul>
                                </div>

                                <!-- Actions -->
                                <form method="POST">
                                    <input type="hidden" name="booking_id" value="<?= $b['id'] ?>">
                                    <div class="d-flex justify-content-between align-items-center pt-3 border-top flex-wrap gap-2">
                                        <div class="d-flex gap-2">
                                            <button type="submit" name="action" value="cancel" class="btn btn-outline-danger btn-sm px-3">
                                                Cancel Booking
                                            </button>
                                            <button type="submit" name="action" value="refund" class="btn btn-outline-secondary btn-sm px-3">
                                                Refund User
                                            </button>
                                        </div>
                                        <div class="d-flex gap-2">
                                            <button type="submit" name="action" value="dispute" class="btn btn-outline-warning text-dark btn-sm px-3">
                                                Open Dispute
                                            </button>
                                            <button type="submit" name="action" value="release" class="btn btn-success btn-sm px-4 fw-bold">
                                                <i class="bi bi-cash-stack me-1"></i> Release Payment
                                            </button>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
                <?php endforeach; ?>
            </tbody>
        </table>
    </div>
</div>

<?php
renderAdminFooter();
?>
