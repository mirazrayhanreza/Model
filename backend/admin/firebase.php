<?php
declare(strict_types=1);

// backend/admin/firebase.php
// Production Firebase Control Center & Storage Manager for Admin Panel (PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/auth.php';
require_once __DIR__ . '/../config/firebase.php';
require_once __DIR__ . '/layout.php';

checkAdminAuth();

$msg = '';
$msgType = 'info';
$uploadedResult = null;

// Handle Form Submissions
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $action = $_POST['action'] ?? '';

    // 1. Direct Upload to Firebase Storage
    if ($action === 'upload_file') {
        try {
            if (!isset($_FILES['file']) || $_FILES['file']['error'] !== UPLOAD_ERR_OK) {
                throw new RuntimeException('Please choose a valid file to upload.');
            }
            $tmpPath = $_FILES['file']['tmp_name'];
            $origName = $_FILES['file']['name'];
            $folder = trim($_POST['folder'] ?? 'admin_uploads');
            $folder = preg_replace('/[^a-zA-Z0-9_\-]/', '', $folder) ?: 'admin_uploads';

            $finfo = finfo_open(FILEINFO_MIME_TYPE);
            $mime = finfo_file($finfo, $tmpPath) ?: 'image/jpeg';
            finfo_close($finfo);

            $content = file_get_contents($tmpPath);
            $ext = pathinfo($origName, PATHINFO_EXTENSION) ?: 'jpg';
            $remotePath = $folder . '/' . time() . '_' . bin2hex(random_bytes(4)) . '.' . $ext;

            $uploadedResult = FirebaseService::uploadToStorage($content, $remotePath, $mime);
            $msg = 'File successfully uploaded to Firebase Storage!';
            $msgType = 'success';
        } catch (Throwable $e) {
            $msg = 'Upload failed: ' . $e->getMessage();
            $msgType = 'danger';
        }
    }

    // 2. Save Firebase Configuration
    if ($action === 'save_config') {
        $projId = trim($_POST['project_id'] ?? '');
        $bucket = trim($_POST['storage_bucket'] ?? '');
        $apiKey = trim($_POST['api_key'] ?? '');

        if (!empty($projId) && !empty($bucket)) {
            FirebaseConfig::saveCustomSettings([
                'project_id' => $projId,
                'storage_bucket' => $bucket,
                'api_key' => $apiKey
            ]);
            $msg = 'Firebase settings updated successfully!';
            $msgType = 'success';
        } else {
            $msg = 'Project ID and Storage Bucket are required.';
            $msgType = 'warning';
        }
    }

    // 3. Upload Service Account Key
    if ($action === 'upload_service_account') {
        if (isset($_FILES['service_account_file']) && $_FILES['service_account_file']['error'] === UPLOAD_ERR_OK) {
            $jsonContent = file_get_contents($_FILES['service_account_file']['tmp_name']);
            $decoded = json_decode($jsonContent, true);
            if (is_array($decoded) && isset($decoded['project_id'])) {
                file_put_contents(FirebaseConfig::SERVICE_ACCOUNT_FILE, json_encode($decoded, JSON_PRETTY_PRINT));
                $msg = 'Service Account JSON uploaded and validated successfully for project: ' . htmlspecialchars($decoded['project_id']);
                $msgType = 'success';
            } else {
                $msg = 'Invalid Service Account JSON format. Must contain project_id.';
                $msgType = 'danger';
            }
        }
    }

    // 4. Sync Models to Firestore
    if ($action === 'sync_firestore') {
        try {
            $db = Database::getInstance();
            $stmt = $db->query("SELECT * FROM models LIMIT 50");
            $models = $stmt->fetchAll(PDO::FETCH_ASSOC);
            $synced = 0;
            foreach ($models as $m) {
                $docId = 'model_' . $m['id'];
                $res = FirebaseService::upsertFirestoreDocument('models', $docId, [
                    'name' => $m['name'],
                    'hourly_rate' => (float)$m['hourly_rate'],
                    'category' => $m['category'] ?? 'Fashion',
                    'location' => $m['location'] ?? 'Dhaka',
                    'rating' => (float)($m['rating'] ?? 5.0),
                    'is_verified' => true
                ]);
                if ($res['success']) {
                    $synced++;
                }
            }
            $msg = "Successfully synchronized {$synced} models to Firebase Firestore!";
            $msgType = 'success';
        } catch (Throwable $e) {
            $msg = 'Firestore Sync error: ' . $e->getMessage();
            $msgType = 'danger';
        }
    }
}

// If API requested
if (isApiRequest()) {
    sendJsonResponse('success', 'Firebase admin status', [
        'project_id' => FirebaseConfig::getProjectId(),
        'storage_bucket' => FirebaseConfig::getStorageBucket(),
        'has_service_account' => FirebaseConfig::hasServiceAccount(),
        'connection' => FirebaseService::testConnection(),
        'uploaded' => $uploadedResult
    ]);
}

