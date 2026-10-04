<?php
declare(strict_types=1);

// backend/admin/country_currency.php
// Binance-Style Dynamic Multi-Country & Payment Method Architecture (PHP 8.2+)

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
} catch (Throwable $e) {
    error_log("Country currency DB connect notice: " . $e->getMessage());
}

// 1. Ensure tables exist (Driver-aware and exception-safe)
if ($db) {
    try {
        if (Database::isMySQL()) {
            $db->exec("
            CREATE TABLE IF NOT EXISTS `countries` (
                `id` INT AUTO_INCREMENT PRIMARY KEY,
                `country_name` VARCHAR(100) NOT NULL,
                `iso_code` VARCHAR(10) NOT NULL,
                `phone_code` VARCHAR(15) NOT NULL,
                `currency_code` VARCHAR(10) NOT NULL,
                `flag` VARCHAR(20) DEFAULT '🌐',
                `status` VARCHAR(20) DEFAULT 'Active',
                `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            ");
            $db->exec("
            CREATE TABLE IF NOT EXISTS `payment_methods` (
                `id` INT AUTO_INCREMENT PRIMARY KEY,
                `country_id` INT NOT NULL,
                `method_name` VARCHAR(100) NOT NULL,
                `method_type` VARCHAR(50) DEFAULT 'Mobile Wallet',
                `logo` VARCHAR(255) DEFAULT '',
                `min_amount` DECIMAL(12,2) DEFAULT 100.00,
                `max_amount` DECIMAL(12,2) DEFAULT 500000.00,
                `status` VARCHAR(20) DEFAULT 'Active',
                `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            ");
            $db->exec("
            CREATE TABLE IF NOT EXISTS `countries_currencies` (
                `id` INT AUTO_INCREMENT PRIMARY KEY,
                `country_code` VARCHAR(10) NOT NULL,
                `country_name` VARCHAR(100) NOT NULL,
                `flag` VARCHAR(20) DEFAULT '🌐',
                `currency_code` VARCHAR(10) NOT NULL,
                `currency_symbol` VARCHAR(10) NOT NULL,
                `rate_to_usd` DECIMAL(12,4) NOT NULL DEFAULT 1.0000,
                `min_deposit` DECIMAL(12,2) DEFAULT 500.00,
                `min_withdrawal` DECIMAL(12,2) DEFAULT 1000.00,
                `status` VARCHAR(20) DEFAULT 'Active',
                `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            ");
        } else {
            $db->exec("
            CREATE TABLE IF NOT EXISTS countries (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                country_name TEXT NOT NULL,
                iso_code TEXT NOT NULL,
                phone_code TEXT NOT NULL,
                currency_code TEXT NOT NULL,
                flag TEXT DEFAULT '🌐',
                status TEXT DEFAULT 'Active',
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            );
            CREATE TABLE IF NOT EXISTS payment_methods (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                country_id INTEGER NOT NULL,
                method_name TEXT NOT NULL,
                method_type TEXT DEFAULT 'Mobile Wallet',
                logo TEXT DEFAULT '',
                min_amount REAL DEFAULT 100.0,
                max_amount REAL DEFAULT 500000.0,
                status TEXT DEFAULT 'Active',
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            );
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
            );
            ");
        }
    } catch (Throwable $e) {
        error_log("Country/currency table check notice: " . $e->getMessage());
    }

    // Pre-seed 61 countries and methods if empty
    try {
        $countCountries = (int)$db->query("SELECT COUNT(*) FROM countries")->fetchColumn();
        if ($countCountries === 0) {
            $seedFile = __DIR__ . '/../config/country_payment_seed.php';
            $seedData = file_exists($seedFile) ? require $seedFile : [];

            if (!empty($seedData)) {
                $insCountry = $db->prepare("INSERT INTO countries (country_name, iso_code, phone_code, currency_code, flag, status) VALUES (?, ?, ?, ?, ?, 'Active')");
                $insMethod = $db->prepare("INSERT INTO payment_methods (country_id, method_name, method_type, logo, min_amount, max_amount, status) VALUES (?, ?, ?, '', ?, ?, 'Active')");

                foreach ($seedData as $c) {
                    $insCountry->execute([$c['country_name'], $c['iso_code'], $c['phone_code'], $c['currency_code'], $c['flag']]);
                    $cId = (int)$db->lastInsertId();
                    foreach ($c['methods'] as $m) {
                        $insMethod->execute([$cId, $m['name'], $m['type'], (float)$m['min'], (float)$m['max']]);
                    }
                }
            }
        }
    } catch (Throwable $e) {
        error_log("Country/currency seeding notice: " . $e->getMessage());
    }
}

// 2. Handle POST Actions (Fully Exception-Safe & Validated)
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $action = $_POST['action'] ?? '';

    if (!$db) {
        $msg = "Database connection unavailable. Cannot perform operation.";
        $msgType = "danger";
    } else {
        try {
            if ($action === 'seed_defaults') {
                $seedFile = __DIR__ . '/../config/country_payment_seed.php';
                $seedData = file_exists($seedFile) ? require $seedFile : [];

                if (!empty($seedData)) {
                    $db->exec("DELETE FROM payment_methods");
                    $db->exec("DELETE FROM countries");
                    $insCountry = $db->prepare("INSERT INTO countries (country_name, iso_code, phone_code, currency_code, flag, status) VALUES (?, ?, ?, ?, ?, 'Active')");
                    $insMethod = $db->prepare("INSERT INTO payment_methods (country_id, method_name, method_type, logo, min_amount, max_amount, status) VALUES (?, ?, ?, '', ?, ?, 'Active')");

                    $cCount = 0;
                    $mCount = 0;
                    foreach ($seedData as $c) {
                        $insCountry->execute([$c['country_name'], $c['iso_code'], $c['phone_code'], $c['currency_code'], $c['flag']]);
                        $cId = (int)$db->lastInsertId();
                        $cCount++;
                        foreach ($c['methods'] as $m) {
                            $insMethod->execute([$cId, $m['name'], $m['type'], (float)$m['min'], (float)$m['max']]);
                            $mCount++;
                        }
                    }
                    $msg = "Successfully re-seeded {$cCount} countries and {$mCount} dynamic payment methods!";
                    $msgType = 'success';
                } else {
                    $msg = "Seed configuration file not found.";
                    $msgType = 'warning';
                }
            } elseif ($action === 'add_country') {
                $name = trim($_POST['country_name'] ?? '');
                $iso = strtoupper(trim($_POST['iso_code'] ?? ''));
                $phone = trim($_POST['phone_code'] ?? '');
                $curr = strtoupper(trim($_POST['currency_code'] ?? ''));
                $flag = trim($_POST['flag'] ?? '🌐');
                $status = in_array($_POST['status'] ?? '', ['Active', 'Inactive'], true) ? $_POST['status'] : 'Active';

                if (!empty($name) && !empty($curr)) {
                    $stmt = $db->prepare("INSERT INTO countries (country_name, iso_code, phone_code, currency_code, flag, status) VALUES (?, ?, ?, ?, ?, ?)");
                    $stmt->execute([$name, $iso, $phone, $curr, $flag, $status]);
                    $msg = "Country '{$name} ({$curr})' added successfully!";
                    $msgType = 'success';
                } else {
                    $msg = "Country Name and Currency Code are required.";
                    $msgType = 'danger';
                }
            } elseif ($action === 'edit_country') {
                $id = (int)($_POST['id'] ?? 0);
                $name = trim($_POST['country_name'] ?? '');
                $iso = strtoupper(trim($_POST['iso_code'] ?? ''));
                $phone = trim($_POST['phone_code'] ?? '');
                $curr = strtoupper(trim($_POST['currency_code'] ?? ''));
                $flag = trim($_POST['flag'] ?? '🌐');
                $status = in_array($_POST['status'] ?? '', ['Active', 'Inactive'], true) ? $_POST['status'] : 'Active';

                if ($id > 0 && !empty($name) && !empty($curr)) {
                    $stmt = $db->prepare("UPDATE countries SET country_name = ?, iso_code = ?, phone_code = ?, currency_code = ?, flag = ?, status = ? WHERE id = ?");
                    $stmt->execute([$name, $iso, $phone, $curr, $flag, $status, $id]);
                    $msg = "Country '{$name}' (#{$id}) updated successfully!";
                    $msgType = 'success';
                } else {
                    $msg = "Valid Country ID, Name, and Currency are required for update.";
                    $msgType = 'danger';
                }
            } elseif ($action === 'toggle_country_status') {
                $id = (int)($_POST['id'] ?? 0);
                $currStatus = $_POST['current_status'] ?? 'Active';
                $newStatus = ($currStatus === 'Active') ? 'Inactive' : 'Active';
                if ($id > 0) {
                    $stmt = $db->prepare("UPDATE countries SET status = ? WHERE id = ?");
                    $stmt->execute([$newStatus, $id]);
                    $msg = "Country status updated to {$newStatus}.";
                    $msgType = 'info';
                }
            } elseif ($action === 'delete_country') {
                $id = (int)($_POST['id'] ?? 0);
                if ($id > 0) {
                    $db->prepare("DELETE FROM payment_methods WHERE country_id = ?")->execute([$id]);
                    $db->prepare("DELETE FROM countries WHERE id = ?")->execute([$id]);
                    $msg = "Country and all its bound payment methods deleted.";
                    $msgType = 'warning';
                }
            } elseif ($action === 'add_payment_method') {
                $countryId = (int)($_POST['country_id'] ?? 0);
                $methodName = trim($_POST['method_name'] ?? '');
                $methodType = trim($_POST['method_type'] ?? 'Mobile Wallet');
                $minAmount = max(1.0, (float)($_POST['min_amount'] ?? 100.0));
                $maxAmount = max($minAmount, (float)($_POST['max_amount'] ?? 500000.0));
                $status = in_array($_POST['status'] ?? '', ['Active', 'Inactive'], true) ? $_POST['status'] : 'Active';

                if ($countryId > 0 && !empty($methodName)) {
                    $stmt = $db->prepare("INSERT INTO payment_methods (country_id, method_name, method_type, logo, min_amount, max_amount, status) VALUES (?, ?, ?, '', ?, ?, ?)");
                    $stmt->execute([$countryId, $methodName, $methodType, $minAmount, $maxAmount, $status]);
                    $msg = "Payment method '{$methodName}' ({$methodType}) added successfully!";
                    $msgType = 'success';
                } else {
                    $msg = "Please select a valid Country and provide Payment Method Name.";
                    $msgType = 'danger';
                }
            } elseif ($action === 'edit_payment_method') {
                $id = (int)($_POST['id'] ?? 0);
                $countryId = (int)($_POST['country_id'] ?? 0);
                $methodName = trim($_POST['method_name'] ?? '');
                $methodType = trim($_POST['method_type'] ?? 'Mobile Wallet');
                $minAmount = max(1.0, (float)($_POST['min_amount'] ?? 100.0));
                $maxAmount = max($minAmount, (float)($_POST['max_amount'] ?? 500000.0));
                $status = in_array($_POST['status'] ?? '', ['Active', 'Inactive'], true) ? $_POST['status'] : 'Active';

                if ($id > 0 && $countryId > 0 && !empty($methodName)) {
                    $stmt = $db->prepare("UPDATE payment_methods SET country_id = ?, method_name = ?, method_type = ?, min_amount = ?, max_amount = ?, status = ? WHERE id = ?");
                    $stmt->execute([$countryId, $methodName, $methodType, $minAmount, $maxAmount, $status, $id]);
                    $msg = "Payment method '{$methodName}' (#{$id}) updated successfully!";
                    $msgType = 'success';
                } else {
                    $msg = "Valid Method ID, Country, and Method Name are required for update.";
                    $msgType = 'danger';
                }
            } elseif ($action === 'toggle_method_status') {
                $id = (int)($_POST['id'] ?? 0);
                $currStatus = $_POST['current_status'] ?? 'Active';
                $newStatus = ($currStatus === 'Active') ? 'Inactive' : 'Active';
                if ($id > 0) {
                    $stmt = $db->prepare("UPDATE payment_methods SET status = ? WHERE id = ?");
                    $stmt->execute([$newStatus, $id]);
                    $msg = "Payment method status updated to {$newStatus}.";
                    $msgType = 'info';
                }
            } elseif ($action === 'delete_payment_method') {
                $id = (int)($_POST['id'] ?? 0);
                if ($id > 0) {
                    $db->prepare("DELETE FROM payment_methods WHERE id = ?")->execute([$id]);
                    $msg = "Payment method deleted successfully.";
                    $msgType = 'warning';
                }
            }
        } catch (Throwable $e) {
            error_log("Country/currency POST error: " . $e->getMessage());
            $msg = "Operation notice: " . $e->getMessage();
            $msgType = 'danger';
        }
    }
}

// 3. Fetch all countries and payment methods (Driver-safe with fallback)
$countries = [];
$methodsByCountry = [];
$allMethods = [];

if ($db) {
    try {
        $countries = $db->query("SELECT * FROM countries ORDER BY country_name ASC")->fetchAll(PDO::FETCH_ASSOC);
        $allMethodsStmt = $db->query("SELECT pm.*, c.country_name, c.flag, c.currency_code FROM payment_methods pm JOIN countries c ON pm.country_id = c.id ORDER BY c.country_name ASC, pm.id ASC");
        $allMethods = $allMethodsStmt->fetchAll(PDO::FETCH_ASSOC);
        foreach ($allMethods as $m) {
            $methodsByCountry[$m['country_id']][] = $m;
        }
    } catch (Throwable $e) {
        error_log("Country/currency query error: " . $e->getMessage());
        $msg = "Database query notice: " . $e->getMessage();
        $msgType = "warning";
    }
} else {
    $msg = "Database offline. Viewing system cached regional data.";
    $msgType = "warning";
}

// Fallback seed countries if query returns empty
if (empty($countries)) {
    $countries = [
        ['id' => 1, 'country_name' => 'Bangladesh', 'iso_code' => 'BD', 'phone_code' => '+880', 'currency_code' => 'BDT', 'flag' => '🇧🇩', 'status' => 'Active'],
        ['id' => 2, 'country_name' => 'India', 'iso_code' => 'IN', 'phone_code' => '+91', 'currency_code' => 'INR', 'flag' => '🇮🇳', 'status' => 'Active'],
        ['id' => 3, 'country_name' => 'UAE', 'iso_code' => 'AE', 'phone_code' => '+971', 'currency_code' => 'AED', 'flag' => '🇦🇪', 'status' => 'Active'],
        ['id' => 4, 'country_name' => 'Malaysia', 'iso_code' => 'MY', 'phone_code' => '+60', 'currency_code' => 'MYR', 'flag' => '🇲🇾', 'status' => 'Active'],
        ['id' => 5, 'country_name' => 'United States', 'iso_code' => 'US', 'phone_code' => '+1', 'currency_code' => 'USD', 'flag' => '🇺🇸', 'status' => 'Active']
    ];
    $methodsByCountry = [
        1 => [
            ['id' => 1, 'country_id' => 1, 'method_name' => 'bKash', 'method_type' => 'Mobile Wallet', 'min_amount' => 100.0, 'max_amount' => 50000.0, 'status' => 'Active', 'country_name' => 'Bangladesh', 'flag' => '🇧🇩', 'currency_code' => 'BDT'],
            ['id' => 2, 'country_id' => 1, 'method_name' => 'Nagad', 'method_type' => 'Mobile Wallet', 'min_amount' => 100.0, 'max_amount' => 50000.0, 'status' => 'Active', 'country_name' => 'Bangladesh', 'flag' => '🇧🇩', 'currency_code' => 'BDT'],
            ['id' => 3, 'country_id' => 1, 'method_name' => 'City Bank Transfer', 'method_type' => 'Bank Transfer', 'min_amount' => 1000.0, 'max_amount' => 1000000.0, 'status' => 'Active', 'country_name' => 'Bangladesh', 'flag' => '🇧🇩', 'currency_code' => 'BDT']
        ],
        3 => [
            ['id' => 4, 'country_id' => 3, 'method_name' => 'FAB Bank Transfer', 'method_type' => 'Bank Transfer', 'min_amount' => 50.0, 'max_amount' => 200000.0, 'status' => 'Active', 'country_name' => 'UAE', 'flag' => '🇦🇪', 'currency_code' => 'AED']
        ]
    ];
    $allMethods = array_merge($methodsByCountry[1], $methodsByCountry[3]);
}

renderAdminHeader('Countries & Payment Methods', 'countries');
?>

<!-- Title & Action Toolbar -->
<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: #0f172a; letter-spacing: -0.02em;">
            <i class="bi bi-globe2 text-danger me-2"></i>Multi-Country & Payment Method Control
        </h2>
        <p class="text-secondary mb-0">
            Binance-Style Dynamic P2P Architecture: Available payment methods vary dynamically based on the verified user's country/region.
        </p>
    </div>
    <div class="d-flex gap-2 flex-wrap">
        <form method="POST" onsubmit="return confirm('Restore all 60+ countries and standard Binance P2P payment methods?');" class="d-inline">
            <input type="hidden" name="action" value="seed_defaults">
            <button type="submit" class="btn btn-outline-secondary fw-semibold">
                <i class="bi bi-arrow-clockwise me-1"></i>Reset Master Defaults
            </button>
        </form>
        <button class="btn btn-outline-primary fw-semibold" data-bs-toggle="modal" data-bs-target="#addMethodModal">
            <i class="bi bi-wallet2 me-1"></i>+ Add Payment Method
        </button>
        <button class="btn btn-primary fw-semibold shadow-sm" data-bs-toggle="modal" data-bs-target="#addCountryModal">
            <i class="bi bi-plus-circle-fill me-1"></i>+ Add Country
        </button>
    </div>
</div>

<?php if ($msg): ?>
    <div class="alert alert-<?= $msgType ?> alert-dismissible fade show shadow-sm" role="alert">
        <i class="bi bi-info-circle-fill me-2"></i><?= htmlspecialchars($msg) ?>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
<?php endif; ?>

<!-- Quick Stats Header -->
<div class="row g-3 mb-4">
    <div class="col-md-3 col-sm-6">
        <div class="card card-custom p-3 bg-white shadow-sm">
            <div class="text-secondary small fw-semibold">Supported Countries</div>
            <div class="h3 fw-bold mb-0 text-dark"><?= count($countries) ?> Countries</div>
            <div class="small text-muted mt-1">Global Marketplace Coverage</div>
        </div>
    </div>
    <div class="col-md-3 col-sm-6">
        <div class="card card-custom p-3 bg-white shadow-sm">
            <div class="text-secondary small fw-semibold">P2P Payment Methods</div>
            <div class="h3 fw-bold mb-0 text-success"><?= count($allMethods) ?> Wallets & Banks</div>
            <div class="small text-muted mt-1">Dynamic Country-Linked Options</div>
        </div>
    </div>
    <div class="col-md-3 col-sm-6">
        <div class="card card-custom p-3 bg-white shadow-sm">
            <div class="text-secondary small fw-semibold">Method Categories</div>
            <div class="h3 fw-bold mb-0 text-primary">8 Types</div>
            <div class="small text-muted mt-1">Mobile, Bank, Instant, Cash, Card, Crypto</div>
        </div>
    </div>
    <div class="col-md-3 col-sm-6">
        <div class="card card-custom p-3 bg-white shadow-sm">
            <div class="text-secondary small fw-semibold">Mobile App Sync</div>
            <div class="h3 fw-bold mb-0 text-danger">Live REST API</div>
            <div class="small text-muted mt-1">Endpoint: <code>/backend/api/countries.php</code></div>
        </div>
    </div>
</div>

<!-- Tabs: Countries, Payment Methods, and Binance Matrix -->
<div class="card card-custom p-0 overflow-hidden mb-4 shadow-sm">
    <div class="border-bottom px-4 pt-3 bg-light">
        <ul class="nav nav-tabs border-0" id="matrixTab" role="tablist">
            <li class="nav-item">
                <a class="nav-link active fw-semibold" data-bs-toggle="tab" href="#matrix">
                    <i class="bi bi-grid-3x3-gap-fill me-1 text-danger"></i>Binance Master Matrix (<?= count($countries) ?> Countries)
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link fw-semibold" data-bs-toggle="tab" href="#countries_list">
                    <i class="bi bi-flag-fill me-1 text-primary"></i>Countries Management (<?= count($countries) ?>)
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link fw-semibold" data-bs-toggle="tab" href="#methods_list">
                    <i class="bi bi-credit-card-2-front-fill me-1 text-success"></i>All Payment Methods (<?= count($allMethods) ?>)
                </a>
            </li>
        </ul>
    </div>

    <div class="tab-content p-4" id="matrixTabContent">
        <!-- Tab 1: Binance Master Matrix -->
        <div class="tab-pane fade show active" id="matrix">
            <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
                <span class="text-secondary small">Filter country-to-wallet bindings:</span>
                <input type="text" id="matrixSearch" class="form-control form-control-sm w-auto" placeholder="Search country, currency, or wallet..." onkeyup="filterMatrixTable()">
            </div>

            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0" id="matrixTable">
                    <thead class="table-light text-secondary small text-uppercase">
                        <tr>
                            <th>Country</th>
                            <th>ISO / Phone</th>
                            <th>Currency</th>
                            <th>Dynamic Wallet / Payment Methods (Binance-Style)</th>
                            <th>Status</th>
                            <th class="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php foreach ($countries as $c): ?>
                            <?php $cMethods = $methodsByCountry[$c['id']] ?? []; ?>
                            <tr>
                                <td>
                                    <span class="fs-4 me-2"><?= htmlspecialchars($c['flag']) ?></span>
                                    <strong class="text-dark"><?= htmlspecialchars($c['country_name']) ?></strong>
                                </td>
                                <td>
                                    <span class="badge bg-secondary"><?= htmlspecialchars($c['iso_code']) ?></span>
                                    <span class="text-muted small ms-1"><?= htmlspecialchars($c['phone_code']) ?></span>
                                </td>
                                <td>
                                    <span class="badge bg-dark fw-bold"><?= htmlspecialchars($c['currency_code']) ?></span>
                                </td>
                                <td>
                                    <?php if (empty($cMethods)): ?>
                                        <span class="text-muted small fst-italic">No payment methods bound</span>
                                    <?php else: ?>
                                        <div class="d-flex flex-wrap gap-1">
                                            <?php foreach ($cMethods as $m): ?>
                                                <?php
                                                $badgeClass = match($m['method_type'] ?? '') {
                                                    'Mobile Wallet' => 'bg-pink text-white',
                                                    'Instant Bank' => 'bg-info text-dark',
                                                    'Bank Transfer' => 'bg-primary text-white',
                                                    'Cash' => 'bg-success text-white',
                                                    'E-Wallet' => 'bg-warning text-dark',
                                                    default => 'bg-secondary text-white'
                                                };
                                                ?>
                                                <span class="badge <?= $badgeClass ?> px-2 py-1" style="font-size: 11px; cursor: pointer;" 
                                                      onclick="openEditMethod(<?= htmlspecialchars(json_encode($m)) ?>)"
                                                      title="Click to edit: <?= htmlspecialchars($m['method_type']) ?> (<?= $m['min_amount'] ?> - <?= $m['max_amount'] ?>)">
                                                    <?= htmlspecialchars($m['method_name']) ?> <i class="bi bi-pencil-square ms-1"></i>
                                                </span>
                                            <?php endforeach; ?>
                                        </div>
                                    <?php endif; ?>
                                </td>
                                <td>
                                    <span class="badge <?= $c['status'] === 'Active' ? 'bg-success' : 'bg-secondary' ?>">
                                        <?= htmlspecialchars($c['status']) ?>
                                    </span>
                                </td>
                                <td class="text-end">
                                    <button class="btn btn-sm btn-outline-success" onclick="openAddMethodForCountry(<?= $c['id'] ?>)">
                                        <i class="bi bi-plus-lg me-1"></i>Method
                                    </button>
                                </td>
                            </tr>
                        <?php endforeach; ?>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Tab 2: Countries Management -->
        <div class="tab-pane fade" id="countries_list">
            <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
                <span class="text-secondary small">Manage countries, currencies, and dial codes:</span>
                <input type="text" id="countrySearch" class="form-control form-control-sm w-auto" placeholder="Search country or currency..." onkeyup="filterCountryTable()">
            </div>

            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0" id="countryTable">
                    <thead class="table-light text-secondary small text-uppercase">
                        <tr>
                            <th>#</th>
                            <th>Country</th>
                            <th>ISO</th>
                            <th>Dial Code</th>
                            <th>Currency</th>
                            <th>Methods Count</th>
                            <th>Status</th>
                            <th class="text-end pe-3">Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php foreach ($countries as $c): ?>
                            <tr>
                                <td><?= $c['id'] ?></td>
                                <td>
                                    <span class="fs-4 me-2"><?= htmlspecialchars($c['flag']) ?></span>
                                    <strong><?= htmlspecialchars($c['country_name']) ?></strong>
                                </td>
                                <td><?= htmlspecialchars($c['iso_code']) ?></td>
                                <td><?= htmlspecialchars($c['phone_code']) ?></td>
                                <td><span class="badge bg-dark"><?= htmlspecialchars($c['currency_code']) ?></span></td>
                                <td>
                                    <span class="badge bg-info text-dark">
                                        <?= count($methodsByCountry[$c['id']] ?? []) ?> Methods
                                    </span>
                                </td>
                                <td>
                                    <form method="POST" class="d-inline">
                                        <input type="hidden" name="action" value="toggle_country_status">
                                        <input type="hidden" name="id" value="<?= $c['id'] ?>">
                                        <input type="hidden" name="current_status" value="<?= $c['status'] ?>">
                                        <button type="submit" class="btn btn-sm <?= $c['status'] === 'Active' ? 'btn-success' : 'btn-secondary' ?>">
                                            <?= $c['status'] ?>
                                        </button>
                                    </form>
                                </td>
                                <td class="text-end pe-3">
                                    <div class="d-inline-flex gap-1">
                                        <!-- Edit Country Button -->
                                        <button type="button" class="btn btn-sm btn-outline-primary" 
                                                onclick="openEditCountry(<?= htmlspecialchars(json_encode($c)) ?>)" title="Edit Country">
                                            <i class="bi bi-pencil-fill"></i>
                                        </button>

                                        <!-- Delete Country Button -->
                                        <form method="POST" class="d-inline" onsubmit="return confirm('Delete <?= htmlspecialchars(addslashes($c['country_name'])) ?> and ALL associated payment methods?');">
                                            <input type="hidden" name="action" value="delete_country">
                                            <input type="hidden" name="id" value="<?= $c['id'] ?>">
                                            <button type="submit" class="btn btn-sm btn-outline-danger" title="Delete Country">
                                                <i class="bi bi-trash-fill"></i>
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        <?php endforeach; ?>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Tab 3: All Payment Methods List -->
        <div class="tab-pane fade" id="methods_list">
            <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
                <div class="d-flex align-items-center gap-2">
                    <span class="text-secondary small">Filter by country:</span>
                    <select id="methodCountryFilter" class="form-select form-select-sm w-auto" onchange="filterMethodTable()">
                        <option value="">All Countries (<?= count($allMethods) ?>)</option>
                        <?php foreach ($countries as $c): ?>
                            <option value="<?= htmlspecialchars($c['country_name']) ?>"><?= htmlspecialchars($c['flag']) ?> <?= htmlspecialchars($c['country_name']) ?></option>
                        <?php endforeach; ?>
                    </select>
                </div>
                <input type="text" id="methodSearch" class="form-control form-control-sm w-auto" placeholder="Search payment method..." onkeyup="filterMethodTable()">
            </div>

            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0" id="methodTable">
                    <thead class="table-light text-secondary small text-uppercase">
                        <tr>
                            <th>Country</th>
                            <th>Method Name</th>
                            <th>Method Type</th>
                            <th>Min / Max Limit</th>
                            <th>Status</th>
                            <th class="text-end pe-3">Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php foreach ($allMethods as $m): ?>
                            <tr>
                                <td>
                                    <span class="me-1"><?= htmlspecialchars($m['flag'] ?? '') ?></span>
                                    <strong><?= htmlspecialchars($m['country_name'] ?? '') ?></strong>
                                    <span class="badge bg-secondary ms-1"><?= htmlspecialchars($m['currency_code'] ?? '') ?></span>
                                </td>
                                <td>
                                    <strong class="text-dark"><?= htmlspecialchars($m['method_name']) ?></strong>
                                </td>
                                <td>
                                    <span class="badge bg-light text-dark border"><?= htmlspecialchars($m['method_type']) ?></span>
                                </td>
                                <td>
                                    <span class="text-muted small">
                                        <?= number_format((float)$m['min_amount']) ?> - <?= number_format((float)$m['max_amount']) ?> <?= htmlspecialchars($m['currency_code'] ?? '') ?>
                                    </span>
                                </td>
                                <td>
                                    <form method="POST" class="d-inline">
                                        <input type="hidden" name="action" value="toggle_method_status">
                                        <input type="hidden" name="id" value="<?= $m['id'] ?>">
                                        <input type="hidden" name="current_status" value="<?= $m['status'] ?>">
                                        <button type="submit" class="btn btn-sm <?= $m['status'] === 'Active' ? 'btn-success' : 'btn-secondary' ?>">
                                            <?= $m['status'] ?>
                                        </button>
                                    </form>
                                </td>
                                <td class="text-end pe-3">
                                    <div class="d-inline-flex gap-1">
                                        <!-- Edit Payment Method Button -->
                                        <button type="button" class="btn btn-sm btn-outline-primary"
                                                onclick="openEditMethod(<?= htmlspecialchars(json_encode($m)) ?>)" title="Edit Method">
                                            <i class="bi bi-pencil-fill"></i>
                                        </button>

                                        <!-- Delete Payment Method Button -->
                                        <form method="POST" class="d-inline" onsubmit="return confirm('Delete payment method <?= htmlspecialchars(addslashes($m['method_name'])) ?>?');">
                                            <input type="hidden" name="action" value="delete_payment_method">
                                            <input type="hidden" name="id" value="<?= $m['id'] ?>">
                                            <button type="submit" class="btn btn-sm btn-outline-danger" title="Delete Method">
                                                <i class="bi bi-trash-fill"></i>
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        <?php endforeach; ?>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- Modal: Add Country -->
<div class="modal fade" id="addCountryModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <form method="POST" class="modal-content shadow border-0">
            <input type="hidden" name="action" value="add_country">
            <div class="modal-header bg-light">
                <h5 class="modal-title fw-bold text-dark">
                    <i class="bi bi-plus-circle-fill text-primary me-2"></i>Add New Country
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-4">
                <div class="mb-3">
                    <label class="form-label small fw-semibold">Country Name *</label>
                    <input type="text" name="country_name" class="form-control" placeholder="e.g. Saudi Arabia" required>
                </div>
                <div class="row g-2 mb-3">
                    <div class="col-4">
                        <label class="form-label small fw-semibold">ISO Code *</label>
                        <input type="text" name="iso_code" class="form-control" placeholder="SA" maxlength="5" required>
                    </div>
                    <div class="col-4">
                        <label class="form-label small fw-semibold">Dial Code *</label>
                        <input type="text" name="phone_code" class="form-control" placeholder="+966" required>
                    </div>
                    <div class="col-4">
                        <label class="form-label small fw-semibold">Currency Code *</label>
                        <input type="text" name="currency_code" class="form-control" placeholder="SAR" maxlength="5" required>
                    </div>
                </div>
                <div class="row g-2 mb-3">
                    <div class="col-6">
                        <label class="form-label small fw-semibold">Flag Emoji</label>
                        <input type="text" name="flag" class="form-control" placeholder="🇸🇦" value="🌐">
                    </div>
                    <div class="col-6">
                        <label class="form-label small fw-semibold">Initial Status</label>
                        <select name="status" class="form-select">
                            <option value="Active">Active</option>
                            <option value="Inactive">Inactive</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="modal-footer bg-light">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="submit" class="btn btn-primary fw-semibold">Save Country</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal: Edit Country -->
<div class="modal fade" id="editCountryModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <form method="POST" class="modal-content shadow border-0">
            <input type="hidden" name="action" value="edit_country">
            <input type="hidden" name="id" id="editCountryId">
            <div class="modal-header bg-light">
                <h5 class="modal-title fw-bold text-dark">
                    <i class="bi bi-pencil-square text-primary me-2"></i>Edit Country Profile
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-4">
                <div class="mb-3">
                    <label class="form-label small fw-semibold">Country Name *</label>
                    <input type="text" name="country_name" id="editCountryName" class="form-control" required>
                </div>
                <div class="row g-2 mb-3">
                    <div class="col-4">
                        <label class="form-label small fw-semibold">ISO Code *</label>
                        <input type="text" name="iso_code" id="editCountryIso" class="form-control" maxlength="5" required>
                    </div>
                    <div class="col-4">
                        <label class="form-label small fw-semibold">Dial Code *</label>
                        <input type="text" name="phone_code" id="editCountryPhone" class="form-control" required>
                    </div>
                    <div class="col-4">
                        <label class="form-label small fw-semibold">Currency Code *</label>
                        <input type="text" name="currency_code" id="editCountryCurrency" class="form-control" maxlength="5" required>
                    </div>
                </div>
                <div class="row g-2 mb-3">
                    <div class="col-6">
                        <label class="form-label small fw-semibold">Flag Emoji</label>
                        <input type="text" name="flag" id="editCountryFlag" class="form-control">
                    </div>
                    <div class="col-6">
                        <label class="form-label small fw-semibold">Status</label>
                        <select name="status" id="editCountryStatus" class="form-select">
                            <option value="Active">Active</option>
                            <option value="Inactive">Inactive</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="modal-footer bg-light">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="submit" class="btn btn-primary fw-semibold">Update Country</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal: Add Payment Method -->
<div class="modal fade" id="addMethodModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <form method="POST" class="modal-content shadow border-0">
            <input type="hidden" name="action" value="add_payment_method">
            <div class="modal-header bg-light">
                <h5 class="modal-title fw-bold text-dark">
                    <i class="bi bi-wallet2 text-success me-2"></i>Add Dynamic Payment Method
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-4">
                <div class="mb-3">
                    <label class="form-label small fw-semibold">Target Country *</label>
                    <select name="country_id" id="modalCountrySelect" class="form-select" required>
                        <option value="">-- Choose Country --</option>
                        <?php foreach ($countries as $c): ?>
                            <option value="<?= $c['id'] ?>"><?= htmlspecialchars($c['flag']) ?> <?= htmlspecialchars($c['country_name']) ?> (<?= htmlspecialchars($c['currency_code']) ?>)</option>
                        <?php endforeach; ?>
                    </select>
                </div>
                <div class="mb-3">
                    <label class="form-label small fw-semibold">Payment Method Name *</label>
                    <input type="text" name="method_name" class="form-control" placeholder="e.g. bKash, UPI, Touch 'n Go, SEPA" required>
                </div>
                <div class="mb-3">
                    <label class="form-label small fw-semibold">Method Type *</label>
                    <select name="method_type" class="form-select" required>
                        <option value="Mobile Wallet">Mobile Wallet (bKash, Nagad, M-Pesa, etc.)</option>
                        <option value="Bank Transfer">Bank Transfer (NEFT, Wire, Direct)</option>
                        <option value="Instant Bank">Instant Bank (UPI, PIX, FAST, PayNow, BLIK, etc.)</option>
                        <option value="Cash">Cash (Cash in Hand, Cash Counter)</option>
                        <option value="Card">Card (Debit, Credit)</option>
                        <option value="E-Wallet">E-Wallet (GrabPay, Alipay, WeChat, Boost)</option>
                        <option value="Crypto/USDT">Crypto/USDT (TRC20, ERC20)</option>
                        <option value="Other">Other</option>
                    </select>
                </div>
                <div class="row g-2 mb-3">
                    <div class="col-6">
                        <label class="form-label small fw-semibold">Min Transaction Amount</label>
                        <input type="number" name="min_amount" class="form-control" value="100" min="1" step="any">
                    </div>
                    <div class="col-6">
                        <label class="form-label small fw-semibold">Max Transaction Amount</label>
                        <input type="number" name="max_amount" class="form-control" value="500000" min="1" step="any">
                    </div>
                </div>
                <div class="mb-3">
                    <label class="form-label small fw-semibold">Status</label>
                    <select name="status" class="form-select">
                        <option value="Active">Active</option>
                        <option value="Inactive">Inactive</option>
                    </select>
                </div>
            </div>
            <div class="modal-footer bg-light">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="submit" class="btn btn-success fw-semibold">Save Payment Method</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal: Edit Payment Method -->
<div class="modal fade" id="editMethodModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <form method="POST" class="modal-content shadow border-0">
            <input type="hidden" name="action" value="edit_payment_method">
            <input type="hidden" name="id" id="editMethodId">
            <div class="modal-header bg-light">
                <h5 class="modal-title fw-bold text-dark">
                    <i class="bi bi-pencil-square text-success me-2"></i>Edit Payment Method
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-4">
                <div class="mb-3">
                    <label class="form-label small fw-semibold">Target Country *</label>
                    <select name="country_id" id="editMethodCountryId" class="form-select" required>
                        <?php foreach ($countries as $c): ?>
                            <option value="<?= $c['id'] ?>"><?= htmlspecialchars($c['flag']) ?> <?= htmlspecialchars($c['country_name']) ?> (<?= htmlspecialchars($c['currency_code']) ?>)</option>
                        <?php endforeach; ?>
                    </select>
                </div>
                <div class="mb-3">
                    <label class="form-label small fw-semibold">Payment Method Name *</label>
                    <input type="text" name="method_name" id="editMethodName" class="form-control" required>
                </div>
                <div class="mb-3">
                    <label class="form-label small fw-semibold">Method Type *</label>
                    <select name="method_type" id="editMethodType" class="form-select" required>
                        <option value="Mobile Wallet">Mobile Wallet (bKash, Nagad, M-Pesa, etc.)</option>
                        <option value="Bank Transfer">Bank Transfer (NEFT, Wire, Direct)</option>
                        <option value="Instant Bank">Instant Bank (UPI, PIX, FAST, PayNow, BLIK, etc.)</option>
                        <option value="Cash">Cash (Cash in Hand, Cash Counter)</option>
                        <option value="Card">Card (Debit, Credit)</option>
                        <option value="E-Wallet">E-Wallet (GrabPay, Alipay, WeChat, Boost)</option>
                        <option value="Crypto/USDT">Crypto/USDT (TRC20, ERC20)</option>
                        <option value="Other">Other</option>
                    </select>
                </div>
                <div class="row g-2 mb-3">
                    <div class="col-6">
                        <label class="form-label small fw-semibold">Min Transaction Amount</label>
                        <input type="number" name="min_amount" id="editMethodMin" class="form-control" min="1" step="any">
                    </div>
                    <div class="col-6">
                        <label class="form-label small fw-semibold">Max Transaction Amount</label>
                        <input type="number" name="max_amount" id="editMethodMax" class="form-control" min="1" step="any">
                    </div>
                </div>
                <div class="mb-3">
                    <label class="form-label small fw-semibold">Status</label>
                    <select name="status" id="editMethodStatus" class="form-select">
                        <option value="Active">Active</option>
                        <option value="Inactive">Inactive</option>
                    </select>
                </div>
            </div>
            <div class="modal-footer bg-light">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="submit" class="btn btn-success fw-semibold">Update Payment Method</button>
            </div>
        </form>
    </div>
</div>

<script>
function openAddMethodForCountry(countryId) {
    var select = document.getElementById('modalCountrySelect');
    if (select) {
        select.value = countryId;
    }
    var modal = new bootstrap.Modal(document.getElementById('addMethodModal'));
    modal.show();
}

function openEditCountry(c) {
    document.getElementById('editCountryId').value = c.id;
    document.getElementById('editCountryName').value = c.country_name;
    document.getElementById('editCountryIso').value = c.iso_code;
    document.getElementById('editCountryPhone').value = c.phone_code;
    document.getElementById('editCountryCurrency').value = c.currency_code;
    document.getElementById('editCountryFlag').value = c.flag || '🌐';
    document.getElementById('editCountryStatus').value = c.status || 'Active';
    var modal = new bootstrap.Modal(document.getElementById('editCountryModal'));
    modal.show();
}

function openEditMethod(m) {
    document.getElementById('editMethodId').value = m.id;
    document.getElementById('editMethodCountryId').value = m.country_id;
    document.getElementById('editMethodName').value = m.method_name;
    document.getElementById('editMethodType').value = m.method_type;
    document.getElementById('editMethodMin').value = m.min_amount;
    document.getElementById('editMethodMax').value = m.max_amount;
    document.getElementById('editMethodStatus').value = m.status || 'Active';
    var modal = new bootstrap.Modal(document.getElementById('editMethodModal'));
    modal.show();
}

function filterMatrixTable() {
    var filter = document.getElementById('matrixSearch').value.toLowerCase();
    var rows = document.querySelectorAll('#matrixTable tbody tr');
    rows.forEach(function(row) {
        row.style.display = row.innerText.toLowerCase().indexOf(filter) > -1 ? '' : 'none';
    });
}

function filterCountryTable() {
    var filter = document.getElementById('countrySearch').value.toLowerCase();
    var rows = document.querySelectorAll('#countryTable tbody tr');
    rows.forEach(function(row) {
        row.style.display = row.innerText.toLowerCase().indexOf(filter) > -1 ? '' : 'none';
    });
}

function filterMethodTable() {
    var searchFilter = document.getElementById('methodSearch').value.toLowerCase();
    var countryFilter = document.getElementById('methodCountryFilter').value.toLowerCase();
    var rows = document.querySelectorAll('#methodTable tbody tr');
    rows.forEach(function(row) {
        var text = row.innerText.toLowerCase();
        var matchesSearch = text.indexOf(searchFilter) > -1;
        var matchesCountry = !countryFilter || text.indexOf(countryFilter) > -1;
        row.style.display = (matchesSearch && matchesCountry) ? '' : 'none';
    });
}
</script>

<style>
.bg-pink {
    background-color: #E91E63 !important;
}
</style>

<?php
renderAdminFooter();
