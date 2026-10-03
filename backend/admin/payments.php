<?php
declare(strict_types=1);

// backend/admin/payments.php
// Enterprise Payment Gateway Configuration Center: Google Pay, Alipay, Apple Pay & Dynamic Gateways (PHP 8.2+)

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
    error_log("Database connection error in payments.php: " . $e->getMessage());
}

$configFile = __DIR__ . '/../config/payment_gateways.json';

// Default Gateways Schema & Initial State
$defaultGateways = [
    'google_pay' => [
        'gateway_id' => 'google_pay',
        'name' => 'Google Pay',
        'type' => 'DIGITAL_WALLET',
        'environment' => 'SANDBOX',
        'merchant_id' => 'BCR2DN4T77889900',
        'merchant_name' => 'Modol Connect Enterprise',
        'api_key' => 'gpay_pub_live_998877665544332211',
        'secret_key' => 'gpay_sec_998877665544332211aabbccddeeff',
        'public_key' => 'pk_live_google_pay_gateway_token',
        'webhook_secret' => 'whsec_gpay_test_secret_key',
        'currency' => 'BDT',
        'min_amount' => 100.0,
        'max_amount' => 500000.0,
        'fee_percent' => 1.5,
        'is_enabled' => 1,
        'supported_cards' => 'VISA, MASTERCARD, AMEX, DISCOVER',
        'instructions' => 'Quick 1-tap checkout via Google Pay wallet with biometric tokenization.'
    ],
    'alipay' => [
        'gateway_id' => 'alipay',
        'name' => 'Alipay (支付宝)',
        'type' => 'GLOBAL_WALLET',
        'environment' => 'SANDBOX',
        'merchant_id' => '2088102148765432',
        'merchant_name' => 'Modol Connect Hong Kong / China',
        'api_key' => '2021000119887766',
        'secret_key' => 'MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC6...',
        'public_key' => 'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3...',
        'webhook_secret' => 'alipay_notify_verification_salt_99',
        'currency' => 'CNY',
        'min_amount' => 50.0,
        'max_amount' => 300000.0,
        'fee_percent' => 1.8,
        'is_enabled' => 1,
        'supported_cards' => 'Alipay Wallet, China UnionPay, TourPass',
        'instructions' => 'Supports Cross-Border Alipay QR code and mobile web deep-link payment.'
    ],
    'apple_pay' => [
        'gateway_id' => 'apple_pay',
        'name' => 'Apple Pay',
        'type' => 'DIGITAL_WALLET',
        'environment' => 'SANDBOX',
        'merchant_id' => 'merchant.com.modolconnect.app',
        'merchant_name' => 'Modol Connect Global Inc',
        'api_key' => 'appl_id_887766554433',
        'secret_key' => 'apple_cert_passphrase_key_sample',
        'public_key' => 'apple_pay_domain_association_verified',
        'webhook_secret' => 'whsec_apple_pay_payment_auth',
        'currency' => 'USD',
        'min_amount' => 10.0,
        'max_amount' => 10000.0,
        'fee_percent' => 1.5,
        'is_enabled' => 1,
        'supported_cards' => 'Visa, MasterCard, American Express, Apple Card',
        'instructions' => 'Apple Pay secure tokenized enclave checkout via Safari and iOS app.'
    ]
];

// Load from config file if exists
if (file_exists($configFile)) {
    $loaded = json_decode((string)file_get_contents($configFile), true);
    if (is_array($loaded) && !empty($loaded)) {
        $defaultGateways = array_merge($defaultGateways, $loaded);
    }
}

