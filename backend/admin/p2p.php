<?php
declare(strict_types=1);

// backend/admin/p2p.php
// Binance-Style P2P Agent Network Dashboard & Order Management (PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';
require_once __DIR__ . '/layout.php';

checkAdminAuth();

$msg = '';
$msgType = 'success';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $action = $_POST['action'] ?? '';
    $orderId = $_POST['order_id'] ?? '';

    if ($action === 'release') {
        $msg = "Order {$orderId} funds successfully released from escrow to buyer wallet!";
        $msgType = 'success';
    } elseif ($action === 'dispute') {
        $msg = "Order {$orderId} has been escalated to Dispute Resolution Center.";
        $msgType = 'warning';
    } elseif ($action === 'cancel') {
        $msg = "Order {$orderId} cancelled and funds unlocked for agent.";
        $msgType = 'danger';
    }
}

$p2pOrders = [
    [
        'id' => 'P20183',
        'buyer' => 'Karim Khan',
        'agent' => 'BD Agent 008 (Uttara Express)',
        'amount' => '৳50,000',
        'method' => 'bKash Personal',
        'timer' => '04:25',
        'status' => 'Paid',
        'badge' => 'badge-approved',
        'txn_id' => '8HHA102891',
        'proof_img' => 'https://images.unsplash.com/photo-1556742049-0a670f4a4591?w=400&h=250&fit=crop',
        'chat' => [
            ['sender' => 'Karim Khan', 'time' => '10:40 AM', 'text' => 'Hello Agent, I sent ৳50,000 via bKash personal.'],
            ['sender' => 'BD Agent 008', 'time' => '10:41 AM', 'text' => 'Please provide transaction ID and screenshot.'],
            ['sender' => 'Karim Khan', 'time' => '10:42 AM', 'text' => 'Screenshot uploaded. Txn ID: 8HHA102891.'],
            ['sender' => 'BD Agent 008', 'time' => '10:43 AM', 'text' => 'Checking bank SMS statement now...']
        ]
    ],
    [
        'id' => 'P20182',
        'buyer' => 'Rahim Uddin',
        'agent' => 'BD Agent 012 (Dhanmondi Fast)',
        'amount' => '৳20,000',
        'method' => 'Nagad',
        'timer' => '08:32',
        'status' => 'Waiting',
        'badge' => 'badge-waiting',
        'txn_id' => 'Pending...',
        'proof_img' => 'https://images.unsplash.com/photo-1563013544-824ae1b704d3?w=400&h=250&fit=crop',
        'chat' => [
            ['sender' => 'BD Agent 012', 'time' => '10:45 AM', 'text' => 'Please cash-in to 01822334455 within 15 minutes.']
        ]
    ],
    [
        'id' => 'P20184',
        'buyer' => 'Hasan Ali',
        'agent' => 'Agent 004 (Gulshan Escrow)',
        'amount' => '৳10,000',
        'method' => 'Rocket',
        'timer' => '--',
        'status' => 'Disputed',
        'badge' => 'badge-disputed',
        'txn_id' => 'TXN77889900',
        'proof_img' => 'https://images.unsplash.com/photo-1559526324-4b87b5e36e44?w=400&h=250&fit=crop',
        'chat' => [
            ['sender' => 'Hasan Ali', 'time' => '09:15 AM', 'text' => 'I already sent payment but agent says not received.'],
            ['sender' => 'Agent 004', 'time' => '09:20 AM', 'text' => 'Statement does not show this Txn ID. Please check sender number.']
        ]
    ],
    [
        'id' => 'P20185',
        'buyer' => 'Maria K.',
        'agent' => 'KL Agent 006 (Maybank)',
        'amount' => 'RM 1,200',
        'method' => 'DuitNow Transfer',
        'timer' => '00:00',
        'status' => 'Released',
        'badge' => 'badge-released',
        'txn_id' => 'MBB88991122',
        'proof_img' => 'https://images.unsplash.com/photo-1556742049-0a670f4a4591?w=400&h=250&fit=crop',
        'chat' => [
            ['sender' => 'KL Agent 006', 'time' => '08:30 AM', 'text' => 'Payment received in Maybank. Released funds.']
        ]
    ]
];

renderAdminHeader('P2P Network Hub', 'p2p');
?>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: #0f172a;">P2P Agent Network</h2>
        <p class="text-secondary mb-0">Binance-style real-time peer-to-peer cash escrow, live order timers, and dispute mitigation.</p>
    </div>
    <div class="d-flex align-items-center gap-2">
        <span class="badge bg-success-subtle text-success border px-3 py-2 fw-semibold">
            <i class="bi bi-circle-fill me-1 small"></i> P2P Escrow Engine Online
        </span>
    </div>
