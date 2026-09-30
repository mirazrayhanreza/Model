<?php
declare(strict_types=1);

// backend/admin/models.php
// Admin Panel - Comprehensive Model Profile View & Edit Interface (PHP 8.2+)

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../config/auth.php';
require_once __DIR__ . '/layout.php';

checkAdminAuth();
$db = Database::getInstance();

// Ensure models table structure
$db->exec("
CREATE TABLE IF NOT EXISTS models (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    uid TEXT UNIQUE,
    name TEXT NOT NULL,
    hourly_rate REAL DEFAULT 1500.0,
    daily_rate REAL DEFAULT 8000.0,
    category TEXT DEFAULT 'Fashion',
    location TEXT DEFAULT 'Dhaka',
    country TEXT DEFAULT 'Bangladesh',
    phone TEXT,
    email TEXT,
    services TEXT DEFAULT 'Fashion & Runway, Commercial, Editorial',
    is_online INTEGER DEFAULT 1,
    is_verified INTEGER DEFAULT 1,
    rating REAL DEFAULT 4.9,
    review_count INTEGER DEFAULT 128,
    bio TEXT,
    avatar_url TEXT,
    status TEXT DEFAULT 'AVAILABLE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
)
");

// Pre-seed sample models if empty
$modelCount = (int)$db->query("SELECT COUNT(*) FROM models")->fetchColumn();
if ($modelCount === 0) {
    $seedModels = [
        [
            'uid' => 'mod_1',
            'name' => 'Jessica Chowdhury',
            'hourly_rate' => 3500.00,
            'daily_rate' => 18000.00,
            'category' => 'Fashion & Runway',
            'location' => 'Gulshan, Dhaka',
            'country' => 'Bangladesh',
            'phone' => '+880 1711 998877',
            'email' => 'jessica.c@modolconnect.com',
            'services' => 'Runway, High Fashion, Magazine Covers',
            'is_online' => 1,
            'is_verified' => 1,
            'rating' => 4.95,
            'review_count' => 142,
            'bio' => 'Professional fashion and runway model with over 6 years of experience in top international and local fashion weeks.',
            'avatar_url' => 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&h=150&fit=crop&crop=faces',
            'status' => 'AVAILABLE'
        ],
        [
            'uid' => 'mod_2',
            'name' => 'Tania Islam',
            'hourly_rate' => 2800.00,
            'daily_rate' => 14000.00,
            'category' => 'Commercial Photography',
            'location' => 'Banani, Dhaka',
            'country' => 'Bangladesh',
            'phone' => '+880 1819 123456',
            'email' => 'tania.islam@modolconnect.com',
            'services' => 'TV Commercials, Billboard, Digital Ads',
            'is_online' => 1,
            'is_verified' => 1,
            'rating' => 4.88,
            'review_count' => 98,
            'bio' => 'Commercial model specialized in brand endorsements, cosmetics commercials, and billboard shoots.',
            'avatar_url' => 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150&h=150&fit=crop&crop=faces',
            'status' => 'AVAILABLE'
        ],
        [
            'uid' => 'mod_3',
            'name' => 'Nila Akter',
            'hourly_rate' => 3200.00,
            'daily_rate' => 16000.00,
            'category' => 'Bridal & Editorial',
            'location' => 'GEC Circle, Chittagong',
            'country' => 'Bangladesh',
            'phone' => '+880 1912 345678',
            'email' => 'nila.akter@modolconnect.com',
            'services' => 'Bridal Makeover, Jewelry Shoots, Traditional Attire',
            'is_online' => 1,
            'is_verified' => 1,
            'rating' => 4.92,
            'review_count' => 115,
            'bio' => 'Award-winning bridal model featured in premier wedding magazines and bridal wear campaigns across South Asia.',
            'avatar_url' => 'https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=150&h=150&fit=crop&crop=faces',
            'status' => 'AVAILABLE'
        ],
        [
            'uid' => 'mod_4',
            'name' => 'Sophia Laurent',
            'hourly_rate' => 4500.00,
            'daily_rate' => 24000.00,
            'category' => 'High Fashion',
            'location' => 'Downtown Dubai',
            'country' => 'UAE',
            'phone' => '+971 50 123 9876',
            'email' => 'sophia.l@modolconnect.com',
            'services' => 'Luxury Brands, Haute Couture, International Runway',
            'is_online' => 1,
            'is_verified' => 1,
            'rating' => 4.98,
            'review_count' => 210,
            'bio' => 'International high fashion model operating in Dubai, Milan, and Paris. Extensive luxury portfolio.',
            'avatar_url' => 'https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?w=150&h=150&fit=crop&crop=faces',
            'status' => 'AVAILABLE'
        ]
    ];

    $insModel = $db->prepare("INSERT INTO models (uid, name, hourly_rate, daily_rate, category, location, country, phone, email, services, is_online, is_verified, rating, review_count, bio, avatar_url, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    foreach ($seedModels as $m) {
        $insModel->execute([
            $m['uid'], $m['name'], $m['hourly_rate'], $m['daily_rate'], $m['category'],
            $m['location'], $m['country'], $m['phone'], $m['email'], $m['services'],
            $m['is_online'], $m['is_verified'], $m['rating'], $m['review_count'],
            $m['bio'], $m['avatar_url'], $m['status']
        ]);
    }
}

$msg = '';
$msgType = 'success';

// Handle POST actions
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $action = $_POST['action'] ?? '';

    if ($action === 'add') {
        $name = trim($_POST['name'] ?? '');
        $category = trim($_POST['category'] ?? 'Fashion & Runway');
        $hourlyRate = (float)($_POST['hourly_rate'] ?? 2500.0);
        $dailyRate = (float)($_POST['daily_rate'] ?? ($hourlyRate * 6));
        $location = trim($_POST['location'] ?? 'Dhaka');
        $country = trim($_POST['country'] ?? 'Bangladesh');
        $phone = trim($_POST['phone'] ?? '');
        $email = trim($_POST['email'] ?? '');
        $bio = trim($_POST['bio'] ?? '');
        $services = trim($_POST['services'] ?? 'Runway, Photography');
        $uid = 'mod_' . time() . '_' . rand(100, 999);
        $avatar = 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&h=150&fit=crop&crop=faces';

        if (!empty($name)) {
            $stmt = $db->prepare("INSERT INTO models (uid, name, hourly_rate, daily_rate, category, location, country, phone, email, services, is_online, is_verified, rating, review_count, bio, avatar_url, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, 1, 5.0, 1, ?, ?, 'AVAILABLE')");
            $stmt->execute([$uid, $name, $hourlyRate, $dailyRate, $category, $location, $country, $phone, $email, $services, $bio, $avatar]);
            $msg = "Model '{$name}' created and added successfully!";
            $msgType = 'success';
        } else {
            $msg = "Model name is required.";
            $msgType = 'danger';
        }
    } elseif ($action === 'edit_model') {
        // Change all information of the model
        $id = (int)($_POST['id'] ?? 0);
        $name = trim($_POST['name'] ?? '');
        $category = trim($_POST['category'] ?? 'Fashion');
        $hourlyRate = (float)($_POST['hourly_rate'] ?? 2000.0);
        $dailyRate = (float)($_POST['daily_rate'] ?? 10000.0);
        $location = trim($_POST['location'] ?? 'Dhaka');
        $country = trim($_POST['country'] ?? 'Bangladesh');
        $phone = trim($_POST['phone'] ?? '');
        $email = trim($_POST['email'] ?? '');
        $bio = trim($_POST['bio'] ?? '');
        $services = trim($_POST['services'] ?? '');
        $rating = (float)($_POST['rating'] ?? 5.0);
        $isVerified = (int)($_POST['is_verified'] ?? 1);
        $status = trim($_POST['status'] ?? 'AVAILABLE');
        $isOnline = (int)($_POST['is_online'] ?? 1);

        if ($id > 0 && !empty($name)) {
            $stmt = $db->prepare("UPDATE models SET name = ?, category = ?, hourly_rate = ?, daily_rate = ?, location = ?, country = ?, phone = ?, email = ?, bio = ?, services = ?, rating = ?, is_verified = ?, status = ?, is_online = ? WHERE id = ?");
            $stmt->execute([$name, $category, $hourlyRate, $dailyRate, $location, $country, $phone, $email, $bio, $services, $rating, $isVerified, $status, $isOnline, $id]);
            $msg = "All information for model '{$name}' (#{$id}) updated successfully!";
            $msgType = 'success';
        }
    } elseif ($action === 'verify') {
        $id = (int)($_POST['id'] ?? 0);
        if ($id > 0) {
            $stmt = $db->prepare("UPDATE models SET is_verified = 1 WHERE id = ?");
            $stmt->execute([$id]);
            $msg = "Model marked as Verified.";
            $msgType = 'success';
        }
    } elseif ($action === 'suspend') {
        $id = (int)($_POST['id'] ?? 0);
        if ($id > 0) {
            $stmt = $db->prepare("UPDATE models SET is_verified = 0, status = 'OFFLINE' WHERE id = ?");
            $stmt->execute([$id]);
            $msg = "Model has been suspended.";
            $msgType = 'warning';
        }
    } elseif ($action === 'delete') {
        $id = (int)($_POST['id'] ?? 0);
        if ($id > 0) {
            $stmt = $db->prepare("DELETE FROM models WHERE id = ?");
            $stmt->execute([$id]);
            $msg = "Model removed permanently.";
            $msgType = 'danger';
        }
    }
}