// Live Connection Test
$connStatus = FirebaseService::testConnection();

renderAdminHeader('Firebase Control Hub', 'firebase');
?>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <h3 class="fw-bold mb-1"><i class="bi bi-fire text-warning me-2"></i>Firebase Backend Hub</h3>
        <p class="text-secondary mb-0">Control Firebase Storage, Firestore real-time sync, and app credentials</p>
    </div>
    <div class="d-flex gap-2">
        <form method="POST" class="d-inline">
            <input type="hidden" name="action" value="sync_firestore">
            <button type="submit" class="btn btn-outline-warning">
                <i class="bi bi-arrow-repeat me-1"></i> Sync to Firestore
            </button>
        </form>
        <a href="firebase.php" class="btn btn-outline-light">
            <i class="bi bi-arrow-clockwise me-1"></i> Refresh Status
        </a>
    </div>
</div>

<?php if ($msg): ?>
    <div class="alert alert-<?= $msgType ?> alert-dismissible fade show" role="alert">
        <?= htmlspecialchars($msg) ?>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
<?php endif; ?>

<!-- Quick Status Cards -->
<div class="row g-3 mb-4">
    <div class="col-md-4">
        <div class="card card-custom p-3">
            <div class="d-flex justify-content-between align-items-start">
                <div>
                    <span class="text-secondary small">Firebase Project ID</span>
                    <h5 class="fw-bold text-white mt-1 mb-0"><?= htmlspecialchars(FirebaseConfig::getProjectId()) ?></h5>
                </div>
                <div class="p-2 rounded bg-dark border border-secondary text-warning">
                    <i class="bi bi-cloud-check fs-4"></i>
                </div>
            </div>
            <div class="mt-3">
                <span class="badge bg-success"><i class="bi bi-check-circle me-1"></i>Configured</span>
            </div>
        </div>
    </div>

    <div class="col-md-4">
        <div class="card card-custom p-3">
            <div class="d-flex justify-content-between align-items-start">
                <div>
                    <span class="text-secondary small">Storage Bucket</span>
                    <h6 class="fw-bold text-white mt-1 mb-0 text-break"><?= htmlspecialchars(FirebaseConfig::getStorageBucket()) ?></h6>
                </div>
                <div class="p-2 rounded bg-dark border border-secondary text-info">
                    <i class="bi bi-hdd-network fs-4"></i>
                </div>
            </div>
            <div class="mt-3">
                <span class="badge bg-<?= $connStatus['connected'] ? 'success' : 'warning' ?>">
                    <i class="bi bi-reception-4 me-1"></i><?= $connStatus['connected'] ? 'Online & Ready' : 'Standby' ?>
                </span>
            </div>
        </div>
    </div>

    <div class="col-md-4">
        <div class="card card-custom p-3">
            <div class="d-flex justify-content-between align-items-start">
                <div>
                    <span class="text-secondary small">Service Account</span>
                    <h6 class="fw-bold text-white mt-1 mb-0">
                        <?= FirebaseConfig::hasServiceAccount() ? 'serviceAccountKey.json Loaded' : 'REST Mode (Default)' ?>
                    </h6>
                </div>
                <div class="p-2 rounded bg-dark border border-secondary text-primary">
                    <i class="bi bi-key fs-4"></i>
                </div>
            </div>
            <div class="mt-3">
                <span class="badge bg-info text-dark">
                    <?= FirebaseConfig::hasServiceAccount() ? 'Full Admin Access' : 'API Key Access' ?>
                </span>
            </div>
        </div>
    </div>
</div>