</div>

<?php if ($msg): ?>
    <div class="alert alert-<?= $msgType ?> alert-dismissible fade show" role="alert">
        <i class="bi bi-check-circle-fill me-2"></i><?= htmlspecialchars($msg) ?>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
<?php endif; ?>

<!-- Top P2P KPI Row -->
<div class="row g-3 mb-4">
    <div class="col-xl-2 col-md-4 col-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Today's Volume</div>
                <div class="fw-bold text-dark fs-5">৳2,850,000</div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-blue" style="width: 38px; height: 38px; font-size: 1.1rem;">
                <i class="bi bi-graph-up"></i>
            </div>
        </div>
    </div>
    <div class="col-xl-2 col-md-4 col-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Active Orders</div>
                <div class="fw-bold text-dark fs-5">185</div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-purple" style="width: 38px; height: 38px; font-size: 1.1rem;">
                <i class="bi bi-arrow-repeat"></i>
            </div>
        </div>
    </div>
    <div class="col-xl-2 col-md-4 col-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Waiting Payment</div>
                <div class="fw-bold text-dark fs-5">52</div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-gold" style="width: 38px; height: 38px; font-size: 1.1rem;">
                <i class="bi bi-hourglass-split"></i>
            </div>
        </div>
    </div>
    <div class="col-xl-2 col-md-4 col-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Paid Waiting Release</div>
                <div class="fw-bold text-dark fs-5">28</div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-cyan" style="width: 38px; height: 38px; font-size: 1.1rem;">
                <i class="bi bi-unlock-fill"></i>
            </div>
        </div>
    </div>
    <div class="col-xl-2 col-md-4 col-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Open Disputes</div>
                <div class="fw-bold text-danger fs-5">12</div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-red" style="width: 38px; height: 38px; font-size: 1.1rem;">
                <i class="bi bi-shield-slash-fill"></i>
            </div>
        </div>
    </div>
    <div class="col-xl-2 col-md-4 col-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Active Agents</div>
                <div class="fw-bold text-dark fs-5">105</div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-green" style="width: 38px; height: 38px; font-size: 1.1rem;">
                <i class="bi bi-person-check-fill"></i>
            </div>
        </div>
    </div>
</div>