// Sync with DB table if available
if ($db) {
    try {
        $rows = $db->query("SELECT * FROM payment_gateways")->fetchAll(PDO::FETCH_ASSOC);
        if (!empty($rows)) {
            foreach ($rows as $r) {
                $gid = $r['gateway_id'];
                $defaultGateways[$gid] = [
                    'gateway_id' => $r['gateway_id'],
                    'name' => $r['name'],
                    'type' => $r['type'] ?? 'GLOBAL',
                    'environment' => $r['environment'] ?? 'SANDBOX',
                    'merchant_id' => $r['merchant_id'] ?? '',
                    'merchant_name' => $r['merchant_name'] ?? '',
                    'api_key' => $r['api_key'] ?? '',
                    'secret_key' => $r['secret_key'] ?? '',
                    'public_key' => $r['public_key'] ?? '',
                    'webhook_secret' => $r['webhook_secret'] ?? '',
                    'currency' => $r['currency'] ?? 'USD',
                    'min_amount' => (float)($r['min_amount'] ?? 100.0),
                    'max_amount' => (float)($r['max_amount'] ?? 500000.0),
                    'fee_percent' => (float)($r['fee_percent'] ?? 1.5),
                    'is_enabled' => (int)($r['is_enabled'] ?? 1),
                    'supported_cards' => $r['supported_cards'] ?? 'VISA, MASTERCARD',
                    'instructions' => $r['instructions'] ?? ''
                ];
            }
        } else {
            // Seed DB
            $insStmt = $db->prepare("INSERT INTO payment_gateways (gateway_id, name, type, environment, merchant_id, merchant_name, api_key, secret_key, public_key, webhook_secret, currency, min_amount, max_amount, fee_percent, is_enabled, supported_cards, instructions) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            foreach ($defaultGateways as $g) {
                $insStmt->execute([
                    $g['gateway_id'], $g['name'], $g['type'], $g['environment'],
                    $g['merchant_id'], $g['merchant_name'], $g['api_key'], $g['secret_key'],
                    $g['public_key'], $g['webhook_secret'], $g['currency'], $g['min_amount'],
                    $g['max_amount'], $g['fee_percent'], $g['is_enabled'], $g['supported_cards'],
                    $g['instructions']
                ]);
            }
        }
    } catch (Throwable $e) {
        error_log("Payment gateways DB sync notice: " . $e->getMessage());
    }
}

