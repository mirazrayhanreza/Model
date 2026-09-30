<?php
declare(strict_types=1);

// backend/admin/country_currency.php
// Multi-Country & Currency Exchange Control (PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';
require_once __DIR__ . '/layout.php';

checkAdminAuth();
$db = Database::getInstance();

// Ensure table exists
$db->exec("
CREATE TABLE IF NOT EXISTS countries_currencies (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    country_code TEXT NOT NULL,
    country_name TEXT NOT NULL,
    flag TEXT DEFAULT '🌐',
    currency_code TEXT NOT NULL,
    currency_symbol TEXT NOT NULL,
    rate_to_usd REAL NOT NULL DEFAULT 1.0,
    min_deposit REAL DEFAULT 500.0,
    min_withdrawal REAL DEFAULT 1000.0,
    status TEXT DEFAULT 'Active',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
)
");

// Pre-seed default countries if table is empty
$countStmt = $db->query("SELECT COUNT(*) FROM countries_currencies");
if ((int)$countStmt->fetchColumn() === 0) {
    $defaults = [
        ['BD', 'Bangladesh', '🇧🇩', 'BDT', '৳', 120.50, 500.00, 1000.00, 'Active'],
        ['AE', 'United Arab Emirates', '🇦🇪', 'AED', 'د.إ', 3.67, 50.00, 100.00, 'Active'],
        ['MY', 'Malaysia', '🇲🇾', 'MYR', 'RM', 4.42, 50.00, 100.00, 'Active'],
        ['SA', 'Saudi Arabia', '🇸🇦', 'SAR', '﷼', 3.75, 50.00, 100.00, 'Active'],
        ['US', 'United States', '🇺🇸', 'USD', '$', 1.00, 10.00, 20.00, 'Active'],
        ['GB', 'United Kingdom', '🇬🇧', 'GBP', '£', 0.78, 10.00, 20.00, 'Active'],
        ['EU', 'European Union', '🇪🇺', 'EUR', '€', 0.92, 10.00, 20.00, 'Active'],
        ['IN', 'India', '🇮🇳', 'INR', '₹', 83.50, 500.00, 1000.00, 'Active']
    ];
    $ins = $db->prepare("INSERT INTO countries_currencies (country_code, country_name, flag, currency_code, currency_symbol, rate_to_usd, min_deposit, min_withdrawal, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
    foreach ($defaults as $d) {
        $ins->execute($d);
    }
}

$msg = '';
$msgType = 'success';

// Handle POST actions
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $action = $_POST['action'] ?? '';

    if ($action === 'add') {
        $countryName = trim($_POST['country_name'] ?? '');
        $countryCode = strtoupper(trim($_POST['country_code'] ?? ''));
        $flag = trim($_POST['flag'] ?? '🌐');
        $currencyCode = strtoupper(trim($_POST['currency_code'] ?? ''));
        $currencySymbol = trim($_POST['currency_symbol'] ?? '');
        $rateToUsd = (float)($_POST['rate_to_usd'] ?? 1.0);
        $minDeposit = (float)($_POST['min_deposit'] ?? 100.0);
        $minWithdrawal = (float)($_POST['min_withdrawal'] ?? 200.0);
        $status = $_POST['status'] ?? 'Active';

        if (!empty($countryName) && !empty($currencyCode)) {
            $stmt = $db->prepare("INSERT INTO countries_currencies (country_code, country_name, flag, currency_code, currency_symbol, rate_to_usd, min_deposit, min_withdrawal, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            $stmt->execute([$countryCode, $countryName, $flag, $currencyCode, $currencySymbol, $rateToUsd, $minDeposit, $minWithdrawal, $status]);
            $msg = "Country & Currency for '{$countryName} ({$currencyCode})' added successfully!";
            $msgType = 'success';
        } else {
            $msg = "Country Name and Currency Code are required.";
            $msgType = 'danger';
        }
    } elseif ($action === 'edit') {
        $id = (int)($_POST['id'] ?? 0);
        $countryName = trim($_POST['country_name'] ?? '');
        $countryCode = strtoupper(trim($_POST['country_code'] ?? ''));
        $flag = trim($_POST['flag'] ?? '🌐');
        $currencyCode = strtoupper(trim($_POST['currency_code'] ?? ''));
        $currencySymbol = trim($_POST['currency_symbol'] ?? '');
        $rateToUsd = (float)($_POST['rate_to_usd'] ?? 1.0);
        $minDeposit = (float)($_POST['min_deposit'] ?? 100.0);
        $minWithdrawal = (float)($_POST['min_withdrawal'] ?? 200.0);
        $status = $_POST['status'] ?? 'Active';

        if ($id > 0 && !empty($countryName)) {
            $stmt = $db->prepare("UPDATE countries_currencies SET country_code = ?, country_name = ?, flag = ?, currency_code = ?, currency_symbol = ?, rate_to_usd = ?, min_deposit = ?, min_withdrawal = ?, status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?");
            $stmt->execute([$countryCode, $countryName, $flag, $currencyCode, $currencySymbol, $rateToUsd, $minDeposit, $minWithdrawal, $status, $id]);
            $msg = "Updated currency rate & country settings for '{$countryName}'.";
            $msgType = 'success';
        }
    } elseif ($action === 'toggle_status') {
        $id = (int)($_POST['id'] ?? 0);
        $currentStatus = $_POST['current_status'] ?? 'Active';
        $newStatus = ($currentStatus === 'Active') ? 'Inactive' : 'Active';
        if ($id > 0) {
            $stmt = $db->prepare("UPDATE countries_currencies SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?");
            $stmt->execute([$newStatus, $id]);
            $msg = "Status changed to {$newStatus}.";
            $msgType = 'info';
        }
    } elseif ($action === 'delete') {
        $id = (int)($_POST['id'] ?? 0);
        if ($id > 0) {
            $stmt = $db->prepare("DELETE FROM countries_currencies WHERE id = ?");
            $stmt->execute([$id]);
            $msg = "Country and currency rate deleted.";
            $msgType = 'warning';
        }
    }
}

// Fetch all countries
$countriesStmt = $db->query("SELECT * FROM countries_currencies ORDER BY id ASC");
$countries = $countriesStmt->fetchAll(PDO::FETCH_ASSOC);

$totalCountries = count($countries);
$activeCountries = count(array_filter($countries, fn($c) => $c['status'] === 'Active'));

renderAdminHeader('Country & Currency', 'country_currency');
?>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: #0f172a;">Country & Currency Exchange</h2>
        <p class="text-secondary mb-0">Manage operating regions, local payment currencies, and real-time forex exchange rates.</p>
    </div>
    <div class="d-flex gap-2">
        <button class="btn btn-primary fw-semibold px-3 py-2 d-flex align-items-center gap-2 shadow-sm" data-bs-toggle="modal" data-bs-target="#addCountryModal">
            <i class="bi bi-plus-circle-fill"></i> Add New Country / Currency
        </button>
    </div>
</div>

<?php if ($msg): ?>
    <div class="alert alert-<?= $msgType ?> alert-dismissible fade show shadow-sm" role="alert">
        <i class="bi bi-check-circle-fill me-2"></i><?= htmlspecialchars($msg) ?>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
<?php endif; ?>

<!-- Summary Cards -->
<div class="row g-3 mb-4">
    <div class="col-xl-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Total Countries</div>
                <div class="fw-bold text-dark fs-4"><?= $totalCountries ?></div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-blue" style="width: 42px; height: 42px; font-size: 1.2rem;">
                <i class="bi bi-globe-americas"></i>
            </div>
        </div>
    </div>
    <div class="col-xl-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Active Currencies</div>
                <div class="fw-bold text-success fs-4"><?= $activeCountries ?></div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-green" style="width: 42px; height: 42px; font-size: 1.2rem;">
                <i class="bi bi-cash-stack"></i>
            </div>
        </div>
    </div>
    <div class="col-xl-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Base Reference Currency</div>
                <div class="fw-bold text-primary fs-4">USD ($)</div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-indigo" style="width: 42px; height: 42px; font-size: 1.2rem;">
                <i class="bi bi-currency-exchange"></i>
            </div>
        </div>
    </div>
    <div class="col-xl-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Primary Local Hub</div>
                <div class="fw-bold text-dark fs-4">BDT (৳) 120.50</div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-gold" style="width: 42px; height: 42px; font-size: 1.2rem;">
                <i class="bi bi-graph-up-arrow"></i>
            </div>
        </div>
    </div>
</div>

<!-- Table Card -->
<div class="card card-custom p-0 overflow-hidden mb-4">
    <div class="p-3 bg-light border-bottom d-flex justify-content-between align-items-center flex-wrap gap-2">
        <h6 class="mb-0 fw-bold text-dark"><i class="bi bi-list-ul me-2 text-primary"></i>Configured Operating Regions & Forex Rates</h6>
        <span class="badge bg-white text-secondary border px-3 py-2">Updated: <?= date('d M Y, h:i A') ?></span>
    </div>
    <div class="table-responsive">
        <table class="table table-custom align-middle mb-0">
            <thead>
                <tr>
                    <th class="ps-4">Country & Code</th>
                    <th>Currency Code</th>
                    <th>Symbol</th>
                    <th>Forex Rate (vs 1 USD)</th>
                    <th>Min Deposit</th>
                    <th>Min Withdrawal</th>
                    <th>Status</th>
                    <th class="text-end pe-4">Actions</th>
                </tr>
            </thead>
            <tbody>
                <?php if (empty($countries)): ?>
                <tr>
                    <td colspan="8" class="text-center py-4 text-muted">No countries configured yet. Click "Add New Country / Currency" to add one.</td>
                </tr>
                <?php else: ?>
                <?php foreach ($countries as $c): ?>
                <tr>
                    <td class="ps-4">
                        <div class="d-flex align-items-center gap-2">
                            <span class="fs-4"><?= htmlspecialchars($c['flag'] ?: '🌐') ?></span>
                            <div>
                                <strong class="text-dark d-block"><?= htmlspecialchars($c['country_name']) ?></strong>
                                <small class="text-muted fw-semibold">ISO: <?= htmlspecialchars($c['country_code']) ?></small>
                            </div>
                        </div>
                    </td>
                    <td><span class="badge bg-light text-dark border px-2 py-1 fs-6 fw-bold"><?= htmlspecialchars($c['currency_code']) ?></span></td>
                    <td><span class="fw-bold fs-5 text-dark"><?= htmlspecialchars($c['currency_symbol']) ?></span></td>
                    <td>
                        <span class="fw-bold text-primary fs-6">1 USD = <?= number_format((float)$c['rate_to_usd'], 4) ?> <?= htmlspecialchars($c['currency_code']) ?></span>
                    </td>
                    <td><span class="text-secondary fw-semibold"><?= htmlspecialchars($c['currency_symbol']) ?><?= number_format((float)$c['min_deposit'], 2) ?></span></td>
                    <td><span class="text-secondary fw-semibold"><?= htmlspecialchars($c['currency_symbol']) ?><?= number_format((float)$c['min_withdrawal'], 2) ?></span></td>
                    <td>
                        <?php if ($c['status'] === 'Active'): ?>
                            <span class="badge-status badge-approved">Active</span>
                        <?php else: ?>
                            <span class="badge-status badge-disputed">Inactive</span>
                        <?php endif; ?>
                    </td>
                    <td class="text-end pe-4">
                        <div class="d-flex justify-content-end gap-1">
                            <button class="btn btn-sm btn-outline-info" data-bs-toggle="modal" data-bs-target="#viewModal<?= $c['id'] ?>" title="View Details">
                                <i class="bi bi-eye"></i> View
                            </button>
                            <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#editModal<?= $c['id'] ?>" title="Edit Rate & Settings">
                                <i class="bi bi-pencil-square"></i> Edit
                            </button>
                            <form method="POST" class="d-inline" onsubmit="return confirm('Toggle status for <?= htmlspecialchars($c['country_name']) ?>?');">
                                <input type="hidden" name="action" value="toggle_status">
                                <input type="hidden" name="id" value="<?= $c['id'] ?>">
                                <input type="hidden" name="current_status" value="<?= $c['status'] ?>">
                                <button type="submit" class="btn btn-sm <?= $c['status'] === 'Active' ? 'btn-outline-warning' : 'btn-outline-success' ?>" title="Toggle Active/Inactive">
                                    <i class="bi <?= $c['status'] === 'Active' ? 'bi-pause-circle' : 'bi-play-circle' ?>"></i>
                                </button>
                            </form>
                            <form method="POST" class="d-inline" onsubmit="return confirm('Are you sure you want to delete <?= htmlspecialchars($c['country_name']) ?>?');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="<?= $c['id'] ?>">
                                <button type="submit" class="btn btn-sm btn-outline-danger" title="Delete">
                                    <i class="bi bi-trash"></i>
                                </button>
                            </form>
                        </div>
                    </td>
                </tr>

                <!-- View Modal -->
                <div class="modal fade" id="viewModal<?= $c['id'] ?>" tabindex="-1">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content border-0 shadow-lg" style="border-radius: 16px;">
                            <div class="modal-header border-bottom py-3">
                                <h5 class="modal-title fw-bold d-flex align-items-center gap-2">
                                    <span class="fs-4"><?= htmlspecialchars($c['flag'] ?: '🌐') ?></span>
                                    <span><?= htmlspecialchars($c['country_name']) ?> Details</span>
                                </h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body p-4">
                                <div class="bg-light p-3 rounded mb-3 text-center">
                                    <div class="fs-1"><?= htmlspecialchars($c['flag'] ?: '🌐') ?></div>
                                    <h4 class="fw-bold mb-0 text-dark"><?= htmlspecialchars($c['country_name']) ?> (<?= htmlspecialchars($c['country_code']) ?>)</h4>
                                    <span class="badge bg-primary text-white mt-1"><?= htmlspecialchars($c['currency_code']) ?> - <?= htmlspecialchars($c['currency_symbol']) ?></span>
                                </div>
                                <ul class="list-group list-group-flush mb-3">
                                    <li class="list-group-item d-flex justify-content-between px-0">
                                        <span class="text-muted">Forex Rate (vs USD):</span>
                                        <strong class="text-primary">1 USD = <?= number_format((float)$c['rate_to_usd'], 4) ?> <?= htmlspecialchars($c['currency_code']) ?></strong>
                                    </li>
                                    <li class="list-group-item d-flex justify-content-between px-0">
                                        <span class="text-muted">1 <?= htmlspecialchars($c['currency_code']) ?> to USD:</span>
                                        <strong>$<?= $c['rate_to_usd'] > 0 ? number_format(1 / (float)$c['rate_to_usd'], 4) : '0.00' ?> USD</strong>
                                    </li>
                                    <li class="list-group-item d-flex justify-content-between px-0">
                                        <span class="text-muted">Minimum Deposit:</span>
                                        <strong><?= htmlspecialchars($c['currency_symbol']) ?><?= number_format((float)$c['min_deposit'], 2) ?> <?= htmlspecialchars($c['currency_code']) ?></strong>
                                    </li>
                                    <li class="list-group-item d-flex justify-content-between px-0">
                                        <span class="text-muted">Minimum Withdrawal:</span>
                                        <strong><?= htmlspecialchars($c['currency_symbol']) ?><?= number_format((float)$c['min_withdrawal'], 2) ?> <?= htmlspecialchars($c['currency_code']) ?></strong>
                                    </li>
                                    <li class="list-group-item d-flex justify-content-between px-0">
                                        <span class="text-muted">Status:</span>
                                        <span class="badge-status <?= $c['status'] === 'Active' ? 'badge-approved' : 'badge-disputed' ?>"><?= $c['status'] ?></span>
                                    </li>
                                </ul>
                            </div>
                            <div class="modal-footer border-top">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                <button type="button" class="btn btn-primary" data-bs-dismiss="modal" data-bs-toggle="modal" data-bs-target="#editModal<?= $c['id'] ?>">Edit Settings</button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Edit Modal -->
                <div class="modal fade" id="editModal<?= $c['id'] ?>" tabindex="-1">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content border-0 shadow-lg" style="border-radius: 16px;">
                            <div class="modal-header border-bottom py-3">
                                <h5 class="modal-title fw-bold">Edit Country & Currency Settings</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <form method="POST">
                                <input type="hidden" name="action" value="edit">
                                <input type="hidden" name="id" value="<?= $c['id'] ?>">
                                <div class="modal-body p-4">
                                    <div class="row g-3">
                                        <div class="col-8">
                                            <label class="form-label small fw-semibold text-secondary">Country Name</label>
                                            <input type="text" name="country_name" class="form-control" required value="<?= htmlspecialchars($c['country_name']) ?>">
                                        </div>
                                        <div class="col-4">
                                            <label class="form-label small fw-semibold text-secondary">Flag (Emoji)</label>
                                            <input type="text" name="flag" class="form-control" required value="<?= htmlspecialchars($c['flag']) ?>">
                                        </div>
                                        <div class="col-6">
                                            <label class="form-label small fw-semibold text-secondary">Country Code (ISO 2)</label>
                                            <input type="text" name="country_code" class="form-control" maxlength="5" required value="<?= htmlspecialchars($c['country_code']) ?>">
                                        </div>
                                        <div class="col-6">
                                            <label class="form-label small fw-semibold text-secondary">Currency Code</label>
                                            <input type="text" name="currency_code" class="form-control" maxlength="5" required value="<?= htmlspecialchars($c['currency_code']) ?>">
                                        </div>
                                        <div class="col-6">
                                            <label class="form-label small fw-semibold text-secondary">Currency Symbol</label>
                                            <input type="text" name="currency_symbol" class="form-control" required value="<?= htmlspecialchars($c['currency_symbol']) ?>">
                                        </div>
                                        <div class="col-6">
                                            <label class="form-label small fw-semibold text-secondary">Rate to 1 USD</label>
                                            <input type="number" step="0.0001" name="rate_to_usd" class="form-control" required value="<?= (float)$c['rate_to_usd'] ?>">
                                        </div>
                                        <div class="col-6">
                                            <label class="form-label small fw-semibold text-secondary">Min Deposit</label>
                                            <input type="number" step="1" name="min_deposit" class="form-control" required value="<?= (float)$c['min_deposit'] ?>">
                                        </div>
                                        <div class="col-6">
                                            <label class="form-label small fw-semibold text-secondary">Min Withdrawal</label>
                                            <input type="number" step="1" name="min_withdrawal" class="form-control" required value="<?= (float)$c['min_withdrawal'] ?>">
                                        </div>
                                        <div class="col-12">
                                            <label class="form-label small fw-semibold text-secondary">Operating Status</label>
                                            <select name="status" class="form-select">
                                                <option value="Active" <?= $c['status'] === 'Active' ? 'selected' : '' ?>>Active (Enabled for users & deposits)</option>
                                                <option value="Inactive" <?= $c['status'] === 'Inactive' ? 'selected' : '' ?>>Inactive (Temporarily suspended)</option>
                                            </select>
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
                <?php endforeach; ?>
                <?php endif; ?>
            </tbody>
        </table>
    </div>
</div>

<!-- Add New Country & Currency Modal -->
<div class="modal fade" id="addCountryModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg" style="border-radius: 16px;">
            <div class="modal-header border-bottom py-3">
                <h5 class="modal-title fw-bold"><i class="bi bi-globe-americas me-2 text-primary"></i>Add New Country & Currency</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form method="POST">
                <input type="hidden" name="action" value="add">
                <div class="modal-body p-4">
                    <div class="row g-3">
                        <div class="col-8">
                            <label class="form-label small fw-semibold text-secondary">Country Name</label>
                            <input type="text" name="country_name" class="form-control" required placeholder="e.g. Qatar">
                        </div>
                        <div class="col-4">
                            <label class="form-label small fw-semibold text-secondary">Flag (Emoji)</label>
                            <input type="text" name="flag" class="form-control" value="🇶🇦" required placeholder="🇶🇦">
                        </div>
                        <div class="col-6">
                            <label class="form-label small fw-semibold text-secondary">Country Code (ISO 2)</label>
                            <input type="text" name="country_code" class="form-control" maxlength="5" required placeholder="QA">
                        </div>
                        <div class="col-6">
                            <label class="form-label small fw-semibold text-secondary">Currency Code</label>
                            <input type="text" name="currency_code" class="form-control" maxlength="5" required placeholder="QAR">
                        </div>
                        <div class="col-6">
                            <label class="form-label small fw-semibold text-secondary">Currency Symbol</label>
                            <input type="text" name="currency_symbol" class="form-control" required placeholder="ر.ق">
                        </div>
                        <div class="col-6">
                            <label class="form-label small fw-semibold text-secondary">Forex Rate (vs 1 USD)</label>
                            <input type="number" step="0.0001" name="rate_to_usd" class="form-control" required placeholder="3.64" value="3.64">
                        </div>
                        <div class="col-6">
                            <label class="form-label small fw-semibold text-secondary">Min Deposit</label>
                            <input type="number" step="1" name="min_deposit" class="form-control" required value="50">
                        </div>
                        <div class="col-6">
                            <label class="form-label small fw-semibold text-secondary">Min Withdrawal</label>
                            <input type="number" step="1" name="min_withdrawal" class="form-control" required value="100">
                        </div>
                        <div class="col-12">
                            <label class="form-label small fw-semibold text-secondary">Operating Status</label>
                            <select name="status" class="form-select">
                                <option value="Active">Active (Accept Transactions)</option>
                                <option value="Inactive">Inactive (Draft / Hidden)</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="modal-footer border-top">
                    <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary fw-semibold px-4">Add Country & Currency</button>
                </div>
            </form>
        </div>
    </div>
</div>

<?php
renderAdminFooter();
?>