// Search and filter parameters
$search = trim($_GET['search'] ?? '');
$filterCategory = trim($_GET['category'] ?? '');
$filterCountry = trim($_GET['country'] ?? '');
$filterVerified = trim($_GET['verified'] ?? '');

$sql = "SELECT * FROM models WHERE 1=1";
$params = [];

if (!empty($search)) {
    $sql .= " AND (name LIKE ? OR location LIKE ? OR category LIKE ? OR email LIKE ?)";
    $params[] = "%{$search}%";
    $params[] = "%{$search}%";
    $params[] = "%{$search}%";
    $params[] = "%{$search}%";
}
if (!empty($filterCategory)) {
    $sql .= " AND category LIKE ?";
    $params[] = "%{$filterCategory}%";
}
if (!empty($filterCountry)) {
    $sql .= " AND country = ?";
    $params[] = $filterCountry;
}
if ($filterVerified !== '') {
    $sql .= " AND is_verified = ?";
    $params[] = (int)$filterVerified;
}

$sql .= " ORDER BY id DESC";
$stmt = $db->prepare($sql);
$stmt->execute($params);
$models = $stmt->fetchAll(PDO::FETCH_ASSOC);

// API response if requested
if (isApiRequest()) {
    sendJsonResponse('success', 'Models list retrieved', ['models' => $models]);
}

