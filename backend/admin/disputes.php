<?php
declare(strict_types=1);

// backend/admin/disputes.php
// Dispute Resolution Center for P2P and Bookings (PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';
require_once __DIR__ . '/layout.php';

checkAdminAuth();

$msg = '';
$msgType = 'success';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $action = $_POST['action'] ?? '';
    $caseId = $_POST['case_id'] ?? '';

    if ($action === 'release_user') {
        $msg = "Case {$caseId} resolved: Escrow funds released to User.";
        $msgType = 'success';
    } elseif ($action === 'release_agent') {
        $msg = "Case {$caseId} resolved: Escrow funds released to Agent.";
        $msgType = 'success';
    } elseif ($action === 'refund_partial') {
        $msg = "Case {$caseId} resolved: 50% partial split refund executed.";
        $msgType = 'info';
    } elseif ($action === 'freeze') {
        $msg = "Suspect account frozen pending law enforcement review.";
        $msgType = 'danger';
    }
}

$disputesList = [
    [
        'id' => 'DSP-901',
        'type' => 'P2P Order Payment Discrepancy',
        'ref' => '#P20184',
        'user' => 'Hasan Ali',
        'agent' => 'Agent 004 (Gulshan Escrow)',
        'amount' => '৳10,000',
        'status' => 'Open',
        'timer' => 'Active 2h',
        'user_statement' => 'I made payment through bKash counter to the agent number 01711223344. Agent claimed the SMS notification was delayed and did not release my wallet balance.',
        'agent_statement' => 'Our merchant dashboard did not reflect this transaction ref TXN77889900 at 09:15 AM. Statement showed no credit matching this exact time.',
        'proof_img' => 'https://images.unsplash.com/photo-1559526324-4b87b5e36e44?w=500&h=300&fit=crop',
        'chat_logs' => 'Hasan: Payment completed. Ref TXN77889900. / Agent: Statement not updated yet.',
        'device_ip' => 'User IP: 103.114.98.22 (Dhaka) | Agent IP: 103.220.14.5 (Dhaka)',
        'wallet_log' => 'Agent Escrow Hold: ৳10,000 | User Balance: ৳1,200'
    ]
];

renderAdminHeader('Dispute Resolution Center', 'disputes');
?>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: #0f172a;">Dispute Center</h2>
        <p class="text-secondary mb-0">Arbitrate and resolve conflict tickets across bookings, deposits, and P2P transfers.</p>
    </div>
</div>

<?php if ($msg): ?>
    <div class="alert alert-<?= $msgType ?> alert-dismissible fade show" role="alert">
        <i class="bi bi-shield-check me-2"></i><?= htmlspecialchars($msg) ?>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
<?php endif; ?>

<!-- Top Status Cards -->
<div class="row g-3 mb-4">
    <div class="col-md-4">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Open Disputes</div>
                <div class="fw-bold text-danger fs-4">12</div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-red">
                <i class="bi bi-exclamation-octagon-fill"></i>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Under Investigation</div>
                <div class="fw-bold text-warning fs-4">8</div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-gold">
                <i class="bi bi-search"></i>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Resolved Cases</div>
                <div class="fw-bold text-success fs-4">96</div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-green">
                <i class="bi bi-check2-all"></i>
            </div>
        </div>
    </div>
</div>

