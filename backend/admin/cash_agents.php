<?php
declare(strict_types=1);

// backend/admin/cash_agents.php
// Cash Agent Management Dashboard, Admin Creation & Operations (PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';
require_once __DIR__ . '/layout.php';

checkAdminAuth();
$db = Database::getInstance();

// Ensure cash_agents table exists with all necessary columns
$db->exec("
CREATE TABLE IF NOT EXISTS cash_agents (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    agent_code TEXT UNIQUE NOT NULL,
    name TEXT NOT NULL,
    phone TEXT UNIQUE NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    country TEXT DEFAULT 'Bangladesh',
    city TEXT DEFAULT 'Dhaka',
    daily_limit REAL DEFAULT 500000.0,
    payment_methods TEXT DEFAULT 'bKash, Nagad, Bank Transfer',
    commission_rate REAL DEFAULT 5.0,
    wallet_balance REAL DEFAULT 0.0,
    orders_count INTEGER DEFAULT 0,
    rating REAL DEFAULT 5.0,
    status TEXT DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
)
");

// Pre-seed sample agents if table is empty
$agentCount = (int)$db->query("SELECT COUNT(*) FROM cash_agents")->fetchColumn();
if ($agentCount === 0) {
    $seedAgents = [
        [
            'agent_code' => 'AGENT-01',
            'name' => 'Dhaka Central Cash Express #01',
            'phone' => '+880 1711 223344',
            'email' => 'sumon@agent.com',
            'password' => password_hash('agent123', PASSWORD_BCRYPT),
            'country' => 'Bangladesh',
            'city' => 'Dhaka Central',
            'daily_limit' => 500000.00,
            'payment_methods' => 'bKash, Nagad, City Bank, Rocket',
            'commission_rate' => 5.0,
            'wallet_balance' => 250000.00,
            'orders_count' => 2540,
            'rating' => 4.95,
            'status' => 'ACTIVE'
        ],
        [
            'agent_code' => 'AGENT-02',
            'name' => 'Dubai Deira Exchange Agent #02',
            'phone' => '+971 4 321 4321',
            'email' => 'deira@agent.com',
            'password' => password_hash('agent123', PASSWORD_BCRYPT),
            'country' => 'UAE',
            'city' => 'Deira, Dubai',
            'daily_limit' => 75000.00,
            'payment_methods' => 'Bank Transfer, Cash Counter, FAB',
            'commission_rate' => 4.5,
            'wallet_balance' => 45000.00,
            'orders_count' => 1850,
            'rating' => 4.92,
            'status' => 'ACTIVE'
        ],
        [
            'agent_code' => 'AGENT-03',
            'name' => 'Kuala Lumpur Bukit Bintang Pay #03',
            'phone' => '+60 12 999 8888',
            'email' => 'kl@agent.com',
            'password' => password_hash('agent123', PASSWORD_BCRYPT),
            'country' => 'Malaysia',
            'city' => 'Bukit Bintang, KL',
            'daily_limit' => 80000.00,
            'payment_methods' => 'DuitNow, Maybank, CIMB Bank',
            'commission_rate' => 4.8,
            'wallet_balance' => 45000.00,
            'orders_count' => 1120,
            'rating' => 4.88,
            'status' => 'ACTIVE'
        ]
    ];

    $insAgent = $db->prepare("INSERT INTO cash_agents (agent_code, name, phone, email, password, country, city, daily_limit, payment_methods, commission_rate, wallet_balance, orders_count, rating, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    foreach ($seedAgents as $sa) {
        $insAgent->execute([
            $sa['agent_code'], $sa['name'], $sa['phone'], $sa['email'], $sa['password'],
            $sa['country'], $sa['city'], $sa['daily_limit'], $sa['payment_methods'],
            $sa['commission_rate'], $sa['wallet_balance'], $sa['orders_count'], $sa['rating'], $sa['status']
        ]);
    }
}

$msg = '';
$msgType = 'success';

// Handle POST actions
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $action = $_POST['action'] ?? '';
    $agentId = (int)($_POST['agent_id'] ?? 0);
    $agentCode = trim($_POST['agent_code'] ?? '');

    if ($action === 'create_agent') {
        // Admin creates a new cash agent
        $name = trim($_POST['name'] ?? '');
        $code = strtoupper(trim($_POST['agent_code'] ?? ''));
        $phone = trim($_POST['phone'] ?? '');
        $email = trim($_POST['email'] ?? '');
        $rawPassword = $_POST['password'] ?? 'agent123';
        $country = trim($_POST['country'] ?? 'Bangladesh');
        $city = trim($_POST['city'] ?? 'Dhaka');
        $dailyLimit = (float)($_POST['daily_limit'] ?? 500000.0);
        $initialBalance = (float)($_POST['wallet_balance'] ?? 0.0);
        $commissionRate = (float)($_POST['commission_rate'] ?? 5.0);
        $paymentMethods = trim($_POST['payment_methods'] ?? 'bKash, Nagad, Bank Transfer');
        $status = trim($_POST['status'] ?? 'ACTIVE');

        if (empty($code)) {
            $code = 'AGENT-' . str_pad((string)(rand(10, 999)), 3, '0', STR_PAD_LEFT);
        }

        if (!empty($name) && !empty($phone) && !empty($email)) {
            try {
                $hash = password_hash($rawPassword, PASSWORD_BCRYPT);
                $stmt = $db->prepare("INSERT INTO cash_agents (agent_code, name, phone, email, password, country, city, daily_limit, payment_methods, commission_rate, wallet_balance, orders_count, rating, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 5.0, ?)");
                $stmt->execute([$code, $name, $phone, $email, $hash, $country, $city, $dailyLimit, $paymentMethods, $commissionRate, $initialBalance, $status]);
                $msg = "Cash Agent '{$name}' ({$code}) created successfully! Login Email: {$email} (Password: {$rawPassword})";
                $msgType = 'success';
            } catch (Throwable $e) {
                $msg = "Failed to create agent: " . $e->getMessage();
                $msgType = 'danger';
            }
        } else {
            $msg = "Agent Name, Phone, and Email are required.";
            $msgType = 'danger';
        }
    } elseif ($action === 'edit_agent') {
        // Admin edits cash agent information
        $id = (int)($_POST['id'] ?? 0);
        $name = trim($_POST['name'] ?? '');
        $phone = trim($_POST['phone'] ?? '');
        $email = trim($_POST['email'] ?? '');
        $country = trim($_POST['country'] ?? 'Bangladesh');
        $city = trim($_POST['city'] ?? 'Dhaka');
        $dailyLimit = (float)($_POST['daily_limit'] ?? 500000.0);
        $commissionRate = (float)($_POST['commission_rate'] ?? 5.0);
        $paymentMethods = trim($_POST['payment_methods'] ?? '');
        $status = trim($_POST['status'] ?? 'ACTIVE');

        if ($id > 0 && !empty($name)) {
            try {
                $stmt = $db->prepare("UPDATE cash_agents SET name = ?, phone = ?, email = ?, country = ?, city = ?, daily_limit = ?, commission_rate = ?, payment_methods = ?, status = ? WHERE id = ?");
                $stmt->execute([$name, $phone, $email, $country, $city, $dailyLimit, $commissionRate, $paymentMethods, $status, $id]);
                $msg = "Cash agent information for '{$name}' updated successfully.";
                $msgType = 'success';
            } catch (Throwable $e) {
                $msg = "Update failed: " . $e->getMessage();
                $msgType = 'danger';
            }
        }
    } elseif ($action === 'add_balance') {
        $amount = (float)($_POST['amount'] ?? 0);
        if ($amount > 0 && $agentId > 0) {
            $stmt = $db->prepare("UPDATE cash_agents SET wallet_balance = wallet_balance + ? WHERE id = ?");
            $stmt->execute([$amount, $agentId]);
            $msg = "Added ৳" . number_format($amount, 2) . " credit to Agent wallet.";
            $msgType = 'success';
        }
    } elseif ($action === 'deduct_balance') {
        $amount = (float)($_POST['amount'] ?? 0);
        if ($amount > 0 && $agentId > 0) {
            $stmt = $db->prepare("UPDATE cash_agents SET wallet_balance = MAX(0, wallet_balance - ?) WHERE id = ?");
            $stmt->execute([$amount, $agentId]);
            $msg = "Deducted ৳" . number_format($amount, 2) . " from Agent wallet.";
            $msgType = 'warning';
        }
    } elseif ($action === 'suspend') {
        if ($agentId > 0) {
            $stmt = $db->prepare("UPDATE cash_agents SET status = 'SUSPENDED' WHERE id = ?");
            $stmt->execute([$agentId]);
            $msg = "Agent has been suspended.";
            $msgType = 'danger';
        }
    } elseif ($action === 'activate') {
        if ($agentId > 0) {
            $stmt = $db->prepare("UPDATE cash_agents SET status = 'ACTIVE' WHERE id = ?");
            $stmt->execute([$agentId]);
            $msg = "Agent has been activated.";
            $msgType = 'success';
        }
    } elseif ($action === 'delete') {
        if ($agentId > 0) {
            $stmt = $db->prepare("DELETE FROM cash_agents WHERE id = ?");
            $stmt->execute([$agentId]);
            $msg = "Agent removed permanently.";
            $msgType = 'warning';
        }
    }
}

