<?php
declare(strict_types=1);

// backend/admin/settings.php
// Enterprise SaaS Settings & Configuration Center (PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';
require_once __DIR__ . '/../config/firebase.php';
require_once __DIR__ . '/layout.php';

checkAdminAuth();

$msg = '';
$msgType = 'success';

$settingsFile = __DIR__ . '/../config/app_settings.json';
$currentSettings = [
    'platform_fee_percent' => 15.0,
    'agent_commission_percent' => 5.0,
    'min_deposit' => 500.0,
    'min_withdrawal' => 1000.0,
    'p2p_timeout_minutes' => 15,
    'currency_default' => 'BDT (৳)',
    'maintenance_mode' => false,
    'app_name' => 'Modol Connect',
    'show_live_gps_tab' => false
];

if (file_exists($settingsFile)) {
    $loaded = json_decode((string)file_get_contents($settingsFile), true);
    if (is_array($loaded)) {
        $currentSettings = array_merge($currentSettings, $loaded);
    }
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $currentSettings['platform_fee_percent'] = (float)($_POST['platform_fee_percent'] ?? 15.0);
    $currentSettings['agent_commission_percent'] = (float)($_POST['agent_commission_percent'] ?? 5.0);
    $currentSettings['min_deposit'] = (float)($_POST['min_deposit'] ?? 500.0);
    $currentSettings['min_withdrawal'] = (float)($_POST['min_withdrawal'] ?? 1000.0);
    $currentSettings['p2p_timeout_minutes'] = (int)($_POST['p2p_timeout_minutes'] ?? 15);
    $currentSettings['app_name'] = trim((string)($_POST['app_name'] ?? 'Modol Connect'));
    $currentSettings['maintenance_mode'] = isset($_POST['maintenance_mode']);
    $currentSettings['show_live_gps_tab'] = isset($_POST['show_live_gps_tab']);

    file_put_contents($settingsFile, json_encode($currentSettings, JSON_PRETTY_PRINT));
    $msg = 'Platform parameters updated successfully!';
}

renderAdminHeader('Settings', 'settings');
?>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: #0f172a;">System Settings</h2>
        <p class="text-secondary mb-0">Platform commissions, escrow fees, timeouts, Firebase cloud credentials, and maintenance mode.</p>
    </div>
</div>

<?php if ($msg): ?>
    <div class="alert alert-<?= $msgType ?> alert-dismissible fade show" role="alert">
        <i class="bi bi-check-circle-fill me-2"></i><?= htmlspecialchars($msg) ?>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
<?php endif; ?>