$totalModels = (int)$db->query("SELECT COUNT(*) FROM models")->fetchColumn();
$verifiedCount = (int)$db->query("SELECT COUNT(*) FROM models WHERE is_verified = 1")->fetchColumn();
$onlineCount = (int)$db->query("SELECT COUNT(*) FROM models WHERE is_online = 1")->fetchColumn();

renderAdminHeader('Model Management', 'models');
?>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: #0f172a;">Model Management</h2>
        <p class="text-secondary mb-0">View all model details, change model information, manage hourly rates, categories, and portfolio media.</p>
    </div>
    <div class="d-flex gap-2">
        <a href="firebase.php" class="btn btn-warning fw-semibold shadow-sm">
            <i class="bi bi-fire me-1"></i> Upload Photos to Firebase
        </a>
        <button class="btn btn-primary fw-semibold px-3 py-2 shadow-sm" data-bs-toggle="modal" data-bs-target="#addModelModal">
            <i class="bi bi-plus-lg me-1"></i> Add New Model
        </button>
    </div>
</div>

<?php if ($msg): ?>
    <div class="alert alert-<?= $msgType ?> alert-dismissible fade show shadow-sm" role="alert">
        <i class="bi bi-check-circle-fill me-2"></i><?= htmlspecialchars($msg) ?>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
<?php endif; ?>

<!-- Quick KPIs -->
<div class="row g-3 mb-4">
    <div class="col-xl-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Total Models</div>
                <div class="fw-bold text-dark fs-4"><?= $totalModels ?></div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-blue" style="width: 40px; height: 40px;">
                <i class="bi bi-stars"></i>
            </div>
        </div>
    </div>
    <div class="col-xl-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Verified Models</div>
                <div class="fw-bold text-success fs-4"><?= $verifiedCount ?></div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-green" style="width: 40px; height: 40px;">
                <i class="bi bi-patch-check-fill"></i>
            </div>
        </div>
    </div>
    <div class="col-xl-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Available & Online</div>
                <div class="fw-bold text-primary fs-4"><?= $onlineCount ?></div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-cyan" style="width: 40px; height: 40px;">
                <i class="bi bi-broadcast"></i>
            </div>
        </div>
    </div>
    <div class="col-xl-3 col-sm-6">
        <div class="kpi-card p-3">
            <div>
                <div class="kpi-title small">Average Rating</div>
                <div class="fw-bold text-warning fs-4"><i class="bi bi-star-fill text-warning me-1"></i>4.92 / 5.0</div>
            </div>
            <div class="kpi-icon-wrap kpi-icon-gold" style="width: 40px; height: 40px;">
                <i class="bi bi-award-fill"></i>
            </div>
        </div>
    </div>