<!-- Live P2P Orders Table -->
<div class="card card-custom p-0 overflow-hidden mb-4">
    <div class="p-3 border-bottom d-flex justify-content-between align-items-center">
        <h5 class="fw-bold text-dark mb-0">Live P2P Escrow Orders</h5>
        <div class="btn-group btn-group-sm">
            <button class="btn btn-outline-secondary active">All (185)</button>
            <button class="btn btn-outline-secondary">Waiting (52)</button>
            <button class="btn btn-outline-secondary">Paid (28)</button>
            <button class="btn btn-outline-secondary">Disputed (12)</button>
        </div>
    </div>

    <div class="table-responsive">
        <table class="table table-custom align-middle">
            <thead>
                <tr>
                    <th class="ps-4">Order ID</th>
                    <th>Buyer (User)</th>
                    <th>Cash Agent</th>
                    <th>Amount</th>
                    <th>Method</th>
                    <th>Timer</th>
                    <th>Status</th>
                    <th class="text-end pe-4">Action</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($p2pOrders as $order): ?>
                <tr>
                    <td class="ps-4 fw-bold text-primary">#<?= $order['id'] ?></td>
                    <td class="fw-semibold text-dark"><?= htmlspecialchars($order['buyer']) ?></td>
                    <td class="text-secondary"><?= htmlspecialchars($order['agent']) ?></td>
                    <td class="fw-bold text-dark fs-6"><?= htmlspecialchars($order['amount']) ?></td>
                    <td><span class="badge bg-light text-dark border"><?= htmlspecialchars($order['method']) ?></span></td>
                    <td>
                        <?php if ($order['timer'] !== '--'): ?>
                            <span class="badge bg-warning-subtle text-warning border px-2 py-1 fw-bold">
                                <i class="bi bi-stopwatch me-1"></i><?= $order['timer'] ?>
                            </span>
                        <?php else: ?>
                            <span class="text-muted">--</span>
                        <?php endif; ?>
                    </td>
                    <td><span class="badge-status <?= $order['badge'] ?>"><?= $order['status'] ?></span></td>
                    <td class="text-end pe-4">
                        <button class="btn btn-sm btn-outline-primary fw-semibold px-3" data-bs-toggle="modal" data-bs-target="#orderModal<?= $order['id'] ?>">
                            Manage Order
                        </button>
                    </td>
                </tr>

                <!-- P2P Order Detail Modal -->
                <div class="modal fade" id="orderModal<?= $order['id'] ?>" tabindex="-1">
                    <div class="modal-dialog modal-lg modal-dialog-centered">
                        <div class="modal-content border-0 shadow-lg" style="border-radius: 16px;">
                            <div class="modal-header border-bottom py-3">
                                <div>
                                    <h5 class="modal-title fw-bold">P2P Order #<?= $order['id'] ?></h5>
                                    <span class="badge-status <?= $order['badge'] ?>"><?= $order['status'] ?></span>
                                </div>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body p-4">
                                <div class="row g-3 mb-4">
                                    <div class="col-sm-6">
                                        <div class="p-3 bg-light rounded-3 border">
                                            <small class="text-muted text-uppercase fw-bold d-block mb-1">Buyer Details</small>
                                            <h6 class="fw-bold text-dark mb-0"><?= htmlspecialchars($order['buyer']) ?></h6>
                                            <small class="text-secondary">Verified Member</small>
                                        </div>
                                    </div>
                                    <div class="col-sm-6">
                                        <div class="p-3 bg-light rounded-3 border">
                                            <small class="text-muted text-uppercase fw-bold d-block mb-1">Escrow Agent</small>
                                            <h6 class="fw-bold text-dark mb-0"><?= htmlspecialchars($order['agent']) ?></h6>
                                            <small class="text-success fw-semibold"><i class="bi bi-shield-check"></i> Escrow Bond Deposited</small>
                                        </div>
                                    </div>
                                </div>

                                <div class="row g-3 mb-4">
                                    <div class="col-sm-4">
                                        <small class="text-muted d-block">Order Amount</small>
                                        <span class="fw-bold fs-5 text-dark"><?= htmlspecialchars($order['amount']) ?></span>
                                    </div>
                                    <div class="col-sm-4">
                                        <small class="text-muted d-block">Payment Method</small>
                                        <span class="fw-semibold text-dark"><?= htmlspecialchars($order['method']) ?></span>
                                    </div>
                                    <div class="col-sm-4">
                                        <small class="text-muted d-block">Transaction Ref</small>
                                        <span class="fw-semibold text-primary font-monospace"><?= htmlspecialchars($order['txn_id']) ?></span>
                                    </div>
                                </div>

                                <!-- Payment Proof Screenshot -->
                                <div class="p-3 bg-light rounded-3 border mb-4">
                                    <span class="text-muted small fw-bold d-block mb-2 text-uppercase">Payment Proof Screenshot</span>
                                    <img src="<?= htmlspecialchars($order['proof_img']) ?>" class="img-fluid rounded border" style="max-height: 180px; width: 100%; object-fit: cover;" alt="Proof">
                                </div>

                                <!-- Order Live Chat Log -->
                                <div class="border rounded-3 p-3 mb-4">
                                    <span class="text-muted small fw-bold d-block mb-2 text-uppercase"><i class="bi bi-chat-left-text me-1"></i>Order Chat Transcript</span>
                                    <div class="d-flex flex-column gap-2" style="max-height: 160px; overflow-y: auto;">
                                        <?php foreach ($order['chat'] as $c): ?>
                                        <div class="p-2 bg-light rounded small">
                                            <div class="d-flex justify-content-between mb-1">
                                                <strong class="text-dark"><?= htmlspecialchars($c['sender']) ?></strong>
                                                <span class="text-muted" style="font-size: 0.7rem;"><?= htmlspecialchars($c['time']) ?></span>
                                            </div>
                                            <div class="text-secondary"><?= htmlspecialchars($c['text']) ?></div>
                                        </div>
                                        <?php endforeach; ?>
                                    </div>
                                </div>

                                <!-- Action Buttons -->
                                <form method="POST">
                                    <input type="hidden" name="order_id" value="<?= $order['id'] ?>">
                                    <div class="d-flex justify-content-between align-items-center pt-2 border-top flex-wrap gap-2">
                                        <button type="submit" name="action" value="cancel" class="btn btn-outline-danger px-3">
                                            <i class="bi bi-x-circle me-1"></i> Cancel Order
                                        </button>
                                        <div class="d-flex gap-2">
                                            <button type="submit" name="action" value="dispute" class="btn btn-outline-warning text-dark px-3">
                                                <i class="bi bi-exclamation-triangle me-1"></i> Open Dispute
                                            </button>
                                            <button type="submit" name="action" value="release" class="btn btn-success px-4 fw-bold">
                                                <i class="bi bi-unlock-fill me-1"></i> Release Funds
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
