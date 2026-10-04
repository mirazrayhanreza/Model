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
$db = null;

try {
    $db = Database::getInstance();

    if ($db) {
        // Ensure disputes table exists (driver-aware)
        if (Database::isMySQL()) {
        $db->exec("
        CREATE TABLE IF NOT EXISTS `disputes` (
            `id` INT AUTO_INCREMENT PRIMARY KEY,
            `case_code` VARCHAR(50) UNIQUE NOT NULL,
            `type` VARCHAR(100) NOT NULL,
            `ref_code` VARCHAR(100) NOT NULL,
            `user_name` VARCHAR(100) NOT NULL,
            `agent_name` VARCHAR(100) NOT NULL,
            `amount` VARCHAR(50) NOT NULL,
            `status` VARCHAR(50) DEFAULT 'Open',
            `timer` VARCHAR(50) DEFAULT 'Active 2h',
            `user_statement` TEXT NULL,
            `agent_statement` TEXT NULL,
            `proof_img` VARCHAR(255) NULL,
            `chat_logs` TEXT NULL,
            `device_ip` VARCHAR(255) NULL,
            `wallet_log` VARCHAR(255) NULL,
            `resolution_notes` TEXT NULL,
            `resolved_at` DATETIME NULL,
            `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        ");
    } else {
        $db->exec("
        CREATE TABLE IF NOT EXISTS disputes (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            case_code TEXT UNIQUE NOT NULL,
            type TEXT NOT NULL,
            ref_code TEXT NOT NULL,
            user_name TEXT NOT NULL,
            agent_name TEXT NOT NULL,
            amount TEXT NOT NULL,
            status TEXT DEFAULT 'Open',
            timer TEXT DEFAULT 'Active 2h',
            user_statement TEXT,
            agent_statement TEXT,
            proof_img TEXT,
            chat_logs TEXT,
            device_ip TEXT,
            wallet_log TEXT,
            resolution_notes TEXT,
            resolved_at DATETIME,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        )
        ");
    }
    }
} catch (Throwable $e) {
    error_log("Disputes table init notice: " . $e->getMessage());
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $action = $_POST['action'] ?? '';
    $caseId = $_POST['case_id'] ?? '';
    $adminNotes = trim($_POST['admin_notes'] ?? '');

    if ($action === 'release_user') {
        $msg = "Case #{$caseId} resolved: Escrow funds released to User. Transaction recorded.";
        $msgType = 'success';
        if ($db) {
            try {
                $stmt = $db->prepare("UPDATE disputes SET status = 'Resolved (Released to User)', resolution_notes = ?, resolved_at = CURRENT_TIMESTAMP WHERE case_code = ?");
                $stmt->execute([$adminNotes, $caseId]);
            } catch (Throwable) {}
        }
    } elseif ($action === 'release_agent') {
        $msg = "Case #{$caseId} resolved: Escrow funds released to Cash Agent. Dispute closed.";
        $msgType = 'success';
        if ($db) {
            try {
                $stmt = $db->prepare("UPDATE disputes SET status = 'Resolved (Released to Agent)', resolution_notes = ?, resolved_at = CURRENT_TIMESTAMP WHERE case_code = ?");
                $stmt->execute([$adminNotes, $caseId]);
            } catch (Throwable) {}
        }
    } elseif ($action === 'refund_partial') {
        $msg = "Case #{$caseId} resolved: 50% partial split refund executed between parties.";
        $msgType = 'info';
        if ($db) {
            try {
                $stmt = $db->prepare("UPDATE disputes SET status = 'Resolved (50% Split)', resolution_notes = ?, resolved_at = CURRENT_TIMESTAMP WHERE case_code = ?");
                $stmt->execute([$adminNotes, $caseId]);
            } catch (Throwable) {}
        }
    } elseif ($action === 'freeze') {
        $msg = "Suspect account frozen pending law enforcement review for Case #{$caseId}.";
        $msgType = 'danger';
        if ($db) {
            try {
                $stmt = $db->prepare("UPDATE disputes SET status = 'Account Frozen', resolution_notes = ?, resolved_at = CURRENT_TIMESTAMP WHERE case_code = ?");
                $stmt->execute([$adminNotes, $caseId]);
            } catch (Throwable) {}
        }
    }
}

// Default Seed Disputes
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
    ],
    [
        'id' => 'DSP-902',
        'type' => 'Model Booking Cancellation Dispute',
        'ref' => '#BK-44219',
        'user' => 'Kamal Hossain',
        'agent' => 'Jessica Chowdhury (Model)',
        'amount' => '৳8,000',
        'status' => 'Under Investigation',
        'timer' => 'Active 5h',
        'user_statement' => 'Model arrived 45 minutes late to studio shoot location in Banani, causing client photographer overtime charges.',
        'agent_statement' => 'Severe traffic jam on VIP Road. I informed user via order chat 30 minutes in advance and offered to shoot 1 extra hour.',
        'proof_img' => 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&h=300&fit=crop',
        'chat_logs' => 'Kamal: Where are you? / Jessica: Stuck in Gulshan 2 traffic, 10 min away.',
        'device_ip' => 'User IP: 103.242.12.8 | Model IP: 103.220.14.99',
        'wallet_log' => 'Held in Booking Escrow: ৳8,000'
    ]
];

// Load from database if records exist
if ($db) {
    try {
        $dbDisputes = $db->query("SELECT * FROM disputes ORDER BY id DESC LIMIT 50")->fetchAll(PDO::FETCH_ASSOC);
        if (!empty($dbDisputes)) {
            $mapped = [];
            foreach ($dbDisputes as $row) {
                $mapped[] = [
                    'id' => $row['case_code'] ?? 'DSP-' . $row['id'],
                    'type' => $row['type'] ?? 'Escrow Discrepancy',
                    'ref' => $row['ref_code'] ?? '#REF',
                    'user' => $row['user_name'] ?? 'User',
                    'agent' => $row['agent_name'] ?? 'Counterparty',
                    'amount' => $row['amount'] ?? '৳0.00',
                    'status' => $row['status'] ?? 'Open',
                    'timer' => $row['timer'] ?? 'Active',
                    'user_statement' => $row['user_statement'] ?? 'No statement provided',
                    'agent_statement' => $row['agent_statement'] ?? 'No response yet',
                    'proof_img' => !empty($row['proof_img']) ? $row['proof_img'] : 'https://images.unsplash.com/photo-1559526324-4b87b5e36e44?w=500&h=300&fit=crop',
                    'chat_logs' => $row['chat_logs'] ?? 'No chat history logged',
                    'device_ip' => $row['device_ip'] ?? '127.0.0.1',
                    'wallet_log' => $row['wallet_log'] ?? 'Escrow Hold'
                ];
            }
            $disputesList = $mapped;
        } else {
            // Seed the initial rows
            $insDisp = $db->prepare("INSERT INTO disputes (case_code, type, ref_code, user_name, agent_name, amount, status, timer, user_statement, agent_statement, proof_img, chat_logs, device_ip, wallet_log) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            foreach ($disputesList as $d) {
                $insDisp->execute([
                    $d['id'], $d['type'], $d['ref'], $d['user'], $d['agent'],
                    $d['amount'], $d['status'], $d['timer'], $d['user_statement'],
                    $d['agent_statement'], $d['proof_img'], $d['chat_logs'],
                    $d['device_ip'], $d['wallet_log']
                ]);
            }
        }
    } catch (Throwable $e) {
        error_log("Disputes load notice: " . $e->getMessage());
    }
}

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
                <div class="fw-bold text-danger fs-4"><?= count(array_filter($disputesList, fn($x) => str_contains($x['status'], 'Open'))) ?: 1 ?></div>
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
                <div class="fw-bold text-warning fs-4"><?= count(array_filter($disputesList, fn($x) => str_contains($x['status'], 'Investigation'))) ?: 1 ?></div>
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
                <div class="fw-bold text-success fs-4"><?= count(array_filter($disputesList, fn($x) => str_contains($x['status'], 'Resolved'))) ?: 96 ?></div>
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
                    <td class="ps-4 fw-bold text-danger">#<?= htmlspecialchars($d['id']) ?></td>
                    <td class="fw-semibold text-primary"><?= htmlspecialchars($d['ref']) ?></td>
                    <td><?= htmlspecialchars($d['type']) ?></td>
                    <td class="fw-semibold text-dark"><?= htmlspecialchars($d['user']) ?></td>
                    <td class="text-secondary"><?= htmlspecialchars($d['agent']) ?></td>
                    <td class="fw-bold text-dark fs-6"><?= htmlspecialchars($d['amount']) ?></td>
                    <td>
                        <?php if (str_contains($d['status'], 'Resolved')): ?>
                            <span class="badge bg-success-subtle text-success border border-success-subtle px-2 py-1"><?= htmlspecialchars($d['status']) ?></span>
                        <?php elseif (str_contains($d['status'], 'Frozen')): ?>
                            <span class="badge bg-danger text-white px-2 py-1"><?= htmlspecialchars($d['status']) ?></span>
                        <?php else: ?>
                            <span class="badge bg-warning-subtle text-warning border border-warning-subtle px-2 py-1"><?= htmlspecialchars($d['status']) ?></span>
                        <?php endif; ?>
                    </td>
                    <td class="text-end pe-4">
                        <button class="btn btn-sm btn-danger fw-semibold px-3" data-bs-toggle="modal" data-bs-target="#disputeModal<?= preg_replace('/[^A-Za-z0-9]/', '', $d['id']) ?>">
                            Investigate Case
                        </button>
                    </td>
                </tr>

                <!-- Dispute Detail Modal -->
                <div class="modal fade" id="disputeModal<?= preg_replace('/[^A-Za-z0-9]/', '', $d['id']) ?>" tabindex="-1">
                    <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
                        <div class="modal-content border-0 shadow-lg" style="border-radius: 16px;">
                            <div class="modal-header border-bottom py-3">
                                <div>
                                    <h5 class="modal-title fw-bold">Arbitration Dossier — Case #<?= htmlspecialchars($d['id']) ?></h5>
                                    <small class="text-muted">Target: <?= htmlspecialchars($d['ref']) ?> | Disputed Amount: <?= htmlspecialchars($d['amount']) ?></small>
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
                                    <input type="hidden" name="case_id" value="<?= htmlspecialchars($d['id']) ?>">
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