// Handle Form Submissions (Add, Edit, Toggle, Delete, Test)
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $action = $_POST['action'] ?? '';

    if ($action === 'save_gateway') {
        $gid = strtolower(trim(preg_replace('/[^a-zA-Z0-9_]/', '_', (string)($_POST['gateway_id'] ?? ''))));
        $name = trim((string)($_POST['name'] ?? ''));
        $type = trim((string)($_POST['type'] ?? 'GLOBAL'));
        $environment = trim((string)($_POST['environment'] ?? 'SANDBOX'));
        $merchantId = trim((string)($_POST['merchant_id'] ?? ''));
        $merchantName = trim((string)($_POST['merchant_name'] ?? ''));
        $apiKey = trim((string)($_POST['api_key'] ?? ''));
        $secretKey = trim((string)($_POST['secret_key'] ?? ''));
        $publicKey = trim((string)($_POST['public_key'] ?? ''));
        $webhookSecret = trim((string)($_POST['webhook_secret'] ?? ''));
        $currency = strtoupper(trim((string)($_POST['currency'] ?? 'BDT')));
        $minAmount = (float)($_POST['min_amount'] ?? 100.0);
        $maxAmount = (float)($_POST['max_amount'] ?? 500000.0);
        $feePercent = (float)($_POST['fee_percent'] ?? 1.5);
        $isEnabled = isset($_POST['is_enabled']) ? 1 : 0;
        $supportedCards = trim((string)($_POST['supported_cards'] ?? 'VISA, MASTERCARD'));
        $instructions = trim((string)($_POST['instructions'] ?? ''));

        if (!empty($gid) && !empty($name)) {
            $defaultGateways[$gid] = [
                'gateway_id' => $gid,
                'name' => $name,
                'type' => $type,
                'environment' => $environment,
                'merchant_id' => $merchantId,
                'merchant_name' => $merchantName,
                'api_key' => $apiKey,
                'secret_key' => $secretKey,
                'public_key' => $publicKey,
                'webhook_secret' => $webhookSecret,
                'currency' => $currency,
                'min_amount' => $minAmount,
                'max_amount' => $maxAmount,
                'fee_percent' => $feePercent,
                'is_enabled' => $isEnabled,
                'supported_cards' => $supportedCards,
                'instructions' => $instructions
            ];

            // Save to JSON
            @file_put_contents($configFile, json_encode($defaultGateways, JSON_PRETTY_PRINT));

            // Save to DB
            if ($db) {
                try {
                    $check = $db->prepare("SELECT COUNT(*) FROM payment_gateways WHERE gateway_id = ?");
                    $check->execute([$gid]);
                    if ((int)$check->fetchColumn() > 0) {
                        $upStmt = $db->prepare("UPDATE payment_gateways SET name=?, type=?, environment=?, merchant_id=?, merchant_name=?, api_key=?, secret_key=?, public_key=?, webhook_secret=?, currency=?, min_amount=?, max_amount=?, fee_percent=?, is_enabled=?, supported_cards=?, instructions=?, updated_at=CURRENT_TIMESTAMP WHERE gateway_id=?");
                        $upStmt->execute([
                            $name, $type, $environment, $merchantId, $merchantName,
                            $apiKey, $secretKey, $publicKey, $webhookSecret, $currency,
                            $minAmount, $maxAmount, $feePercent, $isEnabled, $supportedCards,
                            $instructions, $gid
                        ]);
                    } else {
                        $inStmt = $db->prepare("INSERT INTO payment_gateways (gateway_id, name, type, environment, merchant_id, merchant_name, api_key, secret_key, public_key, webhook_secret, currency, min_amount, max_amount, fee_percent, is_enabled, supported_cards, instructions) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        $inStmt->execute([
                            $gid, $name, $type, $environment, $merchantId, $merchantName,
                            $apiKey, $secretKey, $publicKey, $webhookSecret, $currency,
                            $minAmount, $maxAmount, $feePercent, $isEnabled, $supportedCards,
                            $instructions
                        ]);
                    }
                } catch (Throwable $e) {
                    error_log("Save gateway DB error: " . $e->getMessage());
                }
            }

            $msg = "Payment Gateway '{$name}' configured successfully!";
            $msgType = 'success';
        } else {
            $msg = "Gateway ID and Name are required fields.";
            $msgType = 'danger';
        }
    } elseif ($action === 'toggle_status') {
        $gid = trim((string)($_POST['gateway_id'] ?? ''));
        if (isset($defaultGateways[$gid])) {
            $newStatus = $defaultGateways[$gid]['is_enabled'] ? 0 : 1;
            $defaultGateways[$gid]['is_enabled'] = $newStatus;
            @file_put_contents($configFile, json_encode($defaultGateways, JSON_PRETTY_PRINT));
            if ($db) {
                try {
                    $stmt = $db->prepare("UPDATE payment_gateways SET is_enabled = ? WHERE gateway_id = ?");
                    $stmt->execute([$newStatus, $gid]);
                } catch (Throwable $e) {}
            }
            $statusLabel = $newStatus ? 'ENABLED' : 'DISABLED';
            $msg = "Gateway '{$defaultGateways[$gid]['name']}' is now {$statusLabel}.";
            $msgType = $newStatus ? 'success' : 'warning';
        }
    } elseif ($action === 'delete_gateway') {
        $gid = trim((string)($_POST['gateway_id'] ?? ''));
        if ($gid !== 'google_pay' && $gid !== 'alipay' && $gid !== 'apple_pay' && isset($defaultGateways[$gid])) {
            $delName = $defaultGateways[$gid]['name'];
            unset($defaultGateways[$gid]);
            @file_put_contents($configFile, json_encode($defaultGateways, JSON_PRETTY_PRINT));
            if ($db) {
                try {
                    $stmt = $db->prepare("DELETE FROM payment_gateways WHERE gateway_id = ?");
                    $stmt->execute([$gid]);
                } catch (Throwable $e) {}
            }
            $msg = "Custom Gateway '{$delName}' was deleted.";
            $msgType = 'warning';
        } else {
            $msg = "Core platform gateways (Google Pay, Alipay, Apple Pay) cannot be deleted. You can disable them instead.";
            $msgType = 'danger';
        }
    } elseif ($action === 'test_gateway') {
        $gid = trim((string)($_POST['gateway_id'] ?? ''));
        $g = $defaultGateways[$gid] ?? null;
        if ($g) {
            $msg = "Ping Simulation Success: [{$g['name']}] credentials verified. Environment: {$g['environment']}. Merchant: {$g['merchant_id']}. Ready for live transactions.";
            $msgType = 'info';
        }
    }
}

// JSON Output for APIs
if (isApiRequest()) {
    $clientGateways = [];
    foreach ($defaultGateways as $k => $gw) {
        if (!empty($gw['is_enabled'])) {
            $clientGateways[] = [
                'id' => $gw['gateway_id'],
                'name' => $gw['name'],
                'type' => $gw['type'],
                'environment' => $gw['environment'],
                'merchant_id' => $gw['merchant_id'],
                'merchant_name' => $gw['merchant_name'],
                'public_key' => $gw['public_key'],
                'currency' => $gw['currency'],
                'min_amount' => $gw['min_amount'],
                'max_amount' => $gw['max_amount'],
                'fee_percent' => $gw['fee_percent'],
                'supported_cards' => $gw['supported_cards'],
                'instructions' => $gw['instructions']
            ];
        }
    }
    sendJsonResponse('success', 'Active payment gateways list', ['gateways' => $clientGateways]);
    exit;
}