<!-- Dispute Cases Table -->
<div class="card card-custom p-0 overflow-hidden mb-4">
    <div class="table-responsive">
        <table class="table table-custom align-middle">
            <thead>
                <tr>
                    <th class="ps-4">Case ID</th>
                    <th>Reference</th>
                    <th>Type</th>
                    <th>User</th>
                    <th>Agent / Respondent</th>
                    <th>Disputed Amount</th>
                    <th>Status</th>
                    <th class="text-end pe-4">Action</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($disputesList as $d): ?>
                <tr>
                    <td class="ps-4 fw-bold text-danger">#<?= $d['id'] ?></td>
                    <td class="fw-semibold text-primary"><?= $d['ref'] ?></td>
                    <td><?= htmlspecialchars($d['type']) ?></td>
                    <td class="fw-semibold text-dark"><?= htmlspecialchars($d['user']) ?></td>
                    <td class="text-secondary"><?= htmlspecialchars($d['agent']) ?></td>
                    <td class="fw-bold text-dark fs-6"><?= htmlspecialchars($d['amount']) ?></td>
                    <td><span class="badge-status badge-disputed">Open</span></td>
                    <td class="text-end pe-4">
                        <button class="btn btn-sm btn-danger fw-semibold px-3" data-bs-toggle="modal" data-bs-target="#disputeModal<?= $d['id'] ?>">
                            Investigate Case
                        </button>
                    </td>
                </tr>

                <!-- Dispute Detail Modal -->
                <div class="modal fade" id="disputeModal<?= $d['id'] ?>" tabindex="-1">
                    <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
                        <div class="modal-content border-0 shadow-lg" style="border-radius: 16px;">
                            <div class="modal-header border-bottom py-3">
                                <div>
                                    <h5 class="modal-title fw-bold">Arbitration Dossier — Case #<?= $d['id'] ?></h5>
                                    <small class="text-muted">Target: <?= $d['ref'] ?> | Disputed Amount: <?= $d['amount'] ?></small>
                                </div>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body p-4">
                                <!-- Statements Grid -->
                                <div class="row g-3 mb-4">
                                    <div class="col-sm-6">
                                        <div class="p-3 bg-light rounded-3 border h-100">
                                            <span class="text-primary small fw-bold text-uppercase d-block mb-1">User Statement (<?= htmlspecialchars($d['user']) ?>)</span>
                                            <p class="small text-secondary mb-0"><?= htmlspecialchars($d['user_statement']) ?></p>
                                        </div>
                                    </div>
                                    <div class="col-sm-6">
                                        <div class="p-3 bg-light rounded-3 border h-100">
                                            <span class="text-danger small fw-bold text-uppercase d-block mb-1">Agent Statement (<?= htmlspecialchars($d['agent']) ?>)</span>
                                            <p class="small text-secondary mb-0"><?= htmlspecialchars($d['agent_statement']) ?></p>
                                        </div>
                                    </div>
                                </div>

                                <!-- Payment Screenshot Proof -->
                                <div class="p-3 bg-light rounded-3 border mb-4">
                                    <span class="text-muted small fw-bold text-uppercase d-block mb-2">Uploaded Bank / MFS Screenshot</span>
                                    <img src="<?= htmlspecialchars($d['proof_img']) ?>" class="img-fluid rounded border" style="max-height: 180px; width: 100%; object-fit: cover;" alt="Proof">
                                </div>

                                <!-- Technical & IP Logs -->
                                <div class="card bg-light border p-3 mb-4 small">
                                    <h6 class="fw-bold text-dark mb-2">Telemetry & Audit Trail</h6>
                                    <div class="mb-1 text-muted"><strong>Network:</strong> <?= htmlspecialchars($d['device_ip']) ?></div>
                                    <div class="mb-1 text-muted"><strong>Ledger:</strong> <?= htmlspecialchars($d['wallet_log']) ?></div>
                                    <div class="text-muted"><strong>Chat Snippet:</strong> <?= htmlspecialchars($d['chat_logs']) ?></div>
                                </div>

                                <!-- Arbitrator Notes -->
                                <form method="POST">
                                    <input type="hidden" name="case_id" value="<?= $d['id'] ?>">
                                    <div class="mb-3">
                                        <label class="form-label text-dark fw-semibold small">Admin Adjudication Remarks</label>
                                        <textarea name="admin_notes" class="form-control" rows="2" placeholder="Record mandatory rationale for the chosen arbitration judgment..."></textarea>
                                    </div>

                                    <div class="d-flex justify-content-between align-items-center pt-3 border-top flex-wrap gap-2">
                                        <button type="submit" name="action" value="freeze" class="btn btn-outline-danger btn-sm px-3">
                                            <i class="bi bi-slash-circle me-1"></i> Freeze Account
                                        </button>
                                        <div class="d-flex gap-2">
                                            <button type="submit" name="action" value="refund_partial" class="btn btn-outline-secondary btn-sm px-3">
                                                50% Partial Split
                                            </button>
                                            <button type="submit" name="action" value="release_agent" class="btn btn-outline-primary btn-sm px-3">
                                                Release to Agent
                                            </button>
                                            <button type="submit" name="action" value="release_user" class="btn btn-success btn-sm px-3 fw-bold">
                                                Release to User
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