// Fetch all agents
$agentsStmt = $db->query("SELECT * FROM cash_agents ORDER BY id DESC");
$agentsList = $agentsStmt->fetchAll(PDO::FETCH_ASSOC);

// Metrics
$totalAgents = count($agentsList);
$activeAgents = count(array_filter($agentsList, fn($a) => $a['status'] === 'ACTIVE'));
$totalEscrow = array_sum(array_map(fn($a) => (float)$a['wallet_balance'], $agentsList));
$totalOrders = array_sum(array_map(fn($a) => (int)($a['orders_count'] ?? 0), $agentsList));

renderAdminHeader('Cash Agents', 'cash_agents');
?>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: #0f172a;">Cash Agent Management</h2>
        <p class="text-secondary mb-0">Create new cash agents, monitor regional escrow liquidity, adjust transaction limits, and manage commissions.</p>
    </div>
    <!-- Admin Create Cash Agent Button -->
    <button class="btn btn-primary fw-semibold px-3 py-2 d-flex align-items-center gap-2 shadow-sm" data-bs-toggle="modal" data-bs-target="#createAgentModal">
        <i class="bi bi-person-plus-fill"></i> + Create New Cash Agent
    </button>
</div>

<?php if ($msg): ?>
    <div class="alert alert-<?= $msgType ?> alert-dismissible fade show shadow-sm" role="alert">
        <i class="bi bi-info-circle-fill me-2"></i><?= htmlspecialchars($msg) ?>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