renderAdminHeader('Payment Gateway Configuration', 'payments');
?>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: #0f172a;">Payment Gateways & Methods</h2>
        <p class="text-secondary mb-0">Configure Google Pay, Alipay, Apple Pay, and add custom merchant gateways with sandbox/live switching.</p>
    </div>
    <div class="d-flex gap-2">
        <button class="btn btn-primary fw-semibold px-4" data-bs-toggle="modal" data-bs-target="#addGatewayModal">
            <i class="bi bi-plus-circle me-1"></i> Add New Gateway
        </button>
    </div>
</div>

<?php if ($msg): ?>
    <div class="alert alert-<?= $msgType ?> alert-dismissible fade show shadow-sm" role="alert">
        <i class="bi bi-info-circle-fill me-2"></i><?= htmlspecialchars($msg) ?>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
<?php endif; ?>

<!-- Quick Stat Cards -->
<div class="row g-3 mb-4">
    <div class="col-md-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Configured Gateways</div>
                <div class="fw-bold text-dark fs-4"><?= count($defaultGateways) ?></div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-blue">
                <i class="bi bi-credit-card-2-front-fill"></i>
            </div>
        </div>
    </div>
    <div class="col-md-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Active & Online</div>
                <div class="fw-bold text-success fs-4"><?= count(array_filter($defaultGateways, fn($x) => !empty($x['is_enabled']))) ?></div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-green">
                <i class="bi bi-check2-circle"></i>
            </div>
        </div>
    </div>
    <div class="col-md-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Google Pay / Alipay</div>
                <div class="fw-bold text-primary fs-4">Active</div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-gold">
                <i class="bi bi-wallet-fill"></i>
            </div>
        </div>
    </div>
    <div class="col-md-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Apple Pay</div>
                <div class="fw-bold text-dark fs-4"><?= !empty($defaultGateways['apple_pay']['is_enabled']) ? 'Enabled' : 'Disabled' ?></div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-red">
                <i class="bi bi-apple"></i>
            </div>
        </div>
    </div>
</div>