</div>

<!-- Search & Filters -->
<div class="card card-custom p-3 mb-4">
    <form method="GET" class="row g-3 align-items-center">
        <div class="col-md-4">
            <div class="input-group">
                <span class="input-group-text bg-white border-end-0"><i class="bi bi-search text-muted"></i></span>
                <input type="text" name="search" value="<?= htmlspecialchars($search) ?>" class="form-control border-start-0 ps-0" placeholder="Search model by name, city, category...">
            </div>
        </div>
        <div class="col-md-3 col-6">
            <select name="category" class="form-select">
                <option value="">Category: All</option>
                <option value="Fashion" <?= $filterCategory === 'Fashion' ? 'selected' : '' ?>>Fashion & Runway</option>
                <option value="Commercial" <?= $filterCategory === 'Commercial' ? 'selected' : '' ?>>Commercial Photography</option>
                <option value="Bridal" <?= $filterCategory === 'Bridal' ? 'selected' : '' ?>>Bridal & Editorial</option>
                <option value="High Fashion" <?= $filterCategory === 'High Fashion' ? 'selected' : '' ?>>High Fashion</option>
            </select>
        </div>
        <div class="col-md-2 col-6">
            <select name="country" class="form-select">
                <option value="">Country: All</option>
                <option value="Bangladesh" <?= $filterCountry === 'Bangladesh' ? 'selected' : '' ?>>Bangladesh</option>
                <option value="UAE" <?= $filterCountry === 'UAE' ? 'selected' : '' ?>>UAE</option>
                <option value="Malaysia" <?= $filterCountry === 'Malaysia' ? 'selected' : '' ?>>Malaysia</option>
            </select>
        </div>
        <div class="col-md-3 col-12 d-flex gap-2">
            <button type="submit" class="btn btn-outline-primary w-100"><i class="bi bi-filter me-1"></i> Apply Filters</button>
            <?php if (!empty($search) || !empty($filterCategory) || !empty($filterCountry)): ?>
                <a href="models.php" class="btn btn-light" title="Reset Filters"><i class="bi bi-x-circle"></i></a>
            <?php endif; ?>
        </div>
    </form>
</div>