<div class="card card-custom p-0 overflow-hidden mb-4">
    <div class="border-bottom px-4 pt-3">
        <ul class="nav nav-tabs border-0" id="settingsTab" role="tablist">
            <li class="nav-item"><a class="nav-link active fw-semibold" data-bs-toggle="tab" href="#general">General & App</a></li>
            <li class="nav-item"><a class="nav-link fw-semibold" data-bs-toggle="tab" href="#commission">Commissions & Fees</a></li>
            <li class="nav-item"><a class="nav-link fw-semibold" data-bs-toggle="tab" href="#p2p_limits">P2P & Escrow Limits</a></li>
            <li class="nav-item"><a class="nav-link fw-semibold" data-bs-toggle="tab" href="#firebase_tab">Firebase & Cloud</a></li>
        </ul>
    </div>

    <form method="POST">
        <div class="tab-content p-4" id="settingsTabContent">
            <!-- General Tab -->
            <div class="tab-pane fade show active" id="general">
                <div class="row g-3 max-w-700">
                    <div class="col-md-6">
                        <label class="form-label small fw-semibold text-secondary">Application Name</label>
                        <input type="text" name="app_name" class="form-control" value="<?= htmlspecialchars($currentSettings['app_name']) ?>" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label small fw-semibold text-secondary">Default Base Currency</label>
                        <select name="currency_default" class="form-select">
                            <option value="BDT" selected>BDT (৳) — Bangladesh Taka</option>
                            <option value="AED">AED (د.إ) — UAE Dirham</option>
                            <option value="MYR">MYR (RM) — Malaysian Ringgit</option>
                            <option value="USD">USD ($) — US Dollar</option>
                        </select>
                    </div>
                    <div class="col-12 mt-4">
                        <div class="form-check form-switch">
                            <input class="form-check-input" type="checkbox" name="maintenance_mode" id="maintSwitch" <?= $currentSettings['maintenance_mode'] ? 'checked' : '' ?>>
                            <label class="form-check-label fw-semibold text-dark" for="maintSwitch">Enable System Maintenance Mode</label>
                            <small class="d-block text-muted">Temporarily restricts user app logins while maintenance is underway.</small>
                        </div>
                    </div>
                    <div class="col-12 mt-3 p-3 rounded-3 border bg-light">
                        <div class="form-check form-switch">
                            <input class="form-check-input" type="checkbox" name="show_live_gps_tab" id="gpsTabSwitch" <?= !empty($currentSettings['show_live_gps_tab']) ? 'checked' : '' ?>>
                            <label class="form-check-label fw-bold text-dark" for="gpsTabSwitch">
                                <i class="bi bi-geo-alt-fill text-danger me-1"></i> Show Live GPS Tab in Mobile App Bottom Navigation
                            </label>
                            <small class="d-block text-secondary mt-1">
                                <strong>Default: HIDE.</strong> When toggled OFF, the "Live GPS" tab is completely HIDDEN from Client and Model bottom navigation bars in the Android app. Toggle ON when you want users to see and access the Live GPS tab directly from the bottom bar.
                            </small>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Commission Tab -->
            <div class="tab-pane fade" id="commission">
                <div class="row g-3 max-w-700">
                    <div class="col-md-6">
                        <label class="form-label small fw-semibold text-secondary">Platform Booking Fee (%)</label>
                        <div class="input-group">
                            <input type="number" step="0.5" name="platform_fee_percent" class="form-control" value="<?= $currentSettings['platform_fee_percent'] ?>" required>
                            <span class="input-group-text">%</span>
                        </div>
                        <small class="text-muted">Deducted from each completed model booking.</small>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label small fw-semibold text-secondary">Cash Agent Commission (%)</label>
                        <div class="input-group">
                            <input type="number" step="0.5" name="agent_commission_percent" class="form-control" value="<?= $currentSettings['agent_commission_percent'] ?>" required>
                            <span class="input-group-text">%</span>
                        </div>
                        <small class="text-muted">Commission granted to verified cash-in agents.</small>
                    </div>
                </div>
            </div>

            <!-- P2P & Escrow Limits -->
            <div class="tab-pane fade" id="p2p_limits">
                <div class="row g-3 max-w-700">
                    <div class="col-md-4">
                        <label class="form-label small fw-semibold text-secondary">Minimum Deposit (৳)</label>
                        <input type="number" name="min_deposit" class="form-control" value="<?= $currentSettings['min_deposit'] ?>" required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label small fw-semibold text-secondary">Minimum Withdrawal (৳)</label>
                        <input type="number" name="min_withdrawal" class="form-control" value="<?= $currentSettings['min_withdrawal'] ?>" required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label small fw-semibold text-secondary">P2P Order Timeout</label>
                        <div class="input-group">
                            <input type="number" name="p2p_timeout_minutes" class="form-control" value="<?= $currentSettings['p2p_timeout_minutes'] ?>" required>
                            <span class="input-group-text">Min</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Firebase Tab -->
            <div class="tab-pane fade" id="firebase_tab">
                <div class="p-3 bg-light rounded-3 border mb-3">
                    <h6 class="fw-bold text-dark mb-1"><i class="bi bi-fire text-warning me-2"></i>Firebase Cloud Backend</h6>
                    <p class="small text-secondary mb-3">All Firebase Storage buckets, Firestore sync, and Service Account settings are managed via the dedicated Firebase Hub.</p>
                    <a href="firebase.php" class="btn btn-warning fw-semibold">
                        <i class="bi bi-arrow-up-right-square me-1"></i> Open Firebase Control Hub
                    </a>
                </div>
            </div>
        </div>

        <div class="p-4 border-top bg-light text-end">
            <button type="submit" class="btn btn-primary px-4 fw-semibold">
                <i class="bi bi-save me-1"></i> Save Platform Settings
            </button>
        </div>
    </form>
</div>

<?php
renderAdminFooter();
?>