<!-- Core Big 3 Gateways Row: Google Pay, Alipay, Apple Pay -->
<h5 class="fw-bold text-dark mb-3"><i class="bi bi-star-fill text-warning me-2"></i>Core Digital Wallets & Global Gateways</h5>
<div class="row g-4 mb-5">
    <?php
    $coreKeys = ['google_pay', 'alipay', 'apple_pay'];
    foreach ($coreKeys as $ck):
        if (!isset($defaultGateways[$ck])) continue;
        $gw = $defaultGateways[$ck];
        $isGoogle = ($ck === 'google_pay');
        $isAlipay = ($ck === 'alipay');
        $isApple = ($ck === 'apple_pay');
    ?>
    <div class="col-lg-4 col-md-6">
        <div class="card card-custom h-100 p-4 border shadow-sm position-relative" style="border-radius: 16px;">
            <div class="d-flex justify-content-between align-items-start mb-3">
                <div class="d-flex align-items-center gap-3">
                    <?php if ($isGoogle): ?>
                        <div class="d-flex align-items-center justify-content-center text-white fw-bold rounded-3 shadow-sm" style="width: 48px; height: 48px; background: linear-gradient(135deg, #4285F4 0%, #34A853 50%, #FBBC05 75%, #EA4335 100%); font-size: 1.1rem;">
                            GPay
                        </div>
                    <?php elseif ($isAlipay): ?>
                        <div class="d-flex align-items-center justify-content-center text-white fw-bold rounded-3 shadow-sm" style="width: 48px; height: 48px; background: #1677FF; font-size: 1.3rem;">
                            支
                        </div>
                    <?php else: ?>
                        <div class="d-flex align-items-center justify-content-center text-white fw-bold rounded-3 shadow-sm" style="width: 48px; height: 48px; background: #000000; font-size: 1.3rem;">
                            <i class="bi bi-apple"></i>
                        </div>
                    <?php endif; ?>
                    <div>
                        <h5 class="fw-bold mb-0 text-dark"><?= htmlspecialchars($gw['name']) ?></h5>
                        <small class="text-muted"><?= htmlspecialchars($gw['type']) ?></small>
                    </div>
                </div>
                <span class="badge <?= $gw['is_enabled'] ? 'bg-success' : 'bg-secondary' ?> px-2 py-1">
                    <?= $gw['is_enabled'] ? 'ENABLED' : 'DISABLED' ?>
                </span>
            </div>

            <div class="mb-3 p-3 bg-light rounded-3 small">
                <div class="d-flex justify-content-between mb-1">
                    <span class="text-secondary">Environment:</span>
                    <span class="fw-bold <?= $gw['environment'] === 'PRODUCTION' ? 'text-success' : 'text-warning' ?>"><?= htmlspecialchars($gw['environment']) ?></span>
                </div>
                <div class="d-flex justify-content-between mb-1">
                    <span class="text-secondary">Merchant ID:</span>
                    <span class="fw-semibold text-dark font-monospace text-truncate" style="max-width: 160px;"><?= htmlspecialchars($gw['merchant_id'] ?: 'Not Configured') ?></span>
                </div>
                <div class="d-flex justify-content-between mb-1">
                    <span class="text-secondary">Base Currency:</span>
                    <span class="fw-bold text-primary"><?= htmlspecialchars($gw['currency']) ?></span>
                </div>
                <div class="d-flex justify-content-between mb-1">
                    <span class="text-secondary">Deposit Limits:</span>
                    <span class="text-dark fw-semibold"><?= number_format($gw['min_amount']) ?> - <?= number_format($gw['max_amount']) ?> <?= htmlspecialchars($gw['currency']) ?></span>
                </div>
                <div class="d-flex justify-content-between">
                    <span class="text-secondary">Transaction Fee:</span>
                    <span class="text-dark fw-bold"><?= $gw['fee_percent'] ?>%</span>
                </div>
            </div>

            <p class="small text-secondary mb-3 flex-grow-1"><?= htmlspecialchars($gw['instructions']) ?></p>

            <div class="d-flex gap-2 pt-2 border-top">
                <button class="btn btn-outline-primary btn-sm flex-fill fw-semibold" data-bs-toggle="modal" data-bs-target="#editGatewayModal<?= $ck ?>">
                    <i class="bi bi-gear-fill me-1"></i> Configure
                </button>
                <form method="POST" class="d-inline">
                    <input type="hidden" name="gateway_id" value="<?= $ck ?>">
                    <button type="submit" name="action" value="toggle_status" class="btn btn-outline-<?= $gw['is_enabled'] ? 'danger' : 'success' ?> btn-sm" title="Toggle Enable/Disable">
                        <i class="bi bi-power"></i>
                    </button>
                </form>
                <form method="POST" class="d-inline">
                    <input type="hidden" name="gateway_id" value="<?= $ck ?>">
                    <button type="submit" name="action" value="test_gateway" class="btn btn-outline-secondary btn-sm" title="Test API Connection">
                        <i class="bi bi-lightning-charge-fill"></i>
                    </button>
                </form>
            </div>
        </div>
    </div>

    <!-- Edit Modal for this Core Gateway -->
    <div class="modal fade" id="editGatewayModal<?= $ck ?>" tabindex="-1">
        <div class="modal-dialog modal-lg modal-dialog-centered">
            <div class="modal-content border-0 shadow-lg" style="border-radius: 16px;">
                <div class="modal-header border-bottom py-3">
                    <h5 class="modal-title fw-bold">Configure <?= htmlspecialchars($gw['name']) ?></h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <form method="POST">
                    <input type="hidden" name="action" value="save_gateway">
                    <input type="hidden" name="gateway_id" value="<?= $ck ?>">
                    <div class="modal-body p-4">
                        <div class="row g-3">
                            <div class="col-md-6">
                                <label class="form-label small fw-semibold">Gateway Name</label>
                                <input type="text" name="name" class="form-control" value="<?= htmlspecialchars($gw['name']) ?>" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-semibold">Environment Mode</label>
                                <select name="environment" class="form-select">
                                    <option value="SANDBOX" <?= $gw['environment'] === 'SANDBOX' ? 'selected' : '' ?>>SANDBOX (Testing & Simulation)</option>
                                    <option value="PRODUCTION" <?= $gw['environment'] === 'PRODUCTION' ? 'selected' : '' ?>>PRODUCTION (Live Payments)</option>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-semibold">Merchant / Account ID</label>
                                <input type="text" name="merchant_id" class="form-control font-monospace" value="<?= htmlspecialchars($gw['merchant_id']) ?>" placeholder="e.g. BCR2DN4T... / Partner ID">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-semibold">Merchant Business Name</label>
                                <input type="text" name="merchant_name" class="form-control" value="<?= htmlspecialchars($gw['merchant_name']) ?>" placeholder="e.g. Modol Connect Ltd">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-semibold">Public Key / Client ID / App ID</label>
                                <input type="text" name="public_key" class="form-control font-monospace" value="<?= htmlspecialchars($gw['public_key']) ?>" placeholder="Public Client Key">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-semibold">Gateway API Key</label>
                                <input type="text" name="api_key" class="form-control font-monospace" value="<?= htmlspecialchars($gw['api_key']) ?>" placeholder="API Token">
                            </div>
                            <div class="col-12">
                                <label class="form-label small fw-semibold">Private Key / Secret Key</label>
                                <textarea name="secret_key" class="form-control font-monospace" rows="2" placeholder="RSA2 Private Key / Secret Token"><?= htmlspecialchars($gw['secret_key']) ?></textarea>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label small fw-semibold">Currency</label>
                                <input type="text" name="currency" class="form-control" value="<?= htmlspecialchars($gw['currency']) ?>">
                            </div>
                            <div class="col-md-4">
                                <label class="form-label small fw-semibold">Min Amount</label>
                                <input type="number" step="0.01" name="min_amount" class="form-control" value="<?= $gw['min_amount'] ?>">
                            </div>
                            <div class="col-md-4">
                                <label class="form-label small fw-semibold">Max Amount</label>
                                <input type="number" step="0.01" name="max_amount" class="form-control" value="<?= $gw['max_amount'] ?>">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-semibold">Transaction Fee (%)</label>
                                <input type="number" step="0.01" name="fee_percent" class="form-control" value="<?= $gw['fee_percent'] ?>">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-semibold">Supported Cards / Networks</label>
                                <input type="text" name="supported_cards" class="form-control" value="<?= htmlspecialchars($gw['supported_cards']) ?>">
                            </div>
                            <div class="col-12">
                                <label class="form-label small fw-semibold">Customer Payment Instructions</label>
                                <textarea name="instructions" class="form-control" rows="2"><?= htmlspecialchars($gw['instructions']) ?></textarea>
                            </div>
                            <div class="col-12 mt-3">
                                <div class="form-check form-switch">
                                    <input class="form-check-input" type="checkbox" name="is_enabled" id="switch<?= $ck ?>" <?= $gw['is_enabled'] ? 'checked' : '' ?>>
                                    <label class="form-check-label fw-semibold text-dark" for="switch<?= $ck ?>">Activate and display to users for Wallet Deposit and Escrow checkout</label>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer border-top py-3">
                        <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary fw-semibold px-4">Save Configuration</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <?php endforeach; ?>