<!-- Models Table -->
<div class="card card-custom p-0 overflow-hidden mb-4">
    <div class="table-responsive">
        <table class="table table-custom align-middle mb-0">
            <thead>
                <tr>
                    <th class="ps-4">ID</th>
                    <th>Model Profile</th>
                    <th>Category</th>
                    <th>Hourly & Daily Rate</th>
                    <th>Location & Country</th>
                    <th>Rating</th>
                    <th>Verification</th>
                    <th class="text-end pe-4">Actions</th>
                </tr>
            </thead>
            <tbody>
                <?php if (empty($models)): ?>
                <tr>
                    <td colspan="8" class="text-center py-4 text-muted">No models found matching your criteria.</td>
                </tr>
                <?php else: ?>
                <?php foreach ($models as $m): ?>
                <tr>
                    <td class="ps-4 text-muted fw-bold">#<?= $m['id'] ?></td>
                    <td>
                        <div class="d-flex align-items-center gap-3">
                            <img src="<?= htmlspecialchars($m['avatar_url'] ?: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&h=100&fit=crop&crop=faces') ?>" class="rounded-circle shadow-sm" style="width: 44px; height: 44px; object-fit: cover;" alt="Avatar">
                            <div>
                                <div class="fw-bold text-dark"><?= htmlspecialchars((string)$m['name']) ?></div>
                                <small class="text-muted"><?= htmlspecialchars((string)($m['phone'] ?: ($m['email'] ?: 'No Contact Info'))) ?></small>
                            </div>
                        </div>
                    </td>
                    <td><span class="badge bg-light text-dark border px-2 py-1"><?= htmlspecialchars((string)($m['category'] ?? 'Fashion')) ?></span></td>
                    <td>
                        <div class="text-success fw-bold fs-6">৳<?= number_format((float)$m['hourly_rate'], 2) ?>/hr</div>
                        <small class="text-muted">৳<?= number_format((float)($m['daily_rate'] ?? ((float)$m['hourly_rate'] * 6)), 2) ?>/day</small>
                    </td>
                    <td>
                        <span class="fw-semibold text-secondary"><?= htmlspecialchars((string)($m['location'] ?? 'Dhaka')) ?></span>
                        <small class="text-muted d-block"><?= htmlspecialchars((string)($m['country'] ?? 'Bangladesh')) ?></small>
                    </td>
                    <td>
                        <span class="text-warning fw-bold">
                            <i class="bi bi-star-fill me-1"></i><?= number_format((float)$m['rating'], 2) ?>
                        </span>
                        <small class="text-muted d-block">(<?= (int)($m['review_count'] ?? 1) ?> reviews)</small>
                    </td>
                    <td>
                        <?php if ((int)($m['is_verified'] ?? 1) === 1): ?>
                            <span class="badge-status badge-approved">Verified</span>
                        <?php else: ?>
                            <span class="badge-status badge-pending">Pending</span>
                        <?php endif; ?>
                    </td>
                    <td class="text-end pe-4">
                        <div class="d-flex justify-content-end gap-1">
                            <!-- View All Info Button -->
                            <button class="btn btn-sm btn-outline-info" data-bs-toggle="modal" data-bs-target="#viewModelModal<?= $m['id'] ?>" title="View All Information">
                                <i class="bi bi-eye-fill"></i> View
                            </button>
                            <!-- Change All Info Button -->
                            <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#editModelModal<?= $m['id'] ?>" title="Change All Information">
                                <i class="bi bi-pencil-square"></i> Change
                            </button>
                            <!-- More Dropdown -->
                            <div class="dropdown d-inline">
                                <button class="btn btn-sm btn-outline-secondary dropdown-toggle" data-bs-toggle="dropdown">
                                    <i class="bi bi-three-dots-vertical"></i>
                                </button>
                                <ul class="dropdown-menu dropdown-menu-end shadow-sm">
                                    <li><a class="dropdown-item" href="firebase.php"><i class="bi bi-cloud-arrow-up me-2 text-warning"></i>Firebase Media</a></li>
                                    <li><a class="dropdown-item" href="bookings.php"><i class="bi bi-calendar-event me-2"></i>Bookings History</a></li>
                                    <li><hr class="dropdown-divider"></li>
                                    <form method="POST">
                                        <input type="hidden" name="id" value="<?= $m['id'] ?>">
                                        <?php if ((int)($m['is_verified'] ?? 1) === 1): ?>
                                        <li>
                                            <button type="submit" name="action" value="suspend" class="dropdown-item text-warning" onclick="return confirm('Suspend model profile?');">
                                                <i class="bi bi-slash-circle me-2"></i>Suspend Profile
                                            </button>
                                        </li>
                                        <?php else: ?>
                                        <li>
                                            <button type="submit" name="action" value="verify" class="dropdown-item text-success">
                                                <i class="bi bi-check-circle me-2"></i>Verify Model
                                            </button>
                                        </li>
                                        <?php endif; ?>
                                        <li>
                                            <button type="submit" name="action" value="delete" class="dropdown-item text-danger" onclick="return confirm('Permanently delete model <?= htmlspecialchars($m['name']) ?>?');">
                                                <i class="bi bi-trash me-2"></i>Delete Model
                                            </button>
                                        </li>
                                    </form>
                                </ul>
                            </div>
                        </div>
                    </td>
                </tr>

                <!-- View All Model Information Modal -->
                <div class="modal fade" id="viewModelModal<?= $m['id'] ?>" tabindex="-1">
                    <div class="modal-dialog modal-dialog-centered modal-lg">
                        <div class="modal-content border-0 shadow-lg" style="border-radius: 16px;">
                            <div class="modal-header border-bottom py-3">
                                <h5 class="modal-title fw-bold d-flex align-items-center gap-2">
                                    <i class="bi bi-stars text-primary"></i>
                                    <span>Model Profile: <?= htmlspecialchars($m['name']) ?> (#<?= $m['id'] ?>)</span>
                                </h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body p-4">
                                <div class="row g-4">
                                    <div class="col-md-4 text-center border-end">
                                        <img src="<?= htmlspecialchars($m['avatar_url'] ?: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&h=150&fit=crop&crop=faces') ?>" class="rounded-circle shadow mb-3" style="width: 120px; height: 120px; object-fit: cover;" alt="Avatar">
                                        <h5 class="fw-bold text-dark mb-1"><?= htmlspecialchars($m['name']) ?></h5>
                                        <span class="badge bg-primary text-white mb-2"><?= htmlspecialchars($m['category'] ?? 'Fashion') ?></span>
                                        <div>
                                            <?php if ((int)($m['is_verified'] ?? 1) === 1): ?>
                                                <span class="badge-status badge-approved">Verified Professional</span>
                                            <?php else: ?>
                                                <span class="badge-status badge-pending">Verification Pending</span>
                                            <?php endif; ?>
                                        </div>
                                        <div class="mt-3">
                                            <span class="text-warning fw-bold fs-5">
                                                <i class="bi bi-star-fill"></i> <?= number_format((float)$m['rating'], 2) ?>
                                            </span>
                                            <div class="text-muted small"><?= (int)($m['review_count'] ?? 0) ?> Verified Reviews</div>
                                        </div>
                                    </div>
                                    <div class="col-md-8">
                                        <h6 class="fw-bold text-dark mb-3"><i class="bi bi-info-circle me-2 text-primary"></i>All Information Overview</h6>
                                        <table class="table table-sm table-borderless align-middle mb-3">
                                            <tbody>
                                                <tr>
                                                    <td class="text-secondary fw-semibold" style="width: 150px;">Hourly Rate:</td>
                                                    <td class="text-success fw-bold fs-6">৳<?= number_format((float)$m['hourly_rate'], 2) ?> BDT / hr</td>
                                                </tr>
                                                <tr>
                                                    <td class="text-secondary fw-semibold">Daily Rate:</td>
                                                    <td class="text-dark fw-bold">৳<?= number_format((float)($m['daily_rate'] ?? 0), 2) ?> BDT / day</td>
                                                </tr>
                                                <tr>
                                                    <td class="text-secondary fw-semibold">Location & Country:</td>
                                                    <td class="text-dark"><?= htmlspecialchars($m['location'] ?: 'Dhaka') ?>, <?= htmlspecialchars($m['country'] ?: 'Bangladesh') ?></td>
                                                </tr>
                                                <tr>
                                                    <td class="text-secondary fw-semibold">Contact Phone:</td>
                                                    <td class="text-dark"><?= htmlspecialchars($m['phone'] ?: 'N/A') ?></td>
                                                </tr>
                                                <tr>
                                                    <td class="text-secondary fw-semibold">Contact Email:</td>
                                                    <td class="text-dark"><?= htmlspecialchars($m['email'] ?: 'N/A') ?></td>
                                                </tr>
                                                <tr>
                                                    <td class="text-secondary fw-semibold">Services Offered:</td>
                                                    <td class="text-dark"><?= htmlspecialchars($m['services'] ?: 'Runway, Fashion Photography, Editorial') ?></td>
                                                </tr>
                                                <tr>
                                                    <td class="text-secondary fw-semibold">Availability Status:</td>
                                                    <td>
                                                        <span class="badge bg-success-subtle text-success border border-success-subtle px-2 py-1">
                                                            <?= htmlspecialchars($m['status'] ?? 'AVAILABLE') ?> (<?= ((int)($m['is_online'] ?? 1) === 1 ? 'Online' : 'Offline') ?>)
                                                        </span>
                                                    </td>
                                                </tr>
                                            </tbody>
                                        </table>
                                        <div class="bg-light p-3 rounded">
                                            <span class="fw-bold text-dark d-block mb-1 small text-uppercase">Model Biography:</span>
                                            <p class="text-secondary mb-0 small"><?= nl2br(htmlspecialchars($m['bio'] ?: 'No detailed biography provided.')) ?></p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="modal-footer border-top bg-light">
                                <a href="firebase.php" class="btn btn-warning fw-semibold">
                                    <i class="bi bi-fire me-1"></i> Upload Firebase Portfolio
                                </a>
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                <button type="button" class="btn btn-primary fw-semibold" data-bs-dismiss="modal" data-bs-toggle="modal" data-bs-target="#editModelModal<?= $m['id'] ?>">
                                    <i class="bi bi-pencil-square me-1"></i> Change Model Information
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Edit / Change All Model Information Modal -->
                <div class="modal fade" id="editModelModal<?= $m['id'] ?>" tabindex="-1">
                    <div class="modal-dialog modal-dialog-centered modal-lg">
                        <div class="modal-content border-0 shadow-lg" style="border-radius: 16px;">
                            <div class="modal-header border-bottom py-3">
                                <h5 class="modal-title fw-bold"><i class="bi bi-pencil-square me-2 text-primary"></i>Change All Information: <?= htmlspecialchars($m['name']) ?></h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <form method="POST">
                                <input type="hidden" name="action" value="edit_model">
                                <input type="hidden" name="id" value="<?= $m['id'] ?>">
                                <div class="modal-body p-4">
                                    <div class="row g-3">
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold text-secondary">Model Full Name</label>
                                            <input type="text" name="name" class="form-control" required value="<?= htmlspecialchars($m['name']) ?>">
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold text-secondary">Category</label>
                                            <select name="category" class="form-select">
                                                <option value="Fashion & Runway" <?= ($m['category'] ?? '') === 'Fashion & Runway' ? 'selected' : '' ?>>Fashion & Runway</option>
                                                <option value="Commercial Photography" <?= ($m['category'] ?? '') === 'Commercial Photography' ? 'selected' : '' ?>>Commercial Photography</option>
                                                <option value="Bridal & Editorial" <?= ($m['category'] ?? '') === 'Bridal & Editorial' ? 'selected' : '' ?>>Bridal & Editorial</option>
                                                <option value="High Fashion" <?= ($m['category'] ?? '') === 'High Fashion' ? 'selected' : '' ?>>High Fashion</option>
                                                <option value="Promotional & Brand" <?= ($m['category'] ?? '') === 'Promotional & Brand' ? 'selected' : '' ?>>Promotional & Brand</option>
                                                <option value="Fitness & Glamour" <?= ($m['category'] ?? '') === 'Fitness & Glamour' ? 'selected' : '' ?>>Fitness & Glamour</option>
                                            </select>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold text-secondary">Hourly Rate (৳ BDT)</label>
                                            <input type="number" step="50" name="hourly_rate" class="form-control" required value="<?= (float)$m['hourly_rate'] ?>">
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold text-secondary">Daily Rate (৳ BDT)</label>
                                            <input type="number" step="100" name="daily_rate" class="form-control" value="<?= (float)($m['daily_rate'] ?? 8000.0) ?>">
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold text-secondary">Location / Area</label>
                                            <input type="text" name="location" class="form-control" required value="<?= htmlspecialchars($m['location'] ?: 'Dhaka') ?>">
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold text-secondary">Country</label>
                                            <select name="country" class="form-select">
                                                <option value="Bangladesh" <?= ($m['country'] ?? '') === 'Bangladesh' ? 'selected' : '' ?>>Bangladesh</option>
                                                <option value="UAE" <?= ($m['country'] ?? '') === 'UAE' ? 'selected' : '' ?>>United Arab Emirates</option>
                                                <option value="Malaysia" <?= ($m['country'] ?? '') === 'Malaysia' ? 'selected' : '' ?>>Malaysia</option>
                                                <option value="Saudi Arabia" <?= ($m['country'] ?? '') === 'Saudi Arabia' ? 'selected' : '' ?>>Saudi Arabia</option>
                                                <option value="United States" <?= ($m['country'] ?? '') === 'United States' ? 'selected' : '' ?>>United States</option>
                                            </select>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold text-secondary">Contact Phone</label>
                                            <input type="text" name="phone" class="form-control" value="<?= htmlspecialchars($m['phone'] ?: '') ?>">
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold text-secondary">Contact Email</label>
                                            <input type="email" name="email" class="form-control" value="<?= htmlspecialchars($m['email'] ?: '') ?>">
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold text-secondary">Rating (0 - 5.0)</label>
                                            <input type="number" step="0.01" min="1" max="5" name="rating" class="form-control" value="<?= (float)$m['rating'] ?>">
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold text-secondary">Verification Status</label>
                                            <select name="is_verified" class="form-select">
                                                <option value="1" <?= (int)($m['is_verified'] ?? 1) === 1 ? 'selected' : '' ?>>VERIFIED (Approved)</option>
                                                <option value="0" <?= (int)($m['is_verified'] ?? 1) === 0 ? 'selected' : '' ?>>PENDING / UNVERIFIED</option>
                                            </select>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold text-secondary">Availability Status</label>
                                            <select name="status" class="form-select">
                                                <option value="AVAILABLE" <?= ($m['status'] ?? '') === 'AVAILABLE' ? 'selected' : '' ?>>AVAILABLE (Accepting Bookings)</option>
                                                <option value="BUSY" <?= ($m['status'] ?? '') === 'BUSY' ? 'selected' : '' ?>>BUSY (Currently Booked)</option>
                                                <option value="OFFLINE" <?= ($m['status'] ?? '') === 'OFFLINE' ? 'selected' : '' ?>>OFFLINE</option>
                                            </select>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label small fw-semibold text-secondary">Online Presence</label>
                                            <select name="is_online" class="form-select">
                                                <option value="1" <?= (int)($m['is_online'] ?? 1) === 1 ? 'selected' : '' ?>>Online</option>
                                                <option value="0" <?= (int)($m['is_online'] ?? 1) === 0 ? 'selected' : '' ?>>Offline</option>
                                            </select>
                                        </div>
                                        <div class="col-12">
                                            <label class="form-label small fw-semibold text-secondary">Services Offered (Comma Separated)</label>
                                            <input type="text" name="services" class="form-control" value="<?= htmlspecialchars($m['services'] ?: 'Fashion & Runway, Commercial Photography, Editorial') ?>">
                                        </div>
                                        <div class="col-12">
                                            <label class="form-label small fw-semibold text-secondary">Bio & Portfolio Overview</label>
                                            <textarea name="bio" class="form-control" rows="3"><?= htmlspecialchars($m['bio'] ?: '') ?></textarea>
                                        </div>
                                    </div>
                                </div>
                                <div class="modal-footer border-top">
                                    <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                                    <button type="submit" class="btn btn-primary fw-semibold px-4">Save All Changes</button>
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

<!-- Add Model Modal -->
<div class="modal fade" id="addModelModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content border-0 shadow-lg" style="border-radius: 16px;">
            <div class="modal-header border-bottom py-3">
                <h5 class="modal-title fw-bold"><i class="bi bi-person-plus-fill me-2 text-primary"></i>Add New Model Profile</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form method="POST">
                <input type="hidden" name="action" value="add">
                <div class="modal-body p-4">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label text-secondary small fw-semibold">Full Name</label>
                            <input type="text" name="name" class="form-control" required placeholder="e.g. Ayesha Rahman">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label text-secondary small fw-semibold">Category</label>
                            <select name="category" class="form-select">
                                <option value="Fashion & Runway">Fashion & Runway</option>
                                <option value="Commercial Photography">Commercial Photography</option>
                                <option value="Bridal & Editorial">Bridal & Editorial</option>
                                <option value="High Fashion">High Fashion</option>
                                <option value="Promotional & Brand">Promotional & Brand</option>
                                <option value="Fitness & Glamour">Fitness & Glamour</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label text-secondary small fw-semibold">Hourly Rate (BDT ৳)</label>
                            <input type="number" step="100" name="hourly_rate" class="form-control" required value="2500">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label text-secondary small fw-semibold">Daily Rate (BDT ৳)</label>
                            <input type="number" step="500" name="daily_rate" class="form-control" required value="12000">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label text-secondary small fw-semibold">Location / Area</label>
                            <input type="text" name="location" class="form-control" required value="Gulshan, Dhaka">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label text-secondary small fw-semibold">Country</label>
                            <select name="country" class="form-select">
                                <option value="Bangladesh">Bangladesh</option>
                                <option value="UAE">United Arab Emirates</option>
                                <option value="Malaysia">Malaysia</option>
                                <option value="Saudi Arabia">Saudi Arabia</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label text-secondary small fw-semibold">Contact Phone</label>
                            <input type="text" name="phone" class="form-control" placeholder="+880 1700 000000">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label text-secondary small fw-semibold">Contact Email</label>
                            <input type="email" name="email" class="form-control" placeholder="model@domain.com">
                        </div>
                        <div class="col-12">
                            <label class="form-label text-secondary small fw-semibold">Services Offered</label>
                            <input type="text" name="services" class="form-control" value="Runway, Fashion Photography, Brand Commercials">
                        </div>
                        <div class="col-12">
                            <label class="form-label text-secondary small fw-semibold">Bio / Experience</label>
                            <textarea name="bio" class="form-control" rows="2" placeholder="Experience details, agency representations, awards..."></textarea>
                        </div>
                    </div>
                </div>
                <div class="modal-footer border-top">
                    <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary fw-semibold px-4">Create Model Profile</button>
                </div>
            </form>
        </div>
    </div>
</div>

<?php
renderAdminFooter();
?>