<div class="row g-4">
    <!-- File Upload Section -->
    <div class="col-lg-6">
        <div class="card card-custom p-4 h-100">
            <h5 class="fw-bold text-white mb-3">
                <i class="bi bi-cloud-arrow-up-fill text-danger me-2"></i>Upload File to Firebase Storage
            </h5>
            <p class="text-secondary small mb-3">
                Upload model photos, user avatars, or payment receipts directly to your Firebase Storage bucket (<code><?= htmlspecialchars(FirebaseConfig::getStorageBucket()) ?></code>).
            </p>

            <form method="POST" enctype="multipart/form-data">
                <input type="hidden" name="action" value="upload_file">
                
                <div class="mb-3">
                    <label class="form-label text-secondary small">Target Folder</label>
                    <select name="folder" class="form-select bg-dark text-white border-secondary">
                        <option value="models">models/ (Model Portfolios)</option>
                        <option value="receipts">receipts/ (Payment Screenshots)</option>
                        <option value="users">users/ (User Avatars)</option>
                        <option value="chats">chats/ (Chat Attachments)</option>
                        <option value="general">general/ (App Assets)</option>
                    </select>
                </div>

                <div class="mb-4">
                    <label class="form-label text-secondary small">Choose File (Image or Document)</label>
                    <input type="file" name="file" class="form-control bg-dark text-white border-secondary" required accept="image/*,.pdf">
                </div>

                <button type="submit" class="btn btn-danger w-100 py-2 fw-semibold">
                    <i class="bi bi-upload me-2"></i>Upload to Firebase Storage
                </button>
            </form>

            <?php if ($uploadedResult): ?>
                <div class="mt-4 p-3 bg-dark border border-success rounded">
                    <h6 class="text-success fw-bold mb-2"><i class="bi bi-check2-circle me-1"></i>Uploaded Successfully</h6>
                    <div class="mb-2">
                        <small class="text-secondary">Public Storage URL:</small>
                        <div class="input-group mt-1">
                            <input type="text" class="form-control form-control-sm bg-black text-white border-secondary" id="uploadedUrl" value="<?= htmlspecialchars($uploadedResult['url']) ?>" readonly>
                            <button class="btn btn-sm btn-outline-secondary" onclick="navigator.clipboard.writeText(document.getElementById('uploadedUrl').value); alert('Copied to clipboard!');">Copy</button>
                        </div>
                    </div>
                    <?php if (str_starts_with($uploadedResult['content_type'], 'image/')): ?>
                        <div class="text-center mt-3">
                            <img src="<?= htmlspecialchars($uploadedResult['url']) ?>" class="img-fluid rounded border border-secondary" style="max-height: 180px;" alt="Uploaded Preview">
                        </div>
                    <?php endif; ?>
                </div>
            <?php endif; ?>
        </div>
    </div>

    <!-- Firebase Settings & Config Section -->
    <div class="col-lg-6">
        <div class="card card-custom p-4 mb-4">
            <h5 class="fw-bold text-white mb-3">
                <i class="bi bi-sliders text-warning me-2"></i>Firebase Configuration
            </h5>
            
            <form method="POST">
                <input type="hidden" name="action" value="save_config">
                
                <div class="mb-3">
                    <label class="form-label text-secondary small">Project ID</label>
                    <input type="text" name="project_id" class="form-control bg-dark text-white border-secondary" value="<?= htmlspecialchars(FirebaseConfig::getProjectId()) ?>" required>
                </div>

                <div class="mb-3">
                    <label class="form-label text-secondary small">Storage Bucket Name</label>
                    <input type="text" name="storage_bucket" class="form-control bg-dark text-white border-secondary" value="<?= htmlspecialchars(FirebaseConfig::getStorageBucket()) ?>" required>
                </div>

                <div class="mb-4">
                    <label class="form-label text-secondary small">Firebase Web API Key</label>
                    <input type="password" name="api_key" class="form-control bg-dark text-white border-secondary" value="<?= htmlspecialchars(FirebaseConfig::getApiKey()) ?>">
                    <small class="text-muted">From google-services.json current_key</small>
                </div>

                <button type="submit" class="btn btn-outline-warning w-100 py-2">
                    <i class="bi bi-save me-1"></i>Save Firebase Configuration
                </button>
            </form>
        </div>

        <div class="card card-custom p-4">
            <h6 class="fw-bold text-white mb-2">
                <i class="bi bi-shield-lock text-info me-2"></i>Service Account JSON (Optional)
            </h6>
            <p class="text-secondary small mb-3">
                Upload your Firebase <code>serviceAccountKey.json</code> from Google Cloud Console to grant full administrative privileges to the PHP backend.
            </p>

            <form method="POST" enctype="multipart/form-data">
                <input type="hidden" name="action" value="upload_service_account">
                <div class="input-group">
                    <input type="file" name="service_account_file" class="form-control bg-dark text-white border-secondary" accept=".json" required>
                    <button type="submit" class="btn btn-outline-info">Upload Key</button>
                </div>
            </form>
        </div>
    </div>
</div>

<div class="card card-custom p-4 mt-4">
    <h5 class="fw-bold text-white mb-3"><i class="bi bi-code-slash text-success me-2"></i>Developer API Integration Reference</h5>
    <div class="row g-3">
        <div class="col-md-6">
            <div class="p-3 bg-dark rounded border border-secondary">
                <h6 class="text-warning fw-semibold mb-1">Android & Web Upload Endpoint:</h6>
                <code>POST /api/firebase_upload.php</code>
                <p class="text-secondary small mt-2 mb-0">
                    Send multipart with <code>file</code> or JSON with <code>base64_data</code>. Automatically uploads to Firebase Storage and returns public URL.
                </p>
            </div>
        </div>
        <div class="col-md-6">
            <div class="p-3 bg-dark rounded border border-secondary">
                <h6 class="text-info fw-semibold mb-1">Firebase Auth Verification:</h6>
                <code>POST /api/auth/firebase_auth.php</code>
                <p class="text-secondary small mt-2 mb-0">
                    Send <code>uid</code>, <code>email</code>, <code>name</code>. Links Firebase user authentication with the database wallet and bookings.
                </p>
            </div>
        </div>
    </div>
</div>

<?php
renderAdminFooter();
?>
