<?php
declare(strict_types=1);

// backend/admin/settings.php
// Enterprise SaaS Settings & Configuration Center (PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';
require_once __DIR__ . '/../config/firebase.php';
require_once __DIR__ . '/../config/mailer.php';
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
    'show_live_gps_tab' => false,
    'smtp_enabled' => false,
    'smtp_host' => 'mail.modolconncet.fun',
    'smtp_port' => 465,
    'smtp_encryption' => 'ssl',
    'smtp_username' => 'support@modolconncet.fun',
    'smtp_password' => '',
    'smtp_from_email' => 'support@modolconncet.fun',
    'smtp_from_name' => 'Modol Connect Support'
];

if (file_exists($settingsFile)) {
    $loaded = json_decode((string)file_get_contents($settingsFile), true);
    if (is_array($loaded)) {
        $currentSettings = array_merge($currentSettings, $loaded);
    }
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (isset($_POST['action']) && $_POST['action'] === 'send_test_email') {
        $testRecipient = trim((string)($_POST['test_email_recipient'] ?? ''));
        if (empty($testRecipient) || !filter_var($testRecipient, FILTER_VALIDATE_EMAIL)) {
            $msg = 'Please enter a valid test recipient email address.';
            $msgType = 'danger';
        } else {
            $subject = 'Modol Connect - Test Email & SMTP Diagnostics';
            $body = "<h3>SMTP Test Successful!</h3><p>This is a test email sent from Modol Connect Admin Panel to verify that SMTP / email sending is properly configured.</p><p>Sender: <strong>support@modolconncet.fun</strong><br>Time: " . date('Y-m-d H:i:s') . "</p>";
            $res = Mailer::send($testRecipient, $subject, $body);
            if ($res['success']) {
                $msg = "Test email sent successfully to {$testRecipient} via " . ($res['method'] ?? 'SMTP') . "!";
                $msgType = 'success';
            } else {
                $msg = "Email delivery failed: " . ($res['error'] ?? 'Unknown error') . ". Notice: " . ($res['message'] ?? '');
                $msgType = 'danger';
            }
        }
    } else {
        $currentSettings['platform_fee_percent'] = (float)($_POST['platform_fee_percent'] ?? 15.0);
        $currentSettings['agent_commission_percent'] = (float)($_POST['agent_commission_percent'] ?? 5.0);
        $currentSettings['min_deposit'] = (float)($_POST['min_deposit'] ?? 500.0);
        $currentSettings['min_withdrawal'] = (float)($_POST['min_withdrawal'] ?? 1000.0);
        $currentSettings['p2p_timeout_minutes'] = (int)($_POST['p2p_timeout_minutes'] ?? 15);
        $currentSettings['app_name'] = trim((string)($_POST['app_name'] ?? 'Modol Connect'));
        $currentSettings['maintenance_mode'] = isset($_POST['maintenance_mode']);
        $currentSettings['show_live_gps_tab'] = isset($_POST['show_live_gps_tab']);

        // SMTP settings
        $currentSettings['smtp_enabled'] = isset($_POST['smtp_enabled']);
        $currentSettings['smtp_host'] = trim((string)($_POST['smtp_host'] ?? 'mail.modolconncet.fun'));
        $currentSettings['smtp_port'] = (int)($_POST['smtp_port'] ?? 465);
        $currentSettings['smtp_encryption'] = trim((string)($_POST['smtp_encryption'] ?? 'ssl'));
        $currentSettings['smtp_username'] = trim((string)($_POST['smtp_username'] ?? 'support@modolconncet.fun'));
        if (!empty($_POST['smtp_password'])) {
            $currentSettings['smtp_password'] = (string)$_POST['smtp_password'];
        }
        $currentSettings['smtp_from_email'] = trim((string)($_POST['smtp_from_email'] ?? 'support@modolconncet.fun'));
        $currentSettings['smtp_from_name'] = trim((string)($_POST['smtp_from_name'] ?? 'Modol Connect Support'));

        file_put_contents($settingsFile, json_encode($currentSettings, JSON_PRETTY_PRINT));
        $msg = 'Platform parameters and SMTP settings updated successfully!';
        $msgType = 'success';
    }
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
            <li class="nav-item"><a class="nav-link fw-semibold" data-bs-toggle="tab" href="#email_smtp"><i class="bi bi-envelope-at-fill text-primary me-1"></i>Email & SMTP</a></li>
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

            <!-- Email & SMTP Tab -->
            <div class="tab-pane fade" id="email_smtp">
                <div class="p-3 bg-light rounded-3 border mb-4">
                    <h6 class="fw-bold text-dark mb-1"><i class="bi bi-shield-check text-success me-2"></i>Official Outgoing Mail Server (support@modolconncet.fun)</h6>
                    <p class="small text-secondary mb-0">Configure your domain mail server / SMTP to deliver OTPs, password reset links, and security alerts directly into user inboxes without being flagged as spam.</p>
                </div>

                <div class="row g-3 max-w-700">
                    <div class="col-12">
                        <div class="form-check form-switch p-2 ps-5 rounded border bg-white">
                            <input class="form-check-input" type="checkbox" name="smtp_enabled" id="smtpSwitch" <?= !empty($currentSettings['smtp_enabled']) ? 'checked' : '' ?>>
                            <label class="form-check-label fw-bold text-dark" for="smtpSwitch">Enable SMTP Mail Delivery</label>
                            <small class="d-block text-muted">When enabled, emails are authenticated and sent directly via your SMTP server instead of default unauthenticated PHP mail().</small>
                        </div>
                    </div>

                    <div class="col-md-8">
                        <label class="form-label small fw-semibold text-secondary">SMTP Host / Server</label>
                        <input type="text" name="smtp_host" class="form-control" value="<?= htmlspecialchars($currentSettings['smtp_host'] ?? 'mail.modolconncet.fun') ?>" placeholder="mail.modolconncet.fun or smtp.gmail.com">
                        <small class="text-muted">Webmail/cPanel default is usually <code>mail.yourdomain.com</code></small>
                    </div>

                    <div class="col-md-4">
                        <label class="form-label small fw-semibold text-secondary">SMTP Port</label>
                        <input type="number" name="smtp_port" class="form-control" value="<?= (int)($currentSettings['smtp_port'] ?? 465) ?>" placeholder="465 or 587">
                        <small class="text-muted">SSL: <code>465</code> | TLS: <code>587</code></small>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label small fw-semibold text-secondary">Encryption Protocol</label>
                        <select name="smtp_encryption" class="form-select">
                            <option value="ssl" <?= (($currentSettings['smtp_encryption'] ?? 'ssl') === 'ssl') ? 'selected' : '' ?>>SSL (Port 465 Recommended)</option>
                            <option value="tls" <?= (($currentSettings['smtp_encryption'] ?? '') === 'tls') ? 'selected' : '' ?>>TLS (Port 587)</option>
                        </select>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label small fw-semibold text-secondary">SMTP Username / Email</label>
                        <input type="email" name="smtp_username" class="form-control" value="<?= htmlspecialchars($currentSettings['smtp_username'] ?? 'support@modolconncet.fun') ?>" placeholder="support@modolconncet.fun">
                    </div>

                    <div class="col-md-6">
                        <label class="form-label small fw-semibold text-secondary">SMTP Password</label>
                        <input type="password" name="smtp_password" class="form-control" value="<?= htmlspecialchars($currentSettings['smtp_password'] ?? '') ?>" placeholder="Enter email account password">
                        <small class="text-muted">Password of your support@modolconncet.fun mailbox</small>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label small fw-semibold text-secondary">Sender Display Name</label>
                        <input type="text" name="smtp_from_name" class="form-control" value="<?= htmlspecialchars($currentSettings['smtp_from_name'] ?? 'Modol Connect Support') ?>">
                    </div>

                    <div class="col-12">
                        <label class="form-label small fw-semibold text-secondary">Sender Email Address</label>
                        <input type="email" name="smtp_from_email" class="form-control" value="<?= htmlspecialchars($currentSettings['smtp_from_email'] ?? 'support@modolconncet.fun') ?>" readonly style="background-color: #f8fafc;">
                        <small class="text-muted">Always sends from <strong>support@modolconncet.fun</strong></small>
                    </div>
                </div>

                <hr class="my-4">

                <div class="card p-3 border rounded-3 bg-white max-w-700">
                    <h6 class="fw-bold text-dark mb-1"><i class="bi bi-send-check text-primary me-2"></i>Send Test Email & Verify Delivery</h6>
                    <p class="small text-secondary mb-3">Save your SMTP settings first above, then enter an email address below to test live delivery.</p>
                    <div class="input-group">
                        <input type="email" form="testEmailForm" name="test_email_recipient" class="form-control" placeholder="recipient@gmail.com" required value="<?= htmlspecialchars($_SESSION['admin_email'] ?? 'hmmirazreza2@gmail.com') ?>">
                        <button type="submit" form="testEmailForm" class="btn btn-outline-primary fw-semibold">
                            <i class="bi bi-envelope-paper me-1"></i> Send Test Email
                        </button>
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
    <form id="testEmailForm" method="POST" style="display:none;">
        <input type="hidden" name="action" value="send_test_email">
    </form>
</div>

<?php
renderAdminFooter();
?>