<?php endif; ?>

<!-- Top Metrics -->
<div class="row g-3 mb-4">
    <div class="col-xl-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Total Cash Agents</div>
                <div class="fw-bold text-dark fs-4"><?= $totalAgents ?></div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-blue" style="width: 40px; height: 40px;">
                <i class="bi bi-shop"></i>
            </div>
        </div>
    </div>
    <div class="col-xl-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Active Cash Agents</div>
                <div class="fw-bold text-success fs-4"><?= $activeAgents ?></div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-green" style="width: 40px; height: 40px;">
                <i class="bi bi-check-circle-fill"></i>
            </div>
        </div>
    </div>
    <div class="col-xl-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Total Escrow Float</div>
                <div class="fw-bold text-dark fs-4">৳<?= number_format($totalEscrow, 2) ?></div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-indigo" style="width: 40px; height: 40px;">
                <i class="bi bi-wallet2"></i>
            </div>
        </div>
    </div>
    <div class="col-xl-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Total Orders Handled</div>
                <div class="fw-bold text-primary fs-4"><?= number_format($totalOrders) ?></div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-gold" style="width: 40px; height: 40px;">
                <i class="bi bi-receipt"></i>
            </div>
        </div>
    </div>
</div>

<!-- Agents Table -->
<div class="card card-custom p-0 overflow-hidden mb-4">
    <div class="p-3 bg-light border-bottom d-flex justify-content-between align-items-center flex-wrap gap-2">
        <h6 class="mb-0 fw-bold text-dark"><i class="bi bi-person-badge me-2 text-primary"></i>All Registered Cash Agents</h6>
        <span class="badge bg-white text-secondary border px-3 py-1"><?= count($agentsList) ?> Agents Registered</span>
    </div>
    <div class="table-responsive">
        <table class="table table-custom align-middle mb-0">
            <thead>
                <tr>
                    <th class="ps-4">Agent Details</th>
                    <th>Region / City</th>
                    <th>Country</th>
                    <th>Escrow Float Balance</th>
                    <th>Daily Limit</th>
                    <th>Commission Rate</th>
                    <th>Orders Handled</th>
                    <th>Status</th>
                    <th class="text-end pe-4">Actions</th>
                </tr>
            </thead>
            <tbody>
                <?php if (empty($agentsList)): ?>
                <tr>
                    <td colspan="9" class="text-center py-4 text-muted">No cash agents created yet. Click "+ Create New Cash Agent" above.</td>
                </tr>
                <?php else: ?>
                <?php foreach ($agentsList as $a): ?>
                <tr>
                    <td class="ps-4">
                        <div class="fw-bold text-dark"><?= htmlspecialchars($a['name']) ?></div>
                        <small class="text-muted"><span class="badge bg-light text-primary border me-1"><?= htmlspecialchars($a['agent_code']) ?></span> <?= htmlspecialchars($a['phone']) ?></small>
                    </td>
                    <td><span class="fw-semibold text-secondary"><?= htmlspecialchars($a['city'] ?: 'Dhaka') ?></span></td>
                    <td><span class="fw-semibold text-dark"><?= htmlspecialchars($a['country'] ?: 'Bangladesh') ?></span></td>
                    <td>
                        <span class="fw-bold text-success fs-6">৳<?= number_format((float)$a['wallet_balance'], 2) ?></span>
                    </td>
                    <td class="text-secondary fw-semibold">৳<?= number_format((float)($a['daily_limit'] ?? 500000.0), 2) ?></td>
                    <td><span class="badge bg-info-subtle text-info border border-info-subtle px-2 py-1 fw-bold"><?= number_format((float)$a['commission_rate'], 1) ?>%</span></td>
                    <td><span class="badge bg-light text-dark border"><?= number_format((int)($a['orders_count'] ?? 0)) ?> Orders</span></td>
                    <td>
                        <?php if ($a['status'] === 'ACTIVE'): ?>
                            <span class="badge-status badge-approved">Active</span>
                        <?php else: ?>
                            <span class="badge-status badge-disputed">Suspended</span>
                        <?php endif; ?>
                    </td>
                    <td class="text-end pe-4">
                        <div class="d-flex justify-content-end gap-1">
                            <!-- View Agent Modal Trigger -->
                            <button class="btn btn-sm btn-outline-info" data-bs-toggle="modal" data-bs-target="#viewAgentModal<?= $a['id'] ?>" title="View Agent Profile">
                                <i class="bi bi-eye-fill"></i> View
                            </button>
                            <!-- Edit Agent Modal Trigger -->
                            <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#editAgentModal<?= $a['id'] ?>" title="Edit Information">
                                <i class="bi bi-pencil-square"></i> Edit
                            </button>
                            <!-- Topup Balance -->
                            <button class="btn btn-sm btn-outline-success" data-bs-toggle="modal" data-bs-target="#balanceModal<?= $a['id'] ?>" title="Add Balance">
                                <i class="bi bi-plus-circle"></i>
                            </button>
                            <!-- More Actions Dropdown -->
                            <div class="dropdown d-inline">
                                <button class="btn btn-sm btn-outline-secondary dropdown-toggle" data-bs-toggle="dropdown">
                                    <i class="bi bi-three-dots-vertical"></i>
                                </button>
                                <ul class="dropdown-menu dropdown-menu-end shadow-sm">
                                    <li>
                                        <button class="dropdown-item" data-bs-toggle="modal" data-bs-target="#deductModal<?= $a['id'] ?>">
                                            <i class="bi bi-dash-circle me-2 text-warning"></i>Deduct Balance
                                        </button>
                                    </li>
                                    <li><a class="dropdown-item" href="p2p.php"><i class="bi bi-receipt me-2"></i>P2P Orders</a></li>
                                    <li><hr class="dropdown-divider"></li>
                                    <form method="POST">
                                        <input type="hidden" name="agent_id" value="<?= $a['id'] ?>">
                                        <input type="hidden" name="agent_code" value="<?= htmlspecialchars($a['agent_code']) ?>">
                                        <?php if ($a['status'] === 'ACTIVE'): ?>
                                        <li>
                                            <button type="submit" name="action" value="suspend" class="dropdown-item text-danger" onclick="return confirm('Suspend cash agent <?= htmlspecialchars($a['name']) ?>?');">
                                                <i class="bi bi-slash-circle me-2"></i>Suspend Agent
                                            </button>
                                        </li>
                                        <?php else: ?>
                                        <li>
                                            <button type="submit" name="action" value="activate" class="dropdown-item text-success">
                                                <i class="bi bi-check-circle me-2"></i>Activate Agent
                                            </button>
                                        </li>
                                        <?php endif; ?>
                                        <li>
                                            <button type="submit" name="action" value="delete" class="dropdown-item text-danger" onclick="return confirm('Permanently remove agent <?= htmlspecialchars($a['name']) ?>?');">
                                                <i class="bi bi-trash me-2"></i>Delete Agent
                                            </button>
                                        </li>
                                    </form>
                                </ul>
                            </div>
                        </div>
                    </td>
                </tr>

                <!-- View Agent Details Modal -->
                <div class="modal fade" id="viewAgentModal<?= $a['id'] ?>" tabindex="-1">
                    <div class="modal-dialog modal-dialog-centered modal-lg">
                        <div class="modal-content border-0 shadow-lg" style="border-radius: 16px;">
                            <div class="modal-header border-bottom py-3">
                                <h5 class="modal-title fw-bold d-flex align-items-center gap-2">
                                    <i class="bi bi-shop text-primary"></i>
                                    <span>Agent Details: <?= htmlspecialchars($a['name']) ?> (<?= htmlspecialchars($a['agent_code']) ?>)</span>
                                </h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body p-4">
                                <div class="row g-4">
                                    <div class="col-md-5 text-center border-end">
                                        <div class="kpi-icon-wrap kpi-icon-blue mx-auto mb-3" style="width: 70px; height: 70px; font-size: 2rem;">
                                            <i class="bi bi-cash-coin"></i>
                                        </div>
                                        <h5 class="fw-bold text-dark mb-1"><?= htmlspecialchars($a['name']) ?></h5>
                                        <span class="badge bg-primary text-white mb-2"><?= htmlspecialchars($a['agent_code']) ?></span>
                                        <div>
                                            <?php if ($a['status'] === 'ACTIVE'): ?>
                                                <span class="badge-status badge-approved">Active & Verified Agent</span>
                                            <?php else: ?>
                                                <span class="badge-status badge-disputed">Suspended</span>
                                            <?php endif; ?>
                                        </div>
                                        <div class="bg-light p-3 rounded mt-3 text-start">
                                            <small class="text-secondary fw-semibold d-block">Agent Portal Login URL:</small>
                                            <code class="small text-primary">/agent/login.php</code>
                                            <small class="text-secondary fw-semibold d-block mt-2">Login Email:</small>
                                            <span class="text-dark small fw-bold"><?= htmlspecialchars($a['email']) ?></span>
                                        </div>
                                    </div>
                                    <div class="col-md-7">
                                        <h6 class="fw-bold text-dark mb-3"><i class="bi bi-info-circle me-2 text-primary"></i>Operational Information</h6>
                                        <table class="table table-sm table-borderless align-middle mb-0">
                                            <tbody>
                                                <tr>
                                                    <td class="text-secondary fw-semibold" style="width: 150px;">Phone Number:</td>
                                                    <td class="text-dark fw-bold"><?= htmlspecialchars($a['phone']) ?></td>
                                                </tr>
                                                <tr>
                                                    <td class="text-secondary fw-semibold">Email Address:</td>
                                                    <td class="text-dark"><?= htmlspecialchars($a['email']) ?></td>
                                                </tr>
                                                <tr>
                                                    <td class="text-secondary fw-semibold">Country & City:</td>
                                                    <td class="text-dark"><?= htmlspecialchars($a['country'] ?: 'Bangladesh') ?> (<?= htmlspecialchars($a['city'] ?: 'Dhaka') ?>)</td>
                                                </tr>
                                                <tr>
                                                    <td class="text-secondary fw-semibold">Escrow Balance:</td>
                                                    <td class="text-success fw-bold fs-6">৳<?= number_format((float)$a['wallet_balance'], 2) ?> BDT</td>
                                                </tr>
                                                <tr>
                                                    <td class="text-secondary fw-semibold">Daily Limit:</td>
                                                    <td class="text-dark fw-bold">৳<?= number_format((float)($a['daily_limit'] ?? 500000.0), 2) ?> BDT</td>
                                                </tr>
                                                <tr>
                                                    <td class="text-secondary fw-semibold">Commission Rate:</td>
                                                    <td class="text-primary fw-bold"><?= number_format((float)$a['commission_rate'], 1) ?>%</td>
                                                </tr>
                                                <tr>
                                                    <td class="text-secondary fw-semibold">Payment Methods:</td>
                                                    <td class="text-dark"><?= htmlspecialchars($a['payment_methods'] ?: 'bKash, Nagad, Bank Transfer') ?></td>
                                                </tr>
                                                <tr>
                                                    <td class="text-secondary fw-semibold">Orders Handled:</td>
                                                    <td class="text-dark fw-bold"><?= number_format((int)($a['orders_count'] ?? 0)) ?> Orders</td>
                                                </tr>
                                                <tr>
                                                    <td class="text-secondary fw-semibold">Registered:</td>
                                                    <td class="text-muted"><?= htmlspecialchars(substr((string)$a['created_at'], 0, 10)) ?></td>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                            <div class="modal-footer border-top bg-light">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                <button type="button" class="btn btn-primary fw-semibold" data-bs-dismiss="modal" data-bs-toggle="modal" data-bs-target="#editAgentModal<?= $a['id'] ?>">
                                    <i class="bi bi-pencil-square me-1"></i> Edit Agent Details
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Edit Agent Details Modal -->
                <div class="modal fade" id="editAgentModal<?= $a['id'] ?>" tabindex="-1">
                    <div class="modal-dialog modal-dialog-centered modal-lg">
                        <div class="modal-content border-0 shadow-lg" style="border-radius: 16px;">
                            <div class="modal-header border-bottom py-3">
                                <h5 class="modal-title fw-bold"><i class="bi bi-pencil-square me-2 text-primary"></i>Edit Agent Details: <?= htmlspecialchars($a['name']) ?></h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <form method="POST">
                                <input type="hidden" name="action" value="edit_agent">
                                <input type="hidden" name="id" value="<?= $a['id'] ?>">
                                <div class="modal-body p-4">
                                    <div class="row g-3">
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold text-secondary">Agent / Outlet Name</label>
                                            <input type="text" name="name" class="form-control" required value="<?= htmlspecialchars($a['name']) ?>">
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold text-secondary">Agent Code (Read-Only)</label>
                                            <input type="text" class="form-control bg-light" readonly value="<?= htmlspecialchars($a['agent_code']) ?>">
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold text-secondary">Phone Number</label>
                                            <input type="text" name="phone" class="form-control" required value="<?= htmlspecialchars($a['phone']) ?>">
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold text-secondary">Email Address (Login)</label>
                                            <input type="email" name="email" class="form-control" required value="<?= htmlspecialchars($a['email']) ?>">
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold text-secondary">Country</label>
                                            <select name="country" class="form-select">
                                                <option value="Bangladesh" <?= ($a['country'] ?? '') === 'Bangladesh' ? 'selected' : '' ?>>Bangladesh</option>
                                                <option value="UAE" <?= ($a['country'] ?? '') === 'UAE' ? 'selected' : '' ?>>United Arab Emirates</option>
                                                <option value="Malaysia" <?= ($a['country'] ?? '') === 'Malaysia' ? 'selected' : '' ?>>Malaysia</option>
                                                <option value="Saudi Arabia" <?= ($a['country'] ?? '') === 'Saudi Arabia' ? 'selected' : '' ?>>Saudi Arabia</option>
                                            </select>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold text-secondary">Assigned City / Area</label>
                                            <input type="text" name="city" class="form-control" required value="<?= htmlspecialchars($a['city'] ?: 'Dhaka') ?>">
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold text-secondary">Daily Limit (৳ BDT)</label>
                                            <input type="number" step="1000" name="daily_limit" class="form-control" required value="<?= (float)($a['daily_limit'] ?? 500000.0) ?>">
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold text-secondary">Commission Rate (%)</label>
                                            <input type="number" step="0.1" name="commission_rate" class="form-control" required value="<?= (float)$a['commission_rate'] ?>">
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold text-secondary">Account Status</label>
                                            <select name="status" class="form-select">
                                                <option value="ACTIVE" <?= $a['status'] === 'ACTIVE' ? 'selected' : '' ?>>ACTIVE</option>
                                                <option value="SUSPENDED" <?= $a['status'] === 'SUSPENDED' ? 'selected' : '' ?>>SUSPENDED</option>
                                            </select>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold text-secondary">Supported Payment Methods</label>
                                            <input type="text" name="payment_methods" class="form-control" value="<?= htmlspecialchars($a['payment_methods'] ?: 'bKash, Nagad, Bank Transfer') ?>">
                                        </div>
                                    </div>
                                </div>
                                <div class="modal-footer border-top">
                                    <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                                    <button type="submit" class="btn btn-primary fw-semibold px-4">Save Changes</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- Add Balance Modal -->
                <div class="modal fade" id="balanceModal<?= $a['id'] ?>" tabindex="-1">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content border-0 shadow-lg" style="border-radius: 16px;">
                            <div class="modal-header border-bottom py-3">
                                <h5 class="modal-title fw-bold text-success"><i class="bi bi-plus-circle me-2"></i>Top-up Escrow: <?= htmlspecialchars($a['name']) ?></h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <form method="POST">
                                <input type="hidden" name="action" value="add_balance">
                                <input type="hidden" name="agent_id" value="<?= $a['id'] ?>">
                                <div class="modal-body p-4">
                                    <div class="mb-3">
                                        <label class="form-label small fw-semibold text-secondary">Current Escrow Balance</label>
                                        <div class="fs-5 fw-bold text-dark">৳<?= number_format((float)$a['wallet_balance'], 2) ?></div>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label small fw-semibold text-secondary">Amount to Credit (৳ BDT)</label>
                                        <input type="number" step="100" name="amount" class="form-control form-control-lg" required placeholder="50000" autofocus>
                                    </div>
                                </div>
                                <div class="modal-footer border-top">
                                    <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                                    <button type="submit" class="btn btn-success fw-semibold px-4">Confirm Credit</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- Deduct Balance Modal -->
                <div class="modal fade" id="deductModal<?= $a['id'] ?>" tabindex="-1">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content border-0 shadow-lg" style="border-radius: 16px;">
                            <div class="modal-header border-bottom py-3">
                                <h5 class="modal-title fw-bold text-warning"><i class="bi bi-dash-circle me-2"></i>Deduct Escrow: <?= htmlspecialchars($a['name']) ?></h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <form method="POST">
                                <input type="hidden" name="action" value="deduct_balance">
                                <input type="hidden" name="agent_id" value="<?= $a['id'] ?>">
                                <div class="modal-body p-4">
                                    <div class="mb-3">
                                        <label class="form-label small fw-semibold text-secondary">Current Escrow Balance</label>
                                        <div class="fs-5 fw-bold text-dark">৳<?= number_format((float)$a['wallet_balance'], 2) ?></div>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label small fw-semibold text-secondary">Amount to Debit (৳ BDT)</label>
                                        <input type="number" step="100" name="amount" class="form-control form-control-lg" required placeholder="10000">
                                    </div>
                                </div>
                                <div class="modal-footer border-top">
                                    <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                                    <button type="submit" class="btn btn-warning fw-semibold px-4 text-dark">Confirm Debit</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
                <?php endforeach; ?>
                <?php endif; ?>
            </tbody>
        </table>
    </div>