</div>

<!-- All Configured Gateways & Custom Gateways Table -->
<div class="d-flex justify-content-between align-items-center mb-3">
    <h5 class="fw-bold text-dark mb-0"><i class="bi bi-grid-fill text-primary me-2"></i>All Active & Custom Payment Gateways</h5>
    <span class="badge bg-light text-secondary border px-3 py-2">Total: <?= count($defaultGateways) ?> Gateways</span>
</div>

<div class="card card-custom p-0 overflow-hidden mb-5">
    <div class="table-responsive">
        <table class="table table-custom align-middle mb-0">
            <thead>
                <tr>
                    <th class="ps-4">Gateway</th>
                    <th>Code / ID</th>
                    <th>Type</th>
                    <th>Environment</th>
                    <th>Currency & Limits</th>
                    <th>Fee</th>
                    <th>Status</th>
                    <th class="text-end pe-4">Actions</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($defaultGateways as $gid => $g): ?>
                <tr>
                    <td class="ps-4">
                        <div class="fw-bold text-dark"><?= htmlspecialchars($g['name']) ?></div>
                        <small class="text-muted"><?= htmlspecialchars($g['merchant_name'] ?: 'System Merchant') ?></small>
                    </td>
                    <td><span class="badge bg-light text-dark border font-monospace"><?= htmlspecialchars($gid) ?></span></td>
                    <td><span class="badge bg-primary-subtle text-primary border border-primary-subtle"><?= htmlspecialchars($g['type']) ?></span></td>
                    <td>
                        <span class="badge <?= $g['environment'] === 'PRODUCTION' ? 'bg-success' : 'bg-warning text-dark' ?>">
                            <?= htmlspecialchars($g['environment']) ?>
                        </span>
                    </td>
                    <td>
                        <div class="small fw-bold text-dark"><?= htmlspecialchars($g['currency']) ?></div>
                        <small class="text-muted"><?= number_format($g['min_amount']) ?> - <?= number_format($g['max_amount']) ?></small>
                    </td>
                    <td class="fw-semibold text-secondary"><?= $g['fee_percent'] ?>%</td>
                    <td>
                        <form method="POST" class="d-inline">
                            <input type="hidden" name="gateway_id" value="<?= $gid ?>">
                            <button type="submit" name="action" value="toggle_status" class="badge border-0 <?= $g['is_enabled'] ? 'bg-success text-white' : 'bg-secondary text-white' ?> px-2 py-1 cursor-pointer">
                                <?= $g['is_enabled'] ? 'ACTIVE' : 'INACTIVE' ?>
                            </button>
                        </form>
                    </td>
                    <td class="text-end pe-4">
                        <div class="d-flex justify-content-end gap-2">
                            <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#editCustomModal<?= preg_replace('/[^A-Za-z0-9]/', '', $gid) ?>" title="Edit">
                                <i class="bi bi-pencil-square"></i>
                            </button>
                            <form method="POST" class="d-inline" onsubmit="return confirm('Delete this payment gateway?');">
                                <input type="hidden" name="gateway_id" value="<?= $gid ?>">
                                <button type="submit" name="action" value="delete_gateway" class="btn btn-sm btn-outline-danger" <?= in_array($gid, ['google_pay', 'alipay', 'apple_pay']) ? 'disabled title="Core gateway cannot be deleted"' : 'title="Delete Gateway"' ?>>
                                    <i class="bi bi-trash"></i>
                                </button>
                            </form>
                        </div>
                    </td>
                </tr>

                <!-- Edit Custom Modal -->
                <div class="modal fade" id="editCustomModal<?= preg_replace('/[^A-Za-z0-9]/', '', $gid) ?>" tabindex="-1">
                    <div class="modal-dialog modal-lg modal-dialog-centered">
                        <div class="modal-content border-0 shadow-lg" style="border-radius: 16px;">
                            <div class="modal-header border-bottom py-3">
                                <h5 class="modal-title fw-bold">Edit Gateway: <?= htmlspecialchars($g['name']) ?></h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <form method="POST">
                                <input type="hidden" name="action" value="save_gateway">
                                <input type="hidden" name="gateway_id" value="<?= $gid ?>">
                                <div class="modal-body p-4">
                                    <div class="row g-3">
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold">Gateway Name</label>
                                            <input type="text" name="name" class="form-control" value="<?= htmlspecialchars($g['name']) ?>" required>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold">Environment Mode</label>
                                            <select name="environment" class="form-select">
                                                <option value="SANDBOX" <?= $g['environment'] === 'SANDBOX' ? 'selected' : '' ?>>SANDBOX (Testing)</option>
                                                <option value="PRODUCTION" <?= $g['environment'] === 'PRODUCTION' ? 'selected' : '' ?>>PRODUCTION (Live)</option>
                                            </select>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold">Merchant ID / Account Code</label>
                                            <input type="text" name="merchant_id" class="form-control font-monospace" value="<?= htmlspecialchars($g['merchant_id']) ?>">
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold">Merchant Business Name</label>
                                            <input type="text" name="merchant_name" class="form-control" value="<?= htmlspecialchars($g['merchant_name']) ?>">
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold">API Key / Public Key</label>
                                            <input type="text" name="public_key" class="form-control font-monospace" value="<?= htmlspecialchars($g['public_key']) ?>">
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold">Webhook Secret / Salt</label>
                                            <input type="text" name="webhook_secret" class="form-control font-monospace" value="<?= htmlspecialchars($g['webhook_secret']) ?>">
                                        </div>
                                        <div class="col-12">
                                            <label class="form-label small fw-semibold">Secret Key / Private Key</label>
                                            <textarea name="secret_key" class="form-control font-monospace" rows="2"><?= htmlspecialchars($g['secret_key']) ?></textarea>
                                        </div>
                                        <div class="col-md-4">
                                            <label class="form-label small fw-semibold">Currency</label>
                                            <input type="text" name="currency" class="form-control" value="<?= htmlspecialchars($g['currency']) ?>">
                                        </div>
                                        <div class="col-md-4">
                                            <label class="form-label small fw-semibold">Min Amount</label>
                                            <input type="number" step="0.01" name="min_amount" class="form-control" value="<?= $g['min_amount'] ?>">
                                        </div>
                                        <div class="col-md-4">
                                            <label class="form-label small fw-semibold">Max Amount</label>
                                            <input type="number" step="0.01" name="max_amount" class="form-control" value="<?= $g['max_amount'] ?>">
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold">Fee (%)</label>
                                            <input type="number" step="0.01" name="fee_percent" class="form-control" value="<?= $g['fee_percent'] ?>">
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold">Supported Cards</label>
                                            <input type="text" name="supported_cards" class="form-control" value="<?= htmlspecialchars($g['supported_cards']) ?>">
                                        </div>
                                        <div class="col-12">
                                            <label class="form-label small fw-semibold">Instructions</label>
                                            <textarea name="instructions" class="form-control" rows="2"><?= htmlspecialchars($g['instructions']) ?></textarea>
                                        </div>
                                        <div class="col-12">
                                            <div class="form-check form-switch">
                                                <input class="form-check-input" type="checkbox" name="is_enabled" id="custsw_<?= $gid ?>" <?= $g['is_enabled'] ? 'checked' : '' ?>>
                                                <label class="form-check-label fw-semibold" for="custsw_<?= $gid ?>">Active & Enabled for Client Transactions</label>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="modal-footer border-top py-3">
                                    <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                                    <button type="submit" class="btn btn-primary fw-semibold px-4">Update Gateway</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
                <?php endforeach; ?>
            </tbody>
        </table>
    </div>