</div>

<!-- Admin Create Cash Agent Modal -->
<div class="modal fade" id="createAgentModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content border-0 shadow-lg" style="border-radius: 16px;">
            <div class="modal-header border-bottom py-3">
                <h5 class="modal-title fw-bold"><i class="bi bi-person-plus-fill me-2 text-primary"></i>Create & Register New Cash Agent</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form method="POST">
                <input type="hidden" name="action" value="create_agent">
                <div class="modal-body p-4">
                    <p class="text-secondary small mb-3">Admin will create the cash agent account. The agent can immediately use these credentials to log in to the Cash Agent Portal.</p>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold text-secondary">Agent / Business Name</label>
                            <input type="text" name="name" class="form-control" required placeholder="e.g. Dhanmondi Cash Point #04">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold text-secondary">Agent Code (Optional - auto generated if empty)</label>
                            <input type="text" name="agent_code" class="form-control" placeholder="e.g. AGENT-04">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold text-secondary">Phone Number</label>
                            <input type="text" name="phone" class="form-control" required placeholder="+880 1700 112233">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold text-secondary">Email Address (Agent Login)</label>
                            <input type="email" name="email" class="form-control" required placeholder="agent@outlet.com">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold text-secondary">Initial Login Password</label>
                            <input type="password" name="password" class="form-control" required placeholder="Default: agent123" value="agent123">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold text-secondary">Operating Country</label>
                            <select name="country" class="form-select">
                                <option value="Bangladesh" selected>Bangladesh</option>
                                <option value="UAE">United Arab Emirates</option>
                                <option value="Malaysia">Malaysia</option>
                                <option value="Saudi Arabia">Saudi Arabia</option>
                                <option value="United States">United States</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold text-secondary">Assigned City / Area</label>
                            <input type="text" name="city" class="form-control" required placeholder="e.g. Dhanmondi, Dhaka" value="Dhanmondi, Dhaka">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold text-secondary">Initial Escrow Float Balance (৳ BDT)</label>
                            <input type="number" step="1000" name="wallet_balance" class="form-control" value="50000" placeholder="50000">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold text-secondary">Daily Limit (৳ BDT)</label>
                            <input type="number" step="1000" name="daily_limit" class="form-control" value="500000" placeholder="500000">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold text-secondary">Commission Rate (%)</label>
                            <input type="number" step="0.1" name="commission_rate" class="form-control" value="5.0" placeholder="5.0">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold text-secondary">Status</label>
                            <select name="status" class="form-select">
                                <option value="ACTIVE" selected>ACTIVE (Ready for orders)</option>
                                <option value="SUSPENDED">SUSPENDED</option>
                            </select>
                        </div>
                        <div class="col-12">
                            <label class="form-label small fw-semibold text-secondary">Supported Payment Methods</label>
                            <input type="text" name="payment_methods" class="form-control" value="bKash, Nagad, Rocket, Bank Transfer, Cash Counter">
                        </div>
                    </div>
                </div>
                <div class="modal-footer border-top">
                    <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary fw-semibold px-4">Create Cash Agent</button>
                </div>
            </form>
        </div>
    </div>
</div>

<?php
renderAdminFooter();
?>