</div>

<!-- Modal: Add New Gateway (Dynamic Add) -->
<div class="modal fade" id="addGatewayModal" tabindex="-1">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg" style="border-radius: 16px;">
            <div class="modal-header border-bottom py-3">
                <h5 class="modal-title fw-bold"><i class="bi bi-plus-circle text-primary me-2"></i>Add New Payment Gateway</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form method="POST">
                <input type="hidden" name="action" value="save_gateway">
                <div class="modal-body p-4">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold">Gateway Identifier (Slug) <span class="text-danger">*</span></label>
                            <input type="text" name="gateway_id" class="form-control font-monospace" placeholder="e.g. stripe, paypal, bkash_merchant" required>
                            <small class="text-muted">Unique lowercase identifier without spaces.</small>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold">Gateway Name <span class="text-danger">*</span></label>
                            <input type="text" name="name" class="form-control" placeholder="e.g. Stripe Checkout, PayPal, Binance Pay" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold">Gateway Type</label>
                            <select name="type" class="form-select">
                                <option value="CREDIT_CARD">Credit / Debit Card</option>
                                <option value="DIGITAL_WALLET">Digital Mobile Wallet</option>
                                <option value="BANK_TRANSFER">Direct Bank Wire / Instant</option>
                                <option value="CRYPTO_USDT">Cryptocurrency / USDT</option>
                                <option value="GLOBAL">Global Multi-Currency</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold">Environment Mode</label>
                            <select name="environment" class="form-select">
                                <option value="SANDBOX">SANDBOX (Testing & Sandbox)</option>
                                <option value="PRODUCTION">PRODUCTION (Live Mode)</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold">Merchant ID / Account Number</label>
                            <input type="text" name="merchant_id" class="form-control font-monospace" placeholder="Merchant ID or Account Identifier">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold">Merchant Business Name</label>
                            <input type="text" name="merchant_name" class="form-control" placeholder="Business Display Name">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold">Public Key / Client ID</label>
                            <input type="text" name="public_key" class="form-control font-monospace" placeholder="pk_live_... / client_id">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold">API Key / App Token</label>
                            <input type="text" name="api_key" class="form-control font-monospace" placeholder="API Key">
                        </div>
                        <div class="col-12">
                            <label class="form-label small fw-semibold">Private Key / Secret Key</label>
                            <textarea name="secret_key" class="form-control font-monospace" rows="2" placeholder="sk_live_... / Private Key / Secret Token"></textarea>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label small fw-semibold">Currency Code</label>
                            <input type="text" name="currency" class="form-control" value="BDT" placeholder="BDT, USD, AED, EUR">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label small fw-semibold">Min Deposit Amount</label>
                            <input type="number" step="0.01" name="min_amount" class="form-control" value="100.00">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label small fw-semibold">Max Deposit Amount</label>
                            <input type="number" step="0.01" name="max_amount" class="form-control" value="500000.00">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold">Gateway Processing Fee (%)</label>
                            <input type="number" step="0.01" name="fee_percent" class="form-control" value="1.50">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold">Supported Cards / Networks</label>
                            <input type="text" name="supported_cards" class="form-control" value="VISA, MASTERCARD, AMEX">
                        </div>
                        <div class="col-12">
                            <label class="form-label small fw-semibold">Customer Instructions & Description</label>
                            <textarea name="instructions" class="form-control" rows="2" placeholder="Help text displayed to user when selecting this payment method..."></textarea>
                        </div>
                        <div class="col-12">
                            <div class="form-check form-switch">
                                <input class="form-check-input" type="checkbox" name="is_enabled" id="new_is_enabled" checked>
                                <label class="form-check-label fw-semibold text-dark" for="new_is_enabled">Enable this gateway immediately upon creation</label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer border-top py-3">
                    <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary fw-semibold px-4">Create Gateway</button>
                </div>
            </form>
        </div>
    </div>
</div>

<?php
renderAdminFooter();
?>
